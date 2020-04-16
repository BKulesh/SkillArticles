package ru.skillbranch.skillarticles.ui.custom.spans

import android.text.TextPaint
import android.text.style.BackgroundColorSpan
import androidx.core.graphics.ColorUtils

open class SearchSpan(bgColor: Int, private val fgColor: Int): BackgroundColorSpan(bgColor){
    private val alphaColor: Int by lazy {
        ColorUtils.setAlphaComponent(backgroundColor,160)
    }
    override fun updateDrawState(textPaint: TextPaint) {
        textPaint.color=fgColor
        textPaint.bgColor=alphaColor

    }

}