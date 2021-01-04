package com.babestudios.plum.ui

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.babestudios.plum.data.model.Comic
import com.babestudios.plum.data.model.MarvelCharacter
import com.babestudios.plum.ui.squad.list.BaseHeroesVisitable
import com.babestudios.plum.ui.squad.squadlist.BaseSquadVisitable

data class PlumState(
		//Characters
		val charactersRequest: Async<List<MarvelCharacter>> = Uninitialized,
		val characters: List<MarvelCharacter> = emptyList(),
		val heroesVisitables: List<BaseHeroesVisitable> = emptyList(),
		val charactersRequestOffset: Int = 0,

		//workaround to signal if both data and network is done -> show loading or not
		val charactersRequestIsCompleted: Boolean = false,

		//Squad list
		val squadMembers: List<MarvelCharacter> = emptyList(),
		val squadVisitables: List<BaseSquadVisitable> = emptyList(),

		//Details
		val selectedHero: MarvelCharacter? = null,
		val comicsRequest: Async<Pair<Comic, Comic>?> = Uninitialized,
		val comics: Pair<Comic, Comic>? = null
) : MvRxState