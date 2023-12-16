package com.abid.storyapp.view.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.abid.storyapp.data.response.DetailResponse
import com.abid.storyapp.databinding.ActivityDetailBinding
import com.abid.storyapp.view.ViewModelFactory
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var detailViewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detailViewModel = obtainViewModel(this@DetailActivity)

        var token = ""
        val id = intent.getStringExtra("id")
        detailViewModel.getSession().observe(this){ session ->
            if(session.isLogin){
                token = session.token
                if(id != null){
                    val stories = token?.let { detailViewModel.getStoryDetail(id, it) }
                    if(stories == true) Toast.makeText(this@DetailActivity, "Cannot retrieve story data", Toast.LENGTH_SHORT).show()
                }
            }
        }

        detailViewModel.detailStory.observe(this){ story ->
            setDetailStory(story)
        }

        detailViewModel.isLoading.observe(this){
            showLoading(it)
        }
    }

    private fun setDetailStory(story: DetailResponse){
        binding.tvDetailName.text = story.story?.name
        binding.tvDetailDescription.text = story.story?.description
        Glide.with(binding.root.context)
            .load(story.story?.photoUrl)
            .into(binding.ivDetailPhoto)
    }

    private fun showLoading(isLoading: Boolean){
        if(isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): DetailViewModel {
        val factory = ViewModelFactory(applicationContext)
        return ViewModelProvider(activity, factory).get(DetailViewModel::class.java)
    }
}