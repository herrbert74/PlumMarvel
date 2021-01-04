package com.babestudios.plum.data.interceptors

import com.babestudios.plum.BuildConfig
import com.babestudios.plum.data.getNonce
import com.babestudios.plum.data.marvelMd5Digest
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

	override fun intercept(chain: Interceptor.Chain): Response {

		val timestamp = getNonce()
		val md5Digest = marvelMd5Digest(
				BuildConfig.MARVEL_PUBLIC_API_API_KEY,
				BuildConfig.MARVEL_PRIVATE_API_API_KEY,
				timestamp
		)

		val url = chain.request().url.newBuilder()
				.addQueryParameter("apikey", BuildConfig.MARVEL_PUBLIC_API_API_KEY)
				.addQueryParameter("ts", timestamp.toString())
				.addQueryParameter("hash", md5Digest)
				.build()

		val request = chain.request().newBuilder()
				.url(url)
				.build()

		return chain.proceed(request)
	}
}