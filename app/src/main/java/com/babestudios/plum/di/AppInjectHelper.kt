package com.babestudios.plum.di

import android.content.Context

object AppInjectHelper {
	fun provideAppComponent(applicationContext: Context): AppComponent {
		return if (applicationContext is AppComponentProvider) {
			(applicationContext as AppComponentProvider).provideAppComponent()
		} else {
			throw IllegalStateException("The application context you have passed does not implement CoreComponentProvider")
		}
	}
}
