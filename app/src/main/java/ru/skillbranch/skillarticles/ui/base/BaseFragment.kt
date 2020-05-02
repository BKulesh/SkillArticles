package ru.skillbranch.skillarticles.ui.base

import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.skillbranch.skillarticles.ui.RootActivity
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState

abstract class BaseFragment<T: BaseViewModel<out IViewModelState>>: Fragment() {
    val root: RootActivity
      get()=activity as RootActivity
    open val binding: Binding?=null
    protected abstract val viewModel: T
    protected abstract val layout: Int

    abstract fun setupViews()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saveInstanceState: Bundle?
        ): View? =inflater.inflate(layout,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.restoreState()
        binding?.restoreUI(savedInstanceState)

        viewModel.observeState(viewLifecycleOwner){
            binding?.bind(it)
        }
        if  (binding?.isInFlated==false) binding?.onFinishInFlate()

        viewModel.observeNotifications(viewLifecycleOwner) {
            root.renderNotification(it)
        }

        setupViews()

    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        binding?.rebind()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.saveState()
        binding?.saveUI(outState)

        super.onSaveInstanceState(outState)
    }


}