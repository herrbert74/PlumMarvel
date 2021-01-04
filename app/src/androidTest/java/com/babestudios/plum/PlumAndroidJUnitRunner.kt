package com.babestudios.plum

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

/**
 * Use our TestApplication to inject dependencies
 */

class PlumAndroidJUnitRunner : AndroidJUnitRunner() {

	@Throws(
		InstantiationException::class,
		IllegalAccessException::class,
		ClassNotFoundException::class
	)
	override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
		val testApplicationClassName = TestApp::class.java.canonicalName
		return super.newApplication(cl, testApplicationClassName, context)
	}
}