package ru.skillbranch.skillarticles.ui.custom.markdown

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.Spannable
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import androidx.core.animation.doOnEnd
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.setPaddingOptionally
import java.nio.charset.Charset
import java.security.MessageDigest
import kotlin.math.hypot


@SuppressLint("ViewConstructor")
//class MarkdownImageView private constructor(
class MarkdownImageView private constructor(
    context: Context,
    fontSize: Float
) : ViewGroup(context, null, 0), IMarkdownView {

    override var fontSize: Float = fontSize
    set(value){
        tv_alt?.textSize=fontSize*0.75f
        tv_title.textSize=fontSize
        field=value
    }

    override val spannableContent: Spannable
    get()=tv_title.text as Spannable

    //views
    private lateinit var imageUrl: String
    private lateinit var imageTitle: CharSequence

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val iv_image: ImageView
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val tv_title: MarkdownTextView
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var tv_alt: TextView? = null

    @Px
    private val titleTopMargin: Int=context.dpToIntPx(8) //8dp
    @Px
    private val titlePadding: Int =context.dpToIntPx(56) //56dp
    @Px
    private val cornerRadius: Float=context.dpToIntPx(4).toFloat()  //4dp
    @ColorInt
    private val colorSurface: Int=context.attrValue(ru.skillbranch.skillarticles.R.attr.colorSurface) //colorSurface
    @ColorInt
    private val colorOnSurface: Int=context.attrValue(ru.skillbranch.skillarticles.R.attr.colorOnSurface) //colorOnSurface
    @ColorInt
    private val colorOnBackground: Int=context.attrValue(ru.skillbranch.skillarticles.R.attr.colorOnBackground) //colorOnBackground
    @ColorInt
    private var lineColor: Int=context.getColor(R.color.color_divider) //R.color.color_divider


    //for draw object allocation
    private var lineHeight: Float = 0f
    private var linePositionY: Float = 0f
    private val linePaint=Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color=lineColor
        strokeWidth=0f
    }

    init {

        //setBackgroundColor(Color.RED)

        layoutParams= LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
        iv_image=ImageView(context).apply {
            //layoutParams= LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
            scaleType=ImageView.ScaleType.CENTER_CROP
            setImageResource(R.drawable.ic_launcher_background)
            outlineProvider= object:ViewOutlineProvider(){
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(
                        Rect(0,0,view.measuredWidth,view.measuredHeight),
                        cornerRadius
                    )
                }
            }
            clipToOutline=true
        }
        addView(iv_image)
        tv_title=MarkdownTextView(context,fontSize*0.75f).apply {
            //layoutParams=LayoutParams(LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT))
            setText("title",TextView.BufferType.SPANNABLE)
            setTextColor(colorOnBackground)
            gravity=Gravity.CENTER
            typeface=Typeface.create(Typeface.MONOSPACE,Typeface.NORMAL)
            setPaddingOptionally(left=titlePadding,right=titlePadding)
        }
        addView(tv_title)

    }


    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    override fun onMeasure(widthMeasureSpec: Int,heightMeasureSpec: Int){
        var userHeight=0
        val width=View.getDefaultSize(suggestedMinimumWidth,widthMeasureSpec)

        val ms=MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY)

        iv_image.measure(ms,heightMeasureSpec)
        tv_title.measure(ms,heightMeasureSpec)
        //measureChild(tv_title,ms,heightMeasureSpec)
        tv_alt?.measure(ms,heightMeasureSpec)

        userHeight+=iv_image.measuredHeight
        userHeight+=titleTopMargin
        linePositionY=userHeight+tv_title.measuredHeight/2f
        userHeight+=tv_title.measuredHeight

        setMeasuredDimension(width,userHeight)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    override fun onLayout(changed: Boolean,l:Int,t: Int,r: Int,b: Int){
        var userHeight=0
        val bodyWith=r-l-paddingLeft-paddingRight
        val left=paddingLeft
        val right=paddingLeft+bodyWith

        iv_image.layout(
            left,
            userHeight,
            right,
            userHeight+iv_image.measuredHeight
        )
        userHeight+=iv_image.measuredHeight+titleTopMargin

        tv_title.layout(
            left,
            userHeight,
            right,
            userHeight+tv_title.measuredHeight
        )

        tv_alt?.layout(
            left,
            iv_image.measuredHeight-(tv_alt?.measuredHeight?:0),
            right,
            iv_image.measuredHeight
        )

    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        canvas.drawLine(0f,linePositionY,titlePadding.toFloat(),linePositionY,linePaint)
        canvas.drawLine(canvas.width-titlePadding.toFloat(),linePositionY,canvas.width.toFloat(),linePositionY,linePaint)
    }



    constructor(
        context: Context,
        fontSize: Float,
        url: String,
        title: CharSequence,
        alt: String?
    ) : this(context, fontSize) {
        imageUrl=url
        imageTitle=title
        tv_title.setText(title,TextView.BufferType.SPANNABLE)

        Glide.with(context).load(url).transform(AspectRatioResizeTransform()).into(iv_image)

        if (alt!=null) {
            tv_alt=TextView(context).apply {
                text=alt
                setTextColor(colorOnSurface)
                setBackgroundColor(ColorUtils.setAlphaComponent(colorSurface,160))
                gravity=Gravity.CENTER
                textSize=fontSize
                setPadding(titleTopMargin)
                isVisible=false

            }
        }
        addView(tv_alt)

        iv_image.setOnClickListener{
            if (tv_alt?.isVisible==true) animateHideAlt()
                                else animateShowAlt()
        }
    }


    private fun animateShowAlt() {
        tv_alt?.isVisible = true
        val endRadius = hypot(tv_alt?.width?.toFloat() ?: 0f, tv_alt?.height?.toFloat() ?: 0f)
        val va = ViewAnimationUtils.createCircularReveal(
            tv_alt,
            tv_alt?.width ?: 0,
            tv_alt?.height ?: 0,
            0f,
            endRadius
        )
        va.start()
    }

    private fun animateHideAlt() {
        val endRadius = hypot(tv_alt?.width?.toFloat() ?: 0f, tv_alt?.height?.toFloat() ?: 0f)
        val va = ViewAnimationUtils.createCircularReveal(
            tv_alt,
            tv_alt?.width ?: 0,
            tv_alt?.height ?: 0,
            endRadius,
            0f
        )
        va.doOnEnd { tv_alt?.isVisible = false }
        va.start()
    }
}


class AspectRatioResizeTransform : BitmapTransformation() {
    private val ID =
        "ru.skillbranch.skillarticles.glide.AspectRatioResizeTransform" //any unique string
    private val ID_BYTES = ID.toByteArray(Charset.forName("UTF-8"))
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val originWidth=toTransform.width
        val originHeight=toTransform.height
        val aspectRatio=originWidth.toFloat()/originHeight
        return Bitmap.createScaledBitmap(toTransform,outWidth,(outWidth/aspectRatio).toInt(),true)
    }

    override fun equals(other: Any?): Boolean =other is AspectRatioResizeTransform

    override fun hashCode(): Int =ID.hashCode()


}
/*
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //TODO implement me
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        //TODO implement me
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        //TODO implement me
    }
*/


