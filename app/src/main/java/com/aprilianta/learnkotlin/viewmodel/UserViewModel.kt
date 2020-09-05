package com.aprilianta.learnkotlin.viewmodel

import androidx.lifecycle.ViewModel
import com.aprilianta.learnkotlin.model.User
import com.aprilianta.learnkotlin.utils.Constant
import com.aprilianta.learnkotlin.utils.SingleLiveEvent
import com.aprilianta.learnkotlin.utils.WrappedResponse
import com.aprilianta.learnkotlin.webservice.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel : ViewModel() {
    private var state : SingleLiveEvent<UserState> = SingleLiveEvent()
    private var api = ApiClient.instance()

    fun validate (name: String?, email: String, password: String) : Boolean {
        state.value = UserState.Reset
        if (name != null) {
            if (name.isEmpty()) {
                state.value = UserState.ShowToast("Please fill column name")
                return false
            }
            if (name.length < 3){
                state.value = UserState.UserValidation(name = "Name must be contain more characters")
                return false
            }
        }

        if (email.isEmpty() || password.isEmpty()){
            state.value = UserState.ShowToast("Please fill all column")
            return false
        }

        if (!Constant.isValidEmail(email)){
            state.value = UserState.UserValidation(email = "Invalid email")
            return false
        }

        if (!Constant.isValidPassword(password)){
            state.value = UserState.UserValidation(password = "Invalid password")
            return false
        }
        return true
    }

    fun login (email: String, password: String){
        state.value = UserState.IsLoading(true)
        api.login(email, password).enqueue(object : Callback<WrappedResponse<User>>{
            override fun onResponse(
                call: Call<WrappedResponse<User>>,
                response: Response<WrappedResponse<User>>
            ) {
                if (response.isSuccessful){
                    val body = response.body() as WrappedResponse<User>
                    if (body.status.equals("1")){
                        state.value = UserState.IsSuccess("Bearer ${body.data!!.api_token}")
                    } else {
                        state.value = UserState.IsFailed("Login failed.")
                    }
                } else {
                    state.value = UserState.Error("Something went wrong.")
                }
                state.value = UserState.IsLoading(false)
            }

            override fun onFailure(call: Call<WrappedResponse<User>>, t: Throwable) {
                state.value = UserState.Error(t.message)
            }

        })
    }

    fun register (name: String, email: String, password: String){
        state.value = UserState.IsLoading(true)
        api.register(name, email, password).enqueue(object : Callback<WrappedResponse<User>>{
            override fun onResponse(
                call: Call<WrappedResponse<User>>,
                response: Response<WrappedResponse<User>>
            ) {
                if (response.isSuccessful){
                    val body = response.body() as WrappedResponse<User>
                    if (body.status.equals("1")){
                        state.value = UserState.IsSuccess("Bearer ${body.data!!.api_token}")
                    } else {
                        state.value = UserState.IsFailed("Register failed.")
                    }
                } else {
                    state.value = UserState.Error("Something went wrong.")
                }
                state.value = UserState.IsLoading(false)
            }

            override fun onFailure(call: Call<WrappedResponse<User>>, t: Throwable) {
                state.value = UserState.Error(t.message)
            }

        })
    }

    fun getState() = state

}

sealed class UserState {
    data class ShowToast (var message : String) : UserState()
    data class IsLoading (var state : Boolean = false) : UserState()
    data class UserValidation (var name : String ? = null,
                               var email : String ? = null,
                               var password : String ? = null) : UserState()
    data class Error (var err : String ?) : UserState()
    data class IsSuccess (var token : String) : UserState()
    data class IsFailed (var message : String) : UserState()
    object Reset : UserState()
}