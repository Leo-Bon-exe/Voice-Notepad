package com.agilefalcon.voicenotepad.di

import android.content.Context
import android.content.SharedPreferences
import com.agilefalcon.voicenotepad.audio_managers.AudioPlayer
import com.agilefalcon.voicenotepad.audio_managers.AudioRecorder
import com.agilefalcon.voicenotepad.data.RecordDataBaseHelper
import com.agilefalcon.voicenotepad.data.RecordRepository
import com.agilefalcon.voicenotepad.helper.AudioShareHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAudioShareHelper(@ApplicationContext context: Context): AudioShareHelper {
        return AudioShareHelper(context)
    }


    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("voicenotepad_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideAudioRecorder(@ApplicationContext context: Context): AudioRecorder {
        return AudioRecorder(context)
    }

    @Provides
    @Singleton
    fun provideAudioPlayer(@ApplicationContext context: Context): AudioPlayer {
        return AudioPlayer(context)
    }

    @Provides
    @Singleton
    fun provideDatabaseHelper(@ApplicationContext context: Context): RecordDataBaseHelper {
        return RecordDataBaseHelper(context)
    }

    @Provides
    @Singleton
    fun provideRecordRepository(dbHelper: RecordDataBaseHelper): RecordRepository {
        return RecordRepository(dbHelper)
    }
}
