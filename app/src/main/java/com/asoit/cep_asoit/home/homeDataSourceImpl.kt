package com.asoit.cep_asoit.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.asoit.cep_asoit.subject.subject
import com.asoit.cep_asoit.utils.Preference
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.URLEncoder
import java.net.UnknownHostException

class homeDataSourceImpl(val preference: Preference, val context: Context) : homeDataSource {

    val LOGIN_URL = "http://silveroaklms.com/login.aspx"
    val HOME_URL = "http://silveroaklms.com/student/show_reports_student_wise.aspx"
    var homeListner: homeListner? = null
    private val _homedata = MutableLiveData<HomeData>()
    override val homeData: LiveData<HomeData>
        get() = _homedata

    override suspend fun fetchHomeData() {

        try {
            val uname = preference.getUsername()
            val pass = preference.getPassword()

            if (cookieExpired()) {

                validateUserAndUpdateCookie(uname, pass)
            }

            //get Home Screen Data
            val HomePage =
                Jsoup.connect(HOME_URL).cookie("ASP.NET_SessionId", preference.getCookie()).get()

            var viewStateGen = HomePage.getElementById("__VIEWSTATEGENERATOR").`val`()
            var validation = HomePage.getElementById("__EVENTVALIDATION").`val`()
            var viewState = HomePage.getElementById("__VIEWSTATE").`val`()


            //save it
            preference.saveViewstate(viewState)
            preference.saveValidation(validation)
            preference.saveViewStateGen(viewStateGen)

            //check if user data is updated or not..if not then find it and save it
            if (preference.userFragUpdated() == false) {
                var institute = HomePage.getElementById("ContentPlaceHolder1_ddl_institute").text()
                var branch = HomePage.getElementById("ContentPlaceHolder1_ddl_branch").text()
                var sem = HomePage.getElementById("ContentPlaceHolder1_ddl_semester").text()
                var division = HomePage.getElementById("ContentPlaceHolder1_ddl_division").text()

                preference.saveInstitute(institute)
                preference.saveBranch(branch)
                preference.saveSem(sem)
                preference.saveDivision(division)

                preference.saveUserFrag(true)
            }


            var list = ArrayList<subject>()
            val elements = HomePage.select("#ContentPlaceHolder1_ddl_subject")[0].children()
            for (i in elements) {
                if (i.text().toLowerCase() != "select subject") {
                    val id = i.attr("value").toString().toInt()
                    Log.d("viewstate", i.attr("value").toString() + i.text())
                    list.add(subject(i.text(), id))
                }
            }

            val name = HomePage.getElementById("ContentPlaceHolder1_welcome").text().drop(10)

            _homedata.postValue(HomeData(name, list))
        } catch (e: UnknownHostException) {
            withContext(Main)
            {
                homeListner?.exception("No internet/wifi connection")
            }

        } catch (e: SocketTimeoutException) {
            withContext(Main)
            {
                homeListner?.exception("Your internet connection is slow")
            }

        } catch (e: IOException) {
            withContext(Main)
            {
                homeListner?.exception("No internet connection")
            }

        } catch (e: Exception) {
            withContext(Main)
            {
                homeListner?.exception(e.message)
            }
        }


    }

    private fun cookieExpired(): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastupdated = preference.getLastCookieUpdated()
        if (lastupdated != null) {
            return (currentTime - lastupdated) > 600000

        } else {
            return true
        }
    }


    @Throws(SocketTimeoutException::class)
    suspend fun validateUserAndUpdateCookie(uname: String?, pass: String?) {
        if (!uname.isNullOrEmpty() && !pass.isNullOrEmpty()) {


            val response = login(uname, pass)
            if (response != true) {
                //password has been changed
                //navigate user back to login screen
                homeListner?.navigateToLogin()
            }


        } else {
            //return to home
            //navigate user back to login screen
            homeListner?.navigateToLogin()
        }
    }

    @Throws(SocketTimeoutException::class)
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
                .requestBody(body).method(
                Connection.Method.POST
            ).execute()

        if (performLogin.cookies().isEmpty()) {
            Log.d("loginPage", "it is null")

            return false
        } else {
            preference.saveCookie(performLogin.cookies()["ASP.NET_SessionId"])
            ///save Time stamp
            val currentTime = System.currentTimeMillis()
            preference.saveLastCookieUpdated(currentTime)

            Log.d("loginPage", performLogin.cookies()["ASP.NET_SessionId"])
            return true
        }


    }

    fun encodeToUtf8(data: String): String {
        return URLEncoder.encode(data, "utf-8")
    }


}