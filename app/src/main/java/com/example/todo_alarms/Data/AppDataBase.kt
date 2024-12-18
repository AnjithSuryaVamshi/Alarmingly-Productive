package com.example.todo_alarms.Data

import android.content.Context
import android.provider.CalendarContract.Instances
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase



@Database(entities = [AlarmEntity::class, TodoEntity::class], version = 1, exportSchema = false)
abstract class AppDataBase :  RoomDatabase()  {
    abstract fun alarmDao(): Alarm_Dao
    abstract fun todoDao(): Todo_Dao
    companion object {
       @Volatile
       private var INSTANCE : AppDataBase? = null
        fun getDatabase(context: Context):AppDataBase{
            return INSTANCE ?: synchronized(this){
                val instance  =  Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "app_database"

                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}