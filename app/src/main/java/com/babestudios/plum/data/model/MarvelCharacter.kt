package com.babestudios.plum.data.model

data class MarvelCharacter(val id: Int,
						   val name: String,
						   val description: String,
						   val thumbnailPath: String,
						   val comics: List<Comic>
) {
	override fun equals(other: Any?): Boolean {
		return other?.let {
			id == (other as MarvelCharacter).id
		} ?: false
	}

	override fun hashCode(): Int {
		var result = id
		result = 31 * result + description.hashCode()
		result = 31 * result + name.hashCode()
		result = 31 * result + thumbnailPath.hashCode()
		result = 31 * result + comics.hashCode()
		return result
	}
}
