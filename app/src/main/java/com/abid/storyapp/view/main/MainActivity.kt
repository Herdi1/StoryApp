package com.abid.storyapp.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.abid.storyapp.R
import com.abid.storyapp.data.di.Injection
import com.abid.storyapp.data.response.ListStoryItem
import com.abid.storyapp.data.response.Story
import com.abid.storyapp.databinding.ActivityMainBinding
import com.abid.storyapp.view.ViewModelFactory
import com.abid.storyapp.view.detail.DetailActivity
import com.abid.storyapp.view.login.LoginActivity
import com.abid.storyapp.view.maps.MapsActivity
import com.abid.storyapp.view.upload.UploadActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>{
        ViewModelFactory(applicationContext)
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.show()

        viewModel.isLoading.observe(this){
            showLoading(it)
        }

        var token = ""

        viewModel.getSession().observe(this){ user ->
            if(!user.isLogin){
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }else{
                token = user.token
                val stories = viewModel.getStory(token)
                if(stories == null) Toast.makeText(this@MainActivity, "Cannot retrieve story data", Toast.LENGTH_SHORT).show()
            }
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStories.addItemDecoration(itemDecoration)

        viewModel.listItem.observe(this){ storiesList ->
            setStories(storiesList)
        }


        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this@MainActivity, UploadActivity::class.java)
            intent.putExtra("token", token)
            startActivity(intent)
        }
    }



    private fun setStories(story: List<ListStoryItem>){
        binding.rvStories.layoutManager = LinearLayoutManager(this)

        val adapter = StoryAdapter{ storyClicked ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("id", storyClicked.id)

            startActivity(intent)
        }

        binding.rvStories.adapter = adapter
        adapter.submitList(story)
//        viewModel.insertStory(story).observe(this, {
//            adapter.submitData(lifecycle, it)
//        })
        //viewModel.insertStory(story)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_logout -> {
                viewModel.logout()
            }
            R.id.action_maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(isLoading: Boolean){
        if(isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.INVISIBLE
        }
    }
}