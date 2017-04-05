package com.signity.bonbon.ui.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.SmsMessage;
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
import com.signity.bonbon.model.UserRecord;
import com.signity.bonbon.model.VerifyOtpResponse;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.login.LoginScreenActivity;

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
    ImageButton backBtn;
    Button resend;
    EditText edtOTp;
    private TextInputLayout input_layout_otp;
    private PrefManager prefManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(getActivity());
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
        backBtn = (ImageButton) rootView.findViewById(com.signity.bonbon.R.id.backBtn);
        input_layout_otp = (TextInputLayout) rootView.findViewById(com.signity.bonbon.R.id.input_layout_otp);
        backBtn.setOnClickListener(this);
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
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(btnDone.getWindowToken(), 0);
                break;

            case R.id.backButton:
                getActivity().onBackPressed();
                break;
            case R.id.backBtn:
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
            if (isNameExist && isEmailExist) {
                /*Intent intent_home = new Intent(getActivity(), MainActivity.class);
                intent_home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_home);*/
                ((LoginScreenActivity) getActivity()).setResultForActivity(Activity.RESULT_OK);
            } else {
                proceedToEmail(from);
            }

    }

    private void proceedtoActivity() {
            if (isNameExist && isEmailExist) {
//                            showAlertDialogForLogin(getActivity(), "Sucess", "You have login successfully. Please continue.");
                /*Intent intent_home = new Intent(getActivity(), MainActivity.class);
                intent_home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_home);*/
                ((LoginScreenActivity) getActivity()).setResultForActivity(Activity.RESULT_OK);
            } else {
                proceedToEmail(from);
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
        String deviceToken = prefManager.getSharedValue(AppConstant.DEVICE_TOKEN);
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
                    DialogHandler dialogHandler = new DialogHandler(getActivity());
                    dialogHandler.setdialogForFinish("Message", ""+verifyOtpResponse.getMessage(), false);
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
