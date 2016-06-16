package com.signity.bonbon.ui.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.FontUtil;
import com.signity.bonbon.Utilities.GsonHelper;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.gcm.GCMClientManager;
import com.signity.bonbon.model.EmailResponse;
import com.signity.bonbon.model.ReferAndEarn;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.home.MainActivity;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Profile extends Fragment implements View.OnClickListener {

    public EditText edtName, edtEmail, edtReferal;
    LinearLayout referalBlock;
    Button btnUpdate;
    public Typeface typeFaceRobotoRegular, typeFaceRobotoBold;

    View rootView;


    PrefManager prefManager;
    String userId, name, email;
    GCMClientManager pushClientManager;
    public EditText mobilenumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typeFaceRobotoRegular = FontUtil.getTypeface(getActivity(), FontUtil.FONT_ROBOTO_REGULAR);
        typeFaceRobotoBold = FontUtil.getTypeface(getActivity(), FontUtil.FONT_ROBOTO_BOLD);
        pushClientManager = new GCMClientManager(getActivity(), AppConstant.PROJECT_NUMBER);
        prefManager = new PrefManager(getActivity());
        userId = prefManager.getSharedValue(AppConstant.ID);
        name = prefManager.getSharedValue(AppConstant.NAME);
        email = prefManager.getSharedValue(AppConstant.EMAIL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.profile, container, false);

        mobilenumber = (EditText) rootView.findViewById(R.id.mobilenumber);
        mobilenumber.setFocusable(false);
        mobilenumber.setClickable(false);
        mobilenumber.setEnabled(false);
        mobilenumber.setText(prefManager.getSharedValue(AppConstant.PHONE));
        edtName = (EditText) rootView.findViewById(R.id.edtName);
        edtName.setTypeface(typeFaceRobotoRegular);
        edtEmail = (EditText) rootView.findViewById(R.id.edtEmail);
        edtEmail.setTypeface(typeFaceRobotoRegular);

        edtReferal = (EditText) rootView.findViewById(R.id.edtReferCode);
        referalBlock = (LinearLayout) rootView.findViewById(R.id.referanddone);

        btnUpdate = (Button) rootView.findViewById(R.id.updateButton);
        edtEmail.setTypeface(typeFaceRobotoRegular);
        btnUpdate.setOnClickListener(this);
        edtName.setText(name);
        edtEmail.setText(email);
        setUpReferalBlock();
        return rootView;
    }


    private void setUpReferalBlock() {
        if (prefManager.isReferEarnFn()) {
            if (prefManager.isReferEarnFnEnableForDevice()) {
                referalBlock.setVisibility(View.VISIBLE);
            } else {
                referalBlock.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.updateButton:
                updateUserInfo();
                break;
        }
    }

    private void updateUserInfo() {

        String emailtxt = edtEmail.getText().toString();
        String nameTxt = edtName.getText().toString();


        if (nameTxt.isEmpty()) {
            Toast.makeText(getActivity(), "Please Update Name", Toast.LENGTH_SHORT).show();
            return;
        }
//        if (emailtxt.isEmpty()) {
//            Toast.makeText(getActivity(), "Please Update Email", Toast.LENGTH_SHORT).show();
//            return;
//        }
        prefManager.storeSharedValue(AppConstant.NAME, nameTxt);
        prefManager.storeSharedValue(AppConstant.EMAIL, emailtxt);
        callUpdateProfileService();
    }

    private void callUpdateProfileService() {
        ProgressDialogUtil.showProgressDialog(getActivity());

        PrefManager prefManager = new PrefManager(getActivity());
        String userId = prefManager.getSharedValue(AppConstant.ID);

        final String name = edtName.getText().toString();
        String email = edtEmail.getText().toString();
        String referCode = edtReferal.getText().toString().trim();
        String deviceId = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceToken = pushClientManager.getRegistrationId(getActivity());
        Map<String, String> param = new HashMap<String, String>();

        param.put("user_id", userId);
        param.put("device_id", deviceId);
        param.put("device_token", deviceToken);
        param.put("platform", AppConstant.PLATFORM);
        param.put("full_name", name);
        param.put("email", email);
        if (!referCode.isEmpty()) {
            param.put("user_refer_code", referCode);
        }

        NetworkAdaper.getInstance().getNetworkServices().updateProfile(param, new Callback<EmailResponse>() {

            @Override
            public void success(EmailResponse emailResponse, Response response) {
                ProgressDialogUtil.hideProgressDialog();
                if (emailResponse.getSuccess()) {

                    try {
                        ((MainActivity) getActivity()).user.setText(name);
                        showAlertDialog(getActivity(), "Success", emailResponse.getMessage());
                    } catch (Exception e) {
                        e.getMessage();
                    }

//                    MobData data = mobResponse.getData();
//                    proceedToMobileOtpGeneration(data);
                } else {
                    DialogHandler dialogHandler = new DialogHandler(getActivity());
                    dialogHandler.setdialogForFinish("Message", emailResponse.getMessage(), false);
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

    public void showAlertDialog(Context context, String title,
                                String message) {
        new DialogHandler(context).setdialogForFinish(title, message, false);
    }
}
