package ru.skillbranch.skillarticles.ui.custom

import android.content.Context
import android.graphics.Path
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.shape.MaterialShapeDrawable
import ru.skillbranch.skillarticles.R
import java.time.format.DecimalStyle

class ArticleSubmenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int=0
    ) :ConstraintLayout(context, attrs,defStyleAttr){
    var isOpen=false
    init {
        View.inflate(context, R.layout.layout_submenu,this)
        val materialBg=MaterialShapeDrawable.createWithElevationOverlay(context)
        materialBg.elevation=elevation
        background=materialBg
    }

    fun open(){
        if (isOpen) return
        isOpen=true
        visibility=View.VISIBLE
    }

    fun close(){
        if (!isOpen) return
        isOpen=false
        visibility= View.GONE
    }

    override fun onSaveInstanceState(): Parcelable? {
        return super.onSaveInstanceState()
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
    }
}