package com.signity.bonbon.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.app.DataAdapter;
import com.signity.bonbon.model.PickupAdressModel;
import com.signity.bonbon.ui.Delivery.DeliveryActivity;
import com.signity.bonbon.ui.Delivery.DeliveryPickupActivity;
import com.signity.bonbon.ui.home.MainActivity;
import com.signity.bonbon.ui.shopcart.ShoppingCartActivity2;

/**
 * Created by root on 14/10/15.
 */
public class PickupDetailFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    private static final String TAG = PickupDetailFragment.class.getSimpleName();
    private static final int CITY = 328;
    private static final int AREA = 329;

    private String from, userId;

    private String cityId = "", cityName = "";
    private String areaID = "", areaName = "";
    GoogleMap map;
    private PickupAdressModel pickupAdressModel;
    private TextView textViewAddress;
    private Button buttonProceed, buttonOk;
    private Button btnCall;

    double lat = 0.0, lng = 0.0;
    private PrefManager prefManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(getActivity());
        userId = prefManager.getSharedValue(AppConstant.ID);
        from = getArguments().getString(AppConstant.FROM, "");
        pickupAdressModel = (PickupAdressModel) getArguments().getSerializable("object");
        if (pickupAdressModel != null) {
            Log.i(TAG, pickupAdressModel.toString());
        }
    }

    public static Fragment newInstance(Context context) {
        return Fragment.instantiate(context,
                PickupDetailFragment.class.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.layout_fragment_pickupview, container, false);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        textViewAddress = (TextView) rootView.findViewById(R.id.textViewAddress);

        buttonProceed = (Button) rootView.findViewById(R.id.buttonProceed);
        buttonOk = (Button) rootView.findViewById(R.id.buttonOk);
        btnCall = (Button) rootView.findViewById(R.id.btnCall);
        textViewAddress.setText(pickupAdressModel.getPickupAdd() != null ?
                pickupAdressModel.getPickupAdd() : "");
        buttonProceed.setOnClickListener(this);
        btnCall.setOnClickListener(this);
        buttonOk.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        from = getArguments().getString(AppConstant.FROM, "");
        if (from.equalsIgnoreCase("shopping_cart2")) {
            buttonProceed.setVisibility(View.INVISIBLE);
            buttonOk.setVisibility(View.VISIBLE);
            buttonOk.setText(getString(R.string.str_lbl_get_directions));
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        this.map.getUiSettings().setMapToolbarEnabled(true);
        this.map.getUiSettings().setMyLocationButtonEnabled(true);
        if (pickupAdressModel != null) {
            String latitudeString = pickupAdressModel.getPickupLat();
            String longitudeString = pickupAdressModel.getPickupLng();
            String address = pickupAdressModel.getPickupAdd();
            LatLng latLng = null;
            if (latitudeString != null && !latitudeString.isEmpty() && longitudeString != null && !longitudeString.isEmpty()) {
                lat = Double.parseDouble(latitudeString);
                lng = Double.parseDouble(longitudeString);
                latLng = new LatLng(lat, lng);
                showLocationOnMap(latLng, address);
            }
        }
    }

    public void showLocationOnMap(LatLng latLng, String address) {
        if (latLng != null) {
            String mAddress = address;

            //map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
            map.addMarker(new MarkerOptions()
                    .title(getString(R.string.str_lbl_dilevery_pickup))
                    .snippet(address)
                    .position(latLng));
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonProceed:
                /*Intent intent = new Intent(getActivity(), ShoppingCartActivity2.class);
                intent.putExtra("addressId", pickupAdressModel.getId());
                intent.putExtra("userId", userId);
                intent.putExtra("isForPickup", "yes");
                intent.putExtra("shiping_charges", "0");
                intent.putExtra("minimum_charges", "0");
                intent.putExtra("user_address", pickupAdressModel.getPickupAdd());
                startActivity(intent);
                getActivity().finish();
                AnimUtil.slideFromRightAnim(getActivity());*/
                showAlertDialogForConfirm(getActivity(), getString(R.string.msg_dialog_confirmation), getResources().getString(R.string.str_payment_option_message));
                break;

            case R.id.btnCall:
                if (pickupAdressModel.getPickupPhone() != null && !pickupAdressModel.getPickupPhone().isEmpty()) {
                    PackageManager pm = getActivity().getBaseContext().getPackageManager();
                    if (pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                        Intent intentCall = new Intent(Intent.ACTION_DIAL);
                        intentCall.setData(Uri.parse("tel:" + pickupAdressModel.getPickupPhone()));
                        startActivity(intentCall);
                        AnimUtil.slideFromRightAnim(getActivity());
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.msg_toast_calling_not_supported), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.msg_toast_no_notavailable), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.buttonOk:
                if (lat != 0.0 && lng != 0.0) {
                    String url = "http://maps.google.com/maps?daddr=" + lat + "," + lng;
                    Intent intentMap = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(url));
                    startActivity(intentMap);
                    getActivity().finish();
                }
//                getActivity().onBackPressed();
                break;
        }
    }

    public void showAlertDialogForConfirm(Context context, String title, String message) {

        if(prefManager.getSharedValue(AppConstant.ONLINE_PAYMENT).equalsIgnoreCase("0")){

            Intent intent = new Intent(getActivity(), ShoppingCartActivity2.class);
            intent.putExtra("addressId", pickupAdressModel.getId());
            intent.putExtra("userId", userId);
            intent.putExtra("isForPickup", "yes");
            intent.putExtra("shiping_charges", "0");
            intent.putExtra("minimum_charges", "0");
            intent.putExtra("user_address", pickupAdressModel.getPickupAdd());
            intent.putExtra("payment_method", "2");
            startActivity(intent);
            getActivity().finish();
            AnimUtil.slideFromRightAnim(getActivity());

        }else if(prefManager.getSharedValue(AppConstant.ONLINE_PAYMENT).equalsIgnoreCase("1")){

            final Dialog dialog = new Dialog(getActivity());

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.custom_dialog);
            TextView positveButton = (TextView) dialog.findViewById(R.id.yesBtn);
            positveButton.setVisibility(View.VISIBLE);
            TextView negativeButton = (TextView) dialog.findViewById(R.id.noBtn);
            negativeButton.setVisibility(View.VISIBLE);
            TextView titleTxt = (TextView) dialog.findViewById(R.id.title);
            TextView messageText = (TextView) dialog.findViewById(R.id.message);
            titleTxt.setText(""+title);
            positveButton.setText(getResources().getString(R.string.str_cod));
            negativeButton.setText(getString(R.string.lbl_you_online));
            messageText.setText(""+message);

            dialog.setCanceledOnTouchOutside(true);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            positveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ShoppingCartActivity2.class);
                    intent.putExtra("addressId", pickupAdressModel.getId());
                    intent.putExtra("userId", userId);
                    intent.putExtra("isForPickup", "yes");
                    intent.putExtra("shiping_charges", "0");
                    intent.putExtra("minimum_charges", "0");
                    intent.putExtra("user_address", pickupAdressModel.getPickupAdd());
                    intent.putExtra("payment_method", "2");
                    startActivity(intent);
                    getActivity().finish();
                    AnimUtil.slideFromRightAnim(getActivity());
                    dialog.dismiss();
                }
            });

            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ShoppingCartActivity2.class);
                    intent.putExtra("addressId", pickupAdressModel.getId());
                    intent.putExtra("userId", userId);
                    intent.putExtra("isForPickup", "yes");
                    intent.putExtra("shiping_charges", "0");
                    intent.putExtra("minimum_charges", "0");
                    intent.putExtra("user_address", pickupAdressModel.getPickupAdd());
                    intent.putExtra("payment_method", "3");
                    startActivity(intent);
                    getActivity().finish();
                    AnimUtil.slideFromRightAnim(getActivity());
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
        else {
            Intent intent = new Intent(getActivity(), ShoppingCartActivity2.class);
            intent.putExtra("addressId", pickupAdressModel.getId());
            intent.putExtra("userId", userId);
            intent.putExtra("isForPickup", "yes");
            intent.putExtra("shiping_charges", "0");
            intent.putExtra("minimum_charges", "0");
            intent.putExtra("user_address", pickupAdressModel.getPickupAdd());
            intent.putExtra("payment_method", "2");
            startActivity(intent);
            getActivity().finish();
            AnimUtil.slideFromRightAnim(getActivity());
        }

    }



}
