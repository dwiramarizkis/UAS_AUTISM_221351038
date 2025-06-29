package com.example.autism_221351038

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.autism_221351038.ui.DatasetFeaturesFragment
import com.example.autism_221351038.ui.simulation.SimulationFragment
import com.example.autism_221351038.ui.DatasetFragment
import com.example.autism_221351038.ui.ModelArchitectureFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, AboutFragment())
            }
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragment_container, AboutFragment())
                    }
                    true
                }
                R.id.nav_simulation -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragment_container, SimulationFragment())
                    }
                    true
                }
                R.id.nav_dataset -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragment_container, DatasetFragment())
                    }
                    true
                }
                R.id.nav_features -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragment_container, DatasetFeaturesFragment())
                    }
                    true
                }
                R.id.nav_model_architecture -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragment_container, ModelArchitectureFragment())
                    }
                    true
                }
                else -> false
            }
        }
    }
}