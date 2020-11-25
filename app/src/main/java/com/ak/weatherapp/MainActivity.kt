package com.ak.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


class MainActivity : AppCompatActivity() {


    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if(!isLoationEnabled()){
            Toast.makeText(this,"turn on location",Toast.LENGTH_SHORT).show()

            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)

        }else{

          Dexter.withActivity(this,).withPermissions(
                  Manifest.permission.ACCESS_FINE_LOCATION,
                  Manifest.permission.ACCESS_COARSE_LOCATION
          ).withListener(object : MultiplePermissionsListener{
              override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                  if(report!!.areAllPermissionsGranted()){
                      requestLocation()
                  }
                  if(report.isAnyPermissionPermanentlyDenied){

                      Toast.makeText(this@MainActivity,"permission denied",Toast.LENGTH_SHORT).show()
                  }
              }


              override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                  showRationDialogForPermission()
              }

          }).onSameThread().check()
        }
    }

    private fun isLoationEnabled():Boolean{
        val locationManger : LocationManager = getSystemService(Context.LOCATION_SERVICE)as LocationManager

        return  locationManger.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManger.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private  fun showRationDialogForPermission(){
        AlertDialog.Builder(this).setMessage("permission turnt off").setPositiveButton(
                "go to settings",){
            _,_->try {
                val intent  = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package",packageName,null)
            intent.data = uri
            startActivity(intent)
            }catch (e:ActivityNotFoundException){e.printStackTrace()}
        }.setNegativeButton("concle"){
            dialog,_->dialog.dismiss()
        }.show()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation(){
        val mLocationReqest = LocationRequest()
        mLocationReqest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        mFusedLocationProviderClient.requestLocationUpdates(mLocationReqest,mLocationCallBack, Looper.myLooper())
    }

   private val mLocationCallBack = object :LocationCallback(){
       override fun onLocationResult(locationResult: LocationResult) {

           val mLastLocatin:Location = locationResult.lastLocation
           val latitude = mLastLocatin.latitude

           val longitude = mLastLocatin.longitude
       }
   }
}