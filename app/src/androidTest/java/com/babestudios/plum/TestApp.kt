package com.babestudios.plum

import com.babestudios.plum.di.AndroidTestAppComponent
import com.babestudios.plum.di.AndroidTestAppModule
import com.babestudios.plum.di.AppComponent
import com.babestudios.plum.di.DaggerAndroidTestAppComponent
import com.babestudios.plum.ui.AppNavigation

open class TestApp : App() {

	private lateinit var testAppComponent: AndroidTestAppComponent

	override fun provideAppComponent(): AppComponent {

		if (!this::testAppComponent.isInitialized) {
			testAppComponent = DaggerAndroidTestAppComponent
				.factory()
				.create(AndroidTestAppModule(this), AppNavigation(), this)
		}
		return testAppComponent
	}
}
