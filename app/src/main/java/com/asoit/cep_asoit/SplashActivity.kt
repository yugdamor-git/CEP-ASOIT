package com.asoit.cep_asoit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.asoit.cep_asoit.home.HomeController
import com.asoit.cep_asoit.login.LoginActivity
import com.asoit.cep_asoit.utils.Preference
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    lateinit var preference: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        preference = Preference(this)
        var intent: Intent? = null

        val job = GlobalScope.launch(Main) {
            if (preference.getLoggedIn() != null) {
                if (preference.getLoggedIn() == true) {
                    intent = Intent(this@SplashActivity, HomeController::class.java)
                    intent!!.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                }
                if (preference.getLoggedIn() == false) {

                    intent = Intent(this@SplashActivity, LoginActivity::class.java)

                    intent!!.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK


                }
            }
            delay(1000)

            startActivity(intent)
            overridePendingTransition(0, 0)

        }


    }
}
