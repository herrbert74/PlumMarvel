package com.babestudios.plum

import com.airbnb.mvrx.test.MvRxTestRule
import com.airbnb.mvrx.withState
import com.babestudios.base.ext.callPrivateFunc
import com.babestudios.base.rxjava.SchedulerProvider
import com.babestudios.plum.data.PlumRepositoryContract
import com.babestudios.plum.data.model.MarvelCharacter
import com.babestudios.plum.navigation.features.PlumNavigator
import com.babestudios.plum.ui.PlumState
import com.babestudios.plum.ui.PlumViewModel
import com.babestudios.plum.ui.squad.list.HeroesVisitable
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test

class PlumViewModelTest {

	private val plumRepositoryContract = mockk<PlumRepositoryContract>(relaxed = true)

	private val plumNavigator = mockk<PlumNavigator>()

	private fun marvelCharacters() = listOf(
			MarvelCharacter(id = 1, name = "3D Man", description = "", thumbnailPath = "", comics = emptyList()),
			MarvelCharacter(id = 2, name = "A-Bomb", description = "", thumbnailPath = "", comics = emptyList()),
			MarvelCharacter(id = 3, name = "A.I.M.", description = "", thumbnailPath = "", comics = emptyList()),
			MarvelCharacter(id = 4, name = "Aaron Stack", description = "", thumbnailPath = "", comics = emptyList())
	)

	private var viewModel: PlumViewModel? = null

	@Before
	fun setUp() {
		viewModel = plumViewModel()
		every {
			plumNavigator.fromSquadToDetails()
		} answers
				{
					Exception("")
				}
		every {
			plumRepositoryContract.fetchCharactersCached(0)
		} answers {
			Observable.just(marvelCharacters())
		}
	}


	@Test
	fun `when fetchRates is called then visitables and amounts are correct`() {
		viewModel?.callPrivateFunc("fetchCharacters", 0)
		withState(viewModel!!) {
			assert(it.heroesVisitables.size == 4)
			assert((it.heroesVisitables[1] as HeroesVisitable).marvelCharacter.name == "A-Bomb")
		}
	}

	private fun plumViewModel(): PlumViewModel {
		val schedulerProvider = SchedulerProvider(Schedulers.trampoline(), Schedulers.trampoline())
		return PlumViewModel(
				PlumState(
						heroesVisitables = emptyList()
				),
				plumRepositoryContract,
				plumNavigator,
				schedulerProvider
		)
	}

	companion object {
		@JvmField
		@ClassRule
		val mvrxTestRule = MvRxTestRule()
	}
}
