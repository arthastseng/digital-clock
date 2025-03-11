package com.ssc.android.vs_digital_clock.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ssc.android.vs_digital_clock.R
import com.ssc.android.vs_digital_clock.databinding.LayoutMainBinding
import com.ssc.android.vs_digital_clock.ui.dashboard.TimeDashBoardFragment
import com.ssc.android.vs_digital_clock.ui.setting.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var binding: LayoutMainBinding? = null
    private val settingFragment = SettingsFragment()
    private val dashBoardFragment = TimeDashBoardFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutMainBinding.inflate(layoutInflater)

        binding?.let {
            setSupportActionBar(it.actionBar)
            it.actionBar.inflateMenu(R.menu.menu_time_dash_board)
        }

        val view = binding?.root
        view?.let {
            setContentView(it)
        }

        initUI()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        with(fragmentTransaction) {
            replace(R.id.id_fragment_container, fragment)
            addToBackStack(null)
            commit()
        }
    }

    private fun initUI() {
        replaceFragment(dashBoardFragment)

        binding?.let {

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

    companion object {
        private const val REQUEST_CODE = 1100
    }
}