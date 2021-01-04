package com.babestudios.plum.data

import java.math.BigInteger
import java.security.MessageDigest

fun marvelMd5Digest(publicKey: String, privateKey: String, timeStamp: Long): String {
	return md5Digest(
			"$timeStamp$privateKey$publicKey"
	)
}

fun getNonce(): Long {
	return System.currentTimeMillis() - 10000
}

fun md5Digest(msg: String): String {
	val md = MessageDigest.getInstance("MD5")
	return BigInteger(1, md.digest(msg.toByteArray())).toString(16).padStart(32, '0')
}

