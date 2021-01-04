package com.babestudios.plum.data.interceptors

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * This is a workaround for the following problem:
 * @see <a href="https://github.com/square/retrofit/issues/2867">link</a>
 *
 */

class EmptyBodyInterceptor : Interceptor {
	override fun intercept(chain: Interceptor.Chain): Response {
		val response = chain.proceed(chain.request())
		if (!response.isSuccessful) {
			return response
		}

		if (response.code != 204 && response.code != 205) {
			return response
		}

		if ((response.body?.contentLength() ?: -1) > 0) {
			return response
		}

		val emptyBody = "".toResponseBody("text/plain".toMediaType())

		return response
			.newBuilder()
			.code(200)
			.body(emptyBody)
			.build()
	}
}