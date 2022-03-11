package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.Outcome
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.persistence.AsteroidDatabase
import timber.log.Timber

class MainFragment : Fragment() {

    private lateinit var asteroidAdapter: AsteroidsAdapter
    private lateinit var fragmentBinding: FragmentMainBinding

    private val viewModel: MainViewModel by lazy {
        val dataSource = AsteroidDatabase.getInstance(requireContext()).asteroidDao
        ViewModelProvider(this, MainViewModelFactory(dataSource)).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = FragmentMainBinding.inflate(inflater)
        fragmentBinding.lifecycleOwner = this
        fragmentBinding.viewModel = viewModel
        fragmentBinding.lifecycleOwner = this

        setupAsteroidsAdapter()
        viewModel.getAsteroidsFromDatabaseByWeek().observe(viewLifecycleOwner, Observer { outcome ->
            Timber.v("Asteroids: $outcome")
            when(outcome.status) {
                Outcome.Status.LOADING -> {
                    fragmentBinding.statusLoadingWheel.visibility = View.VISIBLE
                }
                Outcome.Status.ERROR -> {
                    Timber.e(outcome.error, outcome.message)
                }
                Outcome.Status.SUCCESS -> {
                    fragmentBinding.statusLoadingWheel.visibility = View.GONE
                    asteroidAdapter.submitList(outcome.data)
                }
            }
        })

        fragmentBinding.executePendingBindings()
        setHasOptionsMenu(true)

        return fragmentBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }

    private fun setupAsteroidsAdapter() {
        asteroidAdapter = AsteroidsAdapter(OnAsteroidClickListener { asteroid ->
            findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
        })
        fragmentBinding.asteroidRecycler.adapter = asteroidAdapter
        Timber.v("Adapter for Asteroids.")
    }
}
