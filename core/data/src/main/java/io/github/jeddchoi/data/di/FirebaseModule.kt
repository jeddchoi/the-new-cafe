package io.github.jeddchoi.data.di

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseApp(@ApplicationContext context: Context): FirebaseApp {
        return FirebaseApp.initializeApp(context)!!
    }


    @Provides
    @Singleton
    fun provideFirebaseFunctions(
        firebaseApp: FirebaseApp
    ): FirebaseFunctions {
        return Firebase.functions(firebaseApp)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(
        firebaseApp: FirebaseApp
    ): FirebaseAuth {
        return Firebase.auth(firebaseApp)
    }
}