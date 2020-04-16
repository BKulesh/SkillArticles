package ru.skillbranch.skillarticles.ui.custom.spans

import android.graphics.*
import android.text.style.ReplacementSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import ru.skillbranch.skillarticles.data.repositories.Element


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


        paint.forBackground {
            when (type) {
                Element.BlockCode.Type.SINGLE->{
                    rect.set(x, top.toFloat()+padding, canvas.width.toFloat(), bottom.toFloat()-padding)
                    canvas.drawRoundRect(rect,cornerRadius,cornerRadius,paint)
                }
                Element.BlockCode.Type.MIDDLE->{
                    rect.set(x, top.toFloat(), canvas.width.toFloat(), bottom.toFloat())
                    canvas.drawRect(rect,paint)
                }
                Element.BlockCode.Type.START->{

                    /*canvas.drawRoundRect(rect,cornerRadius,cornerRadius,paint)
                    rect.set(x, bottom.toFloat()-cornerRadius, x+cornerRadius, bottom.toFloat())
                    canvas.drawRect(rect,paint)
                    rect.set(canvas.width.toFloat()-cornerRadius, bottom.toFloat()-cornerRadius, canvas.width.toFloat(), bottom.toFloat())
                    canvas.drawRect(rect,paint)*/
                    val corners=floatArrayOf(
                        cornerRadius,cornerRadius,
                        cornerRadius,cornerRadius,
                        0f,0f,
                        0f,0f
                    )
                    rect.set(0f,top+padding,canvas.width.toFloat(),bottom.toFloat())
                    path.reset()
                    path.addRoundRect(rect,corners,Path.Direction.CW)
                    canvas.drawPath(path,paint)
                }
                Element.BlockCode.Type.END->{
                    /*canvas.drawRoundRect(rect,cornerRadius,cornerRadius,paint)
                    rect.set(x, top.toFloat(), x+cornerRadius, top.toFloat()+cornerRadius)
                    canvas.drawRect(rect,paint)
                    rect.set(canvas.width.toFloat()-cornerRadius, top.toFloat(), canvas.width.toFloat(), top.toFloat()+cornerRadius)
                    canvas.drawRect(rect,paint)*/
                    val corners=floatArrayOf(
                        0f,0f,
                        0f,0f,
                        cornerRadius,cornerRadius,
                        cornerRadius,cornerRadius
                    )
                    rect.set(0f,top.toFloat(),canvas.width.toFloat(),bottom.toFloat()-padding)
                    path.reset()
                    path.addRoundRect(rect,corners,Path.Direction.CW)
                    canvas.drawPath(path,paint)

                }
            }

        }

        paint.forText {
            when (type) {
                Element.BlockCode.Type.MIDDLE-> {
                    canvas.drawText(text.toString(),start,end,x+padding,y.toFloat(),paint)
                }
                Element.BlockCode.Type.SINGLE-> {
                    canvas.drawText(text.toString(),start,end,x+padding,y.toFloat(),paint)
                }
                Element.BlockCode.Type.START-> {
                    canvas.drawText(text.toString(),start,end,x+padding,y.toFloat(),paint)
                }
                Element.BlockCode.Type.END-> {
                    canvas.drawText(text.toString(),start,end,x+padding,y.toFloat(),paint)
                }
            }

        }

    }



    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        if (fm!=null) {
            when (type) {
                Element.BlockCode.Type.START -> {
                    fm.ascent = (paint.ascent() - 2 * padding).toInt()
                    fm.descent = paint.descent().toInt()
                }
                Element.BlockCode.Type.END -> {
                    fm.descent = (paint.descent() + 2 * padding).toInt()
                    fm.ascent = paint.ascent().toInt()
                }
                Element.BlockCode.Type.MIDDLE -> {
                    fm.descent = paint.descent().toInt()
                    fm.ascent = paint.ascent().toInt()
                }
                Element.BlockCode.Type.SINGLE -> {
                    fm.ascent = (paint.ascent() - 2 * padding).toInt()
                    fm.descent = (paint.descent() + 2 * padding).toInt()
                }
            }
            fm.top=fm.ascent
            fm.bottom=fm.descent
        }
/*        paint.forText {
            val measureText=paint.measureText(text.toString(),start,end)
            //measureWidth=((measureText+2*padding)*6).toInt()
            if (fm!=null) measureRadius=((fm.bottom-fm.top.toInt())/2).toFloat()
                     else measureRadius=0f
            Log.e("Debug","BlockCodeSpan measureRadius="+measureRadius.toString())
        }*/
        return 0///measureWidth
    }

    private inline fun Paint.forText(block: () -> Unit) {
        val oldSize=textSize
        val oldStyle=typeface?.style?:0
        val oldTypeFace=typeface
        val oldColor=color

        color=textColor
        typeface= Typeface.create(Typeface.MONOSPACE,oldStyle)
        textSize*=0.85f

        //style=Paint.Style.STROKE
        //typeface=Typeface.create(Typeface.MONOSPACE,oldStyle)
        //strokeWidth=0f

        block()

        textSize=oldSize
        typeface=oldTypeFace
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