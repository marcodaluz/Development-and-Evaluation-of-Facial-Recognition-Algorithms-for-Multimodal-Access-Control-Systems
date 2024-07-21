package com.example.projeto1.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto1.R
import com.example.projeto1.api.Attendance
import java.text.SimpleDateFormat
import java.util.*

class attendanceAdapter(private val Attendances: List<Attendance>, private val horaEntrada: String) : RecyclerView.Adapter<attendanceAdapter.UsersViewHolder>() {

    class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val id: TextView = itemView.findViewById(R.id.textViewId)
        private val name: TextView = itemView.findViewById(R.id.textViewName)
        private val time: TextView = itemView.findViewById(R.id.textViewTime)
        private val hora: TextView = itemView.findViewById(R.id.textViewHora)
        private val status: TextView = itemView.findViewById(R.id.textViewStatus)

        fun bind(attendance: Attendance, horaEntrada: String) {
            id.text = attendance.userid.toString()
            name.text = attendance.username
            time.text = attendance.data
            hora.text = attendance.hora

            // Define o formato da hora
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())

            // Compara as horas
            val horaEntradaDate = format.parse(horaEntrada)
            val horaDate = format.parse(attendance.hora)

            if (horaDate != null && horaEntradaDate != null) {
                if (horaDate.before(horaEntradaDate)) {
                    // Se a hora for antes da horaEntrada, status verde
                    status.text = "On time"
                    status.setTextColor(Color.GREEN)
                } else {
                    // Se não, status vermelho
                    status.text = "Late"
                    status.setTextColor(Color.RED)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.line_attendance, parent, false)
        return UsersViewHolder(view)
    }

    override fun getItemCount(): Int = Attendances.size

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bind(Attendances[position], horaEntrada)
    }
}
