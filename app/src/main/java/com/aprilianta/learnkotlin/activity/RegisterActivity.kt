package com.aprilianta.learnkotlin.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aprilianta.learnkotlin.R
import com.aprilianta.learnkotlin.utils.Constant
import com.aprilianta.learnkotlin.viewmodel.UserState
import com.aprilianta.learnkotlin.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.getState().observer(this, Observer {
            handleUIState(it)
        })
        doRegister()

    }

    private fun doRegister() {
        btnSignUp.setOnClickListener{
            val name = etNameRegister.text.toString().trim()
            val email = etEmailRegister.text.toString().trim()
            val password = etPasswordRegister.text.toString().trim()
            if (userViewModel.validate(name, email, password)) {
                userViewModel.register(name, email, password)
            }
        }
    }

    private fun handleUIState(it: UserState) {
        when(it) {
            is UserState.Reset -> {
                setNameError(null)
                setEmailError(null)
                setPasswordError(null)
            }
            is UserState.Error -> {
                isLoading(false)
                toast(it.err)
            }
            is UserState.ShowToast -> toast(it.message)
            is UserState.IsFailed -> {
                isLoading(false)
                toast(it.message)
            }
            is UserState.UserValidation -> {
                it.name?.let {
                    setNameError(it)
                }
                it.email?.let {
                    setEmailError(it)
                }
                it.password?.let {
                    setPasswordError(it)
                }
            }
            is UserState.IsSuccess -> {
                Constant.setToken(this@RegisterActivity, it.token)
                startActivity(Intent(this@RegisterActivity, MainActivity::class.java).
                apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                }).also { finish() }
            }
            is UserState.IsLoading -> isLoading(it.state)

        }
    }

    private fun toast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(state: Boolean) {
        if (state) {
            pg_register.visibility = View.VISIBLE
            btnSignUp.isEnabled = false
        }
        else {
            pg_register.visibility = View.GONE
            btnSignUp.isEnabled = true
        }
    }

    private fun setNameError(err : String?) { in_name_register.error = err }
    private fun setEmailError(err : String?) { in_email_register.error = err }
    private fun setPasswordError(err : String?) { in_password_register.error = err }
}