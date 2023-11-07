package com.abid.storyapp.view.upload

import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.Manifest
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.view.View
import android.widget.CheckBox
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.abid.storyapp.R
import com.abid.storyapp.data.response.UploadResponse
import com.abid.storyapp.data.retrofit.ApiConfig
import com.abid.storyapp.databinding.ActivityUploadBinding
import com.abid.storyapp.getImageUri
import com.abid.storyapp.reduceFileImage
import com.abid.storyapp.uriToFile
import com.abid.storyapp.view.ViewModelFactory
import com.abid.storyapp.view.main.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.util.Locale

class UploadActivity : AppCompatActivity() {
    private val uploadViewModel by viewModels<MainViewModel>{
        ViewModelFactory(applicationContext)
    }
    private lateinit var binding: ActivityUploadBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private var currentImageUri: Uri? = null
    private var lat: Float? = null
    private var lng: Float? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){ isGranted: Boolean ->
            if (isGranted){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
        }

    private fun allPermissionGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        var token = ""
        uploadViewModel.getSession().observe(this){ user ->
            token = user.token
        }

        if(!allPermissionGranted()){
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.actionLocation.setOnClickListener {
            onCheckBoxClicked(it)
        }
        binding.submitBtn.setOnClickListener {
            uploadFile(token)
        }

    }

    private fun startGallery(){
        launchGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launchGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ){ uri: Uri? ->
        if (uri != null){
            currentImageUri = uri
            showImage()
        }else{
            Log.d("Photo Picker", "No Media Selected")
        }
    }

    private fun startCamera(){
        currentImageUri = getImageUri(this)
        launchCameraIntent.launch(currentImageUri)
    }

    private val launchCameraIntent = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ){ isSuccess ->
        if (isSuccess){
            showImage()
        }
    }

    private fun onCheckBoxClicked(view: View){
        if(view is CheckBox){
            val checked: Boolean = view.isChecked

            when(view.id){
                R.id.action_location -> {
                    if(checked){
                        if (ContextCompat.checkSelfPermission(
                                this.applicationContext,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            mFusedLocationClient.lastLocation.addOnCompleteListener(this){ task ->
                                val location: Location? = task.result
                                if (location != null){
                                    val geocoder = Geocoder(this, Locale.getDefault())
                                    val list: MutableList<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                                    lat = list?.get(0)?.latitude?.toFloat()
                                    lng = list?.get(0)?.longitude?.toFloat()
                                    Log.d("UploadActivity", "lat: $lat, lng: $lng" )
                                }
                            }
                        } else {
                            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    }else{
                        lat = null
                        lng = null
                    }
                }
            }
        }
    }

    private fun uploadFile(token: String){
        currentImageUri?.let{ uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.edAddDescription.text.toString()
            showLoading(true)

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            val latitude = lat
            val longitude = lng
            lifecycleScope.launch {
                try{
                    val apiService = ApiConfig.getApiService(token)
                    val successResponse = apiService.uploadStory(multipartBody, requestBody, latitude, longitude)
                    successResponse.message?.let { showToast(it) }
                    showLoading(false)
                    finish()
                }catch (e: HttpException){
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, UploadResponse::class.java)
                    errorResponse.message?.let { showToast(it) }
                    errorResponse.message?.let { Log.d("uploadError: ", it) }
                    showLoading(false)
                }
            }
        } ?: showToast("Photo is empty")
    }

    private fun showImage(){
        currentImageUri?.let {
            Log.d("Image Uri", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object{
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}