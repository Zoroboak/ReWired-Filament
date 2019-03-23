package com.sazerotwo.rewiredfilament.ui

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.sazerotwo.rewiredfilament.R

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            navigationItemSelected(menuItem)
        }

        navigationItemSelected(navigationView.menu.getItem(0))
    }

    private fun navigationItemSelected(menuItem: MenuItem): Boolean {
        // set item as selected to persist highlight
        menuItem.isChecked = true

        when (menuItem.itemId) {
            R.id.nav_map -> {
                val fragment = MapFragment()
                fragment.myContext = this@MainActivity
                supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()
            }
            R.id.nav_reward -> supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer,  RewardsFragment()).commit()
            R.id.nav_login -> supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer,  LoginFragment()).commit()
        }

        // close drawer when item is tapped
        drawerLayout.closeDrawers()

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
