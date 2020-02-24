package ru.skillbranch.skillarticles.ui.base

import android.os.Bundle
import android.view.View
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState

abstract class Binding {
    abstract fun onFininishInfale()
    abstract fun bind(data:IViewModelState)
    fun saveUI(outState: Bundle){}
    fun restoreUI(savedState: Bundle){}
}