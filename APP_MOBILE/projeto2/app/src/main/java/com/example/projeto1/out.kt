package com.example.projeto1

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto1.Adapter.attendanceAdapter
import com.example.projeto1.Adapter.outAdapter
import com.example.projeto1.api.EndPoints
import com.example.projeto1.api.ServiceBuilder
import com.example.projeto1.api.Out
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class out : AppCompatActivity() {
    private val activityTitle = "Exit Records"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_out)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = activityTitle

        // Recupera o userid e horaEntrada passados pela atividade profile
        val userid = intent.getIntExtra("userid", 0) // 0 é um valor default, ajuste conforme necessário
        val horaSaida = intent.getStringExtra("horaSaida") ?: "00:00" // "00:00" é um valor default, ajuste conforme necessário

        val requestById = ServiceBuilder.buildService(EndPoints::class.java)
        val callById = requestById.getOutById(userid)

        callById.enqueue(object : Callback<List<Out>> {
            override fun onResponse(call: Call<List<Out>>, response: Response<List<Out>>) {
                if (response.isSuccessful) {
                    findViewById<RecyclerView>(R.id.recView2).apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(this@out)
                        // Passa a lista de attendances e a hora de entrada para o adaptador
                        adapter = outAdapter(response.body() ?: emptyList(), horaSaida)
                    }
                } else {
                    Toast.makeText(this@out, "Erro ao carregar os dados.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Out>>, t: Throwable) {
                Toast.makeText(this@out, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
