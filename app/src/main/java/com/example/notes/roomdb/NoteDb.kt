package com.example.notes.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notes.dao.NoteDAO
import com.example.notes.model.NoteModel

@Database(entities = [NoteModel::class], version = 1, exportSchema = false)
abstract class NoteDb:RoomDatabase() {

    abstract fun noteDAO():NoteDAO

    companion object{
        @Volatile
        private var INSTANCE:NoteDb?=null

        fun getDatabase(context: Context):NoteDb{
            // if the INSTANCE is not null, then return instance,
            // if it is null, then create the database instance

            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,NoteDb::class.java,
                    "note_db"
                ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
                INSTANCE= instance
                // return instance
                instance
            }
        }
    }
}