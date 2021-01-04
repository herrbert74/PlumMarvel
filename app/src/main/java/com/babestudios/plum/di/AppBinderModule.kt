package com.babestudios.plum.di

import com.babestudios.base.rxjava.ErrorResolver
import com.babestudios.plum.data.PlumErrorResolver
import com.babestudios.plum.data.PlumRepository
import com.babestudios.plum.data.PlumRepositoryContract
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 * Using the [Binds] annotation to bind internal implementations to their interfaces results in less generated code.
 * We also get rid of actually using this module by turning it into an abstract class, then an interface,
 * hence it's separated from [AppModule]
 */
@Module
interface AppBinderModule {

	@Singleton
	@Binds
	fun bindPlumRepositoryContract(plumRepository: PlumRepository): PlumRepositoryContract

	@Singleton
	@Binds
	fun provideErrorResolver(plumErrorResolver: PlumErrorResolver): ErrorResolver
}
