import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto1.R
import com.example.projeto1.api.Attendance
import com.example.projeto1.api.Out
import com.example.projeto1.api.RoomAcess
import java.text.SimpleDateFormat
import java.util.*

class roomAcessAdapter(private val outs: List<RoomAcess>, private val horaSaida: String) : RecyclerView.Adapter<roomAcessAdapter.UsersViewHolder>() {

    class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val id: TextView = itemView.findViewById(R.id.textViewId3)
        private val name: TextView = itemView.findViewById(R.id.textViewName3)
        private val time: TextView = itemView.findViewById(R.id.textViewTime3)
        private val hora: TextView = itemView.findViewById(R.id.textViewHora3)
        private val status: TextView = itemView.findViewById(R.id.textViewStatus3)
        private val roomNumber: TextView = itemView.findViewById(R.id.textViewRoomNumber)

        fun bind(RoomAcess: RoomAcess, horaSaida: String) {
            id.text = RoomAcess.userid.toString()
            name.text = RoomAcess.username
            time.text = RoomAcess.data
            hora.text = RoomAcess.hora
            roomNumber.text = RoomAcess.roomNumber.toString()
            status.text = RoomAcess.status

            if (RoomAcess.status.equals("1")) {
                status.text = "Accept";
                status.setTextColor(Color.GREEN);
            } else {
                status.text = "NO";
                status.setTextColor(Color.RED);
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rooms_acess_line, parent, false)
        return UsersViewHolder(view)
    }

    override fun getItemCount(): Int = outs.size

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bind(outs[position], horaSaida)
    }
}
