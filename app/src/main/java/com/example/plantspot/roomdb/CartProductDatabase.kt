package com.example.plantspot.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CartProductTable::class], version = 2, exportSchema = false)

abstract class CartProductDatabase: RoomDatabase() {

    abstract fun cartProductsDao(): CartProductDAO

    companion object {

        @Volatile

        var INSTANCE: CartProductDatabase? = null

        fun getDatabaseInstance(context: Context): CartProductDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null)
                return tempInstance

            synchronized(this) {
               val roomDb = Room.databaseBuilder(context, CartProductDatabase::class.java, "CartProducts")
                   .allowMainThreadQueries().fallbackToDestructiveMigration().build()
               INSTANCE = roomDb
               return roomDb

            }
        }



    }


}