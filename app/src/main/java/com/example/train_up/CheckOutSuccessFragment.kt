package com.example.train_up

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.train_up.abstractions.BaseTrainUpFragment


class CheckOutSuccessFragment : BaseTrainUpFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_check_out_success, container, false)
    }
}