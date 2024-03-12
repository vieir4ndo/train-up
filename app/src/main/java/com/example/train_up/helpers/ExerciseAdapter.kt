package com.example.train_up.helpers

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.train_up.R
import com.example.train_up.UpdateExerciseFragment
import com.example.train_up.model.Ejercicio

class ExerciseAdapter(private val trainingName : String,
                      private val data: List<Ejercicio>,
                      private val showFrame: (Fragment) -> Unit,
                      private val editMode : Boolean = true,
                      private val callBackFunction : ((Ejercicio) -> Unit)? = null
    ) : RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.title.text = item.nombre;
        holder.description.text = item.descripcion;
        holder.time.text = item.duracion.toString() + " minutos de entrenamiento"

        if (item.imagen != null) {
            val bitmap = BitmapFactory.decodeByteArray(item.imagen!!, 0, item.imagen!!.size)
            holder.image.setImageBitmap(bitmap)
        }
        else {
            holder.imageContainer.visibility = View.GONE
        }

        if (editMode){
            holder.editButton.setOnClickListener{
                val fragment = UpdateExerciseFragment.newInstance(item.usuario, trainingName ,item.nombre) as Fragment;
                showFrame(fragment)
            }
        }
        else {
            holder.editButton.visibility = View.GONE
        }

        if (callBackFunction != null){
            holder.card.setOnClickListener{
                callBackFunction.invoke(item)
            }
        }

        holder.shareButton.visibility = View.GONE
        holder.authorContainer.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var card : CardView = itemView.findViewById(R.id.card_id)
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val time: TextView = itemView.findViewById(R.id.time)
        val imageContainer : CardView = itemView.findViewById(R.id.preview_container)
        val image: ImageView = itemView.findViewById(R.id.preview)

        val editButton: ImageView = itemView.findViewById(R.id.edit_icon)
        val shareButton: ImageView = itemView.findViewById(R.id.share_icon)
        val authorContainer : LinearLayout = itemView.findViewById(R.id.container_author)
    }
}
