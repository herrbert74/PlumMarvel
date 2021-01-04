package com.babestudios.plum.ui.hero

import android.content.res.ColorStateList
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.airbnb.mvrx.*
import com.babestudios.base.mvrx.BaseFragment
import com.babestudios.base.view.MultiStateView.*
import com.babestudios.base.view.ParallaxAppBarView
import com.babestudios.plum.R
import com.babestudios.plum.databinding.FragmentHeroDetailsBinding
import com.babestudios.plum.ui.PlumActivity
import com.babestudios.plum.ui.PlumState
import com.babestudios.plum.ui.PlumViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.disposables.CompositeDisposable

class HeroDetailsFragment : BaseFragment() {

	private val viewModel by existingViewModel(PlumViewModel::class)

	private val eventDisposables: CompositeDisposable = CompositeDisposable()

	private var _binding: FragmentHeroDetailsBinding? = null
	private val binding get() = _binding!!

	private val callback: OnBackPressedCallback = (object : OnBackPressedCallback(true) {
		override fun handleOnBackPressed() {
			viewModel.plumNavigator.popBackStack()
		}
	})

	//region life cycle

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

	override fun onCreateView(
			inflater: LayoutInflater, container: ViewGroup?,
			savedInstanceState: Bundle?
	): View {
		requireActivity().onBackPressedDispatcher.addCallback(this, callback)
		_binding = FragmentHeroDetailsBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initializeUI()
	}

	override fun orientationChanged() {
		val activity = requireActivity() as PlumActivity
		viewModel.setNavigator(activity.injectPlumNavigator())
	}

	override fun onDestroyView() {
		super.onDestroyView()
		callback.remove()
		_binding = null
	}

	private fun initializeUI() {
		viewModel.logScreenView(this::class.simpleName.orEmpty())
		(activity as AppCompatActivity).setSupportActionBar(binding.pabHeroDetails.getToolbar())
		val toolBar = (activity as AppCompatActivity).supportActionBar
		toolBar?.setDisplayHomeAsUpEnabled(true)
		toolBar?.setDisplayShowTitleEnabled(true)
		withState(viewModel) { toolBar?.setTitle(it.selectedHero?.name) }
		binding.pabHeroDetails.setNavigationOnClickListener { viewModel.plumNavigator.popBackStack() }
		val display: Display = requireActivity().windowManager.defaultDisplay
		val point = Point()
		display.getSize(point)
		binding.pabHeroDetails.layoutParams.height = point.x

		selectSubscribes()
		viewModel.fetchComics()
	}

	override fun onResume() {
		super.onResume()
		observeActions()
		showHero()
	}

	//endregion

	//region Render

	private fun showHero() {
		withState(viewModel) { state ->
			val selectedHero = state.selectedHero
			Glide.with(requireContext())
					.load(selectedHero?.thumbnailPath)
					.into(object : CustomViewTarget<ParallaxAppBarView, Drawable>(binding.pabHeroDetails) {
						override fun onLoadFailed(errorDrawable: Drawable?) {

						}

						override fun onResourceCleared(placeholder: Drawable?) {

						}

						override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
							binding.pabHeroDetails.setImageViewResource(resource)
						}

					})
			binding.pabHeroDetails
			binding.lblHeroDetailsDescription.text = selectedHero?.description?.let {
				if (it.isEmpty()) getString(R.string.no_description) else it
			}

			//Comics
			selectedHero?.comics?.getOrNull(0)?.let {
				binding.lblHeroDetailsComics1Description.text = it.title
				Glide.with(requireContext())
						.load(it.comicId)
						.into(binding.ivHeroDetailsComics1)
			}
			selectedHero?.comics?.getOrNull(1)?.let {
				binding.lblHeroDetailsComics2Description.text = it.title
				Glide.with(requireContext())
						.load(it.comicId)
						.into(binding.ivHeroDetailsComics2)
			}
			val count = selectedHero?.comics?.count() ?: 0
			binding.lblHeroDetailsAlso.text =
					requireContext().resources.getQuantityString(R.plurals.other_comics, count, count)

			//Fab

		}
	}

	private fun selectSubscribes() {
		viewModel.selectSubscribe(PlumState::squadMembers) {
			withState(viewModel) { state ->
				if (state.squadMembers.contains(state.selectedHero)) {
					binding.fabHeroDetailsRecruit.setImageResource(R.drawable.ic_fire)
					binding.fabHeroDetailsRecruit.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorError))
				} else {
					binding.fabHeroDetailsRecruit.setImageResource(R.drawable.ic_favorite)
					binding.fabHeroDetailsRecruit.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorAccent))
				}
			}
		}
	}

	override fun invalidate() {
		withState(viewModel) { state ->
			when (state.comicsRequest) {
				is Uninitialized -> {
					binding.msvHeroDetails.viewState = VIEW_STATE_CONTENT
				}
				is Loading -> {
					binding.msvHeroDetails.viewState = VIEW_STATE_LOADING
				}
				is Success -> {
					binding.msvHeroDetails.viewState = VIEW_STATE_CONTENT
					state.comics?.first?.let {
						Glide.with(requireContext())
								.load(it.thumbnail)
								.into(binding.ivHeroDetailsComics1)
					} ?: run {
						binding.lblHeroDetailsLastAppearedIn.visibility = GONE
						binding.lblHeroDetailsAlso.visibility = GONE
					}
					state.comics?.second?.let {
						Glide.with(requireContext())
								.load(it.thumbnail)
								.into(binding.ivHeroDetailsComics2)
					}
				}
				is Fail -> {
					binding.msvHeroDetails.viewState = VIEW_STATE_ERROR
					val tvMsvError = binding.msvHeroDetails.findViewById<TextView>(R.id.tvMsvError)
					tvMsvError.text = state.comicsRequest.error.message
				}
			}
		}
	}

	//endregion

	//region events

	private fun observeActions() {
		eventDisposables.clear()
		RxView.clicks(binding.fabHeroDetailsRecruit)
				.subscribe {
					withState(viewModel) {
						if (it.squadMembers.contains(it.selectedHero)) {
							showRemoveHeroFromSquadDialog()
						} else {
							viewModel.flipSquadMember()
						}

					}
				}
				.let { eventDisposables.add(it) }
	}

	private fun showRemoveHeroFromSquadDialog() {
		AlertDialog.Builder(requireContext())
				.setTitle(R.string.remove_hero_from_squad)
				.setMessage(R.string.do_you_really_remove_hero_from_squad)
				.setPositiveButton(R.string.remove) { _, _ -> viewModel.flipSquadMember() }
				.setNegativeButton(android.R.string.no) { _, _ ->
					// do nothing
				}
				.show()
		observeActions()
	}

	//endregion
}

