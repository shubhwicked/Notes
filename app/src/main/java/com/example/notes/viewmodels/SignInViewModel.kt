package com.example.notes.viewmodels



import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notes.utils.SharedPrefUtils.sharedPrefs


class SignInViewModel : ViewModel() {

    // LiveData to check if user is logged in or not
    private val isSignIn = MutableLiveData<Boolean>()

    // return object of signin live data
    fun getSign():MutableLiveData<Boolean>{
        return isSignIn
    }

    //Method to check user is logged in or not
    fun checkUserSignIn(){
        if (sharedPrefs.getBoolean("is_signed", false))
            isSignIn.postValue(true)
        else
            isSignIn.postValue(false)

    }



}



