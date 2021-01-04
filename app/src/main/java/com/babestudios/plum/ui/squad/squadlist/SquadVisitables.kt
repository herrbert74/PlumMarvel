package com.babestudios.plum.ui.squad.squadlist

import com.babestudios.plum.data.model.MarvelCharacter

sealed class BaseSquadVisitable {
	abstract fun type(squadTypeFactory: SquadAdapter.SquadTypeFactory): Int
}

class SquadVisitable(val marvelCharacter: MarvelCharacter) : BaseSquadVisitable() {
	override fun type(squadTypeFactory: SquadAdapter.SquadTypeFactory): Int {
		return squadTypeFactory.type(marvelCharacter)
	}
}
