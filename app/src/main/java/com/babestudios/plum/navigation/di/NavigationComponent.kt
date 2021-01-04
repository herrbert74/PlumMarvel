package com.babestudios.plum.navigation.di

import com.babestudios.plum.navigation.features.PlumNavigator

/**
 * This is not a real Dagger Component, only an interface to break circular dependency with com.babestudios.plum.navigation.
 * See [Dagger Component Dependencies for Library Development]
 * (https://proandroiddev.com/dagger-component-dependencies-for-library-development-e2df7ce68233)
 * and [Component dependencies](https://dagger.dev/api/2.14/dagger/Component.html#dependencies--)
 */
interface NavigationComponent {
	fun providePlumNavigation(): PlumNavigator
}
