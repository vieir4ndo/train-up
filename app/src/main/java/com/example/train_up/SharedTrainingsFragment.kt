package com.example.train_up

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.train_up.abstractions.BaseTrainUpFragment
import com.example.train_up.helpers.TrainingsAdapter
import com.example.train_up.model.Entrenamiento
import com.example.train_up.model.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SharedTrainingsFragment : BaseTrainUpFragment() {
    private var recyclerView : RecyclerView? = null
    private var nothingHereText: TextView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shared_trainings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView!!.layoutManager = LinearLayoutManager(requireContext())
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
        if (!result.premium){
            loader!!.cancel();
            showFrame(SubscriptionAdvertisementFragment.newInstance(userEmail!!))
        }
        else {
            fetchData()
        }
    }

    companion object {
        fun newInstance(userEmail: String?): SharedTrainingsFragment {
            val fragment = SharedTrainingsFragment()
            val args = Bundle()
            args.putString("userEmail", userEmail)
            fragment.arguments = args
            return fragment
        }
    }

    private fun fetchData() {
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            database.buscarEntrenamientos()
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

    private fun redirectExerciseView(trainingName : String, email: String){
        showFrame(ConsultExercisesFragment.newInstance(email, trainingName))
    }

}