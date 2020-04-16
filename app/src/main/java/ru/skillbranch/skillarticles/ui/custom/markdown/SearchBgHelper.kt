package ru.skillbranch.skillarticles.ui.custom.markdown

import android.content.Context
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.extensions.dpToIntPx

class SearchBgHelper (
    context: Context,
    private val focusListener: (Int) -> Unit
) {
  private val padding: Int=context.dpToIntPx(4)
  private val secondaryColor: Int=context.attrValue(ru.skillbranch.skillarticles.R.attr.colorSecondary)
}