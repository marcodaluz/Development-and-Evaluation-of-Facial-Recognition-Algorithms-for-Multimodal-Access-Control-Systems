package com.example.projeto1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.example.projeto1.dataclasses.User
import java.io.Serializable

class profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val user = intent.getSerializableExtra("userProfile") as? User

        val userNameTextView = findViewById<TextView>(R.id.userNameTextView)
        val userIdTextView = findViewById<TextView>(R.id.userIdTextView)
        val userCourseTextView = findViewById<TextView>(R.id.userCourseTextView)
        val horaEntradaTextView = findViewById<TextView>(R.id.horaEntradaTextView)
        val horaSaidaTextView = findViewById<TextView>(R.id.horaSaidaTextView)
        val departamentoTextView = findViewById<TextView>(R.id.departamentoTextView)
        val emailTextView = findViewById<TextView>(R.id.email)
        val numberTextView = findViewById<TextView>(R.id.number)

        if (user != null) {
            userNameTextView.text = user.username
            userIdTextView.text = "IDº: ${user.userid}"
            userCourseTextView.text = user.cargo
            horaEntradaTextView.text = "Normal Entry Time ${user.HoraEntrada}"
            horaSaidaTextView.text = "Normal Exit Time ${user.HoraSaida}"
            departamentoTextView.text = "Department: ${user.departamento}"
            emailTextView.text = "estg@ipvc.pt"
            numberTextView.text = "9488484848"
        }

        val btnAtend = findViewById<Button>(R.id.goAttendance)
        btnAtend.setOnClickListener {
            val intent = Intent(this, attendance::class.java).apply {
                // Passa o userid e horaEntrada como extras para a nova atividade
                putExtra("userid", user?.userid ?: 0) // Passando 0 como default, ajuste conforme necessário
                putExtra("horaEntrada", user?.HoraEntrada ?: "00:00") // Passando "00:00" como default, ajuste conforme necessário
            }
            startActivity(intent)
        }

        val btnOut = findViewById<Button>(R.id.goOut)
        btnOut.setOnClickListener {
            val intent = Intent(this, out::class.java).apply {
                // Passa o userid e horaEntrada como extras para a nova atividade
                putExtra("userid", user?.userid ?: 0) // Passando 0 como default, ajuste conforme necessário
                putExtra("horaSaida", user?.HoraSaida ?: "00:00") // Passando "00:00" como default, ajuste conforme necessário
            }
            startActivity(intent)
        }

        val btnRoomAcess = findViewById<Button>(R.id.roomAcess)
        btnRoomAcess.setOnClickListener {
            val intent = Intent(this, roomsAcess::class.java).apply {
                // Passa o userid e horaEntrada como extras para a nova atividade
                putExtra("userid", user?.userid ?: 0) // Passando 0 como default, ajuste conforme necessário
                putExtra("horaSaida", user?.HoraSaida ?: "00:00") // Passando "00:00" como default, ajuste conforme necessário
            }
            startActivity(intent)
        }
    }
}
