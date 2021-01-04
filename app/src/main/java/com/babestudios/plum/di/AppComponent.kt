package com.babestudios.plum.di

import android.content.Context
import com.babestudios.base.di.qualifier.ApplicationContext
import com.babestudios.base.rxjava.ErrorResolver
import com.babestudios.base.rxjava.SchedulerProvider
import com.babestudios.plum.data.PlumRepositoryContract
import com.babestudios.plum.navigation.di.NavigationComponent
import com.babestudios.plum.navigation.features.PlumNavigator
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class, AppBinderModule::class],
	dependencies = [NavigationComponent::class])
interface AppComponent {

	@Component.Factory
	interface Factory {
		fun create(
			appModule: AppModule,
			navigationComponent: NavigationComponent,
			@BindsInstance @ApplicationContext applicationContext: Context
		): AppComponent
	}

	fun plumRepository(): PlumRepositoryContract

	fun schedulerProvider(): SchedulerProvider

	fun errorResolver(): ErrorResolver

	fun navigator(): PlumNavigator

	@ApplicationContext
	fun context(): Context
}
