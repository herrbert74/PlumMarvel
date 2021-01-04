package com.babestudios.plum.ui

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.babestudios.plum.R
import com.babestudios.plum.navigation.base.BaseNavigator
import com.babestudios.plum.navigation.di.NavigationComponent
import com.babestudios.plum.navigation.features.PlumNavigator


/**
 * This class holds the navController for any feature through [BaseNavigator]
 * It is a dependency of [com.babestudios.plum.di.AppComponent],
 * so no need to create it in every feature component.
 *
 * This holds all the implementations for [NavigationComponent],
 * and the feature navigations need to be exposed from the component,
 * like e.g. [com.babestudios.plum.navigation.features.PlumNavigator].
 */
class AppNavigation : NavigationComponent {

	//region features

	override fun providePlumNavigation(): PlumNavigator {
		return object : BaseNavigator(), PlumNavigator {
			override var navController: NavController? = null
			override fun fromSquadToDetails() {
				navController?.navigateSafe(R.id.action_squadFragment_to_heroDetailsFragment)
			}
		}
	}

	//endregion
}

@Suppress("MaxLineLength")
		/**
		 * https://stackoverflow.com/questions/51060762/java-lang-illegalargumentexception-navigation-destination-xxx-is-unknown-to-thi
		 */
fun NavController.navigateSafe(
		@IdRes resId: Int,
		args: Bundle? = null,
		navOptions: NavOptions? = null,
		navExtras: Navigator.Extras? = null
) {
	val action = currentDestination?.getAction(resId) ?: graph.getAction(resId)
	if (action != null && currentDestination?.id != action.destinationId) {
		navigate(resId, args, navOptions, navExtras)
	}
}
