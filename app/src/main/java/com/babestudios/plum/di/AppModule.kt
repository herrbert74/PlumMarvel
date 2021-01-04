package com.babestudios.plum.di

import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import com.babestudios.base.data.AnalyticsContract
import com.babestudios.base.rxjava.SchedulerProvider
import com.babestudios.plum.BuildConfig
import com.babestudios.plum.Database
import com.babestudios.plum.data.Analytics
import com.babestudios.plum.data.PlumService
import com.babestudios.plum.data.converters.AdvancedGsonConverterFactory
import com.babestudios.plum.data.interceptors.AuthInterceptor
import com.babestudios.plum.data.interceptors.EmptyBodyInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import javax.inject.Singleton

@Module
@Suppress("unused")
open class AppModule(private val context: Context) {

	@Provides
	@Singleton
	internal fun providePlumRetrofit(): Retrofit {
		val logging = HttpLoggingInterceptor()
		logging.level = HttpLoggingInterceptor.Level.BODY

		val httpClient = OkHttpClient.Builder()
		httpClient.addInterceptor(logging)
		httpClient.addInterceptor(AuthInterceptor())
		httpClient.addInterceptor(EmptyBodyInterceptor())
		return Retrofit.Builder()//
			.baseUrl(BuildConfig.MARVEL_BASE_URL)//
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())//
			.addConverterFactory(AdvancedGsonConverterFactory.create())//
			.client(httpClient.build())//
			.build()
	}

	@Provides
	@Singleton
	internal fun providePlumService(retroFit: Retrofit)
			: PlumService {
		return retroFit.create(PlumService::class.java)
	}

	@Provides
	internal fun provideGson(): Gson {
		return GsonBuilder()
			.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz")
			.create()
	}

	@Provides
	@Singleton
	internal fun provideSchedulerProvider(): SchedulerProvider {
		return SchedulerProvider(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR), AndroidSchedulers.mainThread())
	}

	@Provides
	@Singleton
	internal fun provideAnalytics(): AnalyticsContract {
		return Analytics()
	}

	@Provides
	@Singleton
	internal fun provideDatabase(
			driver: AndroidSqliteDriver
	): Database {
		return Database(driver)
	}

	@Provides
	@Singleton
	internal fun provideSqlDriver(): AndroidSqliteDriver {
		return AndroidSqliteDriver(Database.Schema, context, "Plum.db")
	}

	@Provides
	@Singleton
	internal fun provideConnectivityManager(): ConnectivityManager {
		return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
	}

}