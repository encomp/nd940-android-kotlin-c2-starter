package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomappbar.BottomAppBar
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.Outcome
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.persistence.AsteroidDatabase

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

        showProgressBar()
        setupAsteroidsAdapter()
        setUpBottomBar()
        viewModel.getAsteroidsFromDatabaseByWeek().observe(viewLifecycleOwner, Observer { outcome ->
            processOutcome(outcome)
        })

        fragmentBinding.executePendingBindings()
        return fragmentBinding.root
    }

    override fun onResume() {
        super.onResume()
        activity?.findViewById<BottomAppBar>(R.id.bottomAppBar)?.visibility = View.VISIBLE
    }

    private fun setupAsteroidsAdapter() {
        asteroidAdapter = AsteroidsAdapter(OnAsteroidClickListener { asteroid ->
            activity?.findViewById<BottomAppBar>(R.id.bottomAppBar)?.visibility = View.GONE
            findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
        })
        fragmentBinding.asteroidRecycler.adapter = asteroidAdapter
    }

    private fun setUpBottomBar() {
        val bottomAppBar: BottomAppBar? = activity?.findViewById<BottomAppBar>(R.id.bottomAppBar)
        bottomAppBar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.show_today -> {
                    showProgressBar()
                    viewModel.getAsteroidsFromDatabaseByDate()
                        .observe(viewLifecycleOwner, Observer { outcome ->
                            processOutcome(outcome)
                        })
                    return@setOnMenuItemClickListener true
                }
                R.id.show_week -> {
                    showProgressBar()
                    viewModel.getAsteroidsFromDatabaseByWeek()
                        .observe(viewLifecycleOwner, Observer { outcome ->
                            processOutcome(outcome)
                        })
                    return@setOnMenuItemClickListener true
                }
                R.id.show_all -> {
                    showProgressBar()
                    viewModel.getAllAsteroidsFromDatabase()
                        .observe(viewLifecycleOwner, Observer { outcome ->
                            processOutcome(outcome)
                        })
                    return@setOnMenuItemClickListener true
                }
                else -> false
            }
        }
    }

    private fun showProgressBar() {
        fragmentBinding.asteroidRecycler.visibility = View.GONE
        fragmentBinding.statusLoadingWheel.visibility = View.VISIBLE
    }

    private fun processOutcome(outcome: Outcome<List<Asteroid>>) {
        when (outcome.status) {
            Outcome.Status.LOADING -> showProgressBar()
            Outcome.Status.ERROR -> {
                showRecyclerView()
                Toast.makeText(context, outcome.message, Toast.LENGTH_LONG).show()
            }
            Outcome.Status.SUCCESS -> {
                asteroidAdapter.submitList(outcome.data)
                showRecyclerView()
            }
        }
    }

    private fun showRecyclerView() {
        fragmentBinding.statusLoadingWheel.visibility = View.GONE
        fragmentBinding.asteroidRecycler.visibility = View.VISIBLE
    }
}
