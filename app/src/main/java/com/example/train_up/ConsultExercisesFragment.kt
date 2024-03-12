package com.example.train_up

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.train_up.abstractions.BaseTrainUpFragment
import com.example.train_up.helpers.ExerciseAdapter
import com.example.train_up.helpers.Loader
import com.example.train_up.model.Entrenamiento
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ConsultExercisesFragment : BaseTrainUpFragment() {
    private var trainingName: String? = null;
    private var recyclerView: RecyclerView? = null
    private var trainingNameTextView : TextView? = null
    private var nothingHereText: TextView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_consult_exercise, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userEmail = it.getString("userEmail")
            trainingName = it.getString("trainingName")
        }
        loader = Loader(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView!!.layoutManager = LinearLayoutManager(requireContext())
        trainingNameTextView = view.findViewById(R.id.training_name)
        nothingHereText = view.findViewById(R.id.nothing_here_text)

        fetchData()
    }

    private fun fetchData() {
        loader!!.show()

        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            database.mostrarDetallesEntrenamiento(trainingName!!, userEmail!!)
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private fun handleResult(result : Entrenamiento?) {
        loader!!.cancel()

        trainingNameTextView!!.text = result?.nombre;

        if (result?.ejercicios!!.isNotEmpty()){
            val adapter = ExerciseAdapter(trainingName!!, result.ejercicios!!, ::showFrame, false)
            recyclerView!!.adapter = adapter
            nothingHereText!!.visibility = View.GONE
        }
        else{
            recyclerView!!.visibility = View.GONE
        }
    }

    companion object {
        fun newInstance(userEmail: String?, trainingName: String?): ConsultExercisesFragment {
            val fragment = ConsultExercisesFragment()
            val args = Bundle()
            args.putString("userEmail", userEmail)
            args.putString("trainingName", trainingName)
            fragment.arguments = args
            return fragment
        }
    }
}