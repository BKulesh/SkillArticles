package ru.skillbranch.skillarticles.ui.custom.markdown

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.graphics.withTranslation
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.attrValue

class MarkdownTextView @JvmOverloads constructor(
    context: Context,
    fontSize: Float
): TextView(context,null,0),IMarkdownView {

    override var fontSize: Float=fontSize
      //get() = fontSize
      set(value) {
          textSize=value
          field=value
      }

    override val spannableContent: Spannable
        get() = text as Spannable

    val color=context.attrValue(R.attr.colorOnBackground)

    private val searchBgHelper=SearchBgHelper(context) {

    }

    init {
        //setBackgroundColor(Color.GREEN)
        setTextColor(color)
        textSize=fontSize
        movementMethod=LinkMovementMethod.getInstance()
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