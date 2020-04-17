package ru.skillbranch.skillarticles.ui.custom.markdown

import android.content.Context
import android.graphics.Canvas
import android.text.Spanned
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.graphics.withTranslation

class MarkDownTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?=null,
    defStyleAttr: Int=0): TextView(context,attrs,defStyleAttr) {
    private val searchBgHelper=SearchBgHelper(context) {

    }

     override fun onDraw(canvas: Canvas){
         super.onDraw(canvas)
         if (layout!=null && text is Spanned) {
             canvas.withTranslation (totalPaddingLeft.toFloat(),totalPaddingTop.toFloat()) {
                 searchBgHelper.draw(canvas=canvas,text=text as Spanned,layout = layout)
             }
         }
         super.onDraw(canvas)
     }
}