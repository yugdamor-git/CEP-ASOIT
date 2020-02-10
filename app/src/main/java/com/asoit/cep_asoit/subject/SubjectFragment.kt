package com.asoit.cep_asoit.subject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.asoit.cep_asoit.R
import com.asoit.cep_asoit.home.homeListner
import com.asoit.cep_asoit.utils.Preference
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.wang.avi.AVLoadingIndicatorView
import kotlinx.android.synthetic.main.subject_fragment.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Connection
import java.net.URLEncoder

class SubjectFragment : Fragment(), homeListner {
    override fun navigateToLogin() {

    }

    override fun exception(message: String?) {
        Toast.makeText(this.requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance() = SubjectFragment()
    }

    lateinit var adLoader: AdLoader
    lateinit var preference: Preference
    val HOME_URL = "http://silveroaklms.com/student/show_reports_student_wise.aspx"
    private val TEST_ID = "ca-app-pub-3940256099942544/2247696110"
    private val APP_UNIT_ID = "ca-app-pub-8275774542361644/7449550697"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.subject_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        preference = Preference(this.requireContext())
        val dataSourceImpl = subjectDataSourceImpl(preference)
        dataSourceImpl.homeListner = this
        MobileAds.initialize(this.requireContext())

        val builder = AdLoader.Builder(this.requireContext(), APP_UNIT_ID)
            .forUnifiedNativeAd {

                if (isAdded && activity != null) {
                    val adView = layoutInflater.inflate(
                        R.layout.native_ad_layout,
                        null
                    ) as UnifiedNativeAdView

                    //funcation to set all data

                    populateUnifiedNativeAdView(it, adView)

                    subject_ad_frame.removeAllViews()
                    subject_ad_frame.addView(adView)
                }
            }

        adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {

                Toast.makeText(
                    this@SubjectFragment.requireContext(),
                    "Failed to load native ad: " + errorCode,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())

        val subject_name: String?
        val subject_id: Int

        arguments.let {
            val args = SubjectFragmentArgs.fromBundle(it!!)
            subject_id = args.subjectId
            subject_name = args.subjectName

        }

        tv_subjectfarg_name.text = subject_name

        var body = encodeAndGenBody(subject_id)

        subject_pull_to.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                fetchdata(dataSourceImpl, subject_id)

            }

        })





        fetchdata(dataSourceImpl, subject_id)



        setupRecyclerView()

        dataSourceImpl.subjectdata.observe(this.viewLifecycleOwner, Observer {

            val pendingQue = getPendingQue(it)
            if (subject_recycler != null) {
                subject_recycler.adapter =
                    SubjectAdapter(this@SubjectFragment.requireContext(), pendingQue)
            }

            if (cleared_tv != null) {
                cleared_tv.text = it.cleared
                pending_tv.text = it.pending
                total_tv.text = it.total
                percent_clear_tv.text = it.percentCleared + " %"
            }


        })


    }

    private fun loadadd() {
        if (adLoader != null) {
            adLoader.loadAd(AdRequest.Builder().build())
        }

    }

    private fun setupRecyclerView() {

        subject_recycler.apply {
            layoutManager = GridLayoutManager(this@SubjectFragment.requireContext(), 4)
            isNestedScrollingEnabled = false
            setHasFixedSize(false)

        }

    }

    private fun fetchdata(subdata: subjectDataSourceImpl, subjectId: Int) {
        subject_pull_to.isRefreshing = false
        GlobalScope.launch(IO) {
            withContext(Main)
            {

                activity?.findViewById<AVLoadingIndicatorView>(R.id.av_home_c)?.smoothToShow()

            }
            subdata.fetchData(subjectId)
            withContext(Main)
            {

                activity?.findViewById<AVLoadingIndicatorView>(R.id.av_home_c)?.smoothToHide()
            }
        }
    }

    private fun populateUnifiedNativeAdView(
        adFromGoogle: UnifiedNativeAd?,
        myAdView: UnifiedNativeAdView
    ) {


        myAdView.iconView = myAdView.findViewById(R.id.ad_icon)
        myAdView.bodyView = myAdView.findViewById(R.id.ad_body)
        myAdView.priceView = myAdView.findViewById(R.id.ad_price)
        myAdView.starRatingView = myAdView.findViewById(R.id.ad_rating)
        myAdView.storeView = myAdView.findViewById(R.id.ad_store)
        myAdView.advertiserView = myAdView.findViewById(R.id.ad_advertiser)
        myAdView.headlineView = myAdView.findViewById(R.id.ad_headline)
        myAdView.callToActionView = myAdView.findViewById(R.id.ad_call_to_action)

        myAdView.mediaView = myAdView.findViewById<MediaView>(R.id.ad_media_view)

        (myAdView.headlineView as TextView).text = adFromGoogle?.headline
        myAdView.mediaView.setMediaContent(adFromGoogle?.mediaContent)

        Log.d("homefrag", adFromGoogle.toString())

        if (adFromGoogle?.headline == null) {
            myAdView.headlineView.visibility = View.GONE
        } else {

            val headlineView = myAdView.headlineView as TextView
            headlineView.text = adFromGoogle.headline
        }
//............................................................................................................................
        if (adFromGoogle?.body == null) {
            myAdView.bodyView.visibility = View.GONE
        } else {
            val bodyView = myAdView.bodyView as TextView
            bodyView.text = adFromGoogle.body
        }


//..........................................................................................................................
        if (adFromGoogle?.headline == null) {
            myAdView.headlineView.visibility = View.GONE
        } else {
            val headlineView = myAdView.headlineView as TextView
            headlineView.text = adFromGoogle.headline

        }
//..........................................................................................................................
        if (adFromGoogle?.callToAction == null) {
            myAdView.callToActionView.visibility = View.GONE
        } else {
            val calltoActionView = myAdView.callToActionView as Button
            calltoActionView.text = adFromGoogle.callToAction
        }
//..........................................................................................................................
        if (adFromGoogle?.icon == null) {
            myAdView.iconView.visibility = View.GONE
        } else {
            val iconView = myAdView.iconView as ImageView
            iconView.setImageDrawable(adFromGoogle.icon.drawable)

        }

//..........................................................................................................................
        if (adFromGoogle?.price == null) {
            myAdView.priceView.visibility = View.GONE
        } else {
            val priceView = myAdView.priceView as TextView
            priceView.text = adFromGoogle.price
        }
//..........................................................................................................................
        if (adFromGoogle?.starRating == null) {
            myAdView.starRatingView.visibility = View.GONE
        } else {
            val ratingView = myAdView.starRatingView as RatingBar
            ratingView.rating = adFromGoogle.starRating.toFloat()

        }
//..........................................................................................................................
        if (adFromGoogle?.store == null) {
            myAdView.storeView.visibility = View.GONE
        } else {
            val storeView = myAdView.storeView as TextView
            storeView.text = adFromGoogle.store

        }

//..........................................................................................................................
        if (adFromGoogle?.advertiser == null) {
            myAdView.advertiserView.visibility = View.GONE
            myAdView.findViewById<TextView>(R.id.sponsored_by).visibility = View.GONE
        } else {
            val advertiserView = myAdView.advertiserView as TextView
            advertiserView.text = adFromGoogle.advertiser
            myAdView.findViewById<TextView>(R.id.sponsored_by).text = "Sponsored by "+adFromGoogle.advertiser

        }


        myAdView.setNativeAd(adFromGoogle)
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
