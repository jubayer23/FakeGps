package com.creative.fakegps;

import android.*;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.creative.fakegps.Utility.DeviceInfoUtils;
import com.creative.fakegps.Utility.RunnTimePermissions;
import com.creative.fakegps.alertbanner.AlertDialogForAnything;
import com.creative.fakegps.appdata.MydApplication;
import com.creative.fakegps.mockLocation.MockLocationProvider;

import android.location.LocationListener;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends BaseActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    MockLocationProvider mock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init();

        //initToolbar();

        checkAllPermissionsAndSetUpMap();
    }

    private void checkAllPermissionsAndSetUpMap() {
        /**
         * This is marshmallow runtime Permissions
         * It will ask user for grand permission in queue order[FIFO]
         * If user gave all permission then check whether user device has google play service or not!
         * NB : before adding runtime request for permission Must add manifest permission for that
         * specific request
         * */
        if (RunnTimePermissions.requestForAllRuntimePermissions(this)) {
            if (!DeviceInfoUtils.isGooglePlayServicesAvailable(MainActivity.this)) {
                AlertDialogForAnything.showAlertDialogWhenComplte(this, "Warning", "This app need google play service to work properly. Please install it!!", false);
            }

            setUpMap();
        }
    }

    private void setUpMap() {
        showProgressDialog("please wait..", true, false);
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        dismissProgressDialog();
        if (mMap != null) {
            return;
        }
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            RunnTimePermissions.requestForAllRuntimePermissions(this);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    public void startMocking(View view) {
        mock = new MockLocationProvider(LocationManager.NETWORK_PROVIDER, this);

        //Set test location
        mock.pushLocation(24.899602, 91.853744);

        LocationManager locMgr = (LocationManager)
                getSystemService(LOCATION_SERVICE);
        LocationListener lis = new LocationListener() {
            public void onLocationChanged(Location location) {
                //You will get the mock location
                Log.d("DEBUG_lat", String.valueOf(location.getLatitude()));
                Log.d("DEBUG_lang", String.valueOf(location.getLongitude()));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
            //...
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, lis);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RunnTimePermissions.PERMISSION_ALL) {
            // DeviceInfoUtils.checkMarshMallowPermission(this);
            int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
            int result2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
            //int result3 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
            if (result == PackageManager.PERMISSION_GRANTED
                    && result2 == PackageManager.PERMISSION_GRANTED) {
                //Log.d("DEBUG", "fragment attach");
                MydApplication.deviceImieNumber = DeviceInfoUtils.getDeviceImieNumber(this);

                setUpMap();
            }
        }


    }
}
