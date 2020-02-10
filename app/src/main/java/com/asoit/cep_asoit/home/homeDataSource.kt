package com.asoit.cep_asoit.home

import androidx.lifecycle.LiveData

interface homeDataSource {

    val homeData: LiveData<HomeData>

    suspend fun fetchHomeData()

}