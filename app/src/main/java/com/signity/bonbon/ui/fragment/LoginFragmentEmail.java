
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.model.EmailResponse;
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
 * Created by root on 14/10/15.
 */
public class LoginFragmentEmail extends Fragment {

    Button updateButton, backButton;
    EditText edtName, edtEmail, edtReferal;
    LinearLayout referalBlock;
    private PrefManager prefManager;

    String from, fullName, email;
    public EditText mobilenumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        prefManager = new PrefManager(getActivity());
        from = bundle.getString(AppConstant.FROM);
        fullName = bundle.getString("full_name");
        email = bundle.getString("email");
    }

    public static Fragment newInstance(Context context) {
        return Fragment.instantiate(context,
                LoginFragmentEmail.class.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(com.signity.bonbon.R.layout.fragment_layout_login_email, container, false);
        mobilenumber = (EditText) rootView.findViewById(R.id.mobilenumber);
        mobilenumber.setFocusable(false);
        mobilenumber.setClickable(false);
        mobilenumber.setEnabled(false);
        mobilenumber.setText(prefManager.getSharedValue(AppConstant.PHONE));
        updateButton = (Button) rootView.findViewById(R.id.updateButton);
        backButton = (Button) rootView.findViewById(R.id.backButton);
        edtName = (EditText) rootView.findViewById(R.id.edtName);
        edtEmail = (EditText) rootView.findViewById(R.id.edtEmail);
        edtReferal = (EditText) rootView.findViewById(R.id.edtReferCode);
        referalBlock = (LinearLayout) rootView.findViewById(R.id.referanddone);

        edtEmail.setText("" + email.toString());
        edtName.setText("" + fullName.toString());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

        setUpReferalBlock();

        return rootView;
    }

    private void setUpReferalBlock() {
        if (prefManager.isReferEarnFn() && prefManager.isReferEarnFnEnableForDevice()) {
            referalBlock.setVisibility(View.VISIBLE);
        }
        else {
            referalBlock.setVisibility(View.GONE);
        }
    }

    private void updateProfile() {
        String emailtxt = edtEmail.getText().toString().trim();
        String nameTxt = edtName.getText().toString().trim();

        String referCode = edtReferal.getText().toString().trim();

        if (nameTxt.isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.lbl_entername), Toast.LENGTH_SHORT).show();
            return;
        }

        if(!emailtxt.isEmpty()){
            if(!checkValidEmail(emailtxt.trim())){
                Toast.makeText(getActivity(), getString(R.string.msg_toast_enter_email), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        prefManager.storeSharedValue(AppConstant.NAME, nameTxt);
        prefManager.storeSharedValue(AppConstant.EMAIL, emailtxt);

        ProgressDialogUtil.showProgressDialog(getActivity());

        PrefManager prefManager = new PrefManager(getActivity());
        String userId = prefManager.getSharedValue(AppConstant.ID);
        String deviceId = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceToken = prefManager.getSharedValue(AppConstant.DEVICE_TOKEN);
        Map<String, String> param = new HashMap<String, String>();

        param.put("user_id", userId);
        param.put("device_id", deviceId);
        param.put("device_token", deviceToken);
        param.put("platform", AppConstant.PLATFORM);
        param.put("full_name", nameTxt);
        param.put("email", emailtxt);
        if (!referCode.isEmpty()) {
            param.put("user_refer_code", referCode);
        }
        NetworkAdaper.getInstance().getNetworkServices().updateProfile(param, new Callback<EmailResponse>() {
            @Override
            public void success(EmailResponse emailResponse, Response response) {
                ProgressDialogUtil.hideProgressDialog();
                if (emailResponse.getSuccess()) {
                    ((LoginScreenActivity) getActivity()).setResultForActivity(Activity.RESULT_OK);
                } else {
                    DialogHandler dialogHandler = new DialogHandler(getActivity());
                    dialogHandler.setdialogForFinish(getResources().getString(R.string.dialog_title),""+emailResponse.getMessage(), false);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                DialogHandler dialogHandler = new DialogHandler(getActivity());
                dialogHandler.setdialogForFinish(getResources().getString(R.string.dialog_title), getResources().getString(R.string.error_code_message), false);
            }
        });
    }

    public void showAlertDialogForLogin(Context context, String title,
                                        String message) {
        final DialogHandler dialogHandler = new DialogHandler(context);
        dialogHandler.setDialog(title, message);
        dialogHandler.setPostiveButton(getResources().getString(R.string.str_lbl_ok), true)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogHandler.dismiss();
                        Intent intent_home = new Intent(getActivity(), MainActivity.class);
                        intent_home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent_home);
                    }
                });

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
