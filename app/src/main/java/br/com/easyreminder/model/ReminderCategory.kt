package br.com.easyreminder.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import br.com.easyreminder.R

enum class ReminderCategory(
    val displayName: String,
    @ColorRes val colorRes: Int,
    @DrawableRes val iconRes: Int
) {
    TRABALHO("Trabalho", R.color.category_work, R.drawable.ic_category_work),
    CASA("Casa", R.color.category_home, R.drawable.ic_category_home),
    ESTUDOS("Estudos", R.color.category_school, R.drawable.ic_category_school),
    SAUDE("Saúde", R.color.category_health, R.drawable.ic_category_health),
    FINANCAS("Finanças", R.color.category_finance, R.drawable.ic_category_finance),
    OUTROS("Outros", R.color.category_other, R.drawable.ic_category_other)
}