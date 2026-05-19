package com.ayu.tugasapi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        progressBar.visibility = View.VISIBLE
        btnLogin.isEnabled = false

        lifecycleScope.launch {
            try {
                val request = LoginRequest(email, password)
                val response = RetrofitClient.apiService.login(request)

                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    if (loginResponse != null && loginResponse.success) {
                        val token = loginResponse.data?.token ?: ""
                        val userName = loginResponse.data?.user?.name ?: "User"

                        // Simpan token ke SharedPreferences
                        val sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                        sharedPref.edit().putString("token", token).apply()

                        // Pindah ke PasienActivity dan kirim nama user
                        val intent = Intent(this@MainActivity, PasienActivity::class.java)
                        intent.putExtra("EXTRA_NAME", userName)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            loginResponse?.message ?: "Login gagal",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Login gagal: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Terjadi kesalahan: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                // Sembunyikan loading
                progressBar.visibility = View.GONE
                btnLogin.isEnabled = true
            }
        }
    }
}