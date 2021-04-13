package com.example.notes.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.application.NotesApplication
import com.example.notes.model.NoteModel
import com.example.notes.repo.NoteRepo
import com.example.notes.roomdb.NoteDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NotesFragmentViewModel : ViewModel() {

    private lateinit var repos: NoteRepo

    fun initialization(application: NotesApplication) {
        //initialisation of database or if already created returning the instance
        val noteDB= NoteDb.getDatabase(application).noteDAO()
        repos = NoteRepo(application)
    }

    fun insertNote(note: NoteModel) = viewModelScope.launch(Dispatchers.IO) {
        repos.insert(note)
    }

    fun updateNote(note: NoteModel) = viewModelScope.launch(Dispatchers.IO) {
        repos.update(note)
    }

    fun deleteNote(note: NoteModel) = viewModelScope.launch(Dispatchers.IO) {
        repos.delete(note)
    }

    fun getAllNotes(mailID: String): LiveData<List<NoteModel>> {
        return repos.getAllNotes(mailID)
    }

}



