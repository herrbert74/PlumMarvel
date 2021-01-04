package com.babestudios.plum.ui.squad.squadlist

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil


class SquadDiffCallback(
		private val oldList: List<BaseSquadVisitable>,
		private val newList: List<BaseSquadVisitable>
) : DiffUtil.Callback() {

	override fun getOldListSize(): Int = oldList.size

	override fun getNewListSize(): Int = newList.size

	override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		val oldCharacter = (oldList[oldItemPosition] as SquadVisitable).marvelCharacter
		val newCharacter = (newList[newItemPosition] as SquadVisitable).marvelCharacter
		return oldCharacter.id == newCharacter.id
	}

	override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		val oldCharacter = (oldList[oldItemPosition] as SquadVisitable).marvelCharacter
		val newCharacter = (newList[newItemPosition] as SquadVisitable).marvelCharacter
		return (
				oldCharacter.id == newCharacter.id &&
						oldCharacter.name == newCharacter.name
				)
	}

	@Nullable
	override fun getChangePayload(oldPosition: Int, newPosition: Int): Any? {
		return (newList[newPosition] as SquadVisitable).marvelCharacter
	}
}