package com.lchowaniec.letsrunmate_final.Chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lchowaniec.letsrunmate_final.R

class ChatActivity : AppCompatActivity() {
    val ACTIVITY_NUM = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        overridePendingTransition(
            R.anim.fade_in,
            R.anim.fade_out
        )

        val fragment = ChatFragment()
        val manager = supportFragmentManager.beginTransaction()
        val transaction = manager.replace(R.id.chat_container,fragment)
        transaction.commit()
    }



}
