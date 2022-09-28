package dev.tberghuis.voicememos.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.tberghuis.voicememos.service.AudioRecordService
import dev.tberghuis.voicememos.service.AudioTrackService
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class AudioModule {


  @Provides
  @Singleton
  fun provideAudioRecordService(@ApplicationContext appContext: Context): AudioRecordService {
    return AudioRecordService(appContext)
  }


  @Provides
  @Singleton
  fun provideAudioTrackService(@ApplicationContext appContext: Context): AudioTrackService {
    return AudioTrackService(appContext)
  }


}