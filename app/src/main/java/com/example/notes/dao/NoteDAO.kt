package com.example.notes.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.notes.model.NoteModel


@Dao
interface NoteDAO {

    @Insert
    suspend fun insert(noteModel:NoteModel)

    @Update
    suspend fun update(note: NoteModel)

    @Delete
    suspend fun delete(noteModel:NoteModel)

    @Query("SELECT * from notes_table where mail = :mailID order by id DESC")
    fun getAllNotes(mailID:String): LiveData<List<NoteModel>>
}