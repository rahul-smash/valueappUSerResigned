package com.signity.bonbon.ui.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.model.LoginModel;
import com.signity.bonbon.network.NetworkAdaper;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ForgotPassActivity extends AppCompatActivity implements View.OnClickListener {


    ImageButton backButton;
    Button okBtn;
    EditText getEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        backButton = (ImageButton) findViewById(R.id.backButton);
        okBtn = (Button) findViewById(R.id.okBtn);
        getEmail = (EditText) findViewById(R.id.getEmail);
        backButton.setOnClickListener(this);
        okBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButton:
                onBackPressed();
                break;

            case R.id.okBtn:
                if (getEmail.getText().toString().trim().isEmpty()) {
                    Toast.makeText(ForgotPassActivity.this, "Please enter an email id.", Toast.LENGTH_SHORT).show();
                } else if (checkValidEmail(getEmail.getText().toString().trim())) {
                    callNetworkForForgotPassword(getEmail.getText().toString().trim());
                } else {
                    Toast.makeText(ForgotPassActivity.this, "Please enter valid email id.", Toast.LENGTH_SHORT).show();
                }


                break;
        }
    }


    private void callNetworkForForgotPassword(String email) {

        ProgressDialogUtil.showProgressDialog(ForgotPassActivity.this);

        Map<String, String> param = new HashMap<String, String>();
        param.put("email_id", email);
        NetworkAdaper.getInstance().getNetworkServices().forgetPassword(param, new Callback<LoginModel>() {


            @Override
            public void success(LoginModel loginModel, Response response) {

                if (loginModel.getSuccess()) {
                    ProgressDialogUtil.hideProgressDialog();
                    DialogHandler dialogHandler = new DialogHandler(ForgotPassActivity.this);
                    dialogHandler.setdialogForFinish("Success", "" + loginModel.getMessage(), false);
                } else {
                    ProgressDialogUtil.hideProgressDialog();
                    DialogHandler dialogHandler = new DialogHandler(ForgotPassActivity.this);
                    dialogHandler.setdialogForFinish("Message", "" + loginModel.getMessage(), false);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(ForgotPassActivity.this);
    }
}
