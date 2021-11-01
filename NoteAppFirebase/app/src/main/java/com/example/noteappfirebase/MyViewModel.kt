package com.example.noteappfirebase

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyViewModel(application: Application): AndroidViewModel(application) {

    private val notes: MutableLiveData<List<Note>> = MutableLiveData()
    private var db: FirebaseFirestore = Firebase.firestore



    fun getNotes(): LiveData<List<Note>> {
        return notes
    }

    fun getData(){

        db.collection("notes")
            .get()
            .addOnSuccessListener { result ->
                var noteList = arrayListOf<Note>()

                for (document in result) {

                    document.data.map { (key, value)
                        ->
                        noteList.add(Note(document.id,value.toString()))
                    }
                }
                notes.value = noteList
            }
            .addOnFailureListener { exception ->
                Log.w("MainActivity", "Error getting documents.", exception)
            }
    }

    fun addNote(note: Note){
        CoroutineScope(Dispatchers.IO).launch {

            val newNote = hashMapOf(
                "note" to note.note
            )
            db.collection("notes")
                .add(newNote)
                getData()


        }
    }

    fun editNote(noteID: String, noteText: String){
        CoroutineScope(Dispatchers.IO).launch {

            db.collection("notes").document(noteID)
                .update(mapOf(
                    "note" to noteText,
                ))
            getData()
        }
    }

    fun deleteNote(noteID: String){
        CoroutineScope(Dispatchers.IO).launch {
            db.collection("notes").document(noteID)
                .delete()
                 getData()

        }
    }
}