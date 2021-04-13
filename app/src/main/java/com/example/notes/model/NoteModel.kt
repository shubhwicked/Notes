package com.example.notes.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_table")
class NoteModel(@ColumnInfo(name ="title") var title:String, @ColumnInfo(name ="text") var text:String,@ColumnInfo(name = "mail") val mail: String) {

    @PrimaryKey(autoGenerate = true)
    var id = 0
}