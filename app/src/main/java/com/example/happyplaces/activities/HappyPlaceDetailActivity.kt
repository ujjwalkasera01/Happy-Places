@file:Suppress("DEPRECATION")

package com.example.happyplaces.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplaces.databinding.ActivityHappyPlaceDetailBinding
import com.example.happyplaces.models.HappyPlaceModel

class HappyPlaceDetailActivity : AppCompatActivity() {
    private lateinit var bindingHappyPlaceDetailBinding: ActivityHappyPlaceDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingHappyPlaceDetailBinding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        val view = bindingHappyPlaceDetailBinding.root
        setContentView(view)

        var happyPlaceDetailModel : HappyPlaceModel? = null

        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAIL)){
//            happyPlaceDetilModel = intent.getSerializableExtra(
//                MainActivity.EXTRA_PLACE_DETAIL) as HappyPlaceModel
            happyPlaceDetailModel = intent.getParcelableExtra(
                MainActivity.EXTRA_PLACE_DETAIL)
        }

        if(happyPlaceDetailModel != null){
            setSupportActionBar(bindingHappyPlaceDetailBinding.toolbarHappyPlaceDetail)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = happyPlaceDetailModel.title
            bindingHappyPlaceDetailBinding.toolbarHappyPlaceDetail
                .setNavigationOnClickListener{
                onBackPressed()
            }
            bindingHappyPlaceDetailBinding.ivPlaceImage
                .setImageURI(Uri.parse(happyPlaceDetailModel.image))
            bindingHappyPlaceDetailBinding.tvDescription
                .setText(happyPlaceDetailModel.description)
            bindingHappyPlaceDetailBinding.tvLocation
                .setText(happyPlaceDetailModel.location)

            bindingHappyPlaceDetailBinding.btnViewOnMap.setOnClickListener{
                val intent = Intent(this,MapActivity::class.java)
                intent.putExtra(MainActivity.EXTRA_PLACE_DETAIL,happyPlaceDetailModel)
                startActivity(intent)
            }
        }
    }
}