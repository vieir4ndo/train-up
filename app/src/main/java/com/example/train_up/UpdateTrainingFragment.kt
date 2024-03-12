package com.example.train_up

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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

class UpdateTrainingFragment : BaseTrainUpFragment() {
    private var trainingName: String? = null;
    private var name : EditText? = null
    private var description : EditText? = null
    private lateinit var imageView: ImageView
    private var buttonUpdateTraining : Button? = null
    private var buttonAddExercise : Button? = null
    private var nothingHereText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userEmail = it.getString("userEmail")
            trainingName = it.getString("trainingName")
        }
        loader = Loader(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_training, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nothingHereText = view.findViewById(R.id.nothing_here_text)
        fetchData(view)
    }

    private fun fetchData(view: View) {
        loader!!.show()

        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            database.mostrarDetallesEntrenamiento(trainingName!!, userEmail!!)
        }

        scope.launch {
            val result = job.await()

            handleResult(view, result)
        }
    }

    private fun handleResult(view: View, result : Entrenamiento?) {
        loader!!.cancel()

        buttonUpdateTraining = view.findViewById(R.id.save)
        buttonAddExercise = view.findViewById(R.id.add)
        name = view.findViewById(R.id.edit_text_name)
        name!!.isEnabled = false
        description = view.findViewById(R.id.edit_text_description)
        imageView = view.findViewById(R.id.imageViewCreate)

        imageView.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        name!!.setText(result!!.nombre);
        description!!.setText(result!!.descripcion);
        val bitmap = BitmapFactory.decodeByteArray(result!!.imagen, 0, result!!.imagen.size)
        imageView.setImageBitmap(bitmap)

        if (result.ejercicios!!.isNotEmpty()){
            val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            val adapter = ExerciseAdapter(trainingName!!, result.ejercicios!!, ::showFrame)
            recyclerView.adapter = adapter
            nothingHereText!!.visibility = View.GONE
        }

        buttonUpdateTraining!!.setOnClickListener{
            if (validate()){
                fetchDataUpdate();
            }
        }

        buttonAddExercise!!.setOnClickListener {
            val dialog = trainUpDialogFactory.createAddExerciseDialog(requireContext(), ::showFrame, userEmail!!, trainingName!!)
            dialog.show()
        }
    }

    private fun validate() : Boolean{
        if (!validator.isEmpty(name!!, "Nombre es obligatorio"))
            validator.hasMaxLength(name!!, 25, "Nombre debe contener un máximo de 25 caracteres")

        if (!validator.isEmpty(description!!,  "Descripción es obligatorio" ))
            validator.hasMaxLength(description!!, 255, "Descripción debe contener un máximo de 255 caracteres")

        return name!!.error == null && description!!.error == null
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        fun newInstance(userEmail: String?, trainingName: String?): UpdateTrainingFragment {
            val fragment = UpdateTrainingFragment()
            val args = Bundle()
            args.putString("userEmail", userEmail)
            args.putString("trainingName", trainingName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data

            imageView.setImageURI(selectedImageUri)
            imageView.scaleType = ImageView.ScaleType.FIT_XY
        }
    }

    private fun fetchDataUpdate() {
        loader!!.show()

        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            val entrenamiento = Entrenamiento(
                name!!.text.toString(),
                userEmail!!,
                description!!.text.toString(),
                null,
                null,
                null,
                getBytes(imageView)//imageViewToByteArray(imageView)
            )
            database.editarEntrenamiento(trainingName!!, entrenamiento)
        }

        scope.launch {
            val result = job.await()

            handleResultUpdate(result)
        }
    }

    private fun handleResultUpdate(result : Pair<Boolean, String>) {
        loader!!.cancel()
        if (result.first){
            val fragment = TrainingsFragment.newInstance(userEmail!!)
            val dialog = trainUpDialogFactory.createSuccessRedirectFrameDialog(requireContext(), "¡Entrenamiento actualizado con exito!", fragment, ::showFrame)
            dialog.show()
        }
        else {
            val dialog = trainUpDialogFactory.createErrorDismissDialog(requireContext(), result.second)
            dialog.show()
        }
    }
}
