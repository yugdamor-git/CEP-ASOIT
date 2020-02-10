package com.asoit.cep_asoit.subject

//ENROLL NO	NAME	CLEARED	PENDING	TOTAL	% CLEARED	PENDING QUE.
data class subDetails(
    var en_no: String,
    var name: String,
    var cleared: String,
    var pending: String,
    var total: String,
    var percentCleared: String,
    var pendingQue: String
)