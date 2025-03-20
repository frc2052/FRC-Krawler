package com.team2052.frckrawler.data.export

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataExportModule {
  @Binds
  abstract fun bindExporter(
    exporter: OkioDataExporter
  ): DataExporter
}