package com.babestudios.plum.di

import android.content.Context
import com.babestudios.base.data.AnalyticsContract
import com.babestudios.base.rxjava.ErrorResolver
import com.babestudios.base.rxjava.SchedulerProvider
import com.babestudios.plum.data.PlumErrorResolver
import com.babestudios.plum.data.PlumRepositoryContract
import com.babestudios.plum.data.PlumService
import dagger.Module
import dagger.Provides
import io.mockk.every
import io.mockk.mockk
import javax.inject.Singleton

@Module
class AndroidTestAppModule(context: Context) : AppModule(context) {

	//region Mocks/overrides from BinderModules

	@Provides
	@Singleton
	internal fun providePlumRepositoryContract(
		plumService: PlumService,
		analytics: AnalyticsContract,
		schedulerProvider: SchedulerProvider
	): PlumRepositoryContract {
		val mockPlumRepository = mockk<PlumRepositoryContract>(relaxed = true)

		every {
			mockPlumRepository.logAppOpen()
		} returns Unit

		every {
			mockPlumRepository.logScreenView(any())
		} returns Unit

		every {
			mockPlumRepository.logSearch(any())
		} returns Unit

		return mockPlumRepository
	}

	@Provides
	@Singleton
	internal fun provideErrorResolver(): ErrorResolver {
		return mockk<PlumErrorResolver>()
	}

	//endregion
}