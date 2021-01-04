package com.babestudios.plum.ui.squad.squadlist

import androidx.viewbinding.ViewBinding
import com.babestudios.base.list.BaseViewHolder
import com.babestudios.plum.databinding.ItemSquadmemberBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class SquadViewHolder(_binding: ViewBinding) : BaseViewHolder<BaseSquadVisitable>(_binding) {
	override fun bind(visitable: BaseSquadVisitable) {
		val binding = _binding as ItemSquadmemberBinding
		val marvelCharacter = (visitable as SquadVisitable).marvelCharacter
		binding.lblSquadMemberName.text = marvelCharacter.name
		Glide.with(itemView.context)
				.load(marvelCharacter.thumbnailPath)
				.apply(RequestOptions.circleCropTransform())
				.into(binding.ivSquadMember)
	}
}
