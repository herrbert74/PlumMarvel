package com.babestudios.plum.ui.squad.list

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil


class HeroesDiffCallback(
		private val oldList: List<BaseHeroesVisitable>,
		private val newList: List<BaseHeroesVisitable>
) : DiffUtil.Callback() {

	override fun getOldListSize(): Int = oldList.size

	override fun getNewListSize(): Int = newList.size

	override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		val oldCharacter = (oldList[oldItemPosition] as HeroesVisitable).marvelCharacter
		val newCharacter = (newList[newItemPosition] as HeroesVisitable).marvelCharacter
		return oldCharacter.id == newCharacter.id
	}

	override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		val oldCharacter = (oldList[oldItemPosition] as HeroesVisitable).marvelCharacter
		val newCharacter = (newList[newItemPosition] as HeroesVisitable).marvelCharacter
		return (
				oldCharacter.id == newCharacter.id &&
						oldCharacter.name == newCharacter.name
				)
	}

	@Nullable
	override fun getChangePayload(oldPosition: Int, newPosition: Int): Any? {
		return (newList[newPosition] as HeroesVisitable).marvelCharacter
	}
}