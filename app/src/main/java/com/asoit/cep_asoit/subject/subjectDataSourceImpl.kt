package com.asoit.cep_asoit.subject

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.asoit.cep_asoit.home.homeListner
import com.asoit.cep_asoit.utils.Preference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.URLEncoder
import java.net.UnknownHostException

class subjectDataSourceImpl(val preference: Preference) : subjectDataSource {
    private val _subjectdata = MutableLiveData<subDetails>()
    val HOME_URL = "http://silveroaklms.com/student/show_reports_student_wise.aspx"
    var homeListner: homeListner? = null

    override val subjectdata: LiveData<subDetails>
        get() = _subjectdata

    override suspend fun fetchData(subject_id: Int) {

        try {

            var body = encodeAndGenBody(subject_id)

            val response_sub =
                Jsoup.connect(HOME_URL).header("Content-Type", "application/x-www-form-urlencoded")
                    .cookie("ASP.NET_SessionId", preference.getCookie()).requestBody(body).method(
                    Connection.Method.POST
                ).execute()

            val subject_details = getSubjectDetailsFromTable(response_sub)

            _subjectdata.postValue(subject_details)

        } catch (e: UnknownHostException) {
            withContext(Dispatchers.Main)
            {
                homeListner?.exception("No internet/wifi connection")
            }

        } catch (e: SocketTimeoutException) {
            withContext(Dispatchers.Main)
            {
                homeListner?.exception("Your internet connection is slow")
            }

        } catch (e: IOException) {
            withContext(Dispatchers.Main)
            {
                homeListner?.exception("No internet connection")
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main)
            {
                homeListner?.exception(e.message)
            }
        }


    }

    private fun getPendingQue(subjectDetails: subDetails): ArrayList<Int> {
        val trimed = subjectDetails.pendingQue.trim()
        val array = trimed.dropLast(1).split(',')
        val tempArray = ArrayList<Int>()
        for (i in array) {
            tempArray.add(i.trim().drop(2).toInt())

        }

        return tempArray
    }

    private fun encodeAndGenBody(subjectId: Int): String {
        var viewState = preference.getViewstate()
        var validation = preference.getValidation()
        var viewStateGen = preference.getViewStateGen()

        viewState = encodeToUtf8(viewState!!)
        validation = encodeToUtf8(validation!!)
        viewStateGen = encodeToUtf8(viewStateGen!!)

        var body =
            "__EVENTTARGET=ctl00%24ContentPlaceHolder1%24ddl_subject&__EVENTARGUMENT=&__LASTFOCUS=&__VIEWSTATE=$viewState&__VIEWSTATEGENERATOR=$viewStateGen&__EVENTVALIDATION=$validation&ctl00%24ContentPlaceHolder1%24ddl_subject=$subjectId"

        return body
    }

    private fun getSubjectDetailsFromTable(responseSub: Connection.Response): subDetails {
        val table = responseSub.parse().getElementById("ContentPlaceHolder1_table1")
        val rows = table.select("tr")
        val temparray = ArrayList<String>()
        temparray.clear()
        for (i in rows) {
            val col = i.select("td")


            for (c in col) {
                temparray.add(c.text())
            }

        }

        var details = subDetails(
            temparray[0], temparray[1],
            temparray[2],
            temparray[3],
            temparray[4],
            temparray[5],
            temparray[6]
        )
        Log.d("subject", details.toString())
        return details


    }

    fun encodeToUtf8(data: String): String {
        return URLEncoder.encode(data, "utf-8")
    }

}