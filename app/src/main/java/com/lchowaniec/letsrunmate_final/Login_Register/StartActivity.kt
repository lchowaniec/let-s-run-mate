package com.lchowaniec.letsrunmate_final.Login_Register

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lchowaniec.letsrunmate_final.R
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        new_account_btn.setOnClickListener {
            val intent = Intent(this,
                RegisterActivity::class.java)
            startActivity(intent)
        }
        login_btn.setOnClickListener {
            val intent2 = Intent(this,
                LoginActivity::class.java)
            startActivity(intent2)


        }
    }
}

