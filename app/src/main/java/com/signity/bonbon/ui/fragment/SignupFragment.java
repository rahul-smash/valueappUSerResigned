package com.signity.bonbon.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.model.LoginData;
import com.signity.bonbon.model.LoginModel;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.home.MainActivity;
import com.signity.bonbon.ui.login.LoginScreenActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by root on 18/3/16.
 */
public class SignupFragment extends Fragment implements View.OnClickListener {


    String from;

    Button cancelBtn,signupBtn, backButton;
    EditText firstName,emailId,phoneNum,password,confirmPass, edtReferCode;
    String phone,firstName_,email_,pass;


    ProgressDialogUtil progressDialogUtil;
    boolean isEmailExist = false, isNameExist = false;

    PrefManager prefManager;
    Bundle bundle;
    String name="", email="", type="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialogUtil=new ProgressDialogUtil();
        prefManager = new PrefManager(getActivity());
        try {
            name = getArguments().getString("name","").trim();
            email = getArguments().getString("email","").trim();
            type = getArguments().getString("type","").trim();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Fragment newInstance(Context context) {
        return Fragment.instantiate(context,
                SignupFragment.class.getName());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_signup, container, false);
        cancelBtn = (Button) rootView.findViewById(com.signity.bonbon.R.id.cancelBtn);
        signupBtn = (Button) rootView.findViewById(com.signity.bonbon.R.id.signupBtn);
        backButton = (Button) rootView.findViewById(com.signity.bonbon.R.id.backButton);
        firstName = (EditText) rootView.findViewById(com.signity.bonbon.R.id.firstName);
        emailId = (EditText) rootView.findViewById(com.signity.bonbon.R.id.emailId);
        phoneNum = (EditText) rootView.findViewById(com.signity.bonbon.R.id.phoneNum);
        password = (EditText) rootView.findViewById(com.signity.bonbon.R.id.password);
        confirmPass = (EditText) rootView.findViewById(com.signity.bonbon.R.id.confirmPass);
        edtReferCode = (EditText) rootView.findViewById(com.signity.bonbon.R.id.edtReferCode);

        setUpFields();

        setUpReferalBlock();

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );


        backButton.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        signupBtn.setOnClickListener(this);
        return rootView;
    }

    private void setUpFields() {

        if(!type.isEmpty()){
            firstName.setText(""+name);
            emailId.setText(""+email);
            password.setVisibility(View.GONE);
            confirmPass.setVisibility(View.GONE);
        }else {
            password.setVisibility(View.VISIBLE);
            confirmPass.setVisibility(View.VISIBLE);
        }
    }

    private void setUpReferalBlock() {
        if (prefManager.isReferEarnFn() && prefManager.isReferEarnFnEnableForDevice()) {
            edtReferCode.setVisibility(View.VISIBLE);
        }
        else {
            edtReferCode.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.backButton:
                getActivity().onBackPressed();
                break;

            case R.id.cancelBtn:
                getActivity().onBackPressed();
                break;

            case R.id.signupBtn:

                if(checkValidation()){
                    callNetworkServiceForRegister();
                }

                break;
        }
    }

    private boolean checkValidation() {

        if(firstName.getText().toString().trim().isEmpty()){
            Toast.makeText(getActivity(),"Kindly enter your name.",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!checkValidEmail(emailId.getText().toString().trim())){
            Toast.makeText(getActivity(),"Kindly enter your mail id to proceed further",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!vallidPhone()){
            Toast.makeText(getActivity(),"Kindly enter your phone number to proceed further",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(type.isEmpty()){
            if(password.getText().toString().trim().isEmpty()){
                Toast.makeText(getActivity(),"Please enter your password.",Toast.LENGTH_SHORT).show();
                return false;
            }else {

                if(!password.getText().toString().trim().equalsIgnoreCase(confirmPass.getText().toString().trim())){
                    Toast.makeText(getActivity(), "Please Confirm correct password.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        return true;
    }

    private void callNetworkServiceForRegister() {
        progressDialogUtil.showProgressDialog(getActivity());

        phone = phoneNum.getText().toString().trim();
        firstName_ = firstName.getText().toString().trim();
        email_ = emailId.getText().toString().trim();
        pass=password.getText().toString();
        String referCode = edtReferCode.getText().toString().trim();

        String deviceId = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceToken = prefManager.getSharedValue(AppConstant.DEVICE_TOKEN);
        Map<String, String> param = new HashMap<String, String>();
        param.put("full_name", firstName_);
        param.put("email", email_);
        param.put("password", pass);
        param.put("phone", phone);
        param.put("device_id", deviceId);
        param.put("device_token", deviceToken);
        param.put("platform", AppConstant.PLATFORM);
        param.put("type", type);

        if (!referCode.isEmpty()) {
            param.put("user_refer_code", referCode);
        }

        NetworkAdaper.getInstance().getNetworkServices().userSignup(param, new Callback<LoginModel>() {


            @Override
            public void success(LoginModel model, Response response) {

                if (model.getSuccess()) {
                    ProgressDialogUtil.hideProgressDialog();
                    if(model.getData()!=null){
                        saveUserIdToPref(model.getData());
                        proceedtoActivity();
                    }
                    Toast.makeText(getActivity(), ""+model.getMessage(), Toast.LENGTH_SHORT).show();

                } else {
                    ProgressDialogUtil.hideProgressDialog();
                    DialogHandler dialogHandler = new DialogHandler(getActivity());
                    dialogHandler.setdialogForFinish("Message", "" + model.getMessage(), false);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
            }
        });
    }


    private void proceedtoActivity() {
        if(getActivity().getCurrentFocus()!=null) {
            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }

        ((LoginScreenActivity) getActivity()).setResultForActivity(Activity.RESULT_OK);
    }

    private void saveUserIdToPref(LoginData data) {
        PrefManager prefManager = new PrefManager(getActivity());
        prefManager.storeSharedValue(AppConstant.ID, data.getId());
        prefManager.storeSharedValue(AppConstant.PHONE, data.getPhone());
        prefManager.setReferEarnFn(data.getReferFnEnable());
        prefManager.setReferEarnFnEnableForDevice(data.getReferForDeviceEnable());

        if (data.getFullName() != null && !data.getFullName().isEmpty()) {
            prefManager.storeSharedValue(AppConstant.NAME, data.getFullName());
            isNameExist = true;
        }
        if (data.getEmail() != null && !data.getEmail().isEmpty()) {
            prefManager.storeSharedValue(AppConstant.EMAIL, data.getEmail());
            isEmailExist = true;
        }
    }



    private boolean vallidPhone() {
        String phone = phoneNum.getText().toString();
        if (phone.isEmpty()) {
//            Toast.makeText(getActivity(), "Kindly enter your mail id and phone number to proceed further", Toast.LENGTH_SHORT).show();
            return false;
        }
        /*if (phone.length() != 10) {
            Toast.makeText(getActivity(), "Phone No. should be of 10 digits", Toast.LENGTH_SHORT).show();
            return false;
        }*/
        return true;
    }


    public boolean checkValidEmail(String email) {
        boolean isValid = false;
        String PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(email);
        isValid = matcher.matches();
        return isValid;
    }



}
