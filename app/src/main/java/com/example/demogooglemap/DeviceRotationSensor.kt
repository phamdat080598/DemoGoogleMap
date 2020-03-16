package com.example.demogooglemap

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log


class DeviceRotationSensor(private val sensorManager: SensorManager) : SensorEventListener {
    private var mLastAccelerometers = floatArrayOf()
    private var mLastMagnetometers = floatArrayOf()
    private var mLastAccelerometerSet = false
    private var mLastMagnetometerSet = false
    private val mOrientation = FloatArray(3)
    private var mDirection = 0
    private var mAccelerometer: Sensor? = null
    private var mMagnetometer: Sensor? = null
    private lateinit var mOnDeviceRotationListener: IOnDeviceRotationListener

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.getType()) {
            Sensor.TYPE_ACCELEROMETER -> {
                mLastAccelerometers = event.values.clone()
                mLastAccelerometerSet = true
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                mLastMagnetometers = event.values.clone()
                mLastMagnetometerSet = true
            }
            else -> {
            }
        }
        val rotationMatrix = FloatArray(9)
        if (mLastMagnetometerSet && mLastAccelerometerSet) {
            val success = SensorManager.getRotationMatrix(
                rotationMatrix, null, mLastAccelerometers,
                mLastMagnetometers
            )
            if (success) {
                SensorManager.getOrientation(rotationMatrix, mOrientation)
                val bearing = Math.toDegrees(mOrientation[0].toDouble()).toInt()
                if (mOnDeviceRotationListener != null) {
                    mOnDeviceRotationListener.onRotate(bearing)
                    Log.d("nghicv", bearing.toString() + "")
                }
            }
        }
    }

    fun getDirection(): Float {
        return mDirection.toFloat()
    }

    fun register() {
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI)
    }

    fun unregister() {
        sensorManager.unregisterListener(this, mAccelerometer)
        sensorManager.unregisterListener(this, mMagnetometer)
    }

    fun setOnDeviceRotationListener(onDeviceRotationListener: IOnDeviceRotationListener) {
        mOnDeviceRotationListener = onDeviceRotationListener
    }

    interface IOnDeviceRotationListener {
        fun onRotate(bearing: Int)
    }
}