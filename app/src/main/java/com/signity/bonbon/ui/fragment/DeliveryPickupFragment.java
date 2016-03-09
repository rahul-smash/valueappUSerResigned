package com.signity.bonbon.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.gcm.GCMClientManager;
import com.signity.bonbon.model.GetPickupApiResponse;
import com.signity.bonbon.model.PickupAdressModel;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.Delivery.DeliveryActivity;
import com.signity.bonbon.ui.common.CityAreaActivitiy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by root on 14/10/15.
 */
public class DeliveryPickupFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = DeliveryPickupFragment.class.getSimpleName();
    private static final int CITY = 328;
    private static final int AREA = 329;

    private Button buttonCityName, buttonAreaName;
    private ImageView buttonDelivery, buttonPickup;

    private GCMClientManager pushClientManager;

    String from;
    String userId;
    private PrefManager prefManager;
    private AppDatabase appDb;

    private String cityId = "", cityName = "";
    private String areaID = "", areaName = "";
    public String addressId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDb = DbAdapter.getInstance().getDb();
        prefManager = new PrefManager(getActivity());
        userId = prefManager.getSharedValue(AppConstant.ID);
        prefManager = new PrefManager(getActivity());
        pushClientManager = new GCMClientManager(getActivity(), AppConstant.PROJECT_NUMBER);
        from = getArguments().getString(AppConstant.FROM, "");
    }

    public static Fragment newInstance(Context context) {
        return Fragment.instantiate(context,
                DeliveryPickupFragment.class.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.layout_select_pickup, container, false);
        buttonCityName = (Button) mView.findViewById(R.id.buttonCityName);
        buttonAreaName = (Button) mView.findViewById(R.id.buttonAreaName);
        buttonDelivery = (ImageView) mView.findViewById(R.id.buttonDelivery);
        buttonPickup = (ImageView) mView.findViewById(R.id.buttonPickup);
        buttonCityName.setOnClickListener(this);
        buttonAreaName.setOnClickListener(this);
        buttonDelivery.setOnClickListener(this);
        buttonPickup.setOnClickListener(this);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonCityName:
                Intent intentCity = new Intent(getActivity(),
                        CityAreaActivitiy.class);
                intentCity.putExtra("key", "city");
                intentCity.putExtra("title", "City");
                startActivityForResult(intentCity, CITY);
                break;
            case R.id.buttonAreaName:
                if (cityId.isEmpty()) {
                    Toast.makeText(getActivity(), "Select City First", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intentArea = new Intent(getActivity(),
                            CityAreaActivitiy.class);
                    intentArea.putExtra("key", "area");
                    intentArea.putExtra("city_id", cityId);
                    intentArea.putExtra("title", cityName);
                    intentArea.putExtra("city_name", cityName);
                    startActivityForResult(intentArea, AREA);
//                    AnimUtil.slideUpAnim(getActivity());
                }
                break;
            case R.id.buttonDelivery:
                Intent intentDelivery = null;
                buttonPickup.setSelected(false);
                buttonDelivery.setSelected(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intentDelivery = new Intent(getActivity(), DeliveryActivity.class);
                        intentDelivery.putExtra(AppConstant.FROM, "shop_cart");
                        startActivity(intentDelivery);
                        AnimUtil.slideFromRightAnim(getActivity());
                    }
                }, 500);
                break;
            case R.id.buttonPickup:
                if (areaID != null && !areaID.isEmpty()) {
                    buttonPickup.setSelected(true);
                    buttonDelivery.setSelected(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkForPickupLocation(areaID);
                        }
                    }, 500);
                } else {
                    Toast.makeText(getActivity(), "Please select your area for the pickup location", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void checkForPickupLocation(String areaID) {
        ProgressDialogUtil.showProgressDialog(getActivity());
        Map<String, String> params = new HashMap();
        params.put("area_id", areaID);
        NetworkAdaper.getInstance().getNetworkServices().getPickupLocation(params, new Callback<GetPickupApiResponse>() {
            @Override
            public void success(GetPickupApiResponse getPickupApiResponse, Response response) {
                ProgressDialogUtil.hideProgressDialog();
                if (getPickupApiResponse.getSuccess() != null ? getPickupApiResponse.getSuccess() : false) {
                    List<PickupAdressModel> list = getPickupApiResponse.getData();
                    if (list != null && list.size() != 0) {
                        openPickUpDisplayFragment(list.get(0));
                    } else {
                        alertDailogForNoPickup();
                    }
                } else {
                    alertDailogForNoPickup();
                }
            }


            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "" + error.getMessage());
                alertDailogForNoPickup();
            }

            private void alertDailogForNoPickup() {
                DialogHandler dialog = new DialogHandler(getActivity());
                dialog.setdialogForFinish("Message", "Sorry no pickup location available for above area", false);
            }
        });
    }

    private void openPickUpDisplayFragment(PickupAdressModel pickupAddressModel) {
        Fragment fragment = PickupDetailFragment.newInstance(getActivity());
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.FROM, from);
        bundle.putSerializable("object", pickupAddressModel);
        fragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.right_to_center_slide,
                R.anim.center_to_left_slide,
                R.anim.left_to_center_slide,
                R.anim.center_to_right_slide);
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CITY) {
            if (resultCode == Activity.RESULT_OK) {
                String code = data.getExtras().getString("code");
                String title = data.getExtras().getString("title");

                if (code != null) {
                    cityId = code;
                    cityName = title;
                    buttonCityName.setText(title);
//                    buttonCountryCode.setText(code);
                }
            }
        } else if (requestCode == AREA) {
            if (resultCode == Activity.RESULT_OK) {
                String code = data.getExtras().getString("code");
                String title = data.getExtras().getString("title");
                if (code != null) {
                    areaID = code;
                    areaName = title;
                    buttonAreaName.setText(title);
//                    buttonCountryCode.setText(code);
                }
            }
        }
    }
}
