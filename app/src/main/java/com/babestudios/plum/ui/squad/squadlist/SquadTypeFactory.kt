package com.babestudios.plum.ui.squad.squadlist

import androidx.viewbinding.ViewBinding
import com.babestudios.base.list.BaseViewHolder
import com.babestudios.plum.R
import com.babestudios.plum.data.model.MarvelCharacter

class SquadTypeFactory : SquadAdapter.SquadTypeFactory {
	override fun type(marvelCharacter: MarvelCharacter): Int = R.layout.item_squadmember

	override fun holder(type: Int, binding: ViewBinding): BaseViewHolder<*> {
		return when (type) {
			R.layout.item_squadmember -> SquadViewHolder(binding)
			else -> throw IllegalStateException("Illegal view type")
		}
	}
}
