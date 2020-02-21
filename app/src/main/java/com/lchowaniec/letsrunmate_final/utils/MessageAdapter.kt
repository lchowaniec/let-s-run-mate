package com.lchowaniec.letsrunmate_final.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.lchowaniec.letsrunmate_final.Models.Message
import com.lchowaniec.letsrunmate_final.R
import kotlinx.android.synthetic.main.single_message_layout.view.*

class MessageAdapter:RecyclerView.Adapter<RecyclerView.ViewHolder>(){
     var messages :List<Message> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MessageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.single_message_layout,parent,false)

        )
    }
    fun submitList(messagesList: List<Message>){
        messages = messagesList
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is MessageViewHolder ->{
                val mAuth = FirebaseAuth.getInstance()
                val mCurrent = mAuth.currentUser!!.uid

                holder.bind(messages.get(position))
                val from = messages.get(position).from
                if(from != mCurrent){
                    holder.mMessage.setBackgroundResource(R.drawable.message_layout_2)
                    holder.mMessage.totalPaddingRight

                }

            }
        }

    }

    override fun getItemCount(): Int {
        return messages.size
    }
    class MessageViewHolder constructor(
        itemView: View
    ):RecyclerView.ViewHolder(itemView){

        val mMessage = itemView.message_textview
        val mPhoto = itemView.message_profile_photo
        fun bind(message: Message){

            mMessage.setText(message.message)


        }

    }




}