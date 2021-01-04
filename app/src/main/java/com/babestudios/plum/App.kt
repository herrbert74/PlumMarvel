package com.babestudios.plum

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import com.babestudios.base.mvrx.LifeCycleApp
import com.babestudios.plum.di.AppComponent
import com.babestudios.plum.di.AppComponentProvider
import com.babestudios.plum.di.AppModule
import com.babestudios.plum.di.DaggerAppComponent
import com.babestudios.plum.ui.AppNavigation
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.soloader.SoLoader

open class App : Application(), AppComponentProvider, LifeCycleApp {

	private var currentActivity: AppCompatActivity? = null

	private lateinit var appComponent: AppComponent

	override fun onCreate() {
		super.onCreate()
		logAppOpen()
		super.onCreate()
		SoLoader.init(this, false)

		if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
			val client = AndroidFlipperClient.getInstance(this)
			client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
			client.addPlugin(DatabasesFlipperPlugin(this))
			client.start()
		}
	}

	private fun logAppOpen() {
		provideAppComponent().plumRepository().logAppOpen()
	}

	override fun provideAppComponent(): AppComponent {

		if (!this::appComponent.isInitialized) {
			appComponent = DaggerAppComponent
					.factory()
					.create(AppModule(this), AppNavigation(), this)
		}
		return appComponent
	}

	override fun getCurrentActivity(): AppCompatActivity? {
		return currentActivity
	}

	override fun setCurrentActivity(mCurrentActivity: AppCompatActivity) {
		this.currentActivity = mCurrentActivity
	}
}