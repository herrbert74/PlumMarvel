package com.babestudios.plum.ui.squad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.*
import com.babestudios.base.list.BaseViewHolder
import com.babestudios.base.mvrx.BaseFragment
import com.babestudios.base.view.EndlessRecyclerViewScrollListener
import com.babestudios.base.view.MultiStateView.*
import com.babestudios.plum.R
import com.babestudios.plum.databinding.FragmentSquadBinding
import com.babestudios.plum.ui.PlumActivity
import com.babestudios.plum.ui.PlumState
import com.babestudios.plum.ui.PlumViewModel
import com.babestudios.plum.ui.squad.list.BaseHeroesVisitable
import com.babestudios.plum.ui.squad.list.HeroesAdapter
import com.babestudios.plum.ui.squad.list.HeroesTypeFactory
import com.babestudios.plum.ui.squad.squadlist.BaseSquadVisitable
import com.babestudios.plum.ui.squad.squadlist.SquadAdapter
import com.babestudios.plum.ui.squad.squadlist.SquadTypeFactory
import com.babestudios.plum.views.SimpleOffsetItemDecoration
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.disposables.CompositeDisposable


class SquadFragment : BaseFragment() {

	private var heroesAdapter: HeroesAdapter? = null

	private var squadAdapter: SquadAdapter? = null

	private val viewModel by activityViewModel(PlumViewModel::class)

	private val eventDisposables: CompositeDisposable = CompositeDisposable()

	private var _binding: FragmentSquadBinding? = null
	private val binding get() = _binding!!

	//region life cycle

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		selectSubscribes()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentSquadBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initializeUI()
	}

	private fun initializeUI() {
		viewModel.logScreenView(this::class.simpleName.orEmpty())
		binding.tbSquad.title = getString(R.string.squad_title)
		(activity as AppCompatActivity).setSupportActionBar(binding.tbSquad)
		createHeroesRecyclerView()
		createSquadRecyclerView()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	private fun createHeroesRecyclerView() {
		val linearLayoutManager =
				LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
		binding.rvHeroes.layoutManager = linearLayoutManager
		binding.rvHeroes.addItemDecoration(SimpleOffsetItemDecoration(requireContext(), resources.getDimensionPixelSize(R.dimen.smallAvatarWidth)))
		binding.rvHeroes.addOnScrollListener(object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
			override fun onLoadMore(page: Int, totalItemsCount: Int) {
				viewModel.loadMoreHeroes(page)
			}
		})
	}

	private fun createSquadRecyclerView() {
		val linearLayoutManager =
				LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
		binding.rvSquad.layoutManager = linearLayoutManager
	}

	override fun orientationChanged() {
		val activity = requireActivity() as PlumActivity
		viewModel.setNavigator(activity.injectPlumNavigator())
	}

	//endregion

	//region render

	private fun selectSubscribes() {

		viewModel.selectSubscribe(PlumState::charactersRequest) {
			val tvMsvError = binding.msvHeroes.findViewById<TextView>(R.id.tvMsvError)
			withState(viewModel) { state ->
				when (state.charactersRequest) {
					is Uninitialized -> {
					}
					is Loading -> {
						if (binding.rvHeroes.adapter == null) binding.msvHeroes.viewState =
								VIEW_STATE_LOADING
						Unit
					}
					is Fail -> {
						if (state.heroesVisitables.isNotEmpty()) {
							binding.llSquadCannotConnect.visibility = VISIBLE
						} else {
							binding.msvHeroes.viewState = VIEW_STATE_ERROR
							tvMsvError.text = state.charactersRequest.error.message
						}
					}
					is Success -> {
						if (state.charactersRequestIsCompleted || state.heroesVisitables.isNotEmpty()) {
							val tvMsvEmpty = binding.msvHeroes.findViewById<TextView>(R.id.tvMsvEmpty)
							if (state.heroesVisitables.isEmpty()) {
								binding.msvHeroes.viewState = VIEW_STATE_EMPTY
								tvMsvEmpty.text =
										getString(R.string.no_heroes)
								observeActions()
							} else {
								showResult(state.heroesVisitables)
							}
						}
					}
				}
			}
		}

		viewModel.selectSubscribe(PlumState::squadVisitables) { squadVisitables ->
			if (squadVisitables.isEmpty()) {
				binding.lblMySquad.visibility = GONE
				binding.rvSquad.visibility = GONE
			} else {
				binding.lblMySquad.visibility = VISIBLE
				binding.rvSquad.visibility = VISIBLE
				squadAdapter = SquadAdapter(squadVisitables, SquadTypeFactory())
				binding.rvSquad.adapter = squadAdapter
				observeActions()
			}
		}
	}

	private fun showResult(it: List<BaseHeroesVisitable>): Unit? {
		binding.rvHeroes.visibility = VISIBLE
		binding.msvHeroes.viewState = VIEW_STATE_CONTENT
		return if (binding.rvHeroes.adapter == null) {
			heroesAdapter = HeroesAdapter(it, HeroesTypeFactory())
			binding.rvHeroes.adapter = heroesAdapter
			observeActions()
		} else {
			heroesAdapter?.updateItems(it)
		}
	}

//endregion

//region events

	private fun observeActions() {
		eventDisposables.clear()
		RxView.clicks(binding.btnSquadReload)
				.subscribe {
					binding.llSquadCannotConnect.visibility = View.GONE
					viewModel.fetchCharacters()
				}
				?.let { eventDisposables.add(it) }
		squadAdapter?.squadMemberClickedObservable()
				?.subscribe { view: BaseViewHolder<BaseSquadVisitable> ->
					withState(viewModel) { state ->
						state.squadVisitables.let {
							viewModel.squadMemberClicked(view.adapterPosition)
						}
					}
				}
				?.let { eventDisposables.add(it) }
		heroesAdapter?.heroClickedObservable()
				?.subscribe { view: BaseViewHolder<BaseHeroesVisitable> ->
					withState(viewModel) { state ->
						state.heroesVisitables.let {
							viewModel.heroClicked(view.adapterPosition)
						}
					}
				}
				?.let { eventDisposables.add(it) }
	}

//endregion

}
