package com.travelplanner.utils

import com.travelplanner.data.BudgetSummary
import com.travelplanner.data.DayPlan
import com.travelplanner.data.Expense
import com.travelplanner.data.ExpenseCategory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object BudgetUtils {

    fun calculateSummary(expenses: List<Expense>, days: List<DayPlan> = emptyList()): BudgetSummary {
        var total = 0.0; var transport = 0.0; var food = 0.0
        var attraction = 0.0; var accommodation = 0.0; var other = 0.0
        for (e in expenses) {
            total += e.amount
            when (e.category) {
                ExpenseCategory.TRANSPORT -> transport += e.amount
                ExpenseCategory.FOOD -> food += e.amount
                ExpenseCategory.ATTRACTION -> attraction += e.amount
                ExpenseCategory.ACCOMMODATION -> accommodation += e.amount
                ExpenseCategory.OTHER -> other += e.amount
            }
        }
        for (day in days) {
            for (p in day.places) {
                if (p.visited) {
                    total += p.cost
                    when (p.category.uppercase(Locale.getDefault())) {
                        "TRANSPORT" -> transport += p.cost
                        "FOOD", "CAFE", "DRINK" -> food += p.cost
                        "ATTRACTION", "CULTURAL", "NATURE", "EXPERIENCE", "NIGHTLIFE" -> attraction += p.cost
                        "ACCOMMODATION", "HOTEL" -> accommodation += p.cost
                        else -> other += p.cost
                    }
                }
            }
        }
        return BudgetSummary(total, transport, food, attraction, accommodation, other)
    }

    fun budgetPercentage(spent: Double, budget: Double): Int {
        if (budget <= 0) return 0
        return minOf(((spent / budget) * 100).toInt(), 100)
    }

    fun formatCurrency(amount: Double, currency: String = "THB"): String {
        return when (currency) {
            "THB" -> "฿${"%,.0f".format(amount)}"
            "USD" -> "${"$"}${"%,.2f".format(amount)}"
            "EUR" -> "€${"%,.2f".format(amount)}"
            "JPY" -> "¥${"%,.0f".format(amount)}"
            else -> "$currency ${"%,.0f".format(amount)}"
        }
    }

    fun getDaysCount(startDate: String, endDate: String): Int {
        return try {
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val s = fmt.parse(startDate) ?: return 1
            val e = fmt.parse(endDate) ?: return 1
            val diff = TimeUnit.MILLISECONDS.toDays(e.time - s.time).toInt()
            maxOf(1, diff + 1)
        } catch (ex: Exception) { 1 }
    }

    fun formatDate(dateStr: String): String {
        return try {
            val src = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dst = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            dst.format(src.parse(dateStr) ?: Date())
        } catch (e: Exception) { dateStr }
    }

    fun formatShortDate(dateStr: String): String {
        return try {
            val src = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dst = SimpleDateFormat("dd MMM", Locale.ENGLISH)
            dst.format(src.parse(dateStr) ?: Date())
        } catch (e: Exception) { dateStr }
    }
}
