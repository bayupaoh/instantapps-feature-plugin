package com.suitmedia.sample.core.member.di

import com.suitmedia.sample.core.member.MemberPresenter
import com.suitmedia.sample.di.module.APIServiceModule
import com.suitmedia.sample.di.scope.SuitCoreApplicationScope
import dagger.Component

@SuitCoreApplicationScope
@Component(modules = [(APIServiceModule::class)])
interface MemberComponent {
    fun inject(memberPresenter: MemberPresenter)
}