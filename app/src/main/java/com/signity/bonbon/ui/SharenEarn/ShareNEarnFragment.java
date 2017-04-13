package com.signity.bonbon.ui.SharenEarn;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.ga.GATrackers;
import com.signity.bonbon.model.ReferNEarnCodeModel;
import com.signity.bonbon.model.ReferNEarnModel;
import com.signity.bonbon.model.Store;
import com.signity.bonbon.network.NetworkAdaper;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by root on 23/6/16.
 */
public class ShareNEarnFragment extends Fragment implements View.OnClickListener {


    View mView;
    AppDatabase appDb;
    PrefManager prefManager;

    String storeId, userId, code;
    private Store store;
    TextView textTitle, codeTxt;
    //    ImageButton backButton;
    Button shareNearn;
    private String name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(getActivity());
        appDb = DbAdapter.getInstance().getDb();
        userId = prefManager.getSharedValue(AppConstant.ID);
        name = prefManager.getSharedValue(AppConstant.NAME);
        storeId = prefManager.getSharedValue(AppConstant.STORE_ID);
        store = appDb.getStore(storeId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_refer_nearn, container, false);
        GATrackers.getInstance().trackScreenView(getString(R.string.ga_screen_sharenearn));
//        backButton = (ImageButton) mView.findViewById(R.id.backButton);
        shareNearn = (Button) mView.findViewById(R.id.shareNearn);
        textTitle = (TextView) mView.findViewById(R.id.textTitle);
        codeTxt = (TextView) mView.findViewById(R.id.codeTxt);

        code = prefManager.getSharedValue(PrefManager.REFER_OBJ_CODE);



        if (!userId.isEmpty()) {
                callNetworkForCode();
        }else {
            codeTxt.setText(getString(R.string.lbl_login_to_access));
        }

//        backButton.setOnClickListener(this);
        shareNearn.setOnClickListener(this);

        return mView;
    }

    private void callNetworkForCode() {

        ProgressDialogUtil.showProgressDialog(getActivity());

        final PrefManager prefManager = new PrefManager(getActivity());
        String userId = prefManager.getSharedValue(AppConstant.ID);
        String deviceId = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Map<String, String> param = new HashMap<String, String>();

        param.put("user_id", userId);
        param.put("device_id", deviceId);
        NetworkAdaper.getInstance().getNetworkServices().getUserReferCode(param, new Callback<ReferNEarnModel>() {

            @Override
            public void success(ReferNEarnModel referNEarnModel, Response response) {
                ProgressDialogUtil.hideProgressDialog();
                if (referNEarnModel.getStatus()) {

                    ReferNEarnCodeModel model = referNEarnModel.getReferAndEarn();
                    if (model != null) {
                        if (referNEarnModel.getIsRefererFnEnable() != null ? referNEarnModel.getIsRefererFnEnable() : false) {
                            prefManager.setReferEarnFn(referNEarnModel.getIsRefererFnEnable());
                            prefManager.setReferEarnFnEnableForDevice(model.getBlDeviceIdUnique());
                            prefManager.storeSharedValue(PrefManager.REFER_OBJ_MSG, model.getSharedMessage());
                            prefManager.storeSharedValue(PrefManager.REFER_OBJ_CODE, referNEarnModel.getUserReferCode());
                            codeTxt.setText(referNEarnModel.getUserReferCode());
                        } else {
                            prefManager.setReferEarnFn(false);
                            prefManager.setReferEarnFnEnableForDevice(false);
                            prefManager.storeSharedValue(PrefManager.REFER_OBJ_MSG, "");
                            prefManager.storeSharedValue(PrefManager.REFER_OBJ_CODE, "");
                            codeTxt.setText(getString(R.string.lbl_login_to_access));
                        }
                    }

                } else {
                    DialogHandler dialogHandler = new DialogHandler(getActivity());
                    dialogHandler.setdialogForFinish(getResources().getString(R.string.dialog_title), "" + referNEarnModel.getMessage(), false);
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
        startActivity(Intent.createChooser(intent, getString(R.string.lbl_share_with)));

    }

}
