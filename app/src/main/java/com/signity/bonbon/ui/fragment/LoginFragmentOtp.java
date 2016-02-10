package com.signity.bonbon.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.SmsMessage;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.signity.bonbon.model.MobResponse;
import com.signity.bonbon.model.UserRecord;
import com.signity.bonbon.model.VerifyOtpResponse;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.Delivery.DeliveryActivity;
import com.signity.bonbon.ui.home.MainActivity;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Rajinder on 12/10/15.
 */
public class LoginFragmentOtp extends Fragment implements View.OnClickListener {

    String id;
    String phone;
    String otp;
    String status;
    String from;
    String name;
    String email;

    boolean isEmailExist = false, isNameExist = false;

    Button btnDone, backButton;
    Button resend;
    EditText edtOTp;
    private GCMClientManager pushClientManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pushClientManager = new GCMClientManager(getActivity(), AppConstant.PROJECT_NUMBER);
        Bundle bundle = getArguments();
        id = bundle.getString("id");
        phone = bundle.getString("phone");
        otp = bundle.getString("otp");
        status = bundle.getString("status");
        name = bundle.getString("name");
        email = bundle.getString("email");
        from = bundle.getString(AppConstant.FROM);
    }

    public static Fragment newInstance(Context context) {
        return Fragment.instantiate(context,
                LoginFragmentOtp.class.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_layout_login_otp, container, false);
        btnDone = (Button) rootView.findViewById(R.id.btnDone);
        resend = (Button) rootView.findViewById(R.id.resend);
//        skipOtp = (Button) rootView.findViewById(R.id.skipOtp);
        backButton = (Button) rootView.findViewById(R.id.backButton);
        edtOTp = (EditText) rootView.findViewById(R.id.edtOTp);
        btnDone.setOnClickListener(this);
        backButton.setOnClickListener(this);
        resend.setOnClickListener(this);
        addActionDoneEvet(edtOTp);
        resend.setSelected(false);
        setUpCountDownTimer();
        return rootView;
    }


    private void setUpCountDownTimer() {

        new CountDownTimer(20000, 1000) {

            public void onTick(long millisUntilFinished) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss");
                String string = simpleDateFormat.format(millisUntilFinished);
                resend.setText(string + " Sec");
            }

            public void onFinish() {
                resend.setText("Skip");
                resend.setSelected(true);
            }
        }.start();


    }


    private void addActionDoneEvet(EditText edtOTp) {

        edtOTp.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnDone.performClick();
                }
                return false;
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(
                "android.provider.Telephony.SMS_RECEIVED");
        getActivity().registerReceiver(messageReciever, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(messageReciever);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDone:
                String otpcode = edtOTp.getText().toString();
                if (otpcode != null && !otpcode.isEmpty()) {
                    callVerifyOtpService(otpcode);
                } else {
                    edtOTp.setError("Enter your one time password");
                    return;
                }
                break;

            case R.id.backButton:
                getActivity().onBackPressed();
                break;
            case R.id.resend:
                if (resend.isSelected()) {
                    skipAuthenticationProcess();
                }
                break;

        }
    }

    private void skipAuthenticationProcess() {
        saveUserIdToPref();
        userDetailSave(false);
        if (from.equals("menu")) {
            if (isNameExist && isEmailExist) {
                Intent intent_home = new Intent(getActivity(), MainActivity.class);
                intent_home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_home);
            } else {
                proceedToEmail(from);
            }
        } else if (from.equals("shop_cart")) {
            Intent intentDelivery = new Intent(getActivity(), DeliveryActivity.class);
            intentDelivery.putExtra(AppConstant.FROM, "shop_cart");
            startActivity(intentDelivery);
            getActivity().finish();
            AnimUtil.slideFromRightAnim(getActivity());
        }


    }

    private void proceedtoActivity() {
        if (from.equals("menu")) {
            if (isNameExist && isEmailExist) {
//                            showAlertDialogForLogin(getActivity(), "Sucess", "You have login successfully. Please continue.");
                Intent intent_home = new Intent(getActivity(), MainActivity.class);
                intent_home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_home);
            } else {
                proceedToEmail(from);
            }

        } else if (from.equals("shop_cart")) {
            if (isNameExist && isEmailExist) {
                Intent intentDelivery = new Intent(getActivity(), DeliveryActivity.class);
                intentDelivery.putExtra(AppConstant.FROM, "shop_cart");
                startActivity(intentDelivery);
                getActivity().finish();
                AnimUtil.slideFromRightAnim(getActivity());
            } else {
                proceedToEmail(from);
            }
        }
    }

    private void proceedToEmail(String from) {
        Fragment fragment = LoginFragmentEmail.newInstance(getActivity());
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.FROM, from);
        bundle.putString("full_name", name);
        bundle.putString("email", email);
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

    private void saveUserIdToPref() {
        PrefManager prefManager = new PrefManager(getActivity());
        prefManager.storeSharedValue(AppConstant.ID, id);
        prefManager.storeSharedValue(AppConstant.PHONE, phone);
        if (name != null && !name.isEmpty()) {
            prefManager.storeSharedValue(AppConstant.NAME, name);
            isNameExist = true;
        }
        if (email != null && !email.isEmpty()) {
            prefManager.storeSharedValue(AppConstant.EMAIL, email);
            isEmailExist = true;
        }
    }

    private void userDetailSave(boolean userVerifyStatus) {
        PrefManager prefManager = new PrefManager(getActivity());
        UserRecord userRecord = new UserRecord();
        userRecord.setId(id);
        userRecord.setPhone(phone);
        userRecord.setFullName(name);
        userRecord.setEmail(email);
        userRecord.setUserVerify(userVerifyStatus);

        GsonHelper gsonHelper = new GsonHelper();
        HashMap<String, UserRecord> listMap = gsonHelper.getUserRecordData(prefManager.getUserRecord());
        if (listMap != null) {
            listMap.put(phone, userRecord);
        } else {
            listMap = new HashMap<>();
            listMap.put(phone, userRecord);
        }

        String strUserDetail = gsonHelper.getUserRecordData(listMap);
        prefManager.setUserRecord(strUserDetail);
    }

    private void callVerifyOtpService(String otpCode) {

        ProgressDialogUtil.showProgressDialog(getActivity());

        String deviceId = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceToken = pushClientManager.getRegistrationId(getActivity());
        Map<String, String> param = new HashMap<String, String>();
        param.put("phone", phone);
        param.put("device_id", deviceId);
        param.put("device_token", deviceToken);
        param.put("otp", otpCode);
        param.put("platform", AppConstant.PLATFORM);
        NetworkAdaper.getInstance().getNetworkServices().verifyOtp(param, new Callback<VerifyOtpResponse>() {

            @Override
            public void success(VerifyOtpResponse verifyOtpResponse, Response response) {
                ProgressDialogUtil.hideProgressDialog();
                if (verifyOtpResponse.getSuccess()) {
                    saveUserIdToPref();
                    userDetailSave(true);
                    proceedtoActivity();
                } else {
                    showDialogForMessage(getActivity(), "Error", verifyOtpResponse.getMessage());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
            }
        });

    }

    private void showDialogForMessage(Context context, String error, String message) {

        final DialogHandler dialogHandler = new DialogHandler(context);
        dialogHandler.setdialogForFinish(error, message, false);
    }

    private void callNetworkServiceForOtp() {
        ProgressDialogUtil.showProgressDialog(getActivity());

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
                if (mobResponse.getSuccess()) {
                    id = mobResponse.getData().getId();
                    otp = mobResponse.getData().getOtp();
                    status = mobResponse.getData().getStatus();
                    showDialogForMessage(getActivity(), "Message", "OTP sent to your registered mobile");
                } else {
                    showDialogForMessage(getActivity(), "Error", mobResponse.getMessage());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
            }
        });

    }


    int verificationCode = -1;
    BroadcastReceiver messageReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Retrieves a map of extended data from the intent.
            final Bundle bundle = intent.getExtras();

            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();


                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    message = message.replaceAll("[a-z.A-Z]", "").trim();
                    try {
                        verificationCode = Integer.parseInt(message);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (senderNum.contains("-")) {
                        if (verificationCode != -1) {
                            checkAutoReiceveVerificationCode(verificationCode);
                        }
                    } else {

                    }
                } // end for loop
            } // bundle is null
        }
    };

    private void checkAutoReiceveVerificationCode(int verificationCode) {
        edtOTp.setText(String.valueOf(verificationCode));
        btnDone.performClick();

    }

}
