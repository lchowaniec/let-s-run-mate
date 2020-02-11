package com.lchowaniec.letsrunmate_final.Login_Register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.lchowaniec.letsrunmate_final.MainActivity
import com.lchowaniec.letsrunmate_final.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        val mToolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.login_app_bar)
        setSupportActionBar(mToolbar)
        supportActionBar?.title = "Login\t"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        log_email.requestFocus()
        log_login_btn.setOnClickListener {
            if(!log_email.toString().isEmpty() || !log_password.toString().isEmpty()){
                loginUser(log_email.text.toString().trim(),log_password.text.toString().trim())
                login_progress_bar.visibility = View.VISIBLE
                login_wait.visibility = View.VISIBLE
            }

        }

    }
    fun loginUser(email:String, password:String){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){task->
            if(task.isSuccessful){
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                login_progress_bar.visibility = View.GONE
                login_wait.visibility = View.GONE
                finish()
            }else{
                Toast.makeText(baseContext,"Cannot sing in. Check e-mail and password and try again, ",Toast.LENGTH_SHORT).show()
                login_progress_bar.visibility = View.GONE
                login_wait.visibility = View.GONE

            }

        }

    }
}
