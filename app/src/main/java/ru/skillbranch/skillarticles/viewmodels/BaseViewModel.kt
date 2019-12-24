package ru.skillbranch.skillarticles.viewmodels

import androidx.annotation.UiThread
import androidx.lifecycle.*
import java.lang.IllegalArgumentException

abstract class BaseViewModel<T>(initState:T): ViewModel(){
    protected val state: MediatorLiveData<T> = MediatorLiveData<T>().apply{
       value=initState
    }

    protected val currentState
    get() = state.value!!


    @UiThread
    inline fun updateState(update:(currentState:T)->T){
        val updatedState:T=update(currentState)
        state.value=updatedState
    }

    fun observeState(owner: LifecycleOwner,onChanged: (newState: T)->Unit ){
        state.observe(owner, Observer{onChanged(it!!)})
    }

    protected fun <S> subscribeOnDataSource(
        source: LiveData<S>,
        onChanged: (newValue: S,currentState:T) -> T?
    ){
        state.addSource(source) {
            state.value=onChanged(it,currentState)?: return@addSource
        }
    }
}

class ViewModelFactory(private val params: String): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
            return ArticleViewModel(params) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}