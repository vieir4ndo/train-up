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
import com.example.train_up.UpdateTrainingFragment
import com.example.train_up.model.Entrenamiento
import de.hdodenhof.circleimageview.CircleImageView

class TrainingsAdapter(
    private val isUserPremium: Boolean,
    private val function : ((String) -> Unit)?,
    private val data: List<Entrenamiento>,
    private val showFrame: (Fragment) -> Unit,
    private val editMode : Boolean = true,
    private val callBackFunction: ((String, String) -> Unit)? = null
) : RecyclerView.Adapter<TrainingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.title.text = item.nombre;
        holder.description.text = item.descripcion;
        val duration = item.ejercicios?.sumBy { it.duracion };
        holder.time.text = duration.toString() + " minutos de entrenamiento"
        val bitmap = BitmapFactory.decodeByteArray(item.imagen, 0, item.imagen.size)
        holder.image.setImageBitmap(bitmap)

        if (editMode) {
            holder.editButton.setOnClickListener {
                val fragment =
                    UpdateTrainingFragment.newInstance(item.usuario, item.nombre) as Fragment;
                showFrame(fragment)
            }

            holder.shareButton.setOnClickListener{
                function!!.invoke(item.nombre)
            }

            if (item.publico == true || !isUserPremium)
                holder.shareButton.visibility = View.GONE

            holder.authorContainer.visibility = View.GONE
        }
        else{
            holder.editButton.visibility = View.GONE
            holder.shareButton.visibility = View.GONE
            holder.authorName.text = item.datosUsuario?.nombre + " " + item.datosUsuario?.apellidos;
            if (item.datosUsuario?.imagen != null){
                val bitmap = BitmapFactory.decodeByteArray(item.datosUsuario!!.imagen, 0, item.datosUsuario!!.imagen!!.size)
                holder.authorImage.setImageBitmap(bitmap)
            }

            holder.card.setOnClickListener{
                callBackFunction!!.invoke(item.nombre, item.datosUsuario!!.email)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val description: TextView = itemView.findViewById(R.id.description)
        val time: TextView = itemView.findViewById(R.id.time)
        val image: ImageView = itemView.findViewById(R.id.preview)

        val editButton: ImageView = itemView.findViewById(R.id.edit_icon)
        val shareButton: ImageView = itemView.findViewById(R.id.share_icon)
        val authorContainer : LinearLayout = itemView.findViewById(R.id.container_author)
        val authorName : TextView = itemView.findViewById(R.id.creator_name)
        val authorImage : CircleImageView = itemView.findViewById(R.id.creator_image)
        val card : CardView = itemView.findViewById(R.id.card_id)
    }
}
