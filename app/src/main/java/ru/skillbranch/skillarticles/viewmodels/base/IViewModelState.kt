package ru.skillbranch.skillarticles.viewmodels.base

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle

interface IViewModelState {
    fun save(outState: SavedStateHandle)
    fun restore (savedState: SavedStateHandle):IViewModelState{
        return this
    }
}