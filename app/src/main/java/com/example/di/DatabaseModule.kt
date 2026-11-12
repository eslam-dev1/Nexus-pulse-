package com.example.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.local.ContactDao
import com.example.data.local.SocialProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideContactDao(database: AppDatabase): ContactDao {
        return database.contactDao()
    }

    @Provides
    @Singleton
    fun provideSocialProfileDao(database: AppDatabase): SocialProfileDao {
        return database.socialProfileDao()
    }
}
