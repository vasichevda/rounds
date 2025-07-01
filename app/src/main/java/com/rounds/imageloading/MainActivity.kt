package com.rounds.imageloading

import ImageAdapter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rounds.imageloading.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageAdapter: ImageAdapter
    private var placeholderDrawable: Drawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        placeholderDrawable =
            ContextCompat.getDrawable(this, R.drawable.ic_placeholder)

        setupRecyclerView()
        observeViewModel()

        if (savedInstanceState == null) {
            viewModel.loadImages()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadImages()
        }
    }

    private fun setupRecyclerView() {
        imageAdapter = ImageAdapter(viewModel.getImageLoader(), placeholderDrawable)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = imageAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewModel.images.observe(this) { images ->
            images?.let { imageAdapter.submitList(it) }
            binding.swipeRefreshLayout.isRefreshing = false
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility =
                if (isLoading && !binding.swipeRefreshLayout.isRefreshing) View.VISIBLE else View.GONE
            if (!isLoading) {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }
}
