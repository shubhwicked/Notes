package com.example.notes.activities

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.navigation.findNavController
import com.example.notes.R
import com.example.notes.application.NotesApplication
import com.example.notes.databinding.ActivityMainBinding
import com.example.notes.utils.SharedPrefUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var application: NotesApplication
    lateinit var acct: GoogleSignInAccount
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.visibility = View.GONE
        application = getApplication() as NotesApplication
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    fun isActInitialised(): Boolean {
        return this::acct.isInitialized
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_option, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_log_out) {
            showAlertDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAlertDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Logout from ${acct.givenName}'s Notes")
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.logout)) { _, _ ->
                mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this) {
                        val editor: SharedPreferences.Editor = SharedPrefUtils.sharedPrefs.edit()
                        editor.putBoolean("is_signed", false)
                        editor.apply()
                        findNavController(R.id.nav_host_fragment).navigate(R.id.notes_fragment_to_signin_fragment)
                    }
            }
            .show()
    }

}