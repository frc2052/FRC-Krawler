package com.team2052.frckrawler.data.local

//@Database(
//    entities = [
//
//               ],
//    version = 1,
//    exportSchema = false
//)
//abstract class FRCKrawlerDatabase: RoomDatabase() {
//
//    abstract fun eventDAO(): EventDAO
//
//    // Maybe not necessary
//    companion object {
//        @Volatile
//        private var instance: FRCKrawlerDatabase? = null
//        private val LOCK = Any()
//
//        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
//            instance ?: createFRCKrawlerDatabase(context)
//        }
//
//        private fun createFRCKrawlerDatabase(context: Context) =
//            Room.databaseBuilder(
//                context.applicationContext,
//                FRCKrawlerDatabase::class.java,
//                DATABASE_NAME
//            )
//    }
//
//}