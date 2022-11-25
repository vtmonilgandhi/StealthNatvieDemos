package com.stealthmonitoring.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import com.stealthmonitoring.R
import com.stealthmonitoring.databinding.ActivityHomePageBinding
import com.stealthmonitoring.screens.fragments.IncidentsFragment
import com.stealthmonitoring.screens.fragments.LiveViewFragment

class HomePageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomePageBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private val mOnNavigationItemSelectedListener =
        NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.liveView -> {
                    loadFragment(LiveViewFragment(), "FRAGMENT_1")
                    return@OnItemSelectedListener true
                }
                R.id.incidents -> {
                    loadFragment(IncidentsFragment(), "FRAGMENT_2")
                    return@OnItemSelectedListener true
                }
            }
            false
        }

    private  fun loadFragment(fragment: Fragment, tag: String){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.viewPager,fragment,tag)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.apply {
            toggle = ActionBarDrawerToggle(
                this@HomePageActivity,
                binding.drawerLayout,
                R.string.nav_open,
                R.string.nav_close
            )
            binding.drawerLayout.addDrawerListener(toggle)
            toggle.syncState()
            //  toggle.drawerArrowDrawable.color = resources.getColor(R.color.black)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.viewPager,LiveViewFragment(),"FRAGMENT_1")
            transaction.addToBackStack(null)
            transaction.commit()

            binding.navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_account -> {
                        Toast.makeText(
                            this@HomePageActivity,
                            "First Item Clicked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    R.id.nav_incidents -> {
                        /* val fragment: Fragment = IncidentsFragment()
                         val fragmentManager: FragmentManager = supportFragmentManager
                         fragmentManager.beginTransaction()
                             .replace(R.id.dashboard_container, fragment)
                             .commit()*/
                    }
                    R.id.nav_logout -> {
                        val intent = Intent(this@HomePageActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
                true
            }
        }

        binding.bottomNavigationView.setOnItemSelectedListener(
            mOnNavigationItemSelectedListener
        )
    }

    override fun onBackPressed() {
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            true
        }
        return super.onOptionsItemSelected(item)
    }
}