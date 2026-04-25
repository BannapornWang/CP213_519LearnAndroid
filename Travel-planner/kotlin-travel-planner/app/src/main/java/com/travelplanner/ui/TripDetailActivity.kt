package com.travelplanner.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayout
import com.travelplanner.R
import com.travelplanner.data.Booking
import com.travelplanner.data.BudgetSummary
import com.travelplanner.data.DayPlan
import com.travelplanner.data.Expense
import com.travelplanner.data.ExpenseCategory
import com.travelplanner.data.Trip
import com.travelplanner.data.TripStatus
import com.travelplanner.databinding.ActivityTripDetailBinding
import com.travelplanner.ui.adapters.ExpenseAdapter
import com.travelplanner.ui.adapters.PlaceAdapter
import com.travelplanner.ui.adapters.ItineraryEditAdapter
import com.travelplanner.ui.adapters.EditItineraryItem
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.travelplanner.utils.BudgetUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TripDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TRIP_ID = "extra_trip_id"
    }

    private lateinit var binding: ActivityTripDetailBinding
    private val viewModel: TripViewModel by viewModels()
    private var currentTrip: Trip? = null
    private var selectedDayIndex = 0

    private lateinit var placeAdapter: PlaceAdapter
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var editAdapter: ItineraryEditAdapter
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tripId = intent.getStringExtra(EXTRA_TRIP_ID) ?: run { finish(); return }
        viewModel.loadTrip(tripId)

        setupTabs()
        setupAdapters()
        setupButtons()
        observeTrip()
    }

    private fun setupTabs() {
        listOf("Itinerary", "Budget", "Record").forEach {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(it))
        }
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) { showTab(tab.position) }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        showTab(0)
    }

    private fun setupAdapters() {
        placeAdapter = PlaceAdapter(
            onToggleVisited = { place ->
                currentTrip?.let { trip ->
                    val days   = viewModel.getDays(trip).toMutableList()
                    val day    = days[selectedDayIndex]
                    val places = day.places.map { if (it.id == place.id) it.copy(visited = !it.visited) else it }.toMutableList()
                    days[selectedDayIndex] = day.copy(places = places)
                    viewModel.updateDays(trip, days)
                }
            },
            onDelete = { place ->
                currentTrip?.let { trip ->
                    val days = viewModel.getDays(trip).toMutableList()
                    val day  = days[selectedDayIndex]
                    days[selectedDayIndex] = day.copy(places = day.places.filter { it.id != place.id }.toMutableList())
                    viewModel.updateDays(trip, days)
                }
            }
        )
        binding.recyclerPlaces.layoutManager = LinearLayoutManager(this)
        binding.recyclerPlaces.adapter = placeAdapter

        expenseAdapter = ExpenseAdapter { expense ->
            currentTrip?.let { viewModel.removeExpense(it, expense.id) }
        }
        binding.recyclerExpenses.layoutManager = LinearLayoutManager(this)
        binding.recyclerExpenses.adapter = expenseAdapter

        lateinit var itemTouchHelper: ItemTouchHelper
        editAdapter = ItineraryEditAdapter(mutableListOf()) { holder ->
            itemTouchHelper.startDrag(holder)
        }
        binding.recyclerEditItinerary.layoutManager = LinearLayoutManager(this)
        binding.recyclerEditItinerary.adapter = editAdapter

        itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, 0) {
            
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = if (viewHolder is ItineraryEditAdapter.PlaceViewHolder) ItemTouchHelper.UP or ItemTouchHelper.DOWN else 0
                val swipeFlags = if (viewHolder is ItineraryEditAdapter.PlaceViewHolder) ItemTouchHelper.START else 0
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun canDropOver(
                recyclerView: RecyclerView,
                current: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true // allow dropping/moving over heterogenous views like headers
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPos = viewHolder.bindingAdapterPosition
                val toPos = target.bindingAdapterPosition
                if (fromPos == RecyclerView.NO_POSITION || toPos == RecyclerView.NO_POSITION) return false
                editAdapter.moveItem(fromPos, toPos)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    editAdapter.removeItem(pos)
                }
            }

            override fun onChildDraw(
                c: android.graphics.Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView
                    val paint = android.graphics.Paint()
                    paint.color = android.graphics.Color.RED
                    c.drawRect(
                        itemView.right.toFloat() + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat(),
                        paint
                    )
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
            
            override fun isLongPressDragEnabled(): Boolean = true
            override fun isItemViewSwipeEnabled(): Boolean = true
        })
        itemTouchHelper.attachToRecyclerView(binding.recyclerEditItinerary)
    }

    private fun setupButtons() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnEditItinerary.setOnClickListener { toggleEditMode() }
        binding.btnAddExpense.setOnClickListener {
            currentTrip?.let { showAddExpenseDialog(it) }
        }
        binding.btnOptimizeRoute.setOnClickListener {
            currentTrip?.let { trip ->
                val dayList = viewModel.getDays(trip).toMutableList()
                if (dayList.isNotEmpty()) {
                    val optimized = com.travelplanner.utils.ItineraryUtils.optimizeRoute(dayList[selectedDayIndex].places)
                    dayList[selectedDayIndex] = dayList[selectedDayIndex].copy(places = optimized.toMutableList())
                    viewModel.updateDays(trip, dayList)
                    Toast.makeText(this, "Route optimized!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun toggleEditMode() {
        isEditMode = !isEditMode
        if (isEditMode) {
            binding.btnEditItinerary.text = "Done"
            binding.btnEditItinerary.setTextColor(Color.parseColor("#22C55E")) // Success color
            binding.chipGroupDays.visibility = View.GONE
            binding.btnOptimizeRoute.visibility = View.GONE
            binding.recyclerPlaces.visibility = View.GONE
            binding.recyclerEditItinerary.visibility = View.VISIBLE
            
            // Populate the edit adapter with all days and places
            currentTrip?.let { trip ->
                val existingDays = viewModel.getDays(trip)
                val items = mutableListOf<EditItineraryItem>()
                
                val totalDays = BudgetUtils.getDaysCount(trip.startDate, trip.endDate)
                val fmt = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                val startCal = java.util.Calendar.getInstance().apply { time = fmt.parse(trip.startDate) ?: java.util.Date() }

                for (i in 0 until totalDays) {
                    val dateStr = fmt.format(startCal.time)
                    items.add(EditItineraryItem.DayHeader(i, BudgetUtils.formatShortDate(dateStr)))
                    
                    val dayPlan = if (i < existingDays.size) existingDays[i] else null
                    dayPlan?.places?.forEach { place ->
                        items.add(EditItineraryItem.PlaceItem(place))
                    }
                    startCal.add(java.util.Calendar.DAY_OF_YEAR, 1)
                }
                
                editAdapter.submitList(items)
                binding.btnEditItinerary.text = "Done "
            }
        } else {
            binding.btnEditItinerary.text = "Edit"
            binding.btnEditItinerary.setTextColor(getColor(R.color.accent))
            binding.chipGroupDays.visibility = View.VISIBLE
            binding.btnOptimizeRoute.visibility = View.VISIBLE
            binding.recyclerPlaces.visibility = View.VISIBLE
            binding.recyclerEditItinerary.visibility = View.GONE
            
            // Save changes
            currentTrip?.let { trip ->
                val newItems = editAdapter.getItems()
                val newDays = mutableListOf<DayPlan>()
                var currentDayPlan: DayPlan? = null
                var currentPlaces = mutableListOf<com.travelplanner.data.Place>()
                
                val fmt = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                
                for (item in newItems) {
                    when (item) {
                        is EditItineraryItem.DayHeader -> {
                            if (currentDayPlan != null) {
                                newDays.add(currentDayPlan.copy(places = currentPlaces))
                            }
                            
                            val tempCal = java.util.Calendar.getInstance().apply { time = fmt.parse(trip.startDate) ?: java.util.Date() }
                            tempCal.add(java.util.Calendar.DAY_OF_YEAR, item.dayIndex)
                            val dateStr = fmt.format(tempCal.time)
                            
                            currentDayPlan = DayPlan(dateStr)
                            currentPlaces = mutableListOf()
                        }
                        is EditItineraryItem.PlaceItem -> {
                            currentPlaces.add(item.place)
                        }
                    }
                }
                if (currentDayPlan != null) {
                    newDays.add(currentDayPlan.copy(places = currentPlaces))
                }
                
                viewModel.updateDays(trip, newDays)
            }
        }
    }

    private fun observeTrip() {
        viewModel.currentTrip.observe(this) { trip ->
            trip ?: return@observe
            currentTrip = trip
            renderTrip(trip)
        }
    }

    // ──────────────────────────────────────────────────────────────────────────

    private val STATUS_COLORS = mapOf(
        TripStatus.PLANNING  to "#F97316",
        TripStatus.UPCOMING  to "#0EA5E9",
        TripStatus.ONGOING   to "#22C55E",
        TripStatus.COMPLETED to "#6B7280"
    )
    private val STATUS_LABELS = mapOf(
        TripStatus.PLANNING  to "Planning",
        TripStatus.UPCOMING  to "Upcoming",
        TripStatus.ONGOING   to "Ongoing",
        TripStatus.COMPLETED to "Completed"
    )

    private fun renderTrip(trip: Trip) {
        val days     = viewModel.getDays(trip)
        val expenses = viewModel.getExpenses(trip)
        val summary  = BudgetUtils.calculateSummary(expenses, days)

        renderHeader(trip, summary)
        renderMeta(trip, summary.total)
        renderDayChips(trip, days)
        renderBudgetOverview(trip, summary)
        expenseAdapter.submitList(expenses)
        renderRecord(trip, days)

        showTab(binding.tabLayout.selectedTabPosition)
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private fun renderHeader(trip: Trip, summary: BudgetSummary) {
        binding.textTripTitle.text  = trip.title
        binding.textHeaderDest.text = trip.destination

        val hex   = STATUS_COLORS[trip.status] ?: "#6B7280"
        val color = Color.parseColor(hex)
        val bg    = Color.argb(51, Color.red(color), Color.green(color), Color.blue(color))

        binding.textStatus.text = STATUS_LABELS[trip.status] ?: trip.status.name
        binding.textStatus.setTextColor(color)
        binding.textStatus.background?.setTint(bg)

        // Tap status badge to cycle
        binding.textStatus.setOnClickListener {
            val statuses = TripStatus.values()
            val next = statuses[(statuses.indexOf(trip.status) + 1) % statuses.size]
            viewModel.updateStatus(trip, next)
        }
    }

    // ── Meta row ──────────────────────────────────────────────────────────────

    private fun renderMeta(trip: Trip, totalSpent: Double) {
        binding.textMetaDates.text = "${BudgetUtils.formatShortDate(trip.startDate)} – ${BudgetUtils.formatShortDate(trip.endDate)}"
        binding.textMetaDays.text  = "${BudgetUtils.getDaysCount(trip.startDate, trip.endDate)} days"
        binding.textMetaSpent.text = BudgetUtils.formatCurrency(totalSpent, trip.currency)
    }

    // ── Itinerary ─────────────────────────────────────────────────────────────

    private fun renderDayChips(trip: Trip, days: List<DayPlan>) {
        binding.chipGroupDays.removeAllViews()
        days.forEachIndexed { idx, day ->
            val chip = Chip(this).apply {
                text = "Day ${idx + 1}\n${BudgetUtils.formatShortDate(day.date)}"
                isCheckable = true
                isChecked   = idx == selectedDayIndex
                shapeAppearanceModel = shapeAppearanceModel.withCornerSize(100f)
                if (idx == selectedDayIndex) {
                    setChipBackgroundColorResource(R.color.primary)
                    setTextColor(Color.WHITE)
                } else {
                    setChipBackgroundColorResource(R.color.muted)
                    setTextColor(getColor(R.color.text_primary))
                }
                setOnClickListener {
                    selectedDayIndex = idx
                    renderDayChips(trip, days)
                    placeAdapter.submitList(days[idx].places)
                }
            }
            binding.chipGroupDays.addView(chip)
        }
        if (days.isNotEmpty()) {
            placeAdapter.submitList(days[selectedDayIndex].places)
        }
    }

    // ── Budget ────────────────────────────────────────────────────────────────

    private val CAT_COLORS = listOf("#0EA5E9", "#F97316", "#A855F7", "#22C55E")

    private fun renderBudgetOverview(trip: Trip, summary: BudgetSummary) {
        val remaining = trip.budget - summary.total
        val isOver    = remaining < 0
        binding.textRemaining.text = "${if (isOver) "Over " else "Left "}${BudgetUtils.formatCurrency(Math.abs(remaining), trip.currency)}"
        val remainColor = if (isOver) Color.parseColor("#EF4444") else Color.parseColor("#22C55E")
        binding.textRemaining.setTextColor(remainColor)
        binding.textRemaining.background?.setTint(Color.argb(51,
            Color.red(remainColor), Color.green(remainColor), Color.blue(remainColor)))

        binding.textBudgetSpent.text = BudgetUtils.formatCurrency(summary.total, trip.currency)
        binding.textBudgetTotal.text = BudgetUtils.formatCurrency(trip.budget, trip.currency)

        val catAmounts = listOf(summary.transport, summary.food, summary.attraction, summary.accommodation)
        val bars       = listOf(binding.barTransport, binding.barFood, binding.barAttraction, binding.barAccommodation)
        val texts      = listOf(binding.textBudgetTransport, binding.textBudgetFood, binding.textBudgetAttraction, binding.textBudgetAccommodation)
        val rows       = listOf(binding.rowTransport, binding.rowFood, binding.rowAttraction, binding.rowAccommodation)

        catAmounts.forEachIndexed { i, amount ->
            if (amount == 0.0) {
                rows[i].visibility = View.GONE
            } else {
                rows[i].visibility = View.VISIBLE
                texts[i].text = BudgetUtils.formatCurrency(amount, trip.currency)
                val color = Color.parseColor(CAT_COLORS[i])
                bars[i].post {
                    val parentWidth = (bars[i].parent as? View)?.width ?: 0
                    if (parentWidth > 0) {
                        val fill = if (summary.total > 0) ((amount / summary.total) * parentWidth).toInt() else 0
                        val lp   = bars[i].layoutParams
                        lp.width = fill.coerceAtMost(parentWidth)
                        bars[i].layoutParams = lp
                    }
                    bars[i].background.setTint(color)
                }
            }
        }
    }

    // ── Bookings ──────────────────────────────────────────────────────────────

    private fun renderBookings(bookings: List<Booking>) {
        binding.textBookingCount.text = "${bookings.size} booking${if (bookings.size != 1) "s" else ""}"
        binding.linearBookings.removeAllViews()
        if (bookings.isEmpty()) {
            binding.linearBookings.addView(TextView(this).apply {
                text = "No bookings yet"
                setTextColor(getColor(R.color.text_secondary))
                textSize = 14f
            })
        } else {
            bookings.forEach { booking ->
                binding.linearBookings.addView(TextView(this).apply {
                    text = "✈ ${booking.title}\nConfirmation: ${booking.confirmationNo}\n${booking.details}\n${BudgetUtils.formatDate(booking.date)}"
                    setTextColor(getColor(R.color.text_primary))
                    textSize = 14f
                    setPadding(0, 16, 0, 16)
                })
                binding.linearBookings.addView(View(this).apply {
                    setBackgroundColor(getColor(R.color.border))
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
                })
            }
        }
    }

    // ── Record ────────────────────────────────────────────────────────────────

    private fun renderRecord(trip: Trip, days: List<DayPlan>) {
        val visitedCount = days.sumOf { d -> d.places.count { it.visited } }
        val totalPlaces  = days.sumOf { it.places.size }
        binding.textSummaryDestination.text = trip.destination
        binding.textSummaryDays.text        = "${BudgetUtils.getDaysCount(trip.startDate, trip.endDate)} days"
        binding.textSummaryPlaces.text      = "$visitedCount / $totalPlaces visited"

        binding.ratingBar.rating = trip.rating.toFloat()
        binding.ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) viewModel.updateRating(trip, rating.toInt())
        }

        binding.chipGroupMood.removeAllViews()
        listOf("😊 Happy","😎 Relaxed","🤩 Excited","😴 Tired","😌 Peaceful","🏔 Adventurous").forEach { mood ->
            val key = mood.substringAfter(" ").lowercase()
            val chip = Chip(this).apply {
                text = mood
                isCheckable = true
                isChecked   = trip.mood == key
                shapeAppearanceModel = shapeAppearanceModel.withCornerSize(100f)
                setOnClickListener { viewModel.updateMood(trip, key) }
            }
            binding.chipGroupMood.addView(chip)
        }
    }

    // ── Tab visibility ────────────────────────────────────────────────────────

    private fun showTab(index: Int) {
        binding.layoutItinerary.visibility = if (index == 0) View.VISIBLE else View.GONE
        binding.layoutBudget.visibility    = if (index == 1) View.VISIBLE else View.GONE
        binding.layoutRecord.visibility    = if (index == 2) View.VISIBLE else View.GONE
    }

    // ── Add expense dialog ────────────────────────────────────────────────────

    private fun showAddExpenseDialog(trip: Trip) {
        val categories = ExpenseCategory.values().map { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } }
        var selectedCat = ExpenseCategory.FOOD
        val view    = layoutInflater.inflate(R.layout.dialog_add_expense, null)
        val spinner = view.findViewById<Spinner>(R.id.spinnerCategory)
        val editAmt = view.findViewById<EditText>(R.id.editAmount)
        val editDesc= view.findViewById<EditText>(R.id.editDescription)

        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                selectedCat = ExpenseCategory.values()[pos]
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }

        AlertDialog.Builder(this)
            .setTitle("Add Expense")
            .setView(view)
            .setPositiveButton("Add") { _, _ ->
                val amount = editAmt.text.toString().toDoubleOrNull() ?: 0.0
                val desc   = editDesc.text.toString().trim()
                if (amount <= 0) {
                    Toast.makeText(this, "Enter valid amount", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val finalDesc = if (desc.isEmpty()) selectedCat.name.lowercase().replaceFirstChar { it.uppercase() } else desc
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                viewModel.addExpense(trip, selectedCat, amount, finalDesc, today)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
