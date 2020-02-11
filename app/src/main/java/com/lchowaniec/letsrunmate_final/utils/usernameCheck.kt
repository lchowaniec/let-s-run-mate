package com.lchowaniec.letsrunmate_final.utils

class usernameCheck{

    open fun changeUsername(username:String):String{
        return username.replace(" ",".")
    }
}