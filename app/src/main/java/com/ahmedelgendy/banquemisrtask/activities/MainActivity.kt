package com.ahmedelgendy.banquemisrtask.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.ahmedelgendy.banquemisrtask.R
import com.ahmedelgendy.banquemisrtask.databinding.ActivityMainBinding
import com.ahmedelgendy.banquemisrtask.general.SharedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController

    var currentDestination: NavDestination? = null
    var navHostFragment: Fragment? = null


    @Inject
    lateinit var sharedPrefHelper: SharedPrefHelper


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)




        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)






        navController = Navigation.findNavController(
            this@MainActivity, R.id.nav_host_fragment_content_main
        )

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)



        binding.bottomNavigation.setupWithNavController(navController)


    }


    private fun getCurrentFragment(): Fragment? {
        return navHostFragment?.childFragmentManager?.fragments?.get(0)
    }


    fun showLoading(show: Boolean) {
        binding.loadingView.visibility = if (show) {
            View.VISIBLE
        } else
            View.GONE
    }


}
