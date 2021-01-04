import com.babestudios.plum.buildsrc.Libs

plugins {
	id("jacoco")
	id("org.jetbrains.kotlin.plugin.allopen")
	id("com.android.application")
	id("kotlin-android")
	id("kotlin-android-extensions")
	id("kotlin-kapt")
	id("com.squareup.sqldelight")
}

val marvelPrivateApiKey: String by project
val marvelPublicApiKey: String by project

android {
	compileSdkVersion(29)
	defaultConfig {
		applicationId = "com.babestudios.plum"
		versionCode = 1
		versionName = "1.0"
		vectorDrawables.useSupportLibrary = true
		minSdkVersion(19)
		targetSdkVersion(29)
		consumerProguardFiles("consumer-rules.pro")
		multiDexEnabled = true
		testInstrumentationRunner = "com.babestudios.plum.PlumAndroidJUnitRunner"
	}
	buildTypes {
		all {
			buildConfigField(
				"String",
				"MARVEL_BASE_URL",
				"\"https://gateway.marvel.com:443/v1/public/\""
			)
			buildConfigField("String", "MARVEL_PUBLIC_API_API_KEY", marvelPublicApiKey)
			buildConfigField("String", "MARVEL_PRIVATE_API_API_KEY", marvelPrivateApiKey)

		}
		getByName("release") {
			isDebuggable = false
			isMinifyEnabled = true
			proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
		}
		getByName("debug") {
			isTestCoverageEnabled = true
			isMinifyEnabled = false
		}
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}

	testOptions {
		unitTests.isIncludeAndroidResources = true
	}

	androidExtensions {
		isExperimental = true
	}

	viewBinding {
		isEnabled = true
	}

	applicationVariants.all {
		val isTest: Boolean =
			gradle.startParameter.taskNames.find { it.contains("test") || it.contains("Test") } != null
		if (isTest) {
			apply(plugin = "kotlin-allopen")
			allOpen {
				annotation("com.babestudios.base.annotation.Mockable")
			}
		}
	}

	tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
		kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
	}
}

/*sqldelight {
	database("MyDatabase") {
		packageName = "com.example.db"
		sourceFolders = listOf("db")
		schemaOutputDirectory = file("build/dbs")
	}
	linkSqlite = false
}*/

dependencies {

	implementation(Libs.Kotlin.stdLibJdk8)
	implementation(Libs.baBeStudiosBase)
	implementation(Libs.AndroidX.appcompat)
	implementation(Libs.AndroidX.coreKtx)
	implementation(Libs.MvRx.mvrx)
	implementation(Libs.MvRx.testing)
	implementation(Libs.AndroidX.constraintLayout)
	implementation(Libs.AndroidX.Navigation.ktx)
	implementation(Libs.AndroidX.Navigation.fragment)
	implementation(Libs.JakeWharton.RxBinding.core)
	implementation(Libs.Google.material)
	debugImplementation(Libs.Facebook.Flipper.debug)
	releaseImplementation(Libs.Facebook.Flipper.release)
	implementation(Libs.Facebook.soloader)
	implementation(Libs.AndroidX.Navigation.ktx)
	implementation(Libs.AndroidX.coreKtx)
	implementation(Libs.Google.Dagger.dagger)
	implementation(Libs.SquareUp.OkHttp3.loggingInterceptor)

	implementation(Libs.glide)
	implementation(Libs.jsoup)
	implementation(Libs.Google.gson)
	implementation(Libs.SquareUp.Retrofit2.retrofit)
	implementation(Libs.SquareUp.Retrofit2.rxJava2Adapter)
	implementation(Libs.SquareUp.Retrofit2.converterGson)
	androidTestImplementation(Libs.SquareUp.OkHttp3.loggingInterceptor)
	implementation(Libs.SquareUp.SqlDelight.driver)
	implementation(Libs.SquareUp.SqlDelight.rxJava)

	kapt(Libs.Google.Dagger.compiler)
	kaptAndroidTest(Libs.Google.Dagger.compiler)

	implementation(Libs.RxJava2.rxJava)
	implementation(Libs.RxJava2.rxAndroid)
	implementation(Libs.RxJava2.rxKotlin)

	implementation(Libs.Javax.inject)
	kapt(Libs.Javax.annotations)

	testImplementation(Libs.Test.mockK)
	testImplementation(Libs.AndroidX.Test.Ext.jUnit)

	androidTestImplementation(Libs.Test.mockKAndroidTest)
	androidTestImplementation(Libs.Test.conditionWatcher)
	androidTestImplementation(Libs.Test.barista)
	androidTestImplementation(Libs.AndroidX.Test.Ext.jUnit)
	androidTestImplementation(Libs.AndroidX.Test.rules)
	androidTestImplementation(Libs.AndroidX.Test.runner)
	androidTestImplementation(Libs.Google.gson)
	androidTestImplementation(Libs.SquareUp.Retrofit2.retrofit)
	androidTestImplementation(Libs.SquareUp.Retrofit2.rxJava2Adapter)
	implementation("com.android.support:multidex:1.0.3")
}
