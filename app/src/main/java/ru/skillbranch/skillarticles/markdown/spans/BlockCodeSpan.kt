package ru.skillbranch.skillarticles.markdown.spans

import android.R.attr.radius
import android.graphics.*
import android.text.style.ReplacementSpan
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import ru.skillbranch.skillarticles.markdown.Element


class BlockCodeSpan(
    @ColorInt
    private val textColor: Int,
    @ColorInt
    private val bgColor: Int,
    @Px
    private val cornerRadius: Float,
    @Px
    private val padding: Float,
    private val type: Element.BlockCode.Type
) : ReplacementSpan() {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var rect = RectF()
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var path = Path()

    //@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    //var measureWidth: Int = 0

    var measureRadius: Float=0f

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        //rect.set(x, top.toFloat(), x + measureWidth, bottom.toFloat())
        rect.set(x, top.toFloat(), canvas.width.toFloat(), bottom.toFloat())

        /*val oval = RectF(x , y.toFloat() , x + measureRadius*2,y.toFloat()+measureRadius*2)
        path.addArc(oval, 0f, 1f)
        canvas.drawArc(oval, 180f, 90f,true,paint)*/

        paint.forBackground {
            when (type) {
                Element.BlockCode.Type.SINGLE->{
                    //canvas.drawRoundRect(rect,cornerRadius,cornerRadius,paint)
                    canvas.drawRoundRect(rect,cornerRadius,cornerRadius,paint)
                }
                Element.BlockCode.Type.MIDDLE->{
                    canvas.drawRect(rect,paint)
                }
                Element.BlockCode.Type.START->{
                    //val measureHeight=canvas.width
                    //path.moveTo(x,y.toFloat())

                    //val oval = RectF(x - radius, y.toFloat() - radius, x + radius,y.toFloat()+measureRadius)
                    //path.addArc(oval, 0f, 1f)

                    canvas.drawRoundRect(rect,cornerRadius,cornerRadius,paint)
                    rect.set(x, bottom.toFloat()-cornerRadius, x+cornerRadius, bottom.toFloat())
                    canvas.drawRect(rect,paint)
                    rect.set(canvas.width.toFloat()-cornerRadius, bottom.toFloat()-cornerRadius, canvas.width.toFloat(), bottom.toFloat())
                    canvas.drawRect(rect,paint)
                }
                Element.BlockCode.Type.END->{
                    canvas.drawRoundRect(rect,cornerRadius,cornerRadius,paint)
                    rect.set(x, top.toFloat(), x+cornerRadius, top.toFloat()+cornerRadius)
                    canvas.drawRect(rect,paint)
                    rect.set(canvas.width.toFloat()-cornerRadius, top.toFloat(), canvas.width.toFloat(), top.toFloat()+cornerRadius)
                    canvas.drawRect(rect,paint)
                }
            //canvas.drawRect(rect,paint)
            }
            //canvas.drawRect(rect,paint)

        }

        paint.forText {
            canvas.drawText(text.toString(),start,end,x+padding,y.toFloat(),paint)
            //canvas.drawText(":"+measureRadius.toString(),start,end,x+padding,y.toFloat(),paint)
        }

    }



    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {

        paint.forText {
            val measureText=paint.measureText(text.toString(),start,end)
            //measureWidth=((measureText+2*padding)*6).toInt()
            if (fm!=null) measureRadius=((fm.bottom-fm.top.toInt())/2).toFloat()
                     else measureRadius=100f
            Log.e("Debug","BlockCodeSpan measureRadius="+measureRadius.toString())
        }
        return 0///measureWidth
    }

    private inline fun Paint.forText(block: () -> Unit) {
        val oldSize=textSize
        val oldStyle=typeface?.style?:0
        val oldFont=typeface
        val oldColor=color

        color=textColor
        typeface= Typeface.create(Typeface.MONOSPACE,oldStyle)
        textSize*=0.85f

        style=Paint.Style.STROKE
        typeface=Typeface.create(Typeface.MONOSPACE,oldStyle)
        strokeWidth=0f

        block()

        textSize=oldSize
        typeface=oldFont
        color=oldColor
    }

    private inline fun Paint.forBackground(block: () -> Unit) {
        val oldColor=color
        val oldStyle=style

        color=bgColor
        style=Paint.Style.FILL

        block()

        color=oldColor
        style=oldStyle
    }
}
