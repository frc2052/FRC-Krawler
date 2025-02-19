package com.team2052.frckrawler.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.team2052.frckrawler.data.adapter.ZonedDateTimeJsonAdapter
import com.team2052.frckrawler.data.remote.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  private const val BASE_TBA_URL = "https://www.thebluealliance.com/api/v3/"

  @Provides
  @Singleton
  fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
    return OkHttpClient.Builder()
      .addInterceptor(TbaAuthInterceptor())
      .cache(
        Cache(
          directory = File(context.cacheDir, "http_cache"),
          maxSize = 50L * 1024L * 1024L, // 50 MiB
        )
      )
      .build()
  }

  @Provides
  @Singleton
  fun provideMoshi(): Moshi {
    return Moshi.Builder()
      .add(KotlinJsonAdapterFactory())
      .add(ZonedDateTimeJsonAdapter())
      .build()
  }

  @Provides
  @Singleton
  fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
    return Retrofit.Builder()
      .baseUrl(BASE_TBA_URL)
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()
  }

  @Provides
  @Singleton
  fun provideEventService(retrofit: Retrofit): EventService =
    retrofit.create(EventService::class.java)

  @Provides
  @Singleton
  fun provideMatchService(retrofit: Retrofit): MatchService =
    retrofit.create(MatchService::class.java)

  @Provides
  @Singleton
  fun provideTeamService(retrofit: Retrofit): TeamService =
    retrofit.create(TeamService::class.java)

}