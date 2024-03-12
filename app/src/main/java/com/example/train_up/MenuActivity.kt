package com.example.train_up

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MenuActivity : AppCompatActivity() {

    private var sharedPreferences : SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val email = sharedPreferences!!.getString("email", "")

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.menu)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.page_home -> {
                    showFrame(HomeFragment.newInstance(email))
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.page_profile -> {
                    showFrame(ProfileFragment.newInstance(email))
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.page_trainings -> {
                    showFrame(TrainingsFragment.newInstance(email))
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.page_subscription -> {
                    showFrame(SubscribeFragment.newInstance(email))
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.page_shared_trainings -> {
                    showFrame(SharedTrainingsFragment.newInstance(email))
                    return@setOnNavigationItemSelectedListener true
                }
                else -> false
            }
        }

        bottomNavigationView.selectedItemId = R.id.page_home

        showFrame(HomeFragment.newInstance(email))
    }

    private fun showFrame(frame: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameContainer, frame)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        return
    }
}
