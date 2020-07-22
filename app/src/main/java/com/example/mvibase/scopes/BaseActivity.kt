package com.example.mvibase.scopes

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.example.mvibase.IAction
import com.example.mvibase.stores.BaseStore
import io.reactivex.disposables.CompositeDisposable

abstract class BaseActivity<A : IAction, R, S : BaseStore<A, R>> : AppCompatActivity() {

    @LayoutRes
    abstract fun resId(): Int

    protected val disposable by lazy {
        CompositeDisposable()
    }

    protected abstract val store: S

    @CallSuper
    open fun render(state: R) {}

    protected fun dispatch(action: A) {
        store.dispatch(action)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(resId())
        store.subscribeObserver(this, ::render)
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }
}