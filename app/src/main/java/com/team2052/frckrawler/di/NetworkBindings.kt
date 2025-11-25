package com.team2052.frckrawler.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.team2052.frckrawler.data.adapter.ZonedDateTimeJsonAdapter
import com.team2052.frckrawler.data.remote.EventService
import com.team2052.frckrawler.data.remote.TbaAuthInterceptor
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File

@ContributesTo(AppScope::class)
@BindingContainer
object NetworkBindings {

  private const val BASE_TBA_URL = "https://www.thebluealliance.com/api/v3/"

  @Provides
  @SingleIn(AppScope::class)
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
  @SingleIn(AppScope::class)
  fun provideMoshi(): Moshi {
    return Moshi.Builder()
      .add(KotlinJsonAdapterFactory())
      .add(ZonedDateTimeJsonAdapter())
      .build()
  }

  @Provides
  @SingleIn(AppScope::class)
  fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
    return Retrofit.Builder()
      .baseUrl(BASE_TBA_URL)
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()
  }

  @Provides
  @SingleIn(AppScope::class)
  fun provideEventService(retrofit: Retrofit): EventService =
    retrofit.create(EventService::class.java)
}
