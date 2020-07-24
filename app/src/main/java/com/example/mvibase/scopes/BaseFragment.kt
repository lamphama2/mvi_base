package com.example.mvibase.scopes

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.mvibase.IAction
import com.example.mvibase.BaseViewModel
import com.example.mvibase.stores.BaseStore
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment<A : IAction, R, S : BaseStore<A, R>> : Fragment() {

    protected val disposable by lazy { CompositeDisposable() }

    protected abstract val store: S

    protected open val mViewModel: BaseViewModel<A, R>?  = null

    private fun _render(state : R) {
        render(state)
    }

    open fun render(state: R){}

    protected fun dispatch(action: A) {
        store.dispatch(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        store.subscribeObserver(viewLifecycleOwner, ::_render)
    }

    override fun onDestroyView() {
        store.unsubscribeObserver(viewLifecycleOwner)
        disposable.clear()
        super.onDestroyView()
    }
}