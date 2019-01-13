package com.suitmedia.sample.core.login.di

import com.suitmedia.sample.core.login.LoginPresenter
import com.suitmedia.sample.di.module.APIServiceModule
import com.suitmedia.sample.di.scope.SuitCoreApplicationScope
import dagger.Component

@SuitCoreApplicationScope
@Component(modules = [(APIServiceModule::class)])
interface LoginComponent {
    fun inject(loginPresenter: LoginPresenter)
}