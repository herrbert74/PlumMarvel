package com.babestudios.plum.ui.squad.list

import androidx.viewbinding.ViewBinding
import com.babestudios.base.list.BaseViewHolder
import com.babestudios.plum.R
import com.babestudios.plum.data.model.MarvelCharacter
import com.babestudios.plum.ui.squad.squadlist.SquadAdapter
import com.babestudios.plum.ui.squad.squadlist.SquadViewHolder

class HeroesTypeFactory : HeroesAdapter.HeroesTypeFactory {
	override fun type(marvelCharacter: MarvelCharacter): Int = R.layout.item_superhero

	override fun holder(type: Int, binding: ViewBinding): BaseViewHolder<*> {
		return when (type) {
			R.layout.item_superhero -> HeroesViewHolder(binding)
			else -> throw IllegalStateException("Illegal view type")
		}
	}
}
