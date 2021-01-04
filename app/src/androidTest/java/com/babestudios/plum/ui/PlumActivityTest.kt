package com.babestudios.plum.ui

import android.content.Intent
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.azimolabs.conditionwatcher.ConditionWatcher
import com.azimolabs.conditionwatcher.Instruction
import com.babestudios.plum.App
import com.babestudios.plum.R
import com.babestudios.plum.TestApp
import com.babestudios.plum.data.model.MarvelCharacter
import com.babestudios.plum.di.AndroidTestAppComponent
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertDisplayedAtPosition
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaListInteractions.clickListItem
import io.mockk.every
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlumActivityTest {

	@Suppress("BooleanLiteralArgument")
	@Rule
	@JvmField
	var activityTestRule = ActivityTestRule(PlumActivity::class.java, true, false)

	private fun marvelCharacters() = listOf(
			MarvelCharacter(id = 1, name = "3D Man", description = "", thumbnailPath = "https://i.annihil.us/u/prod/marvel/i/mg/c/e0/535fecbbb9784.jpg", comics = emptyList()),
			MarvelCharacter(id = 2, name = "A-Bomb", description = "Rick Jones has been", thumbnailPath = "https://i.annihil.us/u/prod/marvel/i/mg/3/20/5232158de5b16.jpg", comics = emptyList()),
			MarvelCharacter(id = 3, name = "A.I.M.", description = "", thumbnailPath = "https://i.annihil.us/u/prod/marvel/i/mg/6/20/52602f21f29e.jpg", comics = emptyList()),
			MarvelCharacter(id = 4, name = "Aaron Stack", description = "", thumbnailPath = "https://i.annihil.us/u/prod/marvel/i/mg/b/40/image_not_available.jpg", comics = emptyList())
	)

	private fun squadMembers() = listOf(
			MarvelCharacter(id = 2, name = "A-Bomb", description = "", thumbnailPath = "https://i.annihil.us/u/prod/marvel/i/mg/3/20/5232158de5b16.jpg", comics = emptyList())
	)

	@Before
	fun setUp() {
		val instrumentation = InstrumentationRegistry.getInstrumentation()
		val app = instrumentation.targetContext.applicationContext as App
		val comp = app.provideAppComponent() as AndroidTestAppComponent

		every {
			comp.plumRepository().fetchCharactersCached(0)
		} answers {
			Observable.just(marvelCharacters())
		}
		every {
			comp.plumRepository().fetchSquadMembers()
		} answers {
			squadMembers()
		}
		every {
			comp.plumRepository().squadMembers
		} answers {
			Observable.just(squadMembers())
		}
		activityTestRule.launchActivity(Intent())
	}

	@Test
	fun whenSquadScreenStarts_thenSuperHeroListIsDisplayed() {
		ConditionWatcher.waitForCondition(HeroesListInstruction())
		assertDisplayedAtPosition(R.id.rvHeroes, 0, R.id.lblSuperheroName, "3D Man")
	}

	@Test
	fun whenSquadScreenStarts_thenSquadIsDisplayed() {
		ConditionWatcher.waitForCondition(HeroesListInstruction())
		assertDisplayedAtPosition(R.id.rvSquad, 0, R.id.lblSquadMemberName, "A-Bomb")
	}

	@Test
	fun whenHeroDetailsScreenStarts_thenHeroIsDisplayed() {
		ConditionWatcher.waitForCondition(HeroesListInstruction())
		clickListItem(R.id.rvHeroes, 1)
		ConditionWatcher.waitForCondition(HeroesDetailsInstruction())
		assertDisplayed(R.id.lblHeroDetailsDescription, "Rick Jones has been")

	}

	/**
	 * Checks if RecyclerView has results or not
	 */
	inner class HeroesListInstruction : Instruction() {

		override fun getDescription(): String {
			return "Wait for heroes list"
		}

		override fun checkCondition(): Boolean {
			val activity =
					(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
							as TestApp).getCurrentActivity()
							?: return false
			val navHostFragment =
					activity.supportFragmentManager.findFragmentById(R.id.navHostFragmentPlum)
			val fragments = navHostFragment?.childFragmentManager?.fragments
			fragments?.let {
				val lastFragment =
						navHostFragment.childFragmentManager.fragments[fragments.size - 1]
				val rv = lastFragment.view?.findViewById<RecyclerView>(R.id.rvHeroes)
				return rv?.let { it.size > 0 } ?: false
			} ?: return false
		}
	}

	/**
	 * Checks if HeroDetails screen is displayed
	 */
	inner class HeroesDetailsInstruction : Instruction() {

		override fun getDescription(): String {
			return "Wait for heroes details screen"
		}

		override fun checkCondition(): Boolean {
			val activity =
					(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
							as TestApp).getCurrentActivity()
							?: return false
			val navHostFragment =
					activity.supportFragmentManager.findFragmentById(R.id.navHostFragmentPlum)
			val fragments = navHostFragment?.childFragmentManager?.fragments
			fragments?.let {
				val lastFragment =
						navHostFragment.childFragmentManager.fragments[fragments.size - 1]
				val lblDescription = lastFragment.view?.findViewById<TextView>(R.id.lblHeroDetailsDescription)
				return lblDescription?.visibility == VISIBLE
			} ?: return false
		}
	}
}
