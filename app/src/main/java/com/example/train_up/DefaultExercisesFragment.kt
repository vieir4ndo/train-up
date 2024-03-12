package com.example.train_up

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.train_up.abstractions.BaseTrainUpFragment
import com.example.train_up.helpers.ExerciseAdapter
import com.example.train_up.helpers.Loader
import com.example.train_up.model.Ejercicio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Date

class DefaultExercisesFragment : BaseTrainUpFragment() {
    private var trainingName: String? = null;
    private var recyclerView: RecyclerView? = null;
    private var exercise: Ejercicio? =null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_default_exercises, container, false)
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

        val exercises : List<Ejercicio> = listOf(
            Ejercicio(
                "Flexiones",
                userEmail!!,
                "Serie de flexiones para fortalecer el pecho",
                20,
                Date(),
                null
            ),
            Ejercicio(
                "Plancha",
                userEmail!!,
                "Mantener la posición de plancha durante 1 minuto",
                60,
                Date(),
                null
            )
        )

        val adapter = ExerciseAdapter(trainingName!!, exercises, ::showFrame, false, ::confirmExerciseInclusion)
        recyclerView!!.adapter = adapter
    }


    private fun confirmExerciseInclusion(ejercicio: Ejercicio){
        exercise = ejercicio
        val dialog = trainUpDialogFactory.createSuccessExecuteFuncDialog(requireContext(), "Haz clic abajo para guardar el ejercicio seleccionado en tu entrenamiento", ::addExercise, "Guardar")
        dialog.show()
    }

    companion object {
        fun newInstance(userEmail: String?, trainingName: String?): DefaultExercisesFragment {
            val fragment = DefaultExercisesFragment()
            val args = Bundle()
            args.putString("userEmail", userEmail)
            args.putString("trainingName", trainingName)
            fragment.arguments = args
            return fragment
        }
    }

    private fun addExercise() {
        loader!!.show()

        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            exercise!!.imagen = null;
            database.crearEjercicio(trainingName!!, exercise!!)
        }

        scope.launch {
            val result = job.await()

            handleResul(result)
        }
    }

    private fun handleResul(result: Pair<Boolean, String>){
        loader!!.cancel()
        if (result.first){
            val fragment = UpdateTrainingFragment.newInstance(userEmail, trainingName)
            val dialog = trainUpDialogFactory.createSuccessRedirectFrameDialog(requireContext(), "¡Ejercicio creado con exito!", fragment, ::showFrame)
            dialog.show()
        }
        else {
            val dialog = trainUpDialogFactory.createErrorDismissDialog(requireContext(), result.second)
            dialog.show()
        }
    }

}

