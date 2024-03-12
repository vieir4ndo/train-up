package com.example.train_up

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.train_up.abstractions.BaseTrainUpFragment
import com.example.train_up.model.Entrenamiento
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Date

class CreateTrainingFragment : BaseTrainUpFragment() {

    private var name : EditText? = null
    private var description : EditText? = null
    private lateinit var imageView: ImageView
    private var imageSelected : Boolean = false;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_training, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonCreateTraining = view.findViewById<Button>(R.id.save)

        name = view.findViewById(R.id.edit_text_name)
        description = view.findViewById(R.id.edit_text_description)
        imageView = view.findViewById(R.id.imageViewCreate)

        imageView.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, CreateTrainingFragment.PICK_IMAGE_REQUEST)
        }

        buttonCreateTraining.setOnClickListener{
            if (!imageSelected){
                val dialog = trainUpDialogFactory.createErrorDismissDialog(requireContext(), "Imagen es obligatório")
                dialog.show()
            }
            else if (validate()){
                fetchData();
            }
        }
    }

    private fun validate() : Boolean{
        if (!validator.isEmpty(name!!, "Nombre es obligatorio"))
            validator.hasMaxLength(name!!, 25, "Nombre debe contener un máximo de 25 caracteres")

        if (!validator.isEmpty(description!!,  "Descripción es obligatorio" ))
            validator.hasMaxLength(description!!, 255, "Descripción debe contener un máximo de 255 caracteres")

        return name!!.error == null && description!!.error == null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CreateTrainingFragment.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data

            imageView.setImageURI(selectedImageUri)
            imageView.scaleType = ImageView.ScaleType.FIT_XY
            imageSelected = true
        }
    }
    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        fun newInstance(userEmail: String?): CreateTrainingFragment {
            val fragment = CreateTrainingFragment()
            val args = Bundle()
            args.putString("userEmail", userEmail)
            fragment.arguments = args
            return fragment
        }
    }

    private fun fetchData() {
        loader!!.show()

        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            val entrenamiento = Entrenamiento(
                name!!.text.toString(),
                userEmail!!,
                description!!.text.toString(),
                0,
                Date(),
                false,
                getBytes(imageView)//imageViewToByteArray(imageView)
                )
            database.crearEntrenamiento(entrenamiento)
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private fun handleResult(result : Pair<Boolean, String>) {
        loader!!.cancel()
        if (result.first){
            val fragment = TrainingsFragment.newInstance(userEmail)
            val dialog = trainUpDialogFactory.createSuccessRedirectFrameDialog(requireContext(), "¡Entrenamiento creado con exito!", fragment, ::showFrame)
            dialog.show()
        }
        else {
            val dialog = trainUpDialogFactory.createErrorDismissDialog(requireContext(), result.second)
            dialog.show()
        }
    }
}
