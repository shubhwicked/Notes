package com.example.notes.repo

import androidx.lifecycle.LiveData
import com.example.notes.application.NotesApplication
import com.example.notes.model.NoteModel
import com.example.notes.roomdb.NoteDb

class NoteRepo(application:NotesApplication) {
//getting the dao
var noteDao = NoteDb.getDatabase(application).noteDAO()
// get list of all notes by email ID
    fun getAllNotes(mailID:String):LiveData<List<NoteModel>> = noteDao.getAllNotes(mailID)

    suspend fun insert(note:NoteModel){
        noteDao.insert(note)
    }

    suspend fun update(note: NoteModel){
        noteDao.update(note)
    }


    suspend fun delete(note: NoteModel){
        noteDao.delete(note)
    }
}