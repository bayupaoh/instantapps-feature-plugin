package com.suitmedia.sample.core.login

import android.content.Context
import com.suitmedia.sample.BaseApplication
import com.suitmedia.sample.base.presenter.BasePresenter
import com.suitmedia.sample.core.login.di.DaggerLoginComponent
import com.suitmedia.sample.di.module.ContextModule

/**
 * Created by dodydmw19 on 7/18/18.
 */

class LoginPresenter(var context: Context) : BasePresenter<LoginView>{

    private var mvpView: LoginView? = null

    init {
        DaggerLoginComponent.builder().contextModule(ContextModule(context)).build().inject(this)
    }

    fun login(){
        mvpView?.onLoginSuccess("success")
    }

    override fun onStart() {
    }

    override fun onPause() {
    }

    override fun onDestroy() {
    }

    override fun attachView(view: LoginView) {
        mvpView = view
    }

    override fun detachView() {
        mvpView = null
    }
}