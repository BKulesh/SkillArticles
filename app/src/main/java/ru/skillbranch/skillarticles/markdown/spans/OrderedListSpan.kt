package ru.skillbranch.skillarticles.markdown.spans

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting


class OrderedListSpan(
    @Px
    private val gapWidth: Float,
    private val order: String,
    @ColorInt
    private val orderColor: Int
) : LeadingMarginSpan {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)

    override fun getLeadingMargin(first: Boolean): Int {
        //TODO implement me()

        return 30+gapWidth.toInt()
    }

    override fun drawLeadingMargin(
        canvas: Canvas, paint: Paint, currentMarginLocation: Int, paragraphDirection: Int,
        lineTop: Int, lineBaseline: Int, lineBottom: Int, text: CharSequence?, lineStart: Int,
        lineEnd: Int, isFirstLine: Boolean, layout: Layout?
    ) {

        //val orderText=order+".lt="+lineTop.toString()+".lbl="+lineBaseline.toString()+
        //".lb="+lineBottom.toString()+".ls="+lineStart.toString()+".le="+lineEnd.toString()
        //val orderText=order+"."
        if (isFirstLine) {
            paint.withCustomColor {
                canvas.drawText(
                    order.toString(),
                    0,
                    order.toString().length,
                    gapWidth,
                    lineBaseline.toFloat(),
                    paint
                )
            }
            //canvas.drawText(text.toString(),1,text.toString().length,0f,lineTop.toFloat(),paint)
        }
    }

    private inline fun Paint.withCustomColor(block: () -> Unit) {
        val oldColor=color
        val oldStyle=style
        //val oldWidth= strokeWidth

        //strokeWidth=quoteWidth
        color=orderColor
        style=Paint.Style.STROKE

        block()

        //strokeWidth=oldWidth
        color=oldColor
        style=oldStyle
    }


}