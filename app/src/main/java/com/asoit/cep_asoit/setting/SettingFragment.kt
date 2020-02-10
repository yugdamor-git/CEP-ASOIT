package com.asoit.cep_asoit.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.asoit.cep_asoit.R
import com.asoit.cep_asoit.login.LoginActivity
import com.asoit.cep_asoit.utils.Preference
import kotlinx.android.synthetic.main.setting_fragment.*

class SettingFragment : Fragment() {

    companion object {
        fun newInstance() = SettingFragment()
    }


    private lateinit var preference: Preference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.setting_fragment, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        preference = Preference(this.requireContext())
        var clicks = 0
        logout_setting.setOnClickListener {

            clicks++
            Toast.makeText(this.requireContext(), clicks.toString(), Toast.LENGTH_SHORT).show()
            if (clicks >= 3) {
                clicks = 0
                preference.clearAllSharedPref()

                val intent = Intent(activity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                activity?.overridePendingTransition(0, 0)
            }


        }
    }

}
