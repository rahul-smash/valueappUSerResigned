package com.signity.bonbon.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.model.AreaSwitchDataModel;
import com.signity.bonbon.model.AreaSwitchModel;
import com.signity.bonbon.model.EmailResponse;
import com.signity.bonbon.model.UserAddressModel;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.common.AddressSelectActivity;
import com.signity.bonbon.ui.Delivery.LocationAreaActivity;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by root on 14/10/15.
 */
public class AddAddressFragment extends Fragment implements View.OnClickListener {

    private static final int COUNTRY = 326;
    private static final int STATE = 327;
    private static final int CITY = 328;
    private static final int AREA = 329;
    public TextView done_text;
    public String addressId;
    EditText address_line1, address_line2, zip_code;
    Button country_name, state_name, city_name, area_name;
    View mView;
    String action;
    LinearLayout countryLayout, stateLayout, cityLayout, areaLayout;
    private String countryID = "", countryName = "";
    private String stateID = "", stateName = "";
    private String cityId = "", cityName = "";
    private String areaID = "", areaName = "";
    private PrefManager prefManager;
    private String userId;
    private String from;
    private TextView edit_address;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(getActivity());
        userId = prefManager.getSharedValue(AppConstant.ID);
        from = getArguments().getString(AppConstant.FROM, "");
        action = getArguments().getString(AppConstant.ACTION, "");
    }

    public static Fragment newInstance(Context context) {
        return Fragment.instantiate(context,
                AddAddressFragment.class.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(com.signity.bonbon.R.layout.add_address_fragment, container, false);
        countryLayout = (LinearLayout) mView.findViewById(R.id.countryLayout);
        stateLayout = (LinearLayout) mView.findViewById(R.id.stateLayout);
        cityLayout = (LinearLayout) mView.findViewById(R.id.cityLayout);
        areaLayout = (LinearLayout) mView.findViewById(R.id.areaLayout);
        edit_address = (TextView) mView.findViewById(com.signity.bonbon.R.id.edit_address);
        address_line1 = (EditText) mView.findViewById(com.signity.bonbon.R.id.address_line1);
        country_name = (Button) mView.findViewById(com.signity.bonbon.R.id.country_name);
        country_name.setOnClickListener(this);
        state_name = (Button) mView.findViewById(com.signity.bonbon.R.id.state_name);
        state_name.setOnClickListener(this);
        city_name = (Button) mView.findViewById(com.signity.bonbon.R.id.city_name);
        city_name.setOnClickListener(this);
        area_name = (Button) mView.findViewById(com.signity.bonbon.R.id.area_name);
        area_name.setOnClickListener(this);
        zip_code = (EditText) mView.findViewById(com.signity.bonbon.R.id.zip_code);
        done_text = (TextView) mView.findViewById(com.signity.bonbon.R.id.done_text);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        if (action.equalsIgnoreCase("EDIT")) {
            UserAddressModel object = (UserAddressModel) getArguments().getSerializable("object");
            addressId = object.getId();
            areaID = object.getAreaId();
            cityId = object.getCityId() != null ? object.getCityId() : "";
            areaName = object.getAreaName();
            edit_address.setText(getString(R.string.lbl_edit_address));

            city_name.setText(object.getCity() != null ? object.getCity() : "");
            area_name.setText(object.getAreaName() != null ? object.getAreaName() : "");
            state_name.setText(object.getState() != null ? object.getState() : "");
            country_name.setText(object.getCountry() != null ? object.getCountry() : "");

            address_line1.setText(object.getAddress().toString());
            zip_code.setText(object.getZipcode().toString());

        } else if (action.equalsIgnoreCase("ADD")) {
            edit_address.setText(getString(R.string.lbl_add_address));
        }

        getDeliveryAreaSwitchesStatus();

        done_text.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             if (action.equalsIgnoreCase("edit")) {

                                                 if (address_line1.getText().toString().isEmpty()) {
                                                     address_line1.setError(getString(R.string.lbl_required_address));
                                                 } else {
                                                     updateDeliveryAddress();
                                                 }

                                             } else {
                                                 if (address_line1.getText().toString().isEmpty()) {
                                                     address_line1.setError(getString(R.string.lbl_required_address));
                                                 } else if (areaID.isEmpty()) {
                                                     city_name.setError(getString(R.string.lbl_city));
                                                     area_name.setError(getString(R.string.lbl_area));
                                                 } else {
                                                     addNewDeliveryAddress();
                                                 }

                                             }
                                         }
                                     }

        );


        return mView;
    }

    private void getDeliveryAreaSwitchesStatus() {


        ProgressDialogUtil.showProgressDialog(getActivity());
        NetworkAdaper.getInstance().getNetworkServices().getDeliveryAreaSwitch(new Callback<AreaSwitchModel>() {

            @Override
            public void success(AreaSwitchModel areaSwitchModel, Response response) {
                ProgressDialogUtil.hideProgressDialog();
                if (areaSwitchModel.getSuccess()) {

                    AreaSwitchDataModel model = areaSwitchModel.getData();

                    if (model != null) {
                        prefManager.storeAreaSwitch(AppConstant.SHOW_MAP, (!model.getDeliveryArea().isEmpty() ? model.getDeliveryArea() : "0"));
                        prefManager.storeAreaSwitch(AppConstant.CITY, (!model.getCity().isEmpty() ? model.getCity() : "0"));
                        prefManager.storeAreaSwitch(AppConstant.STATE, (!model.getState().isEmpty() ? model.getState() : "0"));
                        prefManager.storeAreaSwitch(AppConstant.COUNTRY, (!model.getCountry().isEmpty() ? model.getCountry() : "0"));
                        prefManager.storeAreaSwitch(AppConstant.RADIUS_IN, (!model.getRadiusIn().isEmpty() ? model.getRadiusIn() : "km"));

                        setLayout();
                    }
                } else {
//                    DialogHandler dialogHandler = new DialogHandler(getActivity());
//                    dialogHandler.setdialogForFinish("Message", "" + areaSwitchModel.getMessage(), false);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                DialogHandler dialogHandler = new DialogHandler(getActivity());
                dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), false);
            }
        });

    }


    private void updateDeliveryAddress() {

        ProgressDialogUtil.showProgressDialog(getActivity());


        PrefManager prefManager = new PrefManager(getActivity());
        String userId = prefManager.getSharedValue(AppConstant.ID);

        String firstName = prefManager.getSharedValue(AppConstant.NAME);
        if (firstName.isEmpty()) {
            firstName = "Guest";
        }
        String mobileNumber = prefManager.getSharedValue(AppConstant.PHONE);
        String email = prefManager.getSharedValue(AppConstant.EMAIL);
        if (email.isEmpty()) {
            email = "No Email Info";
        }
        String addressLine1 = String.valueOf(address_line1.getText());
        String cityName = String.valueOf(city_name.getText());
        String stateName = String.valueOf(state_name.getText());
        String countryName = String.valueOf(country_name.getText());
        String zipCode = String.valueOf(zip_code.getText());
        if (zipCode.isEmpty()) {
            zipCode = "0";
        }
        Map<String, String> param = new HashMap<String, String>();
        param.put("user_id", userId);
        param.put("address_id", addressId);
        param.put("area_id", areaID);
        param.put("method", "EDIT");
        param.put("first_name", firstName);
        param.put("mobile", mobileNumber);
        param.put("address", addressLine1);
        param.put("email", email);
        param.put("city", cityName);
        param.put("state", stateName);
        param.put("zipcode", zipCode);
        param.put("area_name", areaName);
        param.put("country", countryName);
        NetworkAdaper.getInstance().getNetworkServices().updateDeliveryAddress(param, new Callback<EmailResponse>() {


            @Override
            public void success(EmailResponse emailResponse, Response response) {
                ProgressDialogUtil.hideProgressDialog();
                if (emailResponse.getSuccess()) {
                    getActivity().onBackPressed();
                    InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(done_text.getWindowToken(), 0);
                } else {
                    DialogHandler dialogHandler = new DialogHandler(getActivity());
                    dialogHandler.setdialogForFinish("Message", "" + emailResponse.getMessage(), false);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                DialogHandler dialogHandler = new DialogHandler(getActivity());
                dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), false);
            }
        });

    }


    private void addNewDeliveryAddress() {
        ProgressDialogUtil.showProgressDialog(getActivity());

        PrefManager prefManager = new PrefManager(getActivity());
        String userId = prefManager.getSharedValue(AppConstant.ID);

        String firstName = prefManager.getSharedValue(AppConstant.NAME);
        if (firstName.isEmpty()) {
            firstName = "Guest";
        }
        String mobileNumber = prefManager.getSharedValue(AppConstant.PHONE);
        String email = prefManager.getSharedValue(AppConstant.EMAIL);
        if (email.isEmpty()) {
            email = "No Email Info";
        }
        String addressLine1 = String.valueOf(address_line1.getText());
        String cityName = String.valueOf(city_name.getText());
        String stateName = String.valueOf(state_name.getText());
        String countryName = String.valueOf(country_name.getText());
        String zipCode = String.valueOf(zip_code.getText());
        if (zipCode.isEmpty()) {
            zipCode = "0";
        }

        Map<String, String> param = new HashMap<String, String>();

        param.put("user_id", userId);
        param.put("method", "ADD");
        param.put("area_id", areaID);
        param.put("first_name", firstName);
        param.put("mobile", mobileNumber);
        param.put("address", addressLine1);
        param.put("email", email);
        param.put("city", cityName);
        param.put("state", stateName);
        param.put("zipcode", zipCode);
        param.put("area_name", areaName);
        param.put("country", countryName);
        NetworkAdaper.getInstance().getNetworkServices().addNewDeliveryAddress(param, new Callback<EmailResponse>() {


            @Override
            public void success(EmailResponse emailResponse, Response response) {
                ProgressDialogUtil.hideProgressDialog();
                if (emailResponse.getSuccess()) {
                    getActivity().onBackPressed();

                    InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(done_text.getWindowToken(), 0);

                } else {
                    DialogHandler dialogHandler = new DialogHandler(getActivity());
                    dialogHandler.setdialogForFinish("Message", "" + emailResponse.getMessage(), false);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                DialogHandler dialogHandler = new DialogHandler(getActivity());
                dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), false);
            }
        });
    }

    private void setLayout() {

        if (prefManager.getAreaSwitch(AppConstant.COUNTRY).equalsIgnoreCase("0")) {
            countryLayout.setVisibility(View.GONE);
        } else {
            countryLayout.setVisibility(View.VISIBLE);
        }
        if (prefManager.getAreaSwitch(AppConstant.STATE).equalsIgnoreCase("0")) {
            stateLayout.setVisibility(View.GONE);
        } else {
            stateLayout.setVisibility(View.VISIBLE);
        }
        if (prefManager.getAreaSwitch(AppConstant.CITY).equalsIgnoreCase("0")) {
            cityLayout.setVisibility(View.GONE);
        } else {
            cityLayout.setVisibility(View.VISIBLE);
        }
        /*if (prefManager.getAreaSwitch(AppConstant.SHOW_MAP).equalsIgnoreCase("0")) {
            areaLayout.setVisibility(View.GONE);
        } else {
            areaLayout.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.country_name:
                Intent intentCountry = new Intent(getActivity(),
                        AddressSelectActivity.class);
                intentCountry.putExtra("typeName", "Country");
                intentCountry.putExtra("typeNameValue", "");
                intentCountry.putExtra("id", "");
                startActivityForResult(intentCountry, COUNTRY);
//                AnimUtil.slideUpAnim(getActivity());

                break;

            case R.id.state_name:
                if (countryID.isEmpty()) {
                    if (prefManager.getAreaSwitch(AppConstant.COUNTRY).equalsIgnoreCase("0")) {
                        Intent intentState = new Intent(getActivity(),
                                AddressSelectActivity.class);
                        intentState.putExtra("typeName", "State");
                        intentState.putExtra("typeNameValue", "");
                        intentState.putExtra("id", "");
                        startActivityForResult(intentState, STATE);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.msg_toast_select_country), Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Intent intentState = new Intent(getActivity(),
                            AddressSelectActivity.class);
                    intentState.putExtra("typeName", "State");
                    intentState.putExtra("typeNameValue", countryName);
                    intentState.putExtra("id", countryID);
                    startActivityForResult(intentState, STATE);
//                AnimUtil.slideUpAnim(getActivity());
                }

                break;

            case R.id.city_name:
                if (stateID.isEmpty()) {

                    if (prefManager.getAreaSwitch(AppConstant.STATE).equalsIgnoreCase("0")) {
                        Intent intentCity = new Intent(getActivity(),
                                AddressSelectActivity.class);
                        intentCity.putExtra("typeName", "City");
                        intentCity.putExtra("typeNameValue", "");
                        intentCity.putExtra("id", "");
                        startActivityForResult(intentCity, CITY);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.msg_toast_select_state), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Intent intentCity = new Intent(getActivity(),
                            AddressSelectActivity.class);
                    intentCity.putExtra("typeName", "City");
                    intentCity.putExtra("typeNameValue", stateName);
                    intentCity.putExtra("id", stateID);
                    startActivityForResult(intentCity, CITY);
//                AnimUtil.slideUpAnim(getActivity());
                }

                break;
            case R.id.area_name:

                if (cityId.isEmpty()) {

                    if (prefManager.getAreaSwitch(AppConstant.CITY).equalsIgnoreCase("0")) {
                        if(prefManager.getAreaSwitch(AppConstant.SHOW_MAP).equalsIgnoreCase("0")){
                            Intent intentArea = new Intent(getActivity(),
                                    AddressSelectActivity.class);
                            intentArea.putExtra("typeName", "Area");
                            intentArea.putExtra("typeNameValue", "");
                            intentArea.putExtra("id", "");
                            startActivityForResult(intentArea, AREA);
                        }
                        else if(prefManager.getAreaSwitch(AppConstant.SHOW_MAP).equalsIgnoreCase("1")){

                            Intent intentArea = new Intent(getActivity(),
                                    LocationAreaActivity.class);
                            startActivityForResult(intentArea, AREA);
                        }
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.msg_toast_select_city), Toast.LENGTH_SHORT).show();
                    }

                } else {

                 if(prefManager.getAreaSwitch(AppConstant.SHOW_MAP).equalsIgnoreCase("0")){
                     Intent intentArea = new Intent(getActivity(),
                             AddressSelectActivity.class);
                     intentArea.putExtra("typeName", "Area");
                     intentArea.putExtra("typeNameValue", cityName);
                     intentArea.putExtra("id", cityId);
                     startActivityForResult(intentArea, AREA);
                 }
                 else if(prefManager.getAreaSwitch(AppConstant.SHOW_MAP).equalsIgnoreCase("1")){

                     Intent intentArea = new Intent(getActivity(),
                             LocationAreaActivity.class);
                     startActivityForResult(intentArea, AREA);
                 }
//                    AnimUtil.slideUpAnim(getActivity());
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == COUNTRY) {
            if (resultCode == Activity.RESULT_OK) {
                String id = data.getExtras().getString("id");
                String typeName = data.getExtras().getString("typeName");

                if (id != null) {
                    countryID = id;
                    countryName = typeName;
                    country_name.setText(typeName);
//                    buttonCountryCode.setText(code);
                }
            }
        } else if (requestCode == STATE) {
            if (resultCode == Activity.RESULT_OK) {
                String id = data.getExtras().getString("id");
                String typeName = data.getExtras().getString("typeName");

                if (id != null) {
                    stateID = id;
                    stateName = typeName;
                    state_name.setText(typeName);
//                    buttonCountryCode.setText(code);
                }
            }
        } else if (requestCode == CITY) {

            if (resultCode == Activity.RESULT_OK) {
                String id = data.getExtras().getString("id");
                String typeName = data.getExtras().getString("typeName");

                if (id != null) {
                    cityId = id;
                    cityName = typeName;
                    city_name.setText(typeName);
//                    buttonCountryCode.setText(code);
                }
            }

        } else if (requestCode == AREA) {

            if (resultCode == Activity.RESULT_OK) {
                String id = data.getExtras().getString("id");
                String areaname = data.getExtras().getString("areaName");

                if (id != null) {
                    areaID = id;
                    areaName = areaname;
                    area_name.setText(areaName);
//                    buttonCountryCode.setText(code);
                }
            }
        }

    }
}
