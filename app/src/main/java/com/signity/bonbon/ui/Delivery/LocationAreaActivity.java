package com.signity.bonbon.ui.Delivery;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.AppUtils;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.app.AppController;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.model.GetStoreAreaModel1;
import com.signity.bonbon.model.Store;
import com.signity.bonbon.model.StoreAreaListModel;
import com.signity.bonbon.model.StoreAreaModel;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.service.FetchAddressIntentService;
import com.signity.bonbon.ui.Location.SearchLocationActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by rajesh on 14/7/16.
 */
public class LocationAreaActivity extends AddGoogleLocationServicesActivity implements OnMapReadyCallback, View.OnClickListener {


    GoogleMap mMap;
    SupportMapFragment mapFragment;

    private LatLng mCenterLatLong;


    TextView textViewlocation;

    /*Address component*/
    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput;

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;

    ImageButton backButton;
    Button addArea;
    List<StoreAreaListModel> areaList;
    double currentLat,currentLong;
    Store store;
    AppDatabase appDb;
    String storeId;
    PrefManager prefManager;
    String radiusIn="";
    long radius=0;
    String code="";
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private  final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_loc_area_activity);
        appDb = DbAdapter.getInstance().getDb();
        prefManager = new PrefManager(this);
        storeId = prefManager.getSharedValue(AppConstant.STORE_ID);
        radiusIn= prefManager.getSharedValue(AppConstant.RADIUS_IN);
        store = appDb.getStore(storeId);

        textViewlocation = (TextView) findViewById(R.id.location);
        addArea = (Button) findViewById(R.id.addArea);
        backButton = (ImageButton) findViewById(R.id.backButton);
        mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mResultReceiver = new AddressResultReceiver(new Handler());



        addArea.setOnClickListener(this);
        backButton.setOnClickListener(this);
        textViewlocation.setOnClickListener(this);

        getStoreArea();

        if (!canAccessLocation()) {

            requestPermission();

        }
    }

    private void requestPermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
//            Toast.makeText(LocationAreaActivity.this,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }


    private boolean canAccessLocation() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED){
            return  true;
        }
        else return false;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mCenterLatLong = cameraPosition.target;

                mMap.clear();

                try {
                    currentLat = mCenterLatLong.latitude;
                    currentLong = mCenterLatLong.longitude;
                    Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);
                    startIntentService(mLocation);

                    gettingAreaId();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        });

    }

    private void gettingAreaId() {

        code="";
        radius=calculateDistance(radiusIn);

        for(int i=0; i<areaList.size(); i++){

            long areaRadius= Long.parseLong(areaList.get(i).getRadius());

            if(radius<=areaRadius){
                code=areaList.get(i).getAreaId();
                break;
            }

        }
    }

    protected void startIntentService(Location mLocation) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    /*Update Location or set map component on start receiveing location*/
    @Override
    protected void updateLocation(Location mLastLocation) {
        mCenterLatLong = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // check if map is created successfully or not
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;

            latLong = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong).zoom(19f).build();

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

//            textViewlocation.setText("Lat : " + mLastLocation.getLatitude() + "," + "Long : " + mLastLocation.getLongitude());
            startIntentService(mLastLocation);

        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.msg_toast_map_error), Toast.LENGTH_SHORT)
                    .show();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.addArea:

                try {
                    if(code.isEmpty()){

                        if(areaList!=null && areaList.size()!=0){
                            if(areaList.get(areaList.size()-1).getRadiusCircle().equalsIgnoreCase("Above")){
                                code=areaList.get(areaList.size()-1).getAreaId();
                                addAddress();
                            }
                            else {
                                DialogHandler dialogHandler = new DialogHandler(LocationAreaActivity.this);
                                dialogHandler.setdialogForFinish(getResources().getString(R.string.dialog_title), getString(R.string.msg_dialog_not_able_to_deliver), true);
                            }
                        }


                    }else {
                        addAddress();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.backButton:
                try {
                    if(code.isEmpty()){

                        if(areaList!=null && areaList.size()!=0){
                            if(areaList.get(areaList.size()-1).getRadiusCircle().equalsIgnoreCase("Above")){
                                code=areaList.get(areaList.size()-1).getAreaId();
                                addAddress();
                            }
                            else {
                                DialogHandler dialogHandler = new DialogHandler(LocationAreaActivity.this);
                                dialogHandler.setdialogForFinish(getResources().getString(R.string.dialog_title), getString(R.string.msg_dialog_not_able_to_deliver), true);
                            }
                        }


                    }else {
                        addAddress();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.location:
                openAutocompleteActivity();
//                Intent intent=new Intent(LocationAreaActivity.this, SearchLocationActivity.class);
//                startActivity();
//                AnimUtil.slideFromRightAnim(LocationAreaActivity.this);
                break;
        }
    }


    private void addAddress(){
        String title = null;
        try {
            title = textViewlocation.getText().toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            title="";
        }
        if(title.isEmpty()){
            DialogHandler dialogHandler = new DialogHandler(LocationAreaActivity.this);
            dialogHandler.setdialogForFinish(getResources().getString(R.string.dialog_title), getString(R.string.msg_dialog_not_able_to_deliver), true);
        }else {
            Bundle b = new Bundle();
            b.putString("id", code);
            b.putString("areaName", title);
            Intent i = getIntent(); // gets the intent that called this
            i.putExtras(b);
            setResult(Activity.RESULT_OK, i);
            finish();
            AnimUtil.slidDownAnim(LocationAreaActivity.this);
        }


    }
    private long calculateDistance(String radiusIn) {

        Location locationA = new Location("point A");
        locationA.setLatitude(Double.parseDouble(store.getLat()));
        locationA.setLongitude(Double.parseDouble(store.getLng()));
        Location locationB = new Location("point B");
        locationB.setLatitude(currentLat);
        locationB.setLongitude(currentLong);
        double distance = locationA.distanceTo(locationB) ;
        double distance2=0.0;
        if(radiusIn.equalsIgnoreCase("km")){
            distance2=(distance/1000);
        }else if(radiusIn.equalsIgnoreCase("miles")){
            distance2=distance*0.000621371;
        }
        return Math.round(distance2);
    }

    private void getStoreArea() {

        ProgressDialogUtil.showProgressDialog(LocationAreaActivity.this);
        List<GetStoreAreaModel1> Area = new ArrayList<>();
        NetworkAdaper.getInstance().getNetworkServices().getStoreAreasList(new Callback<StoreAreaModel>() {


            @Override
            public void success(StoreAreaModel storeAreaModel, Response response) {
                if(storeAreaModel.getSuccess()){
                    if(storeAreaModel.getData()!=null && storeAreaModel.getData().size()!=0){
                        areaList=storeAreaModel.getData();
                    }
                }
                ProgressDialogUtil.hideProgressDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                DialogHandler dialogHandler = new DialogHandler(LocationAreaActivity.this);
                dialogHandler.setdialogForFinish(getResources().getString(R.string.dialog_title), getResources().getString(R.string.error_code_message), true);
            }
        });
    }


    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .build();

            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);

        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = getString(R.string.msg_toast_google_play_error)+" " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Toast.makeText(LocationAreaActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlacePicker.getPlace(this, data);
                // TODO call location based filter


                try {
                    LatLng latLong;


                    latLong = place.getLatLng();
                    textViewlocation.setText(place.getAddress());

                    //mLocationText.setText(place.getName() + "");

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLong).zoom(19f).tilt(70).build();


                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    mMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }


        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(LocationAreaActivity.this, data);
        } else if (resultCode == RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
        }
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY);

            mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA);

            mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY);
            mStateOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);

            Log.e("AddressResultReceiver", "AddressResultReceiver------>" + mAddressOutput + " --" + mAreaOutput);
            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
                //  showToast(getString(R.string.address_found));
            }

        }
    }

    protected void displayAddressOutput() {
        //  mLocationAddressTextView.setText(mAddressOutput);
        try {
            if (mAddressOutput != null)
                // mLocationText.setText(mAreaOutput+ "");

                textViewlocation.setText(mAddressOutput);
            //mLocationText.setText(mAreaOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(LocationAreaActivity.this);
    }
}
