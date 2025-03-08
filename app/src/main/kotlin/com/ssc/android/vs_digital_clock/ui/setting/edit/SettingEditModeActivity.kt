package com.ssc.android.vs_digital_clock.ui.setting.edit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ssc.android.vs_digital_clock.R
import com.ssc.android.vs_digital_clock.databinding.LayoutMainBinding
import com.ssc.android.vs_digital_clock.databinding.LayoutSettingEditModeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingEditModeActivity : AppCompatActivity() {
    private var binding: LayoutSettingEditModeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutSettingEditModeBinding.inflate(layoutInflater)

        val view = binding?.root
        view?.let {
            setContentView(it)
        }
        addFragment(SettingEditModeFragment())
    }

    private fun addFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.id_fragment_container, fragment)
        fragmentTransaction.commit()
    }
}