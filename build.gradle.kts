import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
	extra["kotlin_version"] = "1.3.72"
	val kotlinVersion = "1.3.72"
	repositories {
		google()
		jcenter()
	}
	dependencies {
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
		classpath("com.android.tools.build:gradle:4.0.0")
		classpath("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
		classpath("com.squareup.sqldelight:gradle-plugin:1.2.1")
	}
}

allprojects {
	repositories {
		google()
		maven { url = uri("https://jitpack.io") }
		jcenter()
	}

	tasks.withType<KotlinCompile> {
		kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
	}
}
