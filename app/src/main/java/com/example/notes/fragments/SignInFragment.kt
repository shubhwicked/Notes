package com.example.notes.fragments



import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle

import android.util.Log


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import androidx.navigation.navGraphViewModels
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.notes.R
import com.example.notes.activities.MainActivity
import com.example.notes.databinding.SigninFragmentBinding
import com.example.notes.utils.SharedPrefUtils.sharedPrefs
import com.example.notes.utils.SharedPrefUtils.signInCode
import com.example.notes.viewmodels.SignInViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */

class SignInFragment : Fragment(),  View.OnClickListener {
    private lateinit var firstFragmentBinding: SigninFragmentBinding





    private val viewModel by navGraphViewModels<SignInViewModel>(R.id.SignInFragment) {
        defaultViewModelProviderFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = SigninFragmentBinding.inflate(inflater, container, false)
        firstFragmentBinding = binding


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // observer to check for sign in
        viewModel.getSign().observe(requireActivity(), Observer { o ->
            if(o) {
                (activity as MainActivity).acct =
                    GoogleSignIn.getLastSignedInAccount(requireActivity().applicationContext) as GoogleSignInAccount
                findNavController().navigate(R.id.NotesFragment)
                if((activity as MainActivity).isActInitialised()){
                    (activity as AppCompatActivity?)!!.supportActionBar!!.show()
                    (activity as AppCompatActivity?)!!.supportActionBar!!.title =
                        "Hello ${(activity as MainActivity).acct.displayName}"
                }
            }
        })
        // call the method to check the state of user
        viewModel.checkUserSignIn()

        // setting onclick listener for sign in Button
        firstFragmentBinding.signInButton.setOnClickListener(this)
    }
    private fun showToast(message:String){
        Toast.makeText(activity,message,Toast.LENGTH_SHORT).show()
    }


    private fun signIn() {
        /**
         * Start Google login function
         */
        val signInIntent = (activity as MainActivity).mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, signInCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == signInCode) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            updateData(account)
        } catch (e: ApiException) {
            Log.w("TAG", "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun updateData(acc: GoogleSignInAccount?) {

        val editor: SharedPreferences.Editor = sharedPrefs.edit()
        editor.putBoolean("is_signed", true)
        editor.apply()

       viewModel.checkUserSignIn()
        requireActivity().actionBar?.title = acc?.displayName
    }
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        if((activity as MainActivity).isActInitialised()){
            (activity as AppCompatActivity?)!!.supportActionBar!!.show()
            (activity as AppCompatActivity?)!!.supportActionBar!!.title =
                "Hello ${(activity as MainActivity).acct.displayName}"
        }

    }



    override fun onClick(v: View?) {
        YoYo.with(Techniques.Pulse).duration(500).playOn(v as View)
        when (v) {
        firstFragmentBinding.signInButton->{
            signIn()
        }
        }

    }


}




