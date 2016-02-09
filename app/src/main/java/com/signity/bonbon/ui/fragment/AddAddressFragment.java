package com.signity.bonbon.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.Utilities.Util;
import com.signity.bonbon.gcm.GCMClientManager;
import com.signity.bonbon.model.EmailResponse;
import com.signity.bonbon.model.UserAddressModel;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.common.CityAreaActivitiy;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by root on 14/10/15.
 */
public class AddAddressFragment extends Fragment implements View.OnClickListener {

    EditText first_name, mobilenumber, email_address, address_line1, address_line2, zip_code;
    Button city_name, state_name;
    public TextView done_text;
    View mView;
    String cityId = "", cityName = "";
    public String addressId;

    private static final int CITY = 328;
    private static final int AREA = 329;


    private GCMClientManager pushClientManager;
    String action;
    private PrefManager prefManager;
    private String userId;
    private String areaID = "", areaName;
    private String from;
    private TextView edit_address;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pushClientManager = new GCMClientManager(getActivity(), AppConstant.PROJECT_NUMBER);
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
        edit_address = (TextView) mView.findViewById(com.signity.bonbon.R.id.edit_address);
        first_name = (EditText) mView.findViewById(com.signity.bonbon.R.id.first_name);
        mobilenumber = (EditText) mView.findViewById(com.signity.bonbon.R.id.mobilenumber);
        email_address = (EditText) mView.findViewById(com.signity.bonbon.R.id.email_address);
        address_line1 = (EditText) mView.findViewById(com.signity.bonbon.R.id.address_line1);
        city_name = (Button) mView.findViewById(com.signity.bonbon.R.id.city_name);
        city_name.setOnClickListener(this);
        state_name = (Button) mView.findViewById(com.signity.bonbon.R.id.state_name);
        state_name.setOnClickListener(this);
        zip_code = (EditText) mView.findViewById(com.signity.bonbon.R.id.zip_code);
        done_text = (TextView) mView.findViewById(com.signity.bonbon.R.id.done_text);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        if (action.equalsIgnoreCase("EDIT")) {
            UserAddressModel object = (UserAddressModel) getArguments().getSerializable("object");
            addressId = object.getId();
            areaID = object.getAreaId();
            edit_address.setText("Edit address");
            first_name.setText(object.getFirstName().toString());
            mobilenumber.setText(object.getMobile().toString());
            email_address.setText(object.getEmail().toString());

            if (!first_name.getText().toString().isEmpty()) {
                first_name.setInputType(InputType.TYPE_NULL);
                first_name.setEnabled(false);
            }
            if (!mobilenumber.getText().toString().isEmpty()) {
                mobilenumber.setInputType(InputType.TYPE_NULL);
                mobilenumber.setEnabled(false);
            }
            if (!email_address.getText().toString().isEmpty()) {
                email_address.setInputType(InputType.TYPE_NULL);
                email_address.setEnabled(false);
            }

            address_line1.setText(object.getAddress().toString());
            zip_code.setText(object.getZipcode().toString());
        } else if (action.equalsIgnoreCase("ADD")) {
            edit_address.setText("Add address");
            first_name.setText(prefManager.getSharedValue(AppConstant.NAME));
            mobilenumber.setText(prefManager.getSharedValue(AppConstant.PHONE));
            email_address.setText(prefManager.getSharedValue(AppConstant.EMAIL));
            if (!first_name.getText().toString().isEmpty()) {
                first_name.setInputType(InputType.TYPE_NULL);
                first_name.setEnabled(false);
            }
            if (!mobilenumber.getText().toString().isEmpty()) {
                mobilenumber.setInputType(InputType.TYPE_NULL);
                mobilenumber.setEnabled(false);
            }
            if (!email_address.getText().toString().isEmpty()) {
                email_address.setInputType(InputType.TYPE_NULL);
                email_address.setEnabled(false);
            }
        }

        done_text.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             if (action.equalsIgnoreCase("edit")) {

                                                 if (first_name.getText().toString().isEmpty()) {
                                                     first_name.setError("Name Required");
                                                 } else if (mobilenumber.getText().toString().isEmpty()) {
                                                     mobilenumber.setError("Mobile Number Required");
                                                 } else if (address_line1.getText().toString().isEmpty()) {
                                                     address_line1.setError("Address Required");
                                                 } else {
                                                     if (!email_address.getText().toString().isEmpty()) {
                                                         if (!Util.checkValidEmail(email_address.getText().toString())) {
                                                             email_address.setError("Enter Valid Email");
                                                         } else {
                                                             updateDeliveryAddress();
                                                         }
                                                     } else {
                                                         updateDeliveryAddress();
                                                     }
                                                 }

                                             } else {

                                                 if (first_name.getText().toString().isEmpty()) {
                                                     first_name.setError("Name Required");
                                                 } else if (mobilenumber.getText().toString().isEmpty()) {
                                                     mobilenumber.setError("Mobile Number Required");
                                                 } else if (address_line1.getText().toString().isEmpty()) {
                                                     address_line1.setError("Address Required");
                                                 } else if (areaID.isEmpty()) {
                                                     city_name.setError("City");
                                                     state_name.setError("Area");
                                                 } else {
                                                     if (!email_address.getText().toString().isEmpty()) {
                                                         if (!Util.checkValidEmail(email_address.getText().toString())) {
                                                             email_address.setError("Enter Valid Email");
                                                         } else {
                                                             addNewDeliveryAddress();
                                                         }
                                                     } else {
                                                         addNewDeliveryAddress();
                                                     }
                                                 }

                                             }

                                         }
                                     }

        );


        return mView;
    }

    private void updateDeliveryAddress() {

        ProgressDialogUtil.showProgressDialog(getActivity());


        PrefManager prefManager = new PrefManager(getActivity());
        String userId = prefManager.getSharedValue(AppConstant.ID);

        String firstName = String.valueOf(first_name.getText());
        String mobileNumber = String.valueOf(mobilenumber.getText());
        String email = String.valueOf(email_address.getText());
        String addressLine1 = String.valueOf(address_line1.getText());
        String cityName = String.valueOf(city_name.getText());
        String stateName = String.valueOf(state_name.getText());
        String zipCode = String.valueOf(zip_code.getText());

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


        NetworkAdaper.getInstance().getNetworkServices().updateDeliveryAddress(param, new Callback<EmailResponse>() {


            @Override
            public void success(EmailResponse emailResponse, Response response) {
                ProgressDialogUtil.hideProgressDialog();
                if (emailResponse.getSuccess()) {
                    getActivity().onBackPressed();
                } else {
//                    Toast.makeText(getActivity(), emailResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
            }
        });

    }


    private void addNewDeliveryAddress() {
        ProgressDialogUtil.showProgressDialog(getActivity());

        PrefManager prefManager = new PrefManager(getActivity());
        String userId = prefManager.getSharedValue(AppConstant.ID);

        String firstName = String.valueOf(first_name.getText());
        String mobileNumber = String.valueOf(mobilenumber.getText());
        String email = String.valueOf(email_address.getText());
        String addressLine1 = String.valueOf(address_line1.getText());
        String cityName = String.valueOf(city_name.getText());
        String stateName = String.valueOf(state_name.getText());
        String zipCode = String.valueOf(zip_code.getText());

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


        NetworkAdaper.getInstance().getNetworkServices().addNewDeliveryAddress(param, new Callback<EmailResponse>() {


            @Override
            public void success(EmailResponse emailResponse, Response response) {
                ProgressDialogUtil.hideProgressDialog();
                if (emailResponse.getSuccess()) {
                    getActivity().onBackPressed();
                } else {
                    Toast.makeText(getActivity(), "Failed to connect", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case com.signity.bonbon.R.id.city_name:
                Intent intentCity = new Intent(getActivity(),
                        CityAreaActivitiy.class);
                intentCity.putExtra("key", "city");
                intentCity.putExtra("title", "City");
                startActivityForResult(intentCity, CITY);
//                AnimUtil.slideUpAnim(getActivity());

                break;
            case com.signity.bonbon.R.id.state_name:

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
        }
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
                    city_name.setText(title);
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
                    state_name.setText(title);
//                    buttonCountryCode.setText(code);
                }
            }

        }

    }
}
