package com.aprilianta.learnkotlin.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aprilianta.learnkotlin.model.Recipe
import com.aprilianta.learnkotlin.utils.Constant
import com.aprilianta.learnkotlin.utils.SingleLiveEvent
import com.aprilianta.learnkotlin.utils.WrappedListResponse
import com.aprilianta.learnkotlin.utils.WrappedResponse
import com.aprilianta.learnkotlin.webservice.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeViewModel: ViewModel() {
    private var recipes = MutableLiveData<List<Recipe>>()
    private var recipe = MutableLiveData<Recipe>()
    private var state : SingleLiveEvent<RecipeState> = SingleLiveEvent()
    private var api = ApiClient.instance()

    fun validate (title: String, content: String) : Boolean{
        state.value = RecipeState.Reset
        if (title.isEmpty() || content.isEmpty()) {
            state.value = RecipeState.ShowToast("Please fill the column")
            return false
        }

        if (title.length < 10){
            state.value = RecipeState.RecipeValidation(title = "Title must be contain more characters")
            return false
        }

        if (content.length < 10){
            state.value = RecipeState.RecipeValidation(content = "Content must be contain more characters")
            return false
        }
        return true
    }

    fun fetchAllRecipe (token : String) {
        state.value = RecipeState.IsLoading(true)
        api.allRecipe(token).enqueue(object : Callback<WrappedListResponse<Recipe>>{
            override fun onResponse(
                call: Call<WrappedListResponse<Recipe>>,
                response: Response<WrappedListResponse<Recipe>>
            ) {
                if (response.isSuccessful) {
                    val body = response.body() as WrappedListResponse<Recipe>
                    if (body.status.equals("1")) {
                        val r = body.data
                        recipes.postValue(r)
                    }
                } else {
                    state.value = RecipeState.Error("Something went wrong.")
                }
                state.value = RecipeState.IsLoading(false)
            }

            override fun onFailure(call: Call<WrappedListResponse<Recipe>>, t: Throwable) {
                state.value = RecipeState.Error(t.message)
            }
        })
    }

    fun fetchOneRecipe (token: String, id: String) {
        state.value = RecipeState.IsLoading(true)
        api.getOneRecipe(token, id).enqueue(object : Callback<WrappedResponse<Recipe>>{
            override fun onResponse(
                call: Call<WrappedResponse<Recipe>>,
                response: Response<WrappedResponse<Recipe>>
            ) {
                if (response.isSuccessful) {
                    val body = response.body() as WrappedResponse<Recipe>
                    if (body.status.equals("1")) {
                        val r = body.data
                        recipe.postValue(r)
                    }
                } else {
                    state.value = RecipeState.Error("Something went wrong.")
                }
                state.value = RecipeState.IsLoading(false)
            }

            override fun onFailure(call: Call<WrappedResponse<Recipe>>, t: Throwable) {
                state.value = RecipeState.Error(t.message)
            }
        })
    }

    fun create (token: String, title: String, content: String) {
        state.value = RecipeState.IsLoading(true)
        api.createRecipe(token, title, content).enqueue(object : Callback<WrappedResponse<Recipe>>{
            override fun onResponse(
                call: Call<WrappedResponse<Recipe>>,
                response: Response<WrappedResponse<Recipe>>
            ) {
                if (response.isSuccessful) {
                    val body = response.body() as WrappedResponse<Recipe>
                    if (body.status.equals("1")) {
                        // 0 is success create
                        // 1 is success update
                        // 2 is success delete
                        state.value = RecipeState.IsSuccess(0)
                    } else {
                        state.value = RecipeState.Error("Error when create recipe.")
                    }
                } else {
                    state.value = RecipeState.Error("Something went wrong.")
                }
                state.value = RecipeState.IsLoading(false)
            }

            override fun onFailure(call: Call<WrappedResponse<Recipe>>, t: Throwable) {
                state.value = RecipeState.Error(t.message)
            }
        })
    }

    fun update (token: String, id: String, title: String, content: String) {
        state.value = RecipeState.IsLoading(true)
        api.updateRecipe(token, id, title, content).enqueue(object : Callback<WrappedResponse<Recipe>>{
            override fun onResponse(
                call: Call<WrappedResponse<Recipe>>,
                response: Response<WrappedResponse<Recipe>>
            ) {
                if (response.isSuccessful) {
                    val body = response.body() as WrappedResponse<Recipe>
                    if (body.status.equals("1")) {
                        // 0 is success create
                        // 1 is success update
                        // 2 is success delete
                        state.value = RecipeState.IsSuccess(1)
                    } else {
                        state.value = RecipeState.Error("Error when update recipe.")
                    }
                } else {
                    state.value = RecipeState.Error("Something went wrong.")
                }
                state.value = RecipeState.IsLoading(false)
            }

            override fun onFailure(call: Call<WrappedResponse<Recipe>>, t: Throwable) {
                state.value = RecipeState.Error(t.message)
            }
        })
    }

    fun delete (token: String, id: String) {
        state.value = RecipeState.IsLoading(true)
        api.deleteRecipe(token, id).enqueue(object : Callback<WrappedResponse<Recipe>>{
            override fun onResponse(
                call: Call<WrappedResponse<Recipe>>,
                response: Response<WrappedResponse<Recipe>>
            ) {
                if (response.isSuccessful) {
                    val body = response.body() as WrappedResponse<Recipe>
                    if (body.status.equals("1")) {
                        // 0 is success create
                        // 1 is success update
                        // 2 is success delete
                        state.value = RecipeState.IsSuccess(2)
                    } else {
                        state.value = RecipeState.Error("Error when delete recipe.")
                    }
                } else {
                    state.value = RecipeState.Error("Something went wrong.")
                }
                state.value = RecipeState.IsLoading(false)
            }

            override fun onFailure(call: Call<WrappedResponse<Recipe>>, t: Throwable) {
                state.value = RecipeState.Error(t.message)
            }
        })
    }

    fun getRecipes() = recipes
    fun getOneRecipe() = recipe
    fun getState() = state
}

sealed class RecipeState {
    data class ShowToast (var message : String) : RecipeState()
    data class IsLoading (var state : Boolean = false) : RecipeState()
    data class RecipeValidation (var title : String ? = null,
                                var content : String ? = null) : RecipeState()
    data class Error (var err : String ?) : RecipeState()
    data class IsSuccess (var code : Int ?= null) : RecipeState()
    object Reset : RecipeState()
}