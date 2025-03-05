package com.ssc.android.vs_digital_clock.ui

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ssc.android.vs_digital_clock.R
import com.ssc.android.vs_digital_clock.TimeDashBoardFragment
import com.ssc.android.vs_digital_clock.databinding.LayoutMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: LayoutMainBinding? = null
    private val settingFragment = SettingsFragment()
    private val dashBoardFragment = TimeDashBoardFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutMainBinding.inflate(layoutInflater)

        binding?.let {
            setSupportActionBar(it.actionBar)
            it.actionBar.inflateMenu(R.menu.menu_action_bar)
        }

        val view = binding?.root
        view?.let {
            setContentView(it)
        }

        initUI()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.id_fragment_container, fragment)
        fragmentTransaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun initUI() {

        replaceFragment(dashBoardFragment)

        binding?.let {

            //setup action bar menu item click listener
            it.actionBar.setOnMenuItemClickListener { actionItem ->
                when (actionItem.itemId) {
                    R.id.action_refresh -> {
                        //TODO put behavior here
                    }

                    R.id.action_language -> {
                        //TODO put behavior here
                    }

                    else -> Unit
                }
                false
            }

            //setup bottom navigation bar select listener.
            it.bottomNavigation.setOnItemSelectedListener { naviItem ->
                when (naviItem.itemId) {
                    R.id.bottom_item_time_dash_board -> replaceFragment(dashBoardFragment)
                    R.id.bottom_item_setting -> replaceFragment(settingFragment)
                    else -> replaceFragment(dashBoardFragment)
                }
                true
            }
        }
    }
}