package br.com.easyreminder.model

import br.com.easyreminder.R

enum class ReminderCategory(
    val displayName: String,
    val colorHex: String,
    val iconRes: Int
) {
    TRABALHO("Trabalho", "#1F4D3A", R.drawable.ic_category_work),
    CASA("Casa", "#3A6B52", R.drawable.ic_category_home),
    ESTUDOS("Estudos", "#5D8A6B", R.drawable.ic_category_school),
    SAUDE("Saúde", "#7A9E85", R.drawable.ic_category_health),
    FINANCAS("Finanças", "#2E5940", R.drawable.ic_category_finance),
    OUTROS("Outros", "#8A938D", R.drawable.ic_category_other)
}