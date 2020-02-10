package com.asoit.cep_asoit.home

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.asoit.cep_asoit.R
import com.asoit.cep_asoit.utils.Preference
import kotlinx.android.synthetic.main.activity_home_controller.*

class HomeController : AppCompatActivity() {

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_controller)
        navController = findNavController(R.id.fragment)
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
        supportActionBar?.elevation = 5f

    }

    

    override fun onSupportNavigateUp() = navController.navigateUp()
}
