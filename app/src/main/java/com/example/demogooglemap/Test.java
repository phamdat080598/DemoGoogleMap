package com.example.demogooglemap;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.location.Location;
import android.util.Property;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

public class Test {

    static GoogleMap map;
    ArrayList<LatLng> _trips = new ArrayList<>() ;
    Marker _marker;
    LatLngInterpolator _latLngInterpolator = new LatLngInterpolator.Spherical();

    public void animateLine(ArrayList<LatLng> Trips, GoogleMap map, Marker  marker, Context current){
        _trips.addAll(Trips);
        _marker = marker;

        animateMarker();
    }
    public void animateMarker() {
        TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
            @Override
            public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                _marker.setRotation((float) SphericalUtil.computeHeading(startValue, endValue)+90F);//90 do chiều icon mình thêm vào marker tùy chỉnh cho phù hợp
                return _latLngInterpolator.interpolate(fraction, startValue, endValue);
            }
        };
        Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");

        ObjectAnimator animator = ObjectAnimator.ofObject(_marker, property, typeEvaluator, _trips.get(0));

        //ObjectAnimator animator = ObjectAnimator.o(view, "alpha", 0.0f);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
                //  animDrawable.stop();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //  animDrawable.stop();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                //  animDrawable.stop();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //  animDrawable.stop();
                if (_trips.size() > 1) {
                    _trips.remove(0);
                    animateMarker();
                }
            }
        });

        animator.setDuration(280);
        animator.start();
    }

//    private double bearingBetweenLocations(LatLng latLng1,LatLng latLng2) {
//
//        double PI = 3.14159;
//        double lat1 = latLng1.latitude * PI / 180;
//        double long1 = latLng1.longitude * PI / 180;
//        double lat2 = latLng2.latitude * PI / 180;
//        double long2 = latLng2.longitude * PI / 180;
//
//        double dLon = (long2 - long1);
//
//        double y = Math.sin(dLon) * Math.cos(lat2);
//        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
//                * Math.cos(lat2) * Math.cos(dLon);
//
//        double brng = Math.atan2(y, x);
//
//        brng = Math.toDegrees(brng);
//        brng = (brng + 360) % 360;
//
//        return brng-90;
//    }
}
