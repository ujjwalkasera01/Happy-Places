package com.example.happyplaces.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.happyplaces.R
import com.example.happyplaces.adapters.HappyPlacesAdapter
import com.example.happyplaces.databases.DatabaseHandler
import com.example.happyplaces.databinding.ActivityMainBinding
import com.example.happyplaces.models.HappyPlaceModel

class MainActivity : AppCompatActivity() {
    private lateinit var bindingActivity: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindingActivity = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingActivity.root)
        bindingActivity.fabAddHappyPlace.setOnClickListener{
            val intent = Intent(this@MainActivity, AddHappyPlaceActivity::class.java)
            startActivity(intent)
        }
        // TODO(Step 3 : Calling an function which have created for getting list when activity is launched.)
        // START
        getHappyPlacesListFromLocalDB()
        // END
    }
    // TODO(Step 2 : Calling an function which have created for getting list of inserted data from local database. And the list of values are printed in the log.)
    // START
    /**
     * A function to get the list of happy place from local database.
     */
    private fun getHappyPlacesListFromLocalDB() {

        val dbHandler = DatabaseHandler(this)

        val getHappyPlacesList = dbHandler.getHappyPlacesList()

        // TODO (Step 8: Calling an function which have created for getting list of inserted data from local database
                //  and passing the list to recyclerview to populate in UI.)
                // START
        if (getHappyPlacesList.size > 0) {
            bindingActivity.rvHappyPlacesList.visibility = View.VISIBLE
            bindingActivity.tvNoRecordsAvailable.visibility = View.GONE
            setupHappyPlacesRecyclerView(getHappyPlacesList)
        } else {
            bindingActivity.rvHappyPlacesList.visibility = View.GONE
            bindingActivity.tvNoRecordsAvailable.visibility = View.VISIBLE
        }
        // END
    }
    // TODO(Step 7 : Creating a function for setting up the recyclerview to UI.)
    // START
    /**
     * A function to populate the recyclerview to the UI.
     */
    private fun setupHappyPlacesRecyclerView(happyPlacesList: ArrayList<HappyPlaceModel>) {

        bindingActivity.rvHappyPlacesList.layoutManager = LinearLayoutManager(this)
        bindingActivity.rvHappyPlacesList.setHasFixedSize(true)

        val placesAdapter = HappyPlacesAdapter(this, happyPlacesList)
        bindingActivity.rvHappyPlacesList.adapter = placesAdapter
    }
    // END
}