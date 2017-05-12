package com.signity.bonbon.ui.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.signity.bonbon.BuildConfig;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.ui.fragment.LoginFragmentMobile;
import com.signity.bonbon.ui.loginwithemail.LoginWithEmailFragment;

/**
 * Created by rajesh on 12/10/15.
 */
public class LoginScreenActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.signity.bonbon.R.layout.activity_login);
        String from = getIntent().getStringExtra(AppConstant.FROM);
        Fragment fragment = null;
        if(BuildConfig.LOGIN_WITH_EMAIL.equalsIgnoreCase("yes")){
            fragment= LoginWithEmailFragment.newInstance(this);
        }else {

            fragment=LoginFragmentMobile.newInstance(this);
        }
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.FROM, from);
        fragment.setArguments(bundle);
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction()
                    .add(com.signity.bonbon.R.id.container, fragment).commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void setResultForActivity(int result) {
        setResult(result);
        finish();
    }
}