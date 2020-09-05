package com.aprilianta.learnkotlin.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aprilianta.learnkotlin.R
import com.aprilianta.learnkotlin.model.User
import com.aprilianta.learnkotlin.utils.Constant
import com.aprilianta.learnkotlin.viewmodel.RecipeState
import com.aprilianta.learnkotlin.viewmodel.RecipeViewModel
import com.aprilianta.learnkotlin.viewmodel.UserState
import com.aprilianta.learnkotlin.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.getState().observer(this, Observer {
            handleUIState(it)
        })
        doLogin()
        btnRegister.setOnClickListener{
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
    }

    private fun doLogin() {
        btnLogin.setOnClickListener{
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (userViewModel.validate(null, email, password)) {
                userViewModel.login(email, password)
            }
        }
    }

    private fun handleUIState(it: UserState) {
        when(it) {
            is UserState.Reset -> {
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
                it.email?.let {
                    setEmailError(it)
                }
                it.password?.let {
                    setPasswordError(it)
                }
            }
            is UserState.IsSuccess -> {
                Constant.setToken(this@LoginActivity, it.token)
                startActivity(Intent(this@LoginActivity, MainActivity::class.java)).also { finish() }
            }
            is UserState.IsLoading -> isLoading(it.state)

        }
    }

    private fun toast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(state: Boolean) {
        if (state) {
            pg_login.visibility = View.VISIBLE
            btnLogin.isEnabled = false
            btnRegister.isEnabled = false
        }
        else {
            pg_login.visibility = View.GONE
            btnLogin.isEnabled = true
            btnRegister.isEnabled = true
        }
    }

    private fun setEmailError(err : String?) { in_email.error = err }
    private fun setPasswordError(err : String?) { in_password.error = err }

}