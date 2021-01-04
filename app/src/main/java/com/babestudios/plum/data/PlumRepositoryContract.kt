package com.babestudios.plum.data

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.babestudios.base.data.AnalyticsContract
import com.babestudios.base.rxjava.SchedulerProvider
import com.babestudios.plum.Database
import com.babestudios.plum.SelectComicsForMarvelCharacterById
import com.babestudios.plum.data.dto.convertToModel
import com.babestudios.plum.data.model.Comic
import com.babestudios.plum.data.model.MarvelCharacter
import com.babestudios.plum.data.storage.convertToBusinessModel
import com.squareup.sqldelight.runtime.rx.asObservable
import com.squareup.sqldelight.runtime.rx.mapToList
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

interface PlumRepositoryContract : AnalyticsContract {

	//combined cache calls
	fun fetchCharactersCached(offset: Int): Observable<List<MarvelCharacter>>

	//network calls
	fun fetchCharacters(offset: Int): Single<List<MarvelCharacter>>
	fun fetchComics(comicsIds: Pair<Int, Int>): Single<Pair<Comic, Comic>>

	//storage
	fun addSquadMember(marvelCharacter: MarvelCharacter)
	fun removeSquadMember(marvelCharacter: MarvelCharacter)
	fun fetchMarvelCharacters(offset: Int): List<MarvelCharacter>
	fun fetchSquadMembers(): List<MarvelCharacter>
	fun fetchComicsForCharacter(id: Int): List<Comic>
	val squadMembers: Observable<List<MarvelCharacter>>

}

class PlumRepository @Inject constructor(
		private val plumService: PlumService,
		private val analytics: AnalyticsContract,
		private val schedulerProvider: SchedulerProvider,
		private val database: Database,
		private val connectivityManager: ConnectivityManager
) : PlumRepositoryContract {

	//region combined cache calls
	override fun fetchCharactersCached(offset: Int): Observable<List<MarvelCharacter>> {
		return Observable.just(fetchMarvelCharacters(offset))
				.mergeWith(fetchCharacters(offset))
	}

	//endregion

	//region network

	override fun fetchCharacters(offset: Int): Single<List<MarvelCharacter>> {
		return plumService.fetchCharacters(
				MARVEL_PAGING_LIMIT,
				offset
		)
				.map { it.convertToModel() }
				.doOnSuccess { marvelCharacters ->
					val marvelCharacterQueries = database.marvelCharacterQueries
					val marvelCharacterComicQueries = database.marvelCharacter_ComicQueries
					val comicQueries = database.comicQueries
					marvelCharacters.forEach {
						marvelCharacterQueries.insertMarvelCharacter(
								it.id.toLong(),
								it.name,
								it.description,
								it.thumbnailPath
						)
						it.comics.forEach { comic ->
							comicQueries.insertComicIgnore(
									comic.comicId.toLong(),
									comic.title,
									comic.thumbnail
							)
							marvelCharacterComicQueries.insertMarvelCharacterWithComic(
									it.id.toLong(),
									comic.comicId.toLong()
							)
						}
					}
				}
				.compose(schedulerProvider.getSchedulersForSingle())
	}

	override fun fetchComics(comicsIds: Pair<Int, Int>): Single<Pair<Comic, Comic>> {
		return Observables.combineLatest(
				plumService.fetchComic(comicsIds.first),
				plumService.fetchComic(comicsIds.second)
		) { f, s -> f to s }
				.map { it.first.convertToModel() to it.second.convertToModel() }
				.doOnNext { pair ->
					val comicQueries = database.comicQueries
					pair.toList().forEach { comic ->
						comicQueries.insertComicReplace(
								comic.comicId.toLong(),
								comic.title,
								comic.thumbnail
						)
					}
				}
				.compose(schedulerProvider.getSchedulersForObservable())
				.firstOrError()
	}

	//endregion

	//region storage

	override fun addSquadMember(marvelCharacter: MarvelCharacter) {
		database.squadMemberQueries.insertSquadMember(
				marvelCharacter.id.toLong()
		)
	}

	override fun removeSquadMember(marvelCharacter: MarvelCharacter) {
		database.squadMemberQueries.deleteSquadMember(
				marvelCharacter.id.toLong()
		)
	}

	override fun fetchMarvelCharacters(offset: Int): List<MarvelCharacter> {
		return database.marvelCharacterQueries
				.selectAllWithOffset(MARVEL_PAGING_LIMIT.toLong(), offset.toLong())
				.executeAsList()
				.map {
					val comics = fetchComicsForCharacter(it.id.toInt())
					MarvelCharacter(
							it.id.toInt(),
							it.name ?: "",
							it.description ?: "",
							it.thumbnailPath ?: "",
							comics
					)
				}
	}

	override fun fetchSquadMembers(): List<MarvelCharacter> {
		return database.squadMemberQueries
				.selectAll()
				.executeAsList()
				.map {
					val comics = fetchComicsForCharacter(it.id.toInt())
					it.convertToBusinessModel(comics)
				}
	}

	override fun fetchComicsForCharacter(id: Int): List<Comic> {
		return database.marvelCharacter_ComicQueries
				.selectComicsForMarvelCharacterById(id.toLong())
				.executeAsList()
				.map {
					it.convertToBusinessModel()
				}
	}

	override val squadMembers: Observable<List<MarvelCharacter>> =
			database.squadMemberQueries.selectAll()
					.asObservable()
					.mapToList()
					.map { list ->
						list.map {
							val comics = fetchComicsForCharacter(it.id.toInt())
							it.convertToBusinessModel(comics)
						}
					}

	//endregion

	//region analytics

	override fun logAppOpen() {
		analytics.logAppOpen()
	}

	override fun logScreenView(screenName: String) {
		analytics.logScreenView(screenName)
	}

	override fun logSearch(queryText: String) {
		analytics.logSearch(queryText)
	}

	//endregion

	// region Utils

	@Suppress("DEPRECATION")
	private fun isNetworkAvailable(): Boolean {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			val nw = connectivityManager.activeNetwork ?: return false
			val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
			return when {
				actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
				actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
				//for other device how are able to connect with Ethernet
				actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
				//for check internet over Bluetooth
				actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
				else -> false
			}
		} else {
			val nwInfo = connectivityManager.activeNetworkInfo ?: return false
			return nwInfo.isConnected
		}
	}

	//endregion
}

private fun SelectComicsForMarvelCharacterById.convertToBusinessModel(): Comic {
	return Comic(this.comic_id.toInt(), this.title ?: "", this.thumbnail)
}
