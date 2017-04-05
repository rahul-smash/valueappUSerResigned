package com.signity.bonbon.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.GsonHelper;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.model.MobData;
import com.signity.bonbon.model.MobResponse;
import com.signity.bonbon.model.UserRecord;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.login.LoginScreenActivity;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Rajinder on 12/10/15.
 */
public class LoginFragmentMobile extends Fragment implements View.OnClickListener {

    Button btnNext, backButton;
    ImageButton backBtn;
    EditText edtPhone;
    private TextInputLayout input_layout_phone;
    String from;
    PrefManager prefManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(getActivity());
        from = getArguments().getString(AppConstant.FROM);
    }

    public static Fragment newInstance(Context context) {
        return Fragment.instantiate(context,
                LoginFragmentMobile.class.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_layout_login_mobile, container, false);
        btnNext = (Button) rootView.findViewById(com.signity.bonbon.R.id.btnNext);
        edtPhone = (EditText) rootView.findViewById(com.signity.bonbon.R.id.edtPhone);
        backButton = (Button) rootView.findViewById(com.signity.bonbon.R.id.backButton);
        backBtn = (ImageButton) rootView.findViewById(com.signity.bonbon.R.id.backBtn);
        input_layout_phone = (TextInputLayout) rootView.findViewById(com.signity.bonbon.R.id.input_layout_phone);
        btnNext.setOnClickListener(this);
        backButton.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        edtPhone.addTextChangedListener(new MyTextWatcher(edtPhone));
        addActionDoneEvet(edtPhone);
        return rootView;
    }

    private void addActionDoneEvet(EditText edtPhone) {

        edtPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnNext.performClick();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnNext:
                if (validatePhone()) {
                    String phone = edtPhone.getText().toString();
                    if (isUserValidFromDatabase(phone)) {
                      /*  if (from.equals("menu")) {
                            Intent intent_home = new Intent(getActivity(), MainActivity.class);
                            intent_home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent_home);
                            getActivity().finish();
                        } else if (from.equals("shop_cart")) {
                            PrefManager prefManager = new PrefManager(getActivity());
                            String pickUpStatus = prefManager.getPickupFacilityStatus();
                            String deliveryStatus= prefManager.getDeliveryFacilityStatus();

                            Intent intentDelivery = null;
                            if(deliveryStatus.equalsIgnoreCase("1") && pickUpStatus.equalsIgnoreCase("1")){
                                intentDelivery = new Intent(getActivity(), DeliveryPickupActivity.class);
                                intentDelivery.putExtra("title", "Deliver or PickUp");
                            }
                            else if(deliveryStatus.equalsIgnoreCase("1") && pickUpStatus.equalsIgnoreCase("0")){
                                intentDelivery = new Intent(getActivity(), DeliveryActivity.class);
                            }
                            else if(deliveryStatus.equalsIgnoreCase("0") && pickUpStatus.equalsIgnoreCase("1")){
                                intentDelivery = new Intent(getActivity(), DeliveryPickupActivity.class);
                                intentDelivery.putExtra("title", "PickUp");
                            }
                            else {
                                intentDelivery = new Intent(getActivity(), DeliveryActivity.class);
                            }
                            intentDelivery.putExtra(AppConstant.FROM, "shop_cart");
                            startActivity(intentDelivery);
                            getActivity().finish();
                            AnimUtil.slideFromRightAnim(getActivity());
                        }*/

                        ((LoginScreenActivity) getActivity()).setResultForActivity(Activity.RESULT_OK);
                    } else {
                        callNetworkServiceForOtp();
                    }

                    InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(btnNext.getWindowToken(), 0);

                }
                break;
            case R.id.backButton:
                getActivity().onBackPressed();
                break;
            case R.id.backBtn:
                getActivity().onBackPressed();
                break;
        }

    }

    private void callNetworkServiceForOtp() {
        ProgressDialogUtil.showProgressDialog(getActivity());

        String phone = edtPhone.getText().toString();
        String deviceId = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceToken = prefManager.getSharedValue(AppConstant.DEVICE_TOKEN);
        Map<String, String> param = new HashMap<String, String>();
        param.put("phone", phone);
        param.put("device_id", deviceId);
        param.put("device_token", deviceToken);
        param.put("platform", AppConstant.PLATFORM);
        NetworkAdaper.getInstance().getNetworkServices().moblieVerification(param, new Callback<MobResponse>() {

            @Override
            public void success(MobResponse mobResponse, Response response) {
                ProgressDialogUtil.hideProgressDialog();
                if (mobResponse.getSuccess() != null ? mobResponse.getSuccess() : false) {
                    String isOtpSkip = prefManager.getOtoSkip();
                    if (mobResponse.getUserExistStatus() == 1 || isOtpSkip.equalsIgnoreCase("yes")) {
                        proceedToAlreadyExistModule(mobResponse.getData());
                    } else {
                        MobData data = mobResponse.getData();
                        proceedToMobileOtpGeneration(data);
                    }

                } else {
                    DialogHandler dialogHandler = new DialogHandler(getActivity());
                    dialogHandler.setdialogForFinish("Message", ""+mobResponse.getMessage(), false);
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

    private boolean isUserValidFromDatabase(String phone) {
        String stringUser = prefManager.getUserRecord();

        if (stringUser.isEmpty()) {
            return false;
        }
        GsonHelper gsonHelper = new GsonHelper();
        HashMap<String, UserRecord> map = gsonHelper.getUserRecordData(stringUser);
        if (map != null) {
            UserRecord mobLocalData = map.get(phone);
            if (mobLocalData != null) {
                if (mobLocalData.isUserVerify()) {
                    prefManager.storeSharedValue(AppConstant.ID, mobLocalData.getId());
                    prefManager.storeSharedValue(AppConstant.PHONE, mobLocalData.getPhone());
                    prefManager.storeSharedValue(AppConstant.NAME, mobLocalData.getFullName());
                    prefManager.storeSharedValue(AppConstant.EMAIL, mobLocalData.getEmail());
                    return false;
                }
            }
        }
        return false;
    }

    private void proceedToAlreadyExistModule(MobData data) {

        PrefManager prefManager = new PrefManager(getActivity());
        prefManager.storeSharedValue(AppConstant.ID, data.getId());
        prefManager.storeSharedValue(AppConstant.PHONE, data.getPhone());
        if (data.getRefererEnable() != null ? data.getRefererEnable() : false) {
            prefManager.setReferEarnFn(data.getRefererEnable());
            prefManager.setReferEarnFnEnableForDevice(data.getReferAndEarn().getDeviceUnique());
            prefManager.storeSharedValue(PrefManager.REFER_OBJ_MSG, data.getReferAndEarn().getMessage());
            prefManager.storeSharedValue(PrefManager.REFER_OBJ_CODE, data.getUserReferCode());
        } else {
            prefManager.setReferEarnFn(false);
            prefManager.setReferEarnFnEnableForDevice(false);
            prefManager.storeSharedValue(PrefManager.REFER_OBJ_MSG, "");
            prefManager.storeSharedValue(PrefManager.REFER_OBJ_CODE, "");
        }
        String name = data.getFullName();
        String email = data.getEmail();
        if (name != null && !name.isEmpty()) {
            prefManager.storeSharedValue(AppConstant.NAME, name);
        }
        if (email != null && !email.isEmpty()) {
            prefManager.storeSharedValue(AppConstant.EMAIL, email);
        }
        /*if (from.equals("menu")) {
            Intent intent_home = new Intent(getActivity(), MainActivity.class);
            intent_home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent_home);
            getActivity().finish();
        } else if (from.equals("shop_cart")) {
            String pickUpStatus = prefManager.getPickupFacilityStatus();
            String deliveryStatus= prefManager.getDeliveryFacilityStatus();

            Intent intentDelivery = null;
            if(deliveryStatus.equalsIgnoreCase("1") && pickUpStatus.equalsIgnoreCase("1")){
                intentDelivery = new Intent(getActivity(), DeliveryPickupActivity.class);
                intentDelivery.putExtra("title", "Deliver or PickUp");
            }
            else if(deliveryStatus.equalsIgnoreCase("1") && pickUpStatus.equalsIgnoreCase("0")){
                intentDelivery = new Intent(getActivity(), DeliveryActivity.class);
            }
            else if(deliveryStatus.equalsIgnoreCase("0") && pickUpStatus.equalsIgnoreCase("1")){
                intentDelivery = new Intent(getActivity(), DeliveryPickupActivity.class);
                intentDelivery.putExtra("title", "PickUp");
            }
            else {
                intentDelivery = new Intent(getActivity(), DeliveryActivity.class);
            }
            intentDelivery.putExtra(AppConstant.FROM, "shop_cart");
            startActivity(intentDelivery);
            getActivity().finish();
            AnimUtil.slideFromRightAnim(getActivity());
*/
            ((LoginScreenActivity) getActivity()).setResultForActivity(Activity.RESULT_OK);


    }

    private void proceedToMobileOtpGeneration(MobData data) {
        if (data.getRefererEnable() != null ? data.getRefererEnable() : false) {
            prefManager.setReferEarnFn(data.getRefererEnable());
            prefManager.setReferEarnFnEnableForDevice(data.getReferAndEarn().getDeviceUnique());
            prefManager.storeSharedValue(PrefManager.REFER_OBJ_MSG, data.getReferAndEarn().getMessage());
            prefManager.storeSharedValue(PrefManager.REFER_OBJ_CODE, data.getUserReferCode());
        } else {
            prefManager.setReferEarnFn(false);
            prefManager.setReferEarnFnEnableForDevice(false);
            prefManager.storeSharedValue(PrefManager.REFER_OBJ_MSG, "");
            prefManager.storeSharedValue(PrefManager.REFER_OBJ_CODE, "");
        }
        Fragment fragment = LoginFragmentOtp.newInstance(getActivity());
        Bundle bundle = new Bundle();
        bundle.putString("id", data.getId());
        bundle.putString("phone", data.getPhone());
        bundle.putString("name", data.getFullName());
        bundle.putString("email", data.getEmail());
        bundle.putString("otp", data.getOtp());
        bundle.putString("status", data.getStatus());
        bundle.putString(AppConstant.FROM, from);
        fragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(com.signity.bonbon.R.anim.right_to_center_slide,
                com.signity.bonbon.R.anim.center_to_left_slide,
                com.signity.bonbon.R.anim.left_to_center_slide,
                com.signity.bonbon.R.anim.center_to_right_slide);
        ft.replace(com.signity.bonbon.R.id.container, fragment);
//        ft.addToBackStack(null);
        ft.commit();
    }

    private boolean validatePhone() {
        if (edtPhone.getText().toString().trim().isEmpty()) {
            input_layout_phone.setError("Enter Mobile Number");
            return false;
        }
        else if (edtPhone.getText().toString().trim().length()!=10){
            input_layout_phone.setError("Mobile No. should be of 10 digits");
            return false;
        }
        else {
            input_layout_phone.setErrorEnabled(false);
        }

        return true;
    }


    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {

                case R.id.edtPhone:
                    validatePhone();

                    break;


            }
        }
    }


}
