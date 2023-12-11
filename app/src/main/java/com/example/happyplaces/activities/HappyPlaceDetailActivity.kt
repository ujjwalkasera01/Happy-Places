package com.example.happyplaces.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplaces.R
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.example.happyplaces.databinding.ActivityHappyPlaceDetailBinding
import com.example.happyplaces.models.HappyPlaceModel

class HappyPlaceDetailActivity : AppCompatActivity() {
    private lateinit var bindingHappyPlaceDetailBinding: ActivityHappyPlaceDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingHappyPlaceDetailBinding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        val view = bindingHappyPlaceDetailBinding.root
        setContentView(view)

        var happyPlaceDetilModel : HappyPlaceModel? = null

        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAIL)){
//            happyPlaceDetilModel = intent.getSerializableExtra(
//                MainActivity.EXTRA_PLACE_DETAIL) as HappyPlaceModel
            happyPlaceDetilModel = intent.getParcelableExtra(
                MainActivity.EXTRA_PLACE_DETAIL)
        }

        if(happyPlaceDetilModel != null){
            setSupportActionBar(bindingHappyPlaceDetailBinding.toolbarHappyPlaceDetail)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = happyPlaceDetilModel.title
            bindingHappyPlaceDetailBinding.toolbarHappyPlaceDetail
                .setNavigationOnClickListener{
                onBackPressed()
            }
            bindingHappyPlaceDetailBinding.ivPlaceImage
                .setImageURI(Uri.parse(happyPlaceDetilModel.image))
            bindingHappyPlaceDetailBinding.tvDescription
                .setText(happyPlaceDetilModel.description)
            bindingHappyPlaceDetailBinding.tvLocation
                .setText(happyPlaceDetilModel.location)
        }
    }
}