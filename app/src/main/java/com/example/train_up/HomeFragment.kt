package com.example.train_up

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.train_up.abstractions.BaseTrainUpFragment
import com.example.train_up.helpers.TrainingsAdapter
import com.example.train_up.model.Entrenamiento
import com.example.train_up.model.Usuario
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeFragment : BaseTrainUpFragment() {
    private var recyclerView : RecyclerView? = null
    private var profilePicture : CircleImageView? = null
    private var greeting: TextView? = null
    private var nothingHereText: TextView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profilePicture = view.findViewById(R.id.profile_picture)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView!!.layoutManager = LinearLayoutManager(requireContext())
        greeting = view.findViewById(R.id.greeting)
        nothingHereText = view.findViewById(R.id.nothing_here_text)

        getUser()
    }

    private fun getUser(){
        loader!!.show()
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            database.buscarUsuario(userEmail!!)
        }

        scope.launch {
            val result = job.await()

            handleResultGetUser(result!!)
        }
    }

    private fun handleResultGetUser(result : Usuario){
        greeting!!.text = "¡Hola," + result.nombre + "!"

        if (result.imagen != null){
            val bitmap = BitmapFactory.decodeByteArray(result.imagen, 0, result.imagen!!.size)
            profilePicture!!.setImageBitmap(bitmap)
        }

        fetchData()
    }

    private fun fetchData() {
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            database.mostrarEntrenamientos(userEmail!!)
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private fun handleResult(result : List<Entrenamiento>) {
        loader!!.cancel()

        if (result.isNotEmpty()){
            val adapter = TrainingsAdapter(false, null, result, ::showFrame, false, ::redirectExerciseView)
            recyclerView!!.adapter = adapter
            nothingHereText!!.visibility = View.GONE
        }
        else{
            recyclerView!!.visibility = View.GONE
        }
    }

    private fun redirectExerciseView(trainingName : String,  email: String){
        showFrame(ConsultExercisesFragment.newInstance(email, trainingName))
    }

    companion object {
        fun newInstance(userEmail: String?): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString("userEmail", userEmail)
            fragment.arguments = args
            return fragment
        }
    }
}