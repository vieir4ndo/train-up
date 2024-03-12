package com.example.train_up

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

class TrainingsFragment : BaseTrainUpFragment() {
    private var buttonAddTraining  : Button? = null
    private var recyclerView : RecyclerView? = null
    private var isUserPremium: Boolean = false
    private var nothingHereText: TextView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trainings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonAddTraining = view.findViewById(R.id.add);
        recyclerView = view.findViewById(R.id.recyclerView)
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
        isUserPremium = result.premium
        fetchData()
    }

    companion object {
        fun newInstance(userEmail: String?): TrainingsFragment {
            val fragment = TrainingsFragment()
            val args = Bundle()
            args.putString("userEmail", userEmail)
            fragment.arguments = args
            return fragment
        }
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
            recyclerView!!.layoutManager = LinearLayoutManager(requireContext())

            val adapter = TrainingsAdapter(isUserPremium, ::shareTrainingConfirm, result, ::showFrame)
            recyclerView!!.adapter = adapter
            nothingHereText!!.visibility = View.GONE
        }
        else{
            recyclerView!!.visibility = View.GONE
        }

        buttonAddTraining!!.setOnClickListener{
            showFrame(CreateTrainingFragment.newInstance(userEmail))
        }
    }

    private fun shareTrainingConfirm(trainingName : String){
        val dialog = trainUpDialogFactory.createShareTrainingDialog(requireContext(), ::shareTraining, trainingName)
        dialog.show();
    }

    private fun shareTraining(trainingName : String){
        loader!!.show()

        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            database.publicarEntrenamiento(userEmail!!, trainingName)
        }

        scope.launch {
            val result = job.await()

            handleResultShareTraining(result)
        }
    }
    private fun handleResultShareTraining(result : Boolean) {
        loader!!.cancel()

        if (result){
            val dialog = trainUpDialogFactory.createSuccessDismissDialog(requireContext(), "¡Entrenamiento compartido con exito!")
            dialog.show()
        }
        else {
            val dialog = trainUpDialogFactory.createErrorDismissDialog(requireContext(), "¡Hubo un error al procesar tu solicitud!")
            dialog.show()
        }
    }
}