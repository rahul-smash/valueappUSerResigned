package com.signity.bonbon.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.ui.login.LoginScreenActivity;

/**
 * Created by rajesh on 15/10/15.
 */
public class LoginToProceedFragment extends Fragment {


    Button buttonLoginProceed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_layout_login_proceed, container, false);
        buttonLoginProceed = (Button) rootView.findViewById(R.id.buttonLoginProceed);
        buttonLoginProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proceedTOLogin(view);
            }
        });
        return rootView;
    }


    public void proceedTOLogin(View view) {
        Intent intent = new Intent(getActivity(), LoginScreenActivity.class);
        intent.putExtra(AppConstant.FROM, "menu");
        startActivity(intent);
        AnimUtil.slideFromRightAnim(getActivity());
    }
}
