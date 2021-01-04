package com.babestudios.plum.ui.squad.squadlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding
import com.babestudios.base.list.BaseViewHolder
import com.babestudios.plum.data.model.MarvelCharacter
import com.babestudios.plum.databinding.ItemSquadmemberBinding
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class SquadAdapter(
		private var squadVisitables: List<BaseSquadVisitable>,
		private val squadTypeFactory: SquadTypeFactory
) : ListAdapter<BaseSquadVisitable, BaseViewHolder<BaseSquadVisitable>>(SquadDiffItemCallback) {

	override fun getItemCount(): Int {
		return squadVisitables.size
	}

	override fun getItemViewType(position: Int): Int {
		return squadVisitables[position].type(squadTypeFactory)
	}

	private val squadMemberClickSubject =
			PublishSubject.create<BaseViewHolder<BaseSquadVisitable>>()

	fun squadMemberClickedObservable(): Observable<BaseViewHolder<BaseSquadVisitable>> {
		return squadMemberClickSubject
	}

	interface SquadTypeFactory {
		fun type(marvelCharacter: MarvelCharacter): Int
		fun holder(type: Int, binding: ViewBinding): BaseViewHolder<*>
	}

	override fun onCreateViewHolder(
			parent: ViewGroup,
			viewType: Int
	): BaseViewHolder<BaseSquadVisitable> {
		val binding = ItemSquadmemberBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
		)
		val v = squadTypeFactory.holder(viewType, binding) as SquadViewHolder
		RxView.clicks(binding.root)
				.takeUntil(RxView.detaches(parent))
				.map { v }
				.subscribe(squadMemberClickSubject)
		return v
	}

	override fun onBindViewHolder(holder: BaseViewHolder<BaseSquadVisitable>, position: Int) {
		holder.bind(squadVisitables[position])
	}

	/*override fun onBindViewHolder(
			holder: BaseViewHolder<BaseHeroesVisitable>,
			position: Int,
			payloads: MutableList<Any>
	) {
		if (payloads.isEmpty()) holder.bind(heroesVisitables[position])
		else (holder as HeroesViewHolder).bind(payloads[0] as CurrencyQuote)
	}*/

	fun updateItems(visitables: List<BaseSquadVisitable>) {
		val diffCallback = SquadDiffCallback(squadVisitables, visitables)
		val diffResult = DiffUtil.calculateDiff(diffCallback)
		squadVisitables = visitables
		diffResult.dispatchUpdatesTo(this)
	}


}
