package com.babestudios.plum.data.storage

import com.squareup.sqldelight.ColumnAdapter
import javax.inject.Inject

class ListOfStringsAdapter @Inject constructor() : ColumnAdapter<List<Int>, String> {
	override fun decode(databaseValue: String) = databaseValue.split(",").map { it.toInt()}
	override fun encode(value: List<Int>) = value.joinToString(separator = ",")
}