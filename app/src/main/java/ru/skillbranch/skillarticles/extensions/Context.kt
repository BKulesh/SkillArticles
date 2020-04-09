package ru.skillbranch.skillarticles.extensions

import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.TypedValue
import androidx.annotation.AttrRes
import ru.skillbranch.skillarticles.ui.delegates.AttrValue
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun Context.dpToPx(dp: Int): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        this.resources.displayMetrics

    )
}

fun Context.dpToIntPx(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        this.resources.displayMetrics
    ).toInt()
}

/*class AttrValue(@AttrRes private val res: Int):ReadOnlyProperty<Context,Int>{
    private var value:Int?=null
    override fun getValue(thisRef: Context, property: KProperty<*>): Int {
        if (value==null) {
            val tv=TypedValue()
            if (thisRef.theme.resolveAttribute(res,tv,true)) value=tv.data
            else throw Resources.NotFoundException("Resource with id $res not found.")
        }
        return value!!
    }

}
*/
//val Context.AttrValue: AttrValue

//fun Context.AttrValue(ncolor: Int): ReadOnlyProperty<String,Int> {
//     var value:Int?=null
//        return value!!
//    }
//}


fun Context.attrValue (res: Int): Int
{
    var value:Int=0
    val tv=TypedValue()
    if (theme.resolveAttribute(res,tv,true)) value=tv.data
    else throw Resources.NotFoundException("Resource with id $res not found.")
    return value
}

val Context.isNetworkAvailable: Boolean
    get() {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm.activeNetwork?.run {
                val nc = cm.getNetworkCapabilities(this)
                nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI
                )
            } ?: false
        } else {
            cm.activeNetworkInfo?.run { isConnectedOrConnecting } ?: false
        }
    }

