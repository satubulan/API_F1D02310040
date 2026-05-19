package com.ayu.tugasapi

data class PasienResponse(
    val success: Boolean,
    val message: String,
    val data: List<Pasien>
)
