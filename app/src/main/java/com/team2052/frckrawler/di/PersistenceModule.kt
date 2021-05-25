package com.team2052.frckrawler.di

//import com.team2052.frckrawler.data.persistence.FRCKrawlerDatabase
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

//    @Provides
//    @Singleton
//    fun provideFRCKrawlerDatabase(@ApplicationContext context: Context): FRCKrawlerDatabase {
//        context.applicationContext.deleteDatabase(DATABASE_NAME)
//        return Room.databaseBuilder(
//            context.applicationContext,
//            FRCKrawlerDatabase::class.java,
//            DATABASE_NAME
//        ).fallbackToDestructiveMigration().build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideEventDAO(database: FRCKrawlerDatabase): EventDAO =
//        database.eventDAO()

}