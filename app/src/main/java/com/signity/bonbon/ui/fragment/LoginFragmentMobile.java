package com.signity.bonbon.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.GsonHelper;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.gcm.GCMClientManager;
import com.signity.bonbon.model.MobData;
import com.signity.bonbon.model.MobResponse;
import com.signity.bonbon.model.UserRecord;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.Delivery.DeliveryActivity;
import com.signity.bonbon.ui.Delivery.DeliveryPickupActivity;
import com.signity.bonbon.ui.home.MainActivity;

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
    EditText edtPhone;
    private GCMClientManager pushClientManager;
    String from;

    PrefManager prefManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(getActivity());
        pushClientManager = new GCMClientManager(getActivity(), AppConstant.PROJECT_NUMBER);
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
        btnNext.setOnClickListener(this);
        backButton.setOnClickListener(this);
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
                if (vallidPhone()) {
                    String phone = edtPhone.getText().toString();
                    if (isUserValidFromDatabase(phone)) {
                        if (from.equals("menu")) {
                            Intent intent_home = new Intent(getActivity(), MainActivity.class);
                            intent_home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent_home);
                            getActivity().finish();
                        } else if (from.equals("shop_cart")) {
                            PrefManager prefManager = new PrefManager(getActivity());
                            String pickUpStatus = prefManager.getPickupFacilityStatus();
                            Intent intentDelivery = null;
                            if (pickUpStatus.equalsIgnoreCase("0")) {
                                intentDelivery = new Intent(getActivity(), DeliveryActivity.class);
                            } else {
                                intentDelivery = new Intent(getActivity(), DeliveryPickupActivity.class);
                            }
                            intentDelivery.putExtra(AppConstant.FROM, "shop_cart");
                            startActivity(intentDelivery);
                            getActivity().finish();
                            AnimUtil.slideFromRightAnim(getActivity());
                        }
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
        }

    }

    private void callNetworkServiceForOtp() {
        ProgressDialogUtil.showProgressDialog(getActivity());

        String phone = edtPhone.getText().toString();
        String deviceId = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceToken = pushClientManager.getRegistrationId(getActivity());
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
                    dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), false);
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
                    return true;
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
        } else {
            prefManager.setReferEarnFn(false);
            prefManager.setReferEarnFnEnableForDevice(false);
            prefManager.storeSharedValue(PrefManager.REFER_OBJ_MSG, "");
        }
        String name = data.getFullName();
        String email = data.getEmail();
        if (name != null && !name.isEmpty()) {
            prefManager.storeSharedValue(AppConstant.NAME, name);
        }
        if (email != null && !email.isEmpty()) {
            prefManager.storeSharedValue(AppConstant.EMAIL, email);
        }
        if (from.equals("menu")) {
            Intent intent_home = new Intent(getActivity(), MainActivity.class);
            intent_home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent_home);
            getActivity().finish();
        } else if (from.equals("shop_cart")) {
            String pickUpStatus = prefManager.getPickupFacilityStatus();
            Intent intentDelivery = null;
            if (pickUpStatus.equalsIgnoreCase("0")) {
                intentDelivery = new Intent(getActivity(), DeliveryActivity.class);
            } else {
                intentDelivery = new Intent(getActivity(), DeliveryPickupActivity.class);
            }
            intentDelivery.putExtra(AppConstant.FROM, "shop_cart");
            startActivity(intentDelivery);
            getActivity().finish();
            AnimUtil.slideFromRightAnim(getActivity());
        }

    }

    private void proceedToMobileOtpGeneration(MobData data) {
        if (data.getRefererEnable() != null ? data.getRefererEnable() : false) {
            prefManager.setReferEarnFn(data.getRefererEnable());
            prefManager.setReferEarnFnEnableForDevice(data.getReferAndEarn().getDeviceUnique());
            prefManager.storeSharedValue(PrefManager.REFER_OBJ_MSG, data.getReferAndEarn().getMessage());
        } else {
            prefManager.setReferEarnFn(false);
            prefManager.setReferEarnFnEnableForDevice(false);
            prefManager.storeSharedValue(PrefManager.REFER_OBJ_MSG, "");
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

    private boolean vallidPhone() {
        String phone = edtPhone.getText().toString();
        if (phone.isEmpty()) {
//            Toast.makeText(getActivity(), "Enter Phone No.", Toast.LENGTH_SHORT).show();
//            showValidationFailedPopup(getActivity(), "Error", "Enter Phone");
            edtPhone.setError("Enter Phone Number");
            return false;
        }
        if (phone.length() != 10) {
            edtPhone.setError("Phone No. should be of 10 digits");
//            Toast.makeText(getActivity(), "Phone No. should be of 10 digits", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
