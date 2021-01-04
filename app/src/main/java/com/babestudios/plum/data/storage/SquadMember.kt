package com.babestudios.plum.data.storage

import com.babestudios.plum.SelectAll
import com.babestudios.plum.data.model.Comic
import com.babestudios.plum.data.model.MarvelCharacter

fun SelectAll.convertToBusinessModel(comics: List<Comic>): MarvelCharacter {
	return MarvelCharacter(
			this.id.toInt(),
			this.name?:"",
			this.description ?: "",
			this.thumbnailPath?:"",
			comics
	)
}