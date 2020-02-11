package com.lchowaniec.letsrunmate_final.Login_Register

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.lchowaniec.letsrunmate_final.MainActivity
import com.lchowaniec.letsrunmate_final.R
import com.lchowaniec.letsrunmate_final.utils.FirebaseHelper
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {
    //firebase auth
    private lateinit var mAuth: FirebaseAuth
    private val mContext = baseContext
    private lateinit var email:String
    private lateinit var myRef:DatabaseReference
    private lateinit var mFirebaseDatabase:FirebaseDatabase
    private lateinit var user:FirebaseUser

    //vars
    private var add:String =""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val mToolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.register_app_bar)
        setSupportActionBar(mToolbar)
        supportActionBar?.title = "Create account\t"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        //firebase auth
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        myRef = mFirebaseDatabase.reference

        create_acc_btn.setOnClickListener {
            var username = reg_username.text.toString().trim()
            val email = reg_email.text.toString().trim()
            val password = reg_password.text.toString().trim()
            register_user(username, email, password)
            register_progress_bar.visibility = View.VISIBLE
            register_wait.visibility = View.VISIBLE
        }

    }










    private fun register_user( username: String, email: String, password: String) {
        var usern = username

        if (reg_username.text.toString().isEmpty()){
            Toast.makeText(baseContext,"Please enter username",Toast.LENGTH_SHORT).show()
            reg_username.requestFocus()
            register_progress_bar.visibility = View.GONE
            register_wait.visibility = View.GONE
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(reg_email.text.toString()).matches()){
            Toast.makeText(baseContext,"Please enter valid e-mail adress!",Toast.LENGTH_SHORT).show()
            reg_email.requestFocus()
            register_progress_bar.visibility = View.GONE
            register_wait.visibility = View.GONE
            return
        }
        if(reg_password.text.toString().isEmpty()){
            Toast.makeText(baseContext,"Please enter password",Toast.LENGTH_SHORT).show()
            reg_password.requestFocus()
            register_progress_bar.visibility = View.GONE
            register_wait.visibility = View.GONE
            return
        }
        reg_username.requestFocus()


        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                user = mAuth.currentUser!!
                val mFirebaseDatabase = FirebaseDatabase.getInstance()
                val myRef = mFirebaseDatabase.reference
                myRef.addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        isExists(username)


                }})}



            if (task.isSuccessful) {
                Toast.makeText(applicationContext,"Sign up success. Please confirm your e-mail",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                register_progress_bar.visibility = View.GONE
                register_wait.visibility = View.GONE
                finish()
            }else {
                Toast.makeText(baseContext, "Sign up failed. Try Again", Toast.LENGTH_SHORT).show()
                register_progress_bar.visibility = View.GONE
                register_wait.visibility = View.GONE
            }
        }


    }

    private fun isExists(username:String) {
        val ref = FirebaseDatabase.getInstance().reference
        val query = ref.child(getString(R.string.firebase_users))
            .orderByChild(getString(R.string.username_firebase))
            .equalTo(username)
        query.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {

                for(ds: DataSnapshot in p0.children){
                    if(ds.exists()){
                        add = myRef.push().key!!.substring(5,10)


                    }

                }

                val usern = username + add

                FirebaseHelper(applicationContext).addNewUser(email,usern,"Add your own description here, mate !","")
            }
            })}


        }





