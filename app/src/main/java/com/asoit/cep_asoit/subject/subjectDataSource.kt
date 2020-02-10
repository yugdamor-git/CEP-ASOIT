package com.asoit.cep_asoit.subject

import androidx.lifecycle.LiveData

interface subjectDataSource {

    val subjectdata: LiveData<subDetails>


    suspend fun fetchData(subject_id: Int)
}