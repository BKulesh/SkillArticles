package ru.skillbranch.skillarticles.viewmodels.base

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import ru.skillbranch.skillarticles.viewmodels.ArticleViewModel
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewModelDelegate<T : ViewModel>(private val clazz: Class<T>, private val arg: Any?) : ReadOnlyProperty<FragmentActivity, T>
{
    override fun getValue(thisRef: FragmentActivity, property: KProperty<*>): T {
        return ViewModelFactory(arg) as T
    }
}


//*ViewModelProvide
//Необходимо реализовать делегат для свойства viewModel возвращающий ViewModel с указанными аргументами (ru.skillbranch.skillarticles.viewmodels.base.ViewModelDelegate)
//+2
//Реализуй делегат ViewModelDelegate<T : ViewModel>(private val clazz: Class<T>, private val arg: Any?) :  ReadOnlyProperty<FragmentActivity, T>
// реализующий получение экземляра BaseViewModel соответствующего типа <T> с аргументами переданными вторым аргументом конструктора.
//Пример:
//val viewModel : TestViewModel by provideViewModel("test args")

//Реализуй в классе BaseActivity инлайн функцию
//internal inline fun provideViewModel(arg : Any?) : ViewModelDelegate - возвращающую экземпляр делегата ViewModelDelegate