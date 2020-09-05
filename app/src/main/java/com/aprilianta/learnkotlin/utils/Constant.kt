package com.aprilianta.learnkotlin.utils

import android.content.Context

class Constant {
    companion object {
        const val API_ENDPOINT = "https://resep-mau.herokuapp.com/"

        fun getToken (context : Context) : String {
            val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
            val token = pref.getString("TOKEN", "undefined")
            return token!!
        }

        fun setToken (context : Context, token : String) {
            val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
            pref.edit().apply {
                putString("TOKEN", token)
                apply()
            }
        }

        fun clearToken (context : Context) {
            val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
            pref.edit().clear().apply()
        }

        fun isValidEmail (email : String) : Boolean =
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

        fun isValidPassword (password : String) : Boolean =
            password.length > 8

    }
}