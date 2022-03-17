package com.example.locatioinpermissionapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {


    val FINE_LOCATION_RQ = 101

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonTaps()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


    }

    private fun buttonTaps() {
        var fineLocation = findViewById<Button>(R.id.button1)
        var getLocation = findViewById<Button>(R.id.button2)

        fineLocation.setOnClickListener{
            checkForPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION, "location", FINE_LOCATION_RQ)
        }

        getLocation.setOnClickListener{
            fetchLocation()
        }

    }

    private fun fetchLocation() {

        val task = fusedLocationProviderClient.lastLocation

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
           ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
           return
        }
        task.addOnSuccessListener {
            if(it != null) {

                val mapIntent: Intent = Uri.parse(
                    "geo:${it.latitude},${it.longitude}?z=14"
                ).let { location ->
                    // Or map point based on latitude/longitude
                    val location2: Uri = Uri.parse("geo:${it.latitude},${it.longitude}?z=14") // z param is zoom level
                    Intent(Intent.ACTION_VIEW, location2)
                }
                startActivity(mapIntent)
               // Toast.makeText(applicationContext, "${it.latitude} ${it.longitude}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun checkForPermissions(permission: String, name: String, requestCode: Int) {


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_GRANTED -> {

                    Toast.makeText(applicationContext, "$name permission granted", Toast.LENGTH_SHORT).show()

                }
                shouldShowRequestPermissionRationale(permission) -> showDialog(permission, name, requestCode)
                else -> ActivityCompat.requestPermissions(this,arrayOf(permission), requestCode)
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fun innerCheck(name: String) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "$name permission refused", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(applicationContext, "$name permission granted", Toast.LENGTH_SHORT).show()
            }
        }
        when (requestCode) {
            FINE_LOCATION_RQ-> innerCheck("location")
        }
    }


    private fun showDialog(permission: String, name: String, requestCode: Int) {
        val builder =  AlertDialog.Builder(this)

        builder.apply{
            setMessage("Permission to access your $name is required to use this app")
            setTitle("Permission required")
            setPositiveButton("OK"){dialog, which ->
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
            }
        }
        val dialog = builder.create()
        dialog.show()
    }



}