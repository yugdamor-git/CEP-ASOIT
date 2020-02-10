package com.asoit.cep_asoit.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asoit.cep_asoit.R
import com.asoit.cep_asoit.home.HomeController
import com.asoit.cep_asoit.utils.Preference
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.lang.Exception
import java.net.URLEncoder
import java.net.UnknownHostException

class LoginActivity : AppCompatActivity() {

    val LOGIN_URL = "http://silveroaklms.com/login.aspx"
    lateinit var preference: Preference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        av_login_c.smoothToHide()

        preference = Preference(this)


        btn_login.setOnClickListener {

            av_login_c.smoothToShow()
            val username = tv_username.text.toString()
            val password = tv_password.text.toString()
            if (password.isNullOrEmpty() || username.isNullOrEmpty()) {
                Toast.makeText(this, "Please Enter username and password", Toast.LENGTH_SHORT)
                    .show()
                av_login_c.smoothToHide()
                return@setOnClickListener
            }

            CoroutineScope(IO).launch {

                try {
                    withContext(Main)
                    {

                    }
                    val isLoginSuceess = login(username, password)
                    if (isLoginSuceess) {

                        val intent = Intent(this@LoginActivity, HomeController::class.java)
                        withContext(Main) {

                            val currentTime = System.currentTimeMillis()
                            preference.saveLastCookieUpdated(currentTime)
                            preference.saveLoggedIn(true)

                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            av_login_c.smoothToHide()
                            startActivity(intent)
                        }
                    } else {
                        withContext(Main)
                        {


                            Toast.makeText(
                                this@LoginActivity,
                                "Please Enter Valid Username And Password",
                                Toast.LENGTH_LONG
                            ).show()
                            av_login_c.smoothToHide()
                        }
                    }

                } catch (e: UnknownHostException) {
                    withContext(Main) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Please enable mobile-data or wifi",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } catch (e: Exception) {
                    withContext(Main) {
                        Toast.makeText(this@LoginActivity, e.toString(), Toast.LENGTH_LONG).show()

                    }
                } finally {
                    withContext(Main) {
                        av_login_c.smoothToHide()
                    }


                }


            }

        }


    }


    fun login(username: String, password: String): Boolean {
        var uname = username
        var pwd = password
        //getting html page by get method
        val loginPage = Jsoup.connect(LOGIN_URL).get()

        //parsing data from html page
        var viewStateGen = loginPage.getElementById("__VIEWSTATEGENERATOR").`val`()
        var validation = loginPage.getElementById("__EVENTVALIDATION").`val`()
        var viewState = loginPage.getElementById("__VIEWSTATE").`val`()

        //encoding to utf 8
        validation = encodeToUtf8(validation)
        viewState = encodeToUtf8(viewState)
        uname = encodeToUtf8(username)
        pwd = encodeToUtf8(password)

        var body =
            "__EVENTTARGET=btnlogin&__EVENTARGUMENT=&__VIEWSTATE=$viewState&__VIEWSTATEGENERATOR=C2EE9ABB&__EVENTVALIDATION=$validation&txtusername=$uname&txtpassword=$pwd"

        //performing login
        val performLogin =
            Jsoup.connect(LOGIN_URL).header("Content-Type", "application/x-www-form-urlencoded")
                .requestBody(body).method(Connection.Method.POST).execute()

        if (performLogin.cookies().isEmpty()) {
            Log.d("loginPage", "it is null")

            return false
        } else {
            preference.saveCookie(performLogin.cookies()["ASP.NET_SessionId"])
            preference.saveUsername(username)
            preference.savePassword(password)
            Log.d("loginPage", performLogin.cookies()["ASP.NET_SessionId"])
            return true
        }


    }

    fun encodeToUtf8(data: String): String {
        return URLEncoder.encode(data, "utf-8")
    }

}
