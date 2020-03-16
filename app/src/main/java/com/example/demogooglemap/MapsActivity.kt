@file:Suppress("UNREACHABLE_CODE")

package com.example.demogooglemap

import android.Manifest
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*
import kotlin.math.abs
import kotlin.math.atan

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
//    DeviceRotationSensor.IOnDeviceRotationListener {

    private lateinit var mMap: GoogleMap

    private var isPermisstion = false

    private lateinit var locationClient: FusedLocationProviderClient
    private var marker: Marker? = null

    private lateinit var locationCallBack: LocationCallback
    private val duration = 20000

    private val listLatLng = arrayListOf<LatLng>()
    private var v: Float = 0F
    private var lat = 0.0
    private var lng = 0.0
    private var handler: Handler? = null
    private var start: LatLng? = null
    private var end: LatLng? = null
    private var index: Int = 0
    private var next: Int = 0
    private var polylineOptions: PolylineOptions? = null
    private var blackPolylineOps: PolylineOptions? = null
    private var blackPoline: Polyline? = null
    private var greyPoline: Polyline? = null
    private var myLocation: LatLng? = null

    private lateinit var mLatLngInterpolator : LatLngInterpolator

    private val points = arrayListOf<String>(
        "csi_C_zydSTRRPBB?@@B?@AFABfA|@XRVT",
        "}mi_CytydSeBdCSX?@C@A?A?AAC?o@k@",
        "usi_CcqydS?@k@x@aAhAIX",
        "mwi_CclydSe@MuB_@[GsAUgAQ",
        "abj_CaoydSYBIAc@G_@EMByB[gB]a@IaASqBg@_AQ[IaFeAMMw@OKO",
        "_`k_CywydSqAU_AYcB_@i@Qg@KiAQa@Im@M[MgB_@OAWCoAW{@S",
        "yxk_Cq_zdS]f@c@rAe@tAa@|@Uh@Ob@",
        "i~k_CstydSKTQZg@`AGFIHI@W@e@Ak@CcBK{AM{DY",
        "qpl_CcrydSYxDIfAAFKz@",
        "crl_C}gydS?DcAOC?C?A?A@CBA@ABC`@QvA",
        "qul_CadydSVCTAP@J?R@L@bDP"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        requestPermissions()

        if (isServicesConnect()) {
            if (isPermisstion) {
                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }
        locationClient = FusedLocationProviderClient(this)
        for (p in points) {
            listLatLng.addAll(DecodePoly().decodePoly(p))
        }

        mLatLngInterpolator = LatLngInterpolator.Linear()

        eventClick()
    }

    override fun onMapReady(googleMap: GoogleMap) {

        val latLng = DecodePoly().decodePoly(points[0])[0]

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15F)
        MarkerOptions()
        googleMap.isMyLocationEnabled = true;
        googleMap.isTrafficEnabled = false;
        googleMap.isIndoorEnabled = false;
        googleMap.isBuildingsEnabled = true;
        mMap = googleMap
        marker = mMap.addMarker(
            MarkerOptions().position(latLng).anchor(
                0.5f,
                0.5f
            ).icon(
                bitmapDescriptorFromVector(
                    this,
                    R.drawable.ic_car
                )
            ).title("Marker in VietNam").flat(true).anchor(0.5F, 0.5F)
        )
        mMap.moveCamera(cameraUpdate)
    }

    private fun eventClick() {
        btnRun.setOnClickListener {
            val builder = LatLngBounds.builder()
            for (latlng in listLatLng) {
                builder.include(latlng)
            }
            val bounds = builder.build()
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2)
            mMap.animateCamera(cameraUpdate)

            polylineOptions = PolylineOptions()
            polylineOptions!!.color(Color.GRAY)
            polylineOptions!!.width(5F)
            polylineOptions!!.startCap(SquareCap())
            polylineOptions!!.endCap(SquareCap())
            polylineOptions!!.jointType(JointType.ROUND)
            polylineOptions!!.addAll(listLatLng)

            greyPoline = mMap.addPolyline(polylineOptions)

            val test = Test()
            test.animateLine(listLatLng,mMap,marker,this)
        }
    }
    private fun requestPermissions() {
        if (!PermissionUtils.hasPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) || !PermissionUtils.hasPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || !PermissionUtils.hasPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            PermissionUtils.requestPermission(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 100
            )
        } else {
            isPermisstion = true
        }
    }

//    private fun gotoLocation(lat: Double, lng: Double) {
//        val latLng = LatLng(location!!.latitude, location!!.longitude)
//        if (locationOld == null) {
//            locationOld = location
//        }
////        val bearing = SphericalUtil.computeHeading(latLng, latLngOld).toFloat()
////        if(abs(bearing-bearingOld)>10F) {
//        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15F)
//        markerOptions.icon(
//            bitmapDescriptorFromVector(
//                this,
//                R.drawable.ic_car
//            )
//        )
//        marker = mMap.addMarker(
//            markerOptions.position(latLng).anchor(
//                0.5f,
//                0.5f
//            ).title("Marker in VietNam").flat(true).rotation(locationOld!!.bearingTo(location)).zIndex(
//                10F
//            )
//        )
//        mMap.moveCamera(cameraUpdate)
//        //bearingOld=bearing
////        }
//    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    private fun isServicesConnect(): Boolean {
        val availability = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        if (availability == ConnectionResult.SUCCESS) {
            return true
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(availability)) {
            val dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, availability, 404)
            dialog.show()
        } else {
            Toast.makeText(this, "không kết nối được với service!!", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            isPermisstion = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.type_map, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.none -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NONE
                return true
            }
            R.id.normal -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                return true
            }
            R.id.satellite -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                return true
            }
            R.id.terrain -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                return true
            }
            R.id.hybrid -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                return true
            }
            R.id.location -> {
                //getLocation()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}

