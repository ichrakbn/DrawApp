package com.example.firebaseauth

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.firebaseauth.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    //ViewBinding
    private lateinit var binding: ActivitySignUpBinding
    //progressDialog
    private var mProgressBar: ProgressDialog? = null

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    private var email: EditText? = null
    private var password: EditText? = null
    private var age: EditText? = null
    private var name: EditText? = null
    private var RegisterBtn: Button? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.Hlayout.setOnClickListener{
            closeKeyBoard()
        }
       binding.cancelBtn.setOnClickListener {
           onBackPressed()
       }
        //configure progress dialog
        initialise()

    }
    private fun closeKeyBoard() {
        val view = this.currentFocus

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)

    }

    private fun initialise() {
        //get data
        name = findViewById<View>(R.id.Name) as EditText
        age = findViewById<View>(R.id.age) as EditText
        email = findViewById<View>(R.id.email) as EditText
        password = findViewById<View>(R.id.password) as EditText
        RegisterBtn = findViewById<View>(R.id.RegisterBtn) as Button
        mProgressBar = ProgressDialog(this)
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        //init firebase auth
        mAuth = FirebaseAuth.getInstance()
        //handle click begin signup
        RegisterBtn!!.setOnClickListener {
            //validate data
            createNewAccount()
        }
    }

    private fun createNewAccount() {
        var name = name?.text.toString()
        var age = age?.text.toString()
        var email = email?.text.toString()
        var password = password?.text.toString()

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(age)
            && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)
        ) {  //we create a new user
            val mAuth = FirebaseAuth.getInstance()
            mAuth!!
                .createUserWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                    mProgressBar!!.hide()
                    if (task.isSuccessful) {
                        //Sign in success, Update Ui with the signed-in user’s information
                        Log.d(ContentValues.TAG, "createUserWithEmail:success")
                        val userId = mAuth!!.currentUser!!.uid
                        //Verify Email

                        //Update user profile information
                        val currentUserDb = mDatabaseReference!!.child(userId)
                        currentUserDb.child("name").setValue(name)
                        currentUserDb.child("age").setValue(age)
                        updateUserInfoAndUI()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            this@SignUpActivity, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            //We show the progress bar while the registration is done
            mProgressBar!!.setMessage("Registering User...")
            mProgressBar!!.show()
        } else {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
        }

        if (TextUtils.isEmpty(age)){
            //age isn't entered
            binding.age.error = "Please enter age"
        }
        if (TextUtils.isEmpty(name)){
            //name isn't entered
            binding.Name.error = "Please enter name"
        }
        if (name.length > 50){
            //name length  less than 50
            binding.Name.error ="name must be less then 50 characters "
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //invalid email format
            binding.email.error ="invalid email format"
        }
        if (TextUtils.isEmpty(password)){
            //password isn't entered
            binding.password.error = "Please enterr password"
        }
        if (password.length <6){
            //password length os less than 6
            binding.password.error ="paaswort must atleast 6 characters long"
        }


    }

    private fun updateUserInfoAndUI() {
        //start next activity
        val intent = Intent(this@SignUpActivity, SignUpActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //go back to previous activity , when back button of action bar clicked
        return super.onSupportNavigateUp()
    }
}