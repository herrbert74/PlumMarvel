package com.babestudios.plum.ui.squad.list

import androidx.viewbinding.ViewBinding
import com.babestudios.base.list.BaseViewHolder
import com.babestudios.plum.databinding.ItemSuperheroBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class HeroesViewHolder(_binding: ViewBinding) : BaseViewHolder<BaseHeroesVisitable>(_binding) {
	override fun bind(visitable: BaseHeroesVisitable) {
		val binding = _binding as ItemSuperheroBinding
		val marvelCharacter = (visitable as HeroesVisitable).marvelCharacter
		binding.lblSuperheroName.text = marvelCharacter.name

		Glide.with(itemView.context)
				.load(marvelCharacter.thumbnailPath)
				.apply(RequestOptions.circleCropTransform())
				.into(binding.ivSuperhero)
	}
}
