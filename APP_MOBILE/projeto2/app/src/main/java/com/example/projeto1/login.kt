package com.example.projeto1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.projeto1.api.EndPoints
import com.example.projeto1.api.ServiceBuilder
import com.example.projeto1.dataclasses.LoginRequest
import com.example.projeto1.dataclasses.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast

class login : AppCompatActivity() {
    val apiService = ServiceBuilder.buildService(EndPoints::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnlogin = findViewById<Button>(R.id.btnLogin)

        btnlogin.setOnClickListener {
            // Get user input
            val username = findViewById<EditText>(R.id.user_email).text.toString()
            val password = findViewById<EditText>(R.id.user_password).text.toString()

            // Create login request
            val loginRequest = LoginRequest(username, password)

            // Make login request using Retrofit
            apiService.loginUser(loginRequest).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        // Login successful, handle user response
                        val userResponse = response.body()

                        val intent = Intent(this@login, profile::class.java)
                        val bundle = Bundle()
                        bundle.putSerializable("userProfile", userResponse)
                        intent.putExtras(bundle)
                        startActivity(intent)
                        finish()  // Optional: Finish the current activity to prevent going back to the login screen
                    } else {
                        // Login failed, handle error
                        // TODO: Handle login error, e.g., display an error message
                        showToast("Login failed")
                    }
                }



                override fun onFailure(call: Call<User>, t: Throwable) {
                    // Network or other failure
                    // TODO: Handle failure, e.g., display an error message
                    showToast("Network error")
                }
            })
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
