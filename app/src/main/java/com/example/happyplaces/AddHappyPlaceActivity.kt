package com.example.happyplaces

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.audiofx.Equalizer.Settings
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.text.SimpleDateFormat
import java.time.Month
import java.util.Calendar
import java.util.Locale

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var bindingAddHappyPlaceActivity : ActivityAddHappyPlaceBinding

    private val cal = Calendar.getInstance()
    private lateinit var dateSetListener : DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_place)
        bindingAddHappyPlaceActivity = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        val view = bindingAddHappyPlaceActivity.root
        setContentView(view)

        //setSupportActionBar(binding.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        bindingAddHappyPlaceActivity.toolbarAddPlace.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed()
        }

        dateSetListener = DatePickerDialog.OnDateSetListener {
                _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        bindingAddHappyPlaceActivity.etDate.setOnClickListener(this)
        bindingAddHappyPlaceActivity.tvAddImage.setOnClickListener(this)

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
                        1 -> {
                            Toast.makeText(this,
                                "Camera Selection Coming Soon.....",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                pictureDialog.show()
            }
        }
    }

    private fun selectPhotoFromGallery() {
        Dexter.withContext(this@AddHappyPlaceActivity).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    Toast.makeText(this@AddHappyPlaceActivity
                        ,"Storage READ/WRITE permissions are granted."
                        ,Toast.LENGTH_LONG).show()
                }
                else { showRationalDialogForPermissions() }
            }
            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken? ) {
                token?.continuePermissionRequest()

            }
        }).check()
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

    @SuppressLint("WeekBasedYear")
    private fun updateDateInView(){
        val myFormat = "dd.MM.YYYY"
        val sfd = SimpleDateFormat(myFormat, Locale.getDefault())
        bindingAddHappyPlaceActivity.etDate.setText(sfd.format(cal.time).toString())

    }
}