package dev.tberghuis.voicememos.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.tberghuis.voicememos.common.AudioController
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AudioModule {

  @Provides
  @Singleton
  fun provideAudioController(@ApplicationContext appContext: Context): AudioController {
    return AudioController(appContext)
  }
}