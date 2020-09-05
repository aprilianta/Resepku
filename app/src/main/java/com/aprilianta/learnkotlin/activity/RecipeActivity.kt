package com.aprilianta.learnkotlin.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aprilianta.learnkotlin.R
import com.aprilianta.learnkotlin.model.Recipe
import com.aprilianta.learnkotlin.utils.Constant
import com.aprilianta.learnkotlin.viewmodel.RecipeState
import com.aprilianta.learnkotlin.viewmodel.RecipeViewModel
import com.aprilianta.learnkotlin.viewmodel.UserState
import com.aprilianta.learnkotlin.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_recipe.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.content_recipe.*

class RecipeActivity : AppCompatActivity() {
    private lateinit var recipeViewModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        setSupportActionBar(findViewById(R.id.toolbar))
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener { finish() }
        recipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)
        if (isUpdate()){
            tv_title_recipe.setText("Edit Recipe")
            doUpdate()
            recipeViewModel.fetchOneRecipe(Constant.getToken(this), getId().toString())
            recipeViewModel.getOneRecipe().observe(this, Observer {
                fill(it)
            })
        } else {
            tv_title_recipe.setText("Create Recipe")
            doCreate()
        }

        recipeViewModel.getState().observe(this, Observer {
            handleUIState(it)
        })
    }

    private fun isUpdate() = intent.getBooleanExtra("is_update", false)
    private fun getId() = intent.getIntExtra("id",0)

    private fun doUpdate(){
        btnSubmit.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val content = etContent.text.toString().trim()
            if (recipeViewModel.validate(title, content)) {
                recipeViewModel.update(Constant.getToken(this), getId().toString(), title, content)
            }
        }
    }

    private fun doCreate(){
        btnSubmit.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val content = etContent.text.toString().trim()
            if (recipeViewModel.validate(title, content)) {
                recipeViewModel.create(Constant.getToken(this), title, content)
            }
        }
    }

    private fun fill (recipe: Recipe) {
        etTitle.setText(recipe.title)
        etContent.setText(recipe.content)
    }

    private fun handleUIState (it : RecipeState) {
        when(it) {
            is RecipeState.Reset -> {
                setTitleError(null)
                setContentError(null)
            }
            is RecipeState.IsLoading -> isLoading(it.state)
            is RecipeState.Error -> {
                toast(it.err)
                isLoading(false)
            }
            is RecipeState.ShowToast -> toast(it.message)
            is RecipeState.RecipeValidation -> {
                it.title?.let {
                    setTitleError(it)
                }
                it.content?.let {
                    setContentError(it)
                }
            }
            is RecipeState.IsSuccess -> {
                when(it.code) {
                    0 -> {
                        toast("Recipe created.")
                        finish()
                    }
                    1 -> {
                        toast("Recipe updated.")
                        finish()
                    }
                    2 -> {
                        toast("Recipe deleted.")
                        finish()
                    }

                }
            }
        }
    }

    private fun toast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(state: Boolean) {
        if (state) {
            pg_recipe.visibility = View.VISIBLE
            btnSubmit.isEnabled = !state
        }
        else
            pg_recipe.visibility = View.GONE
            btnSubmit.isEnabled = !state
    }

    private fun setTitleError(err : String?) { in_title.error = err }
    private fun setContentError(err : String?) { in_content.error = err }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isUpdate()) {
            menuInflater.inflate(R.menu.menu_recipe, menu)
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                recipeViewModel.delete(Constant.getToken(this@RecipeActivity),
                    getId().toString())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}