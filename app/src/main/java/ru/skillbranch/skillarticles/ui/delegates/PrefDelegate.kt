package ru.skillbranch.skillarticles.ui.delegates

import ru.skillbranch.skillarticles.data.local.PrefManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PrefDelegate<T>(private val defaultvalue: T ) {
    private var storedValue: T? = null

    operator fun provideDelegate(
        thisRef: PrefManager,
        prop : KProperty<*>
    ): ReadWriteProperty<PrefManager, T?> {
        val key: String=prop.name

        return object : ReadWriteProperty<PrefManager, T?> {
            override fun getValue(thisRef: PrefManager,property: KProperty<*>): T? {
                if (storedValue==null) {
                @Suppress("UNCHECKED_CAST")
                storedValue=when(defaultvalue) {
                    is Int-> thisRef.preferences.getInt(key,defaultvalue as Int) as T
                    is Long -> thisRef.preferences.getLong(key,defaultvalue as Long) as T
                    is Float -> thisRef.preferences.getFloat(key,defaultvalue as Float) as T
                    is String -> thisRef.preferences.getString(key,defaultvalue as String) as T
                    is Boolean -> thisRef.preferences.getBoolean(key,defaultvalue as Boolean) as T
                    else -> error("This type can not be stored into Preferences")
                }
                }
                return  storedValue
            }

            override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T?) {
                with(thisRef.preferences.edit()) {
                    when (value) {
                        is String-> putString(key,value)
                        is Boolean-> putBoolean(key,value)
                        is Int -> putInt(key,value)
                        is Long->putLong(key,value)
                        is Float -> putFloat(key,value)
                        else -> error("This type can not be get from Preferences")
                    }
                    apply()
                }
                storedValue=value
            }

        }

    }

}