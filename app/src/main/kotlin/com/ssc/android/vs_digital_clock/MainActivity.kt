package com.ssc.android.vs_digital_clock

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ssc.android.vs_digital_clock.databinding.LayoutMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: LayoutMainBinding? = null
    private val settingFragment = SettingsFragment()
    private val dashBoardFragment = TimeDashBoardFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutMainBinding.inflate(layoutInflater)

        val view = binding?.root
        view?.let {
            setContentView(it)
        }

        replaceFragment(dashBoardFragment)

        binding?.bottomNavigation?.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_item_time_dash_board -> replaceFragment(dashBoardFragment)
                R.id.bottom_item_setting -> replaceFragment(settingFragment)
                else -> replaceFragment(dashBoardFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.id_fragment_container, fragment)
        fragmentTransaction.commit()
    }
}