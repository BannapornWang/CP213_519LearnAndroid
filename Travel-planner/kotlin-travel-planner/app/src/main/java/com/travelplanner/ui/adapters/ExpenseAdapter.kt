package com.travelplanner.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.travelplanner.data.Expense
import com.travelplanner.data.ExpenseCategory
import com.travelplanner.databinding.ItemExpenseBinding
import com.travelplanner.utils.BudgetUtils

private data class CatStyle(val emoji: String, val color: String)

class ExpenseAdapter(
    private val onDelete: (Expense) -> Unit
) : ListAdapter<Expense, ExpenseAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemExpenseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val expense = getItem(position)
        val style   = catStyle(expense.category)
        val color   = Color.parseColor(style.color)
        val bgAlpha = Color.argb(51, Color.red(color), Color.green(color), Color.blue(color))

        with(holder.binding) {
            textCategoryIcon.text = style.emoji
            textCategoryIcon.background.setTint(bgAlpha)

            textDescription.text = expense.description
            textDate.text        = "${BudgetUtils.formatShortDate(expense.date)} · ${expense.category.name.lowercase()}"
            textAmount.text      = BudgetUtils.formatCurrency(expense.amount)
            textAmount.setTextColor(color)

            btnDelete.setOnClickListener { onDelete(expense) }
        }
    }

    private fun catStyle(cat: ExpenseCategory): CatStyle = when (cat) {
        ExpenseCategory.TRANSPORT     -> CatStyle("✈", "#0EA5E9")
        ExpenseCategory.FOOD         -> CatStyle("🍽", "#F97316")
        ExpenseCategory.ATTRACTION   -> CatStyle("📷", "#A855F7")
        ExpenseCategory.ACCOMMODATION -> CatStyle("🛏", "#22C55E")
        ExpenseCategory.OTHER        -> CatStyle("•••", "#6B7280")
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Expense>() {
            override fun areItemsTheSame(a: Expense, b: Expense) = a.id == b.id
            override fun areContentsTheSame(a: Expense, b: Expense) = a == b
        }
    }
}
