package com.babestudios.plum.di

import android.content.Context
import com.babestudios.base.di.qualifier.ApplicationContext
import com.babestudios.plum.navigation.di.NavigationComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidTestAppModule::class],
	dependencies = [NavigationComponent::class])
interface AndroidTestAppComponent : AppComponent {

	@Component.Factory
	interface Factory {
		fun create(
				appModule: AndroidTestAppModule,
				navigationComponent: NavigationComponent,
				@BindsInstance @ApplicationContext applicationContext: Context
		): AndroidTestAppComponent
	}

}
