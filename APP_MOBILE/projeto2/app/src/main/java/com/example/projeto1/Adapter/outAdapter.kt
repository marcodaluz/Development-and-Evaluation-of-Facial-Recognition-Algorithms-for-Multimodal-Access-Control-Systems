package com.example.projeto1.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto1.R
import com.example.projeto1.api.Attendance
import com.example.projeto1.api.Out
import java.text.SimpleDateFormat
import java.util.*

class outAdapter(private val outs: List<Out>, private val horaSaida: String) : RecyclerView.Adapter<outAdapter.UsersViewHolder>() {

    class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val id: TextView = itemView.findViewById(R.id.textViewId2)
        private val name: TextView = itemView.findViewById(R.id.textViewName2)
        private val time: TextView = itemView.findViewById(R.id.textViewTime2)
        private val hora: TextView = itemView.findViewById(R.id.textViewHora2)
        private val status: TextView = itemView.findViewById(R.id.textViewStatus2)

        fun bind(out: Out, horaEntrada: String) {
            id.text = out.userid.toString()
            name.text = out.username
            time.text = out.data
            hora.text = out.hora

            // Define o formato da hora
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())

            // Compara as horas
            val horaEntradaDate = format.parse(horaEntrada)
            val horaDate = format.parse(out.hora)

            if (horaDate != null && horaEntradaDate != null) {
                if (horaDate.before(horaEntradaDate)) {
                    // Se a hora for antes da horaEntrada, status verde
                    status.text = "Leaving early"
                    status.setTextColor(Color.RED)
                } else {
                    // Se não, status vermelho
                    status.text = "Leaving late"
                    status.setTextColor(Color.GREEN)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.line_out, parent, false)
        return UsersViewHolder(view)
    }

    override fun getItemCount(): Int = outs.size

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bind(outs[position], horaSaida)
    }
}
