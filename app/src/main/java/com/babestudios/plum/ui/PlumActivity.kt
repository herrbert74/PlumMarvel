package com.babestudios.plum.ui

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.babestudios.base.ext.isLazyInitialized
import com.babestudios.base.mvrx.BaseActivity
import com.babestudios.base.rxjava.SchedulerProvider
import com.babestudios.plum.R
import com.babestudios.plum.data.PlumRepositoryContract
import com.babestudios.plum.di.AppInjectHelper
import com.babestudios.plum.navigation.features.PlumNavigator

class PlumActivity: BaseActivity() {

	private val comp by lazy {
		AppInjectHelper.provideAppComponent(applicationContext)
	}

	private lateinit var plumNavigator: PlumNavigator

	private lateinit var navController: NavController

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_plum)
		navController = findNavController(R.id.navHostFragmentPlum)
		if (::comp.isLazyInitialized) {
			plumNavigator.bind(navController)
		}
	}

	override fun onBackPressed() {
		if (onBackPressedDispatcher.hasEnabledCallbacks()) {
			onBackPressedDispatcher.onBackPressed()
		} else {
			super.finish()
			overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out)
		}
	}

	fun injectPlumRepositoryContract(): PlumRepositoryContract {
		return comp.plumRepository()
	}

	fun injectSchedulerProvider(): SchedulerProvider {
		return comp.schedulerProvider()
	}

	fun injectPlumNavigator(): PlumNavigator {
		plumNavigator = comp.navigator()
		if (::navController.isInitialized)
			plumNavigator.bind(navController)
		return plumNavigator
	}
}
