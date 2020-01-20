package com.lchowaniec.letsrunmate_final

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        val mToolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.login_app_bar)
        setSupportActionBar(mToolbar)
        supportActionBar?.setTitle("Login\t")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        log_login_btn.setOnClickListener(){
            if(!log_email.toString().isEmpty() || !log_password.toString().isEmpty()){
                loginUser(log_email.text.toString(),log_password.text.toString())
            }

        }

    }
    fun loginUser(email:String, password:String){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){task->
            if(task.isSuccessful){
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(baseContext,"Cannot sing in. Check e-mail and password and try again, ",Toast.LENGTH_SHORT).show()
            }

        }

    }
}
