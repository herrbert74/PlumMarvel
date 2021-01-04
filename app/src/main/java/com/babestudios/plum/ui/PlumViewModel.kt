package com.babestudios.plum.ui

import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.ViewModelContext
import com.babestudios.base.mvrx.BaseViewModel
import com.babestudios.base.rxjava.SchedulerProvider
import com.babestudios.plum.data.MARVEL_PAGING_LIMIT
import com.babestudios.plum.data.PlumRepositoryContract
import com.babestudios.plum.data.model.MarvelCharacter
import com.babestudios.plum.navigation.features.PlumNavigator
import com.babestudios.plum.ui.squad.list.BaseHeroesVisitable
import com.babestudios.plum.ui.squad.list.HeroesVisitable
import com.babestudios.plum.ui.squad.squadlist.BaseSquadVisitable
import com.babestudios.plum.ui.squad.squadlist.SquadVisitable

class PlumViewModel(
		plumState: PlumState,
		private val plumRepository: PlumRepositoryContract,
		var plumNavigator: PlumNavigator,
		private val schedulerProvider: SchedulerProvider
) : BaseViewModel<PlumState>(plumState, plumRepository) {

	private var charactersRequestIsCompleted = false
	companion object : MvRxViewModelFactory<PlumViewModel, PlumState> {

		@JvmStatic
		override fun create(
				viewModelContext: ViewModelContext,
				state: PlumState
		): PlumViewModel? {
			val plumRepositoryContract =
					viewModelContext.activity<PlumActivity>().injectPlumRepositoryContract()
			val plumNavigator =
					viewModelContext.activity<PlumActivity>().injectPlumNavigator()
			val schedulerProvider =
					viewModelContext.activity<PlumActivity>().injectSchedulerProvider()
			return PlumViewModel(
					state,
					plumRepositoryContract,
					plumNavigator,
					schedulerProvider
			)
		}

		override fun initialState(viewModelContext: ViewModelContext): PlumState? {
			val plumRepositoryContract =
					viewModelContext.activity<PlumActivity>().injectPlumRepositoryContract()
			return PlumState(squadMembers = plumRepositoryContract.fetchSquadMembers())
		}
	}

	init {
		fetchCharacters(0)
		plumRepository.squadMembers.execute {
			val squadVisitables = convertToSquadVisitables(it() ?: emptyList())
			copy(
					squadVisitables = squadVisitables,
					squadMembers = it() ?: emptyList()
			)
		}
	}

	private fun convertToSquadVisitables(squadMembers: List<MarvelCharacter>): List<BaseSquadVisitable> {
		return squadMembers.map { SquadVisitable(it) }
	}

//region Squad

	fun fetchCharacters() {
		withState {
			fetchCharacters(it.charactersRequestOffset)
		}
	}

	private fun fetchCharacters(offset: Int) {
		plumRepository.fetchCharactersCached(offset)
				.compose(schedulerProvider.getSchedulersForObservable())
				.doOnComplete {
					//workaround to signal if both data and network is done -> show loading or not
					charactersRequestIsCompleted = true
				}
				.execute {
					val marvelCharacters = when (it) {
						is Success<List<MarvelCharacter>> -> it()
						else -> emptyList()
					}
					val newVisitables = convertToVisitables(marvelCharacters)
					//Replace existing data at the starting offset, but only if there is no error
					val newHeroesVisitables =
							if (it is Success) heroesVisitables.subList(0, offset) + newVisitables
							else heroesVisitables
					copy(
							charactersRequest = it,
							charactersRequestIsCompleted = charactersRequestIsCompleted,
							characters = marvelCharacters,
							heroesVisitables = newHeroesVisitables,
							charactersRequestOffset = offset
					)
				}
	}

	private fun convertToVisitables(heroes: List<MarvelCharacter>): List<BaseHeroesVisitable> {
		return heroes.map { HeroesVisitable(it) }
	}

//endregion

//region Squad

	fun fetchComics() {
		withState { state ->
			val comicsIds = state.selectedHero?.comics?.let { thumbnail -> thumbnail.map { it.comicId } } ?: emptyList()
			if (comicsIds.size > 1) {
				plumRepository.fetchComics(comicsIds[0] to comicsIds[1])
						.compose(schedulerProvider.getSchedulersForSingle())
						.execute {
							copy(
									comicsRequest = it,
									comics = it()
							)
						}
			} else {
				setState { copy(comicsRequest = Success(null), comics = null) }
			}
		}
	}

//endregion

	fun setNavigator(navigator: PlumNavigator) {
		plumNavigator = navigator
	}

	fun loadMoreHeroes(page: Int) {
		fetchCharacters(page * MARVEL_PAGING_LIMIT)
	}

	fun heroClicked(adapterPosition: Int) {
		setState { copy(selectedHero = (this.heroesVisitables[adapterPosition] as HeroesVisitable).marvelCharacter) }
		plumNavigator.fromSquadToDetails()
	}

	fun squadMemberClicked(adapterPosition: Int) {
		setState { copy(selectedHero = (this.squadVisitables[adapterPosition] as SquadVisitable).marvelCharacter) }
		plumNavigator.fromSquadToDetails()
	}

	fun flipSquadMember() {
		withState { state ->
			state.selectedHero?.let {
				if (state.squadMembers.contains(it)) {
					plumRepository.removeSquadMember(it)
				} else {
					plumRepository.addSquadMember(it)
				}
			}
		}
	}

}
