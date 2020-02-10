package com.asoit.cep_asoit.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.asoit.cep_asoit.R
import com.asoit.cep_asoit.login.LoginActivity
import com.asoit.cep_asoit.utils.Preference
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.formats.*
import com.wang.avi.AVLoadingIndicatorView
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.native_ad_layout.*
import kotlinx.android.synthetic.main.native_ad_layout.view.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.net.SocketTimeoutException
import java.net.URLEncoder

class HomeFragment : Fragment(), homeListner {


    override fun exception(message: String?) {
        Toast.makeText(this.requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance() = HomeFragment()
    }

    lateinit var adLoader: AdLoader
    private val APP_UNIT_ID = "ca-app-pub-8275774542361644/1075714032"
    private val TEST_ID = "ca-app-pub-3940256099942544/2247696110"
    lateinit var preference: Preference
    lateinit var homedata: homeDataSourceImpl


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        preference = Preference(this.requireContext())


        homedata = homeDataSourceImpl(preference, this.requireContext())
        homedata.homeListner = this

        fetchdata(homedata)

        setupRecyclerView()
        pull_to_refresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                pull_to_refresh.isRefreshing = false
                fetchdata(homedata)

            }

        })
        homedata.homeData.observe(this.viewLifecycleOwner, Observer {

            if (home_recycler != null) {
                home_recycler.adapter = HomeAdapter(this@HomeFragment.requireContext(), it.homedata)
            }
            if (tv_student_name != null) {
                tv_student_name.text = it.name
                preference.saveName(it.name)
            }


        })
        val currentTime = System.currentTimeMillis()
        Log.d("homefrag", currentTime.toString())

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
                    val frameLayout = view?.findViewById<FrameLayout>(R.id.native_ad_home)
                    if (frameLayout != null) {
                        frameLayout.removeAllViews()
                        frameLayout.addView(adView)
                    }

                }
            }

        adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {

                Toast.makeText(
                    this@HomeFragment.requireContext(),
                    "Failed to load native ad: " + errorCode,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())



    }

    private fun loadadd() {
        adLoader.loadAd(AdRequest.Builder().build())

    }

    private fun fetchdata(homedata: homeDataSourceImpl) {
        GlobalScope.launch(IO) {
            withContext(Main)
            {

                activity?.findViewById<AVLoadingIndicatorView>(R.id.av_home_c)?.smoothToShow()

            }
            homedata.fetchHomeData()
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

    private fun setupRecyclerView() {

        home_recycler.apply {
            layoutManager = LinearLayoutManager(this@HomeFragment.requireContext())
            isNestedScrollingEnabled = false
            setHasFixedSize(false)


        }


    }

    override fun navigateToLogin() {
        preference.clearAllSharedPref()
        val intent = Intent(this.requireContext(), LoginActivity::class.java)
        startActivity(intent)
    }


}
