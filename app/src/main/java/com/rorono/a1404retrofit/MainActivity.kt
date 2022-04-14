package com.rorono.a1404retrofit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.rorono.a1404retrofit.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://cat-fact.herokuapp.com"

class MainActivity : AppCompatActivity() {
    private var TAG = "MainActivity"

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        getCurrentData()
        binding.layoutGenerateNewFact.setOnClickListener {
            getCurrentData()
        }

    }

    private fun getCurrentData() {
        binding.tvTextView.visibility = View.INVISIBLE
        binding.tvTimeStamp.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRequests::class.java)


        CoroutineScope(Dispatchers.IO).launch {
            try {


                val response = api.getCatFacts().awaitResponse()
                if (response.isSuccessful) {
                    val data = response.body()!!
                    Log.d(TAG, data.text)
                    withContext(Dispatchers.Main) {
                        binding.tvTextView.visibility = View.VISIBLE
                        binding.tvTimeStamp.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE

                        binding.tvTextView.text = data.text
                        binding.tvTimeStamp.text = data.createdAt

                    }
                }

            } catch (e: Exception){
                withContext(Dispatchers.Main) {

                    Toast.makeText(applicationContext,"ошибка",Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}

