package com.babestudios.plum.ui.squad.squadlist

import androidx.recyclerview.widget.DiffUtil

object SquadDiffItemCallback : DiffUtil.ItemCallback<BaseSquadVisitable>() {

	override fun areItemsTheSame(oldItem: BaseSquadVisitable, newItem: BaseSquadVisitable): Boolean {
		return (oldItem as SquadVisitable).marvelCharacter.id == (newItem as SquadVisitable).marvelCharacter.id
	}

	override fun areContentsTheSame(oldItem: BaseSquadVisitable, newItem: BaseSquadVisitable): Boolean {
		return (oldItem as SquadVisitable).marvelCharacter.id == (newItem as SquadVisitable).marvelCharacter.id
	}
}