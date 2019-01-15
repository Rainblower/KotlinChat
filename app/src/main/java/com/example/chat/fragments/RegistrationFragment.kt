package com.example.chat.fragments

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.chat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_registration.*
import java.util.*
import kotlin.math.log

class RegistrationFragment : Fragment() {

    private val controller = Navigation.findNavController(this.activity!!,R.id.my_nav_host_fragment)

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

        select_photobutton.setOnClickListener{
            Log.d("Registration", "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }

    private var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d("Registration", "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver,selectedPhotoUri)

            selectedImagePhoto.setImageBitmap(bitmap)

            select_photobutton.alpha = 0f

//            val bitmapDrawable = BitmapDrawable(bitmap)
//            select_photobutton.setBackgroundDrawable(bitmapDrawable)
        }
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

                uploadImageToFirebaseStorage()

            }
            .addOnFailureListener{
                Toast.makeText(this.context,"Failed to create user: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun uploadImageToFirebaseStorage(){
        if(selectedPhotoUri == null){
            controller.navigate(R.id.mainFragment2)
        }
        else{
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images$filename)")

            ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
                Log.d("Registration","Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("Registration", "File location: $it")

                    saveUserToFirebase(it.toString())
                }
            }
        }
    }

    private fun saveUserToFirebase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid,username_edittext.text.toString(), profileImageUrl)

        ref.setValue(user).addOnSuccessListener {
            Log.d("Registration","Save user in Firebase Database")

            controller.navigate(R.id.mainFragment2)
        }

    }
}

class User (val uid: String, val username: String, val profileImageUrl: String)

