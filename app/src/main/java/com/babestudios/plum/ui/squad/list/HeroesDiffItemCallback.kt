package com.babestudios.plum.ui.squad.list

import androidx.recyclerview.widget.DiffUtil

object HeroesDiffItemCallback : DiffUtil.ItemCallback<BaseHeroesVisitable>() {

	override fun areItemsTheSame(oldItem: BaseHeroesVisitable, newItem: BaseHeroesVisitable): Boolean {
		return (oldItem as HeroesVisitable).marvelCharacter.id == (newItem as HeroesVisitable).marvelCharacter.id
	}

	override fun areContentsTheSame(oldItem: BaseHeroesVisitable, newItem: BaseHeroesVisitable): Boolean {
		return (oldItem as HeroesVisitable).marvelCharacter.id == (newItem as HeroesVisitable).marvelCharacter.id
	}
}