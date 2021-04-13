package com.example.notes.fragments

import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.notes.R
import com.example.notes.activities.MainActivity
import com.example.notes.adapter.NoteAdapter
import com.example.notes.adapter.NotesItemClickListener
import com.example.notes.databinding.NotesFragmentBinding

import com.example.notes.model.NoteModel
import com.example.notes.viewmodels.NotesFragmentViewModel

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.bottomsheet.BottomSheetBehavior

class NotesFragment : Fragment(), NotesItemClickListener {
    private lateinit var binding: NotesFragmentBinding
    private lateinit var adapter: NoteAdapter
    private lateinit var mailID: String
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private var editNoteFrag: Boolean = false
    private lateinit var noteItem: NoteModel
    private val viewModel by navGraphViewModels<NotesFragmentViewModel>(R.id.NotesFragment) {
        defaultViewModelProviderFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    activity!!.supportFinishAfterTransition()
                }
            }
        // Set the callback for the backButton and back press
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = NotesFragmentBinding.inflate(inflater, container, false)
        this.binding = binding
        viewModel.initialization((activity as MainActivity).application)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetBehavior = BottomSheetBehavior.from<View>(binding.bottomSheetLayout)
        // getting details for current loggged in user
        (activity as MainActivity).acct =
            GoogleSignIn.getLastSignedInAccount(requireActivity().applicationContext) as GoogleSignInAccount
        mailID = (activity as MainActivity).acct.email.toString()
        binding.rv.layoutManager = LinearLayoutManager(activity,RecyclerView.VERTICAL,false)
        // get the list of all notes by email id
        val allNotes: LiveData<List<NoteModel>> = viewModel.getAllNotes(mailID)
        // initialising Adapter
        adapter = NoteAdapter(this)
        binding.rv.adapter = adapter

        //observing if any changes happens in notes live data
        allNotes.observe(requireActivity(), {
            val noteList: List<NoteModel> = allNotes.value!!
            if (noteList.isEmpty()) {
                binding.noNotesLayout.visibility = View.VISIBLE
                binding.rv.visibility = View.GONE

            } else {
                binding.noNotesLayout.visibility = View.GONE
                binding.rv.visibility = View.VISIBLE
            }
            it.let {
                adapter.updateList(noteList)
            }
        })

        // bottom sheet layout behaviour
        binding.bottomSheetLayout.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        checkInputTextChangeListener()
        binding.btnAddNote.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            if (binding.etNoteTitle.text.isNotEmpty() && binding.etNoteText.text.isNotEmpty()) {
                if (editNoteFrag) {
                    //edit created instance
                    noteItem.title = binding.etNoteTitle.text.toString()
                    noteItem.text = binding.etNoteText.text.toString()
                } else {
                    //In Add case, create new instance
                    noteItem = NoteModel(
                        binding.etNoteTitle.text.toString(),
                        binding.etNoteText.text.toString(),
                        mailID
                    )
                }

                //if bottom sheet expanded -> collapse on add/edit note
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    if (editNoteFrag) {
                        viewModel.updateNote(noteItem)
                    } else {
                        viewModel.insertNote(noteItem)
                    }
                    editNoteFrag=false
                    binding.etNoteTitle.setText("")
                    binding.etNoteText.setText("")

                }

            } else {
                if (binding.etNoteTitle.text.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Enter title", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Enter note", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
    fun rotateAddBtn(v: View, rotate: Boolean): Boolean {
        v.animate().setDuration(400)
            .setListener(object : AnimatorListenerAdapter() {
            })
            .rotation(if (rotate) 360f else 0f)
        return rotate
    }
    override fun onItemClicked(buttonID: String, note: NoteModel) {
        /**
         * Interface to listen button click event from adapter
         */
        noteItem = note
        if (buttonID == "edit" && bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        when (buttonID) {
            "edit" -> {
                editNoteFrag = true
                binding.etNoteTitle.setText(note.title)
                binding.etNoteText.setText(note.text)
            }
            "delete" -> {
                viewModel.deleteNote(note)
            }

        }
    }


    private fun checkInputTextChangeListener() {
        /**
         * check any changes in the text and listen. If any changes occur make change in add and save buttons
         */
        binding.etNoteTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty() && binding.etNoteText.text.isNotEmpty()) {
                    rotateAddBtn(binding.btnAddNote, true)
                    binding.btnAddNote.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_save
                        )
                    )
                } else {
                    rotateAddBtn(binding.btnAddNote, false)
                    binding.btnAddNote.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_add
                        )
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.etNoteText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty() && binding.etNoteTitle.text.isNotEmpty()) {
                    rotateAddBtn(binding.btnAddNote, true)
                    binding.btnAddNote.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_save
                        )
                    )
                } else {
                    rotateAddBtn(binding.btnAddNote, false)
                    binding.btnAddNote.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_add
                        )
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }
}