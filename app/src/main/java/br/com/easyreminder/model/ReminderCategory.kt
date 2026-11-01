package br.com.easyreminder.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import br.com.easyreminder.R

enum class ReminderCategory(
    @StringRes val displayNameRes: Int,
    @ColorRes val colorRes: Int,
    @DrawableRes val iconRes: Int
) {
    TRABALHO(R.string.category_work, R.color.category_work, R.drawable.ic_category_work),
    CASA(R.string.category_home, R.color.category_home, R.drawable.ic_category_home),
    ESTUDOS(R.string.category_school, R.color.category_school, R.drawable.ic_category_school),
    SAUDE(R.string.category_health, R.color.category_health, R.drawable.ic_category_health),
    FINANCAS(R.string.category_finance, R.color.category_finance, R.drawable.ic_category_finance),
    OUTROS(R.string.category_other, R.color.category_other, R.drawable.ic_category_other)
}