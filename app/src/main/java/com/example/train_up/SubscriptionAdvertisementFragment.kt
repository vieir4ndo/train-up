package com.example.train_up

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.train_up.abstractions.BaseTrainUpFragment


class SubscriptionAdvertisementFragment : BaseTrainUpFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_subscription_advertisement, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val becomeMember = view.findViewById<LinearLayout>(R.id.become_member)

        becomeMember.setOnClickListener {
            showFrame(SubscribeFragment.newInstance(userEmail!!))
        }
    }

    companion object {
        fun newInstance(userEmail: String?): SubscriptionAdvertisementFragment {
            val fragment = SubscriptionAdvertisementFragment()
            val args = Bundle()
            args.putString("userEmail", userEmail)
            fragment.arguments = args
            return fragment
        }
    }
}