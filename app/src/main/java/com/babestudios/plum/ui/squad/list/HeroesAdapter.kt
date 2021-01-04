package com.babestudios.plum.ui.squad.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding
import com.babestudios.base.list.BaseViewHolder
import com.babestudios.plum.data.model.MarvelCharacter
import com.babestudios.plum.databinding.ItemSuperheroBinding
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class HeroesAdapter(
		private var heroesVisitables: List<BaseHeroesVisitable>,
		private val heroesTypeFactory: HeroesTypeFactory
) : ListAdapter<BaseHeroesVisitable, BaseViewHolder<BaseHeroesVisitable>>(HeroesDiffItemCallback) {

	override fun getItemCount(): Int {
		return heroesVisitables.size
	}

	override fun getItemViewType(position: Int): Int {
		return heroesVisitables[position].type(heroesTypeFactory)
	}

	private val heroClickSubject =
			PublishSubject.create<BaseViewHolder<BaseHeroesVisitable>>()

	fun heroClickedObservable(): Observable<BaseViewHolder<BaseHeroesVisitable>> {
		return heroClickSubject
	}

	interface HeroesTypeFactory {
		fun type(marvelCharacter: MarvelCharacter): Int
		fun holder(type: Int, binding: ViewBinding): BaseViewHolder<*>
	}

	override fun onCreateViewHolder(
			parent: ViewGroup,
			viewType: Int
	): BaseViewHolder<BaseHeroesVisitable> {
		val binding = ItemSuperheroBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
		)
		val v = heroesTypeFactory.holder(viewType, binding) as HeroesViewHolder
		RxView.clicks(binding.root)
				.takeUntil(RxView.detaches(parent))
				.map { v }
				.subscribe(heroClickSubject)
		return v
	}

	override fun onBindViewHolder(holder: BaseViewHolder<BaseHeroesVisitable>, position: Int) {
		holder.bind(heroesVisitables[position])
	}

	/*override fun onBindViewHolder(
			holder: BaseViewHolder<BaseHeroesVisitable>,
			position: Int,
			payloads: MutableList<Any>
	) {
		if (payloads.isEmpty()) holder.bind(heroesVisitables[position])
		else (holder as HeroesViewHolder).bind(payloads[0] as CurrencyQuote)
	}*/

	fun updateItems(visitables: List<BaseHeroesVisitable>) {
		val diffCallback = HeroesDiffCallback(heroesVisitables, visitables)
		val diffResult = DiffUtil.calculateDiff(diffCallback)
		heroesVisitables = visitables
		diffResult.dispatchUpdatesTo(this)
	}


}
