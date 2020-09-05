package com.aprilianta.learnkotlin.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aprilianta.learnkotlin.R
import com.aprilianta.learnkotlin.adapter.RecipeAdapter
import com.aprilianta.learnkotlin.utils.Constant
import com.aprilianta.learnkotlin.viewmodel.RecipeState
import com.aprilianta.learnkotlin.viewmodel.RecipeViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var recipeViewModel : RecipeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isLoggedIn()
        setupRecyclerView()

        recipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)
        recipeViewModel.getRecipes().observe(this, Observer {
            rv_recipe.adapter?.let { adapter ->
                if (adapter is RecipeAdapter) {
                    adapter.setRecipes(it)
                }
            }
        })

        recipeViewModel.getState().observer(this, Observer {
            handleUIState(it)
        })

        fab_recipe.setOnClickListener {
            startActivity(Intent(this@MainActivity, RecipeActivity::class.java).apply {
                putExtra("is_update", false)
            })
        }
    }

    private fun setupRecyclerView() {
        rv_recipe.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = RecipeAdapter(mutableListOf(), this@MainActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        recipeViewModel.fetchAllRecipe(Constant.getToken(this@MainActivity))
    }

    private fun handleUIState (it : RecipeState) {
        when(it) {
            is RecipeState.IsLoading -> isLoading(it.state)
            is RecipeState.Error -> {
                toast(it.err)
                isLoading(false)
            }
        }
    }

    private fun toast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(state: Boolean) {
        if (state)
            pg_loading.visibility = View.VISIBLE
        else
            pg_loading.visibility = View.GONE
    }

    private fun isLoggedIn(){
        if (Constant.getToken(this@MainActivity).equals("undefined")) {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }).also { finish() }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sign_out -> {
                Constant.clearToken(this@MainActivity)
                startActivity(Intent(this@MainActivity, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }).also { finish() }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}