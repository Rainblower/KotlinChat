package com.example.chat.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.chat.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_registration.*

class RegistrationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment



        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        register_button.setOnClickListener {
            performRegister()
        }
        alredy_have_account_textview.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.toLogin))
    }

    private fun performRegister(){
        val email = email_edittext.text.toString()
        val password = password_edittext.text.toString()
        val username = username_edittext.text.toString()

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this.context,"Please enter text in email/password", Toast.LENGTH_LONG).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener

                //else successful
                Log.d("Main","Successfully created user with uid: ${it.result?.user?.uid}")
            }
            .addOnFailureListener{
                Toast.makeText(this.context,"Failed to create user: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }
}
