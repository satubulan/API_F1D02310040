package com.ayu.tugasapi

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class PasienActivity : AppCompatActivity() {

    private lateinit var tvNamaUser: TextView
    private lateinit var rvPasien: RecyclerView
    private lateinit var progressBarPasien: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pasien)

        // Inisialisasi view
        tvNamaUser = findViewById(R.id.tvNamaUser)
        rvPasien = findViewById(R.id.rvPasien)
        progressBarPasien = findViewById(R.id.progressBarPasien)

        // Ambil nama user dari intent
        val userName = intent.getStringExtra("EXTRA_NAME") ?: "User"
        tvNamaUser.text = "Halo, $userName!"

        // Setup RecyclerView
        rvPasien.layoutManager = LinearLayoutManager(this)

        // Ambil token dari SharedPreferences
        val sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val token = sharedPref.getString("token", "") ?: ""

        // Panggil API data pasien
        loadDataPasien(token)
    }

    private fun loadDataPasien(token: String) {
        // Tampilkan loading
        progressBarPasien.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getPasien("Bearer $token")

                if (response.isSuccessful) {
                    val pasienResponse = response.body()

                    if (pasienResponse != null && pasienResponse.success) {
                        val listPasien = pasienResponse.data

                        // Tampilkan data ke RecyclerView
                        val adapter = PasienAdapter(listPasien)
                        rvPasien.adapter = adapter
                    } else {
                        Toast.makeText(
                            this@PasienActivity,
                            pasienResponse?.message ?: "Gagal memuat data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@PasienActivity,
                        "Error: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@PasienActivity,
                    "Terjadi kesalahan: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                // Sembunyikan loading
                progressBarPasien.visibility = View.GONE
            }
        }
    }
}
