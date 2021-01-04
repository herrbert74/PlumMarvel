package com.babestudios.plum.ui.squad.list

import com.babestudios.plum.data.model.MarvelCharacter
import com.babestudios.plum.ui.squad.squadlist.SquadAdapter

sealed class BaseHeroesVisitable {
	abstract fun type(heroesTypeFactory: HeroesAdapter.HeroesTypeFactory): Int
}

class HeroesVisitable(val marvelCharacter: MarvelCharacter) : BaseHeroesVisitable() {
	override fun type(heroesTypeFactory: HeroesAdapter.HeroesTypeFactory): Int {
		return heroesTypeFactory.type(marvelCharacter)
	}
}
