package com.signity.bonbon.ui.contacts;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.ga.GATrackers;
import com.signity.bonbon.model.Store;

import java.util.List;

public class ContactActivity extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback {

    Button btnBack, btnCall;
    TextView title, txtStorename, txtAddress, txtName;
    AppDatabase appDb;
    public ProgressDialog progress;
    Store store;
    GoogleMap map;
    //    Adapter mAdapter;
    PrefManager prefManager;
    String storeId;
    private  final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_activity);
        ((Button) findViewById(R.id.btnSearch)).setVisibility(View.GONE);
        GATrackers.getInstance().trackScreenView(getString(R.string.ga_screen_contact_us));
        prefManager = new PrefManager(this);
        storeId = prefManager.getSharedValue(AppConstant.STORE_ID);
        appDb = DbAdapter.getInstance().getDb();
        store = appDb.getStore(storeId);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnBack = (Button) findViewById(R.id.backButton);
        btnCall = (Button) findViewById(R.id.btnCall);
        btnCall.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        title = (TextView) findViewById(R.id.textTitle);
        txtStorename = (TextView) findViewById(R.id.txtStorename);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
//        txtName = (TextView) findViewById(R.id.txtName);
        title.setText(getString(R.string.lbl_contact_us));

        if (store != null) {
            txtStorename.setText(store.getStoreName());
            txtAddress.setText(store.getLocation() + ", " + store.getCity() + ", " + store.getState());
//            txtName.setText(store.getContactPerson());
        }


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backButton:
                onBackPressed();
                break;

            case R.id.btnCall:

                PackageManager pm = getBaseContext().getPackageManager();
                if (pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + store.getContactNumber()));
                    startActivity(intent);
                    AnimUtil.slideFromRightAnim(ContactActivity.this);
                } else {
                    Toast.makeText(ContactActivity.this, getString(R.string.msg_toast_calling_not_supported), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(ContactActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        String latitudeString = store.getLat();
        String longitudeString = store.getLng();
        String address = store.getLocation() + "," + store.getCity() + "," + store.getState() + "," + store.getCountry() + "," + store.getZipcode();
        LatLng bonbon = null;
        Log.e("Location", store.getLocation());
        if (latitudeString != null && !latitudeString.isEmpty() && longitudeString != null && !longitudeString.isEmpty()) {
            double lat = Double.parseDouble(latitudeString);
            double lng = Double.parseDouble(longitudeString);
            bonbon = new LatLng(lat, lng);
            showLocationOnMap(bonbon);
        } else {
            new GetLatLngTask().execute(address);
        }
    }

    public void showLocationOnMap(LatLng latLng) {

        if (!canAccessLocation()) {

            requestPermission();

        } else {
            if (latLng != null) {
                String address = store.getLocation() + "," + store.getCity() + "," + store.getState() + "," + store.getCountry() + "," + store.getZipcode();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Toast.makeText(ContactActivity.this, "Location Permission denied", Toast.LENGTH_SHORT).show();
                    return;
                }
                map.setMyLocationEnabled(true);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                map.addMarker(new MarkerOptions()
                        .title(store.getStoreName())
                        .snippet(address)
                        .position(latLng));
            }
        }

    }


    private void requestPermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(ContactActivity.this,getString(R.string.msg_toast_gps_permission),Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    String latitudeString = store.getLat();
                    String longitudeString = store.getLng();
                    String address = store.getLocation() + "," + store.getCity() + "," + store.getState() + "," + store.getCountry() + "," + store.getZipcode();
                    LatLng bonbon = null;
                    Log.e("Location", store.getLocation());
                    if (latitudeString != null && !latitudeString.isEmpty() && longitudeString != null && !longitudeString.isEmpty()) {
                        double lat = Double.parseDouble(latitudeString);
                        double lng = Double.parseDouble(longitudeString);
                        bonbon = new LatLng(lat, lng);
                        showLocationOnMap(bonbon);
                    }
                }
                break;
        }
    }


    private boolean canAccessLocation() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            return true;
        } else return false;
    }

    class GetLatLngTask extends AsyncTask<String, Void, LatLng> {

        @Override
        protected LatLng doInBackground(String... strings) {
            return getLocationFromAddress(strings[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialogUtil.showProgressDialog(ContactActivity.this);
        }

        @Override
        protected void onPostExecute(LatLng latLng) {
            super.onPostExecute(latLng);
            ProgressDialogUtil.hideProgressDialog();
            if (latLng != null) {
                store.setLat(String.valueOf(latLng.latitude));
                store.setLng(String.valueOf(latLng.longitude));
                appDb.addStore(store);
            }
            showLocationOnMap(latLng);
        }
    }


    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(ContactActivity.this);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return p1;
    }

}
