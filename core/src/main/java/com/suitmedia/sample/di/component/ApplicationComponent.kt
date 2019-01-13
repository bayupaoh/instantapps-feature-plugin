package com.suitmedia.sample.di.component

import com.suitmedia.sample.di.module.APIServiceModule
import com.suitmedia.sample.di.scope.SuitCoreApplicationScope
import dagger.Component

@SuitCoreApplicationScope
@Component(modules = [(APIServiceModule::class)])
interface ApplicationComponent {

}