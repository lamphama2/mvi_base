package com.example.mvibase.scopes

import android.os.Bundle
import android.view.View
import com.example.mvibase.BaseViewModel
import com.example.mvibase.IAction
import com.example.mvibase.stores.BaseStore
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.disposables.CompositeDisposable

abstract class BaseBottomSheetDialogFragment<A : IAction, R, S : BaseStore<A, R>> : BottomSheetDialogFragment() {

    protected val disposable by lazy { CompositeDisposable() }

    protected abstract val store: S

    protected val mViewModel: BaseViewModel<A, R>? by lazy { provideViewModel() }

    protected open fun provideViewModel(): BaseViewModel<A, R>? = null

    private var firstSubscription = true

    open fun render(state: R) {}

    protected fun dispatch(action: A) {
        store.dispatch(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        store.subscribeObserver(this, ::render)
        firstSubscription = false
    }

    override fun onDestroyView() {
        store.unsubscribeObserver(viewLifecycleOwner)
        disposable.clear()
        super.onDestroyView()
    }
}