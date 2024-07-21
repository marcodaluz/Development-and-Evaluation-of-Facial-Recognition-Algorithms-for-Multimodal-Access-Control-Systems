package com.example.projeto1

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto1.Adapter.attendanceAdapter
import com.example.projeto1.api.Attendance
import com.example.projeto1.api.EndPoints
import com.example.projeto1.api.RoomAcess
import com.example.projeto1.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import roomAcessAdapter

class roomsAcess : AppCompatActivity() {
    private val activityTitle = "Room Acess"
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rooms_acess)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = activityTitle

        recyclerView = findViewById<RecyclerView>(R.id.recView3)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val userid = intent.getIntExtra("userid", 0)
        val horaEntrada = intent.getStringExtra("horaEntrada") ?: "00:00"


        val requestById = ServiceBuilder.buildService(EndPoints::class.java)
        val callById = requestById.getRoomAcessById(userid)

        callById.enqueue(object : Callback<List<RoomAcess>> {
            override fun onResponse(call: Call<List<RoomAcess>>, response: Response<List<RoomAcess>>) {
                if (response.isSuccessful) {
                    // Atualiza o Adapter com os dados recebidos
                    recyclerView.adapter = roomAcessAdapter(response.body() ?: emptyList(), horaEntrada)
                } else {
                    Toast.makeText(this@roomsAcess, "Erro ao carregar os dados.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<RoomAcess>>, t: Throwable) {
                Toast.makeText(this@roomsAcess, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
