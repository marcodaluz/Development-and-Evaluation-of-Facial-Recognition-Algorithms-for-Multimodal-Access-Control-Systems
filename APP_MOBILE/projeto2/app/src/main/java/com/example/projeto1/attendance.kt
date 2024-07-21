package com.example.projeto1

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto1.Adapter.attendanceAdapter
import com.example.projeto1.api.EndPoints
import com.example.projeto1.api.ServiceBuilder
import com.example.projeto1.api.Attendance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class attendance : AppCompatActivity() {
    private val activityTitle = "Entry Records"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = activityTitle

        // Recupera o userid e horaEntrada passados pela atividade profile
        val userid = intent.getIntExtra("userid", 0) // 0 é um valor default, ajuste conforme necessário
        val horaEntrada = intent.getStringExtra("horaEntrada") ?: "00:00" // "00:00" é um valor default, ajuste conforme necessário

        val requestById = ServiceBuilder.buildService(EndPoints::class.java)
        val callById = requestById.getUserById(userid)

        callById.enqueue(object : Callback<List<Attendance>> {
            override fun onResponse(call: Call<List<Attendance>>, response: Response<List<Attendance>>) {
                if (response.isSuccessful) {
                    findViewById<RecyclerView>(R.id.recView).apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(this@attendance)
                        // Passa a lista de attendances e a hora de entrada para o adaptador
                        adapter = attendanceAdapter(response.body() ?: emptyList(), horaEntrada)
                    }
                } else {
                    Toast.makeText(this@attendance, "Erro ao carregar os dados.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Attendance>>, t: Throwable) {
                Toast.makeText(this@attendance, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
