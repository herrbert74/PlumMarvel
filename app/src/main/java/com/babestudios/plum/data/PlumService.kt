package com.babestudios.plum.data

import com.babestudios.plum.data.dto.CharacterDto
import com.babestudios.plum.data.dto.ComicDto
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val MARVEL_PAGING_LIMIT = 100

interface PlumService {

	@GET("characters")
	fun fetchCharacters(
			@Query("limit") limit: Int,
			@Query("offset") offset: Int
	): Single<CharacterDto>

	@GET("comics/{comicId}")
	fun fetchComic(@Path("comicId") comicId: Int): Observable<ComicDto>
}