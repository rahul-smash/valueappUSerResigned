package com.signity.bonbon.ui.SharenEarn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.ga.GAConstant;
import com.signity.bonbon.ga.GATrackers;
import com.signity.bonbon.gcm.GCMClientManager;
import com.signity.bonbon.model.Store;

/**
 * Created by root on 23/6/16.
 */
public class ShareNEarnFragment extends Fragment implements View.OnClickListener{


    View mView;
    AppDatabase appDb;
    PrefManager prefManager;

    String storeId,userId,code;
    private Store store;
    TextView textTitle,codeTxt;
//    ImageButton backButton;
    Button shareNearn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(getActivity());
        appDb = DbAdapter.getInstance().getDb();
        userId = prefManager.getSharedValue(AppConstant.ID);
        storeId = prefManager.getSharedValue(AppConstant.STORE_ID);
        store = appDb.getStore(storeId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_refer_nearn, container, false);

//        backButton = (ImageButton) mView.findViewById(R.id.backButton);
        shareNearn = (Button)mView.findViewById(R.id.shareNearn);
        textTitle = (TextView) mView.findViewById(R.id.textTitle);
        codeTxt = (TextView) mView.findViewById(R.id.codeTxt);

        code=prefManager.getSharedValue(PrefManager.REFER_OBJ_CODE);



        if (!userId.isEmpty()) {
            if(!code.isEmpty()){
                codeTxt.setText(code);
            }else {
                codeTxt.setText("Login to access your Code.");
            }
        }else {
            codeTxt.setText("Login to access your Code.");
        }

//        backButton.setOnClickListener(this);
        shareNearn.setOnClickListener(this);

        return mView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButton:
                getActivity().onBackPressed();
                break;

            case R.id.shareNearn:
                shareNearnFunction();
                break;
        }
    }

    private void shareNearnFunction() {
        String appShareGAC = getString(R.string.app_name) + GAConstant.GAC_SHARE;
        GATrackers.getInstance().trackEvent(appShareGAC, appShareGAC + GAConstant.SHARED,
                getString(R.string.app_name) + " Shared And Earn");
        String sharemessage = prefManager.getSharedValue(PrefManager.REFER_OBJ_MSG);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, sharemessage);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Refer and Earn");
        startActivity(Intent.createChooser(intent, "Share with"));

    }

}
