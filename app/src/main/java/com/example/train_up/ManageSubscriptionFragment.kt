package com.example.train_up

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.train_up.abstractions.BaseTrainUpFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ManageSubscriptionFragment : BaseTrainUpFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manage_subscription, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cancelSubscriptionButton = view.findViewById<Button>(R.id.cancel_button)

        cancelSubscriptionButton.setOnClickListener {
            fetchData()
        }
    }

    private fun fetchData() {
        loader!!.show()

        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            database.cambiarUsuarioPlanPremium(userEmail!!, false)
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private fun handleResult(result : Boolean) {
        loader!!.cancel()
        if (result){
            val dialog = trainUpDialogFactory.createSuccessDismissDialog(requireContext(), "¡Suscripción cancelada con exito!")
            dialog.show()
        }
        else {
            val dialog = trainUpDialogFactory.createErrorDismissDialog(requireContext(), "¡Hubo un error al procesar tu solicitud!")
            dialog.show()
        }
    }

    companion object {
        fun newInstance(userEmail: String?): ManageSubscriptionFragment {
            val fragment = ManageSubscriptionFragment()
            val args = Bundle()
            args.putString("userEmail", userEmail)
            fragment.arguments = args
            return fragment
        }
    }
}