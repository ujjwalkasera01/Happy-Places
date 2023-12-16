package com.example.happyplaces.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.happyplaces.R
import com.example.happyplaces.databases.DatabaseHandler
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.example.happyplaces.models.HappyPlaceModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var bindingAddHappyPlaceActivity : ActivityAddHappyPlaceBinding

    private val cal = Calendar.getInstance()
    private lateinit var dateSetListener : DatePickerDialog.OnDateSetListener
    private var saveImageToInternalStorage : Uri? = null
    private var mLatitude : Double = 0.0
    private var mLongitude : Double = 0.0

    private var mHappyPlaceDetails : HappyPlaceModel? = null

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingAddHappyPlaceActivity = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        val view = bindingAddHappyPlaceActivity.root
        setContentView(view)

        setSupportActionBar(bindingAddHappyPlaceActivity.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        bindingAddHappyPlaceActivity.toolbarAddPlace.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if(!Places.isInitialized()){
            Places.initialize(this@AddHappyPlaceActivity,
                resources.getString(R.string.google_map_api_key))

        }

        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAIL)){
            mHappyPlaceDetails = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAIL)
        }

        dateSetListener = DatePickerDialog.OnDateSetListener {
                _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        updateDateInView()
        if(mHappyPlaceDetails != null) {
            supportActionBar?.title = "Edit Happy Place"
            bindingAddHappyPlaceActivity.etTitle.setText(mHappyPlaceDetails!!.title)
            bindingAddHappyPlaceActivity.etDescription.setText(mHappyPlaceDetails!!.description)
            bindingAddHappyPlaceActivity.etDate.setText(mHappyPlaceDetails!!.date)
            bindingAddHappyPlaceActivity.etLocation.setText(mHappyPlaceDetails!!.location)
            mLatitude = mHappyPlaceDetails!!.latitude
            mLongitude = mHappyPlaceDetails!!.longitude
            saveImageToInternalStorage = Uri.parse(mHappyPlaceDetails!!.image)
            bindingAddHappyPlaceActivity.ivPlaceImage.setImageURI(saveImageToInternalStorage)
            bindingAddHappyPlaceActivity.btnSave.setText(R.string.update_happy_place)
        }

        bindingAddHappyPlaceActivity.etDate.setOnClickListener(this)
        bindingAddHappyPlaceActivity.tvAddImage.setOnClickListener(this)
        bindingAddHappyPlaceActivity.btnSave.setOnClickListener(this)
        bindingAddHappyPlaceActivity.etLocation.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.et_date -> {
                DatePickerDialog(this@AddHappyPlaceActivity,
                    dateSetListener,cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
            }
            R.id.tv_add_image -> {
                val pictureDialog = AlertDialog.Builder(this@AddHappyPlaceActivity)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf("Select photo from Gallery", "Capture photo from Camera")

                pictureDialog.setItems(pictureDialogItems) { _, which ->
                    when (which) {
                        0 -> { selectPhotoFromGallery() }
                        1 -> { takePhotoFromCamera() }
                    }
                }
                pictureDialog.show()
            }
            R.id.btn_save -> {
                when{
                    bindingAddHappyPlaceActivity.etTitle.text.isNullOrBlank() -> {
                        Toast.makeText(this,"Please enter the title",
                            Toast.LENGTH_SHORT).show()
                    }
                    bindingAddHappyPlaceActivity.etDescription.text.isNullOrBlank() -> {
                        Toast.makeText(this,"Please enter the description",
                            Toast.LENGTH_SHORT).show()
                    }
                    bindingAddHappyPlaceActivity.etLocation.text.isNullOrBlank() -> {
                        Toast.makeText(this,"Please enter the title",
                            Toast.LENGTH_SHORT).show()
                    }
                    saveImageToInternalStorage == null -> {
                        Toast.makeText(this,"Please select the image",
                            Toast.LENGTH_SHORT).show()
                    }else -> {
                        // Assigning all the values to data model class.
                        val happyPlaceModel = HappyPlaceModel(
                            if(mHappyPlaceDetails == null) 0 else mHappyPlaceDetails!!.id,
                            bindingAddHappyPlaceActivity.etTitle.text.toString(),
                            saveImageToInternalStorage.toString(),
                            bindingAddHappyPlaceActivity.etDescription.text.toString(),
                            bindingAddHappyPlaceActivity.etDate.text.toString(),
                            bindingAddHappyPlaceActivity.etLocation.text.toString(),
                            mLatitude,
                            mLongitude
                        )

                        // Here we initialize the database handler class.
                        val dbHandler = DatabaseHandler(this)

                        if(mHappyPlaceDetails == null){
                            val addHappyPlace = dbHandler.addHappyPlace(happyPlaceModel)
                            if (addHappyPlace > 0) {
                                setResult(Activity.RESULT_OK)
                                finish();//finishing activity
                            }
                        }else{
                            val updateHappyPlace = dbHandler.updateHappyPlace(happyPlaceModel)
                            if (updateHappyPlace > 0) {
                                setResult(Activity.RESULT_OK)
                                finish();//finishing activity
                            }
                        }
                    }
                }
            }
            R.id.et_location -> {
                try {
                    val fields = listOf(Place.Field.ID,Place.Field.NAME,Place.Field.LAT_LNG
                        ,Place.Field.ADDRESS)

                    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN
                        ,fields).build(this@AddHappyPlaceActivity)
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)

                }catch (e:Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun takePhotoFromCamera() {
        Dexter.withContext(this@AddHappyPlaceActivity).withPermissions(
            Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, CAMERA)
                }
                else if (report!!.isAnyPermissionPermanentlyDenied()) {
                    //if any of them are permanently disabled
                    // (i.e. "deny and don't ask again" etc..)
                    showRationalDialogForPermissions()
                }
                else {  //if it's a simple "deny" from the user
                    Toast.makeText(this@AddHappyPlaceActivity,
                        "This permission is required to access your gallery",
                        Toast.LENGTH_LONG).show()
                }
            }
            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken? ) {
                token?.continuePermissionRequest()

            }
        }).onSameThread().check()
    }

    private fun selectPhotoFromGallery() {
        Dexter.withContext(this@AddHappyPlaceActivity).withPermissions(
            Manifest.permission.READ_MEDIA_IMAGES
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    val galleryIntent = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, GALLERY)
                }
                else if (report!!.isAnyPermissionPermanentlyDenied()) {
                    //if any of them are permanently disabled
                    // (i.e. "deny and don't ask again" etc..)
                    showRationalDialogForPermissions()
                }
                else {  //if it's a simple "deny" from the user
                    Toast.makeText(this@AddHappyPlaceActivity,
                        "This permission is required to access your gallery",
                        Toast.LENGTH_LONG).show()
                }
            }
            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken? ) {
                token?.continuePermissionRequest()

            }
        }).onSameThread().check()
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this).setMessage(
            "It looks like you have turned off permissions required for this feature. " +
            "It can be enabled under the Application Settings."
        ).setPositiveButton("Go to Settings")
        { _,_ ->
            try{
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package",packageName,null)
                intent.data = uri
                startActivity(intent)
            }catch (e:ActivityNotFoundException) {
                e.printStackTrace()
            }
        }.setNegativeButton("Cancel"){
            dialog,_ ->
            dialog.dismiss()
        }.show()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        val selectedImageBitmap = MediaStore.Images.Media
                            .getBitmap(this.contentResolver, contentURI)

                        saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap)
                        Log.e("Saved Image","Path :: $saveImageToInternalStorage")

                        bindingAddHappyPlaceActivity.ivPlaceImage.setImageBitmap(selectedImageBitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@AddHappyPlaceActivity,
                            "Failed to load", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }else if (requestCode == CAMERA){
                val captureImageBitmap : Bitmap = data!!.extras!!.get("data") as Bitmap

                saveImageToInternalStorage = saveImageToInternalStorage(captureImageBitmap)
                Log.e("Saved Image","Path :: $saveImageToInternalStorage")

                bindingAddHappyPlaceActivity.ivPlaceImage.setImageURI(saveImageToInternalStorage)
            }else if(requestCode== PLACE_AUTOCOMPLETE_REQUEST_CODE){
                val place:Place = Autocomplete.getPlaceFromIntent(data!!)
                bindingAddHappyPlaceActivity.etLocation.setText(place.address)
                mLatitude = place.latLng!!.latitude
                mLongitude = place.latLng!!.longitude
            }
        }
    }

   private fun saveImageToInternalStorage(bitmap: Bitmap):Uri{
       val wrapper = ContextWrapper(applicationContext)
       var file = wrapper.getDir(IMAGE_DIRECTORY,Context.MODE_PRIVATE)
       file = File(file,"${UUID.randomUUID()}.jpg")
       try{
           val stream : OutputStream = FileOutputStream(file)
           bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
           stream.flush()
           stream.close()
       }catch (e:IOException){
           e.printStackTrace()
       }
       return Uri.parse(file.absolutePath)
   }

    @SuppressLint("WeekBasedYear")
    private fun updateDateInView(){
        val myFormat = "dd.MM.YYYY"
        val sfd = SimpleDateFormat(myFormat, Locale.getDefault())
        bindingAddHappyPlaceActivity.etDate.setText(sfd.format(cal.time).toString())

    }
}