package com.signity.bonbon.ui.AboutUs;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.ga.GAConstant;
import com.signity.bonbon.ga.GATrackers;
import com.signity.bonbon.model.GetStoreModel;
import com.signity.bonbon.model.Store;
import com.signity.bonbon.network.NetworkAdaper;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by root on 19/10/15.
 */
public class AboutUsFragment extends Fragment {

    //    TextView txtAboutUs;
    WebView webview;
    View mView;
    AppDatabase appDb;
    PrefManager prefManager;

    String storeId;
    private Store store;


    Button startOrder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(getActivity());
        storeId = prefManager.getSharedValue(AppConstant.STORE_ID);
        appDb = DbAdapter.getInstance().getDb();
        GATrackers.getInstance().trackScreenView(GAConstant.ABOUT_US_SCREEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.about_us, container, false);

        String aboutUsGAC = getString(R.string.app_name) + GAConstant.GAC_ABOUT;

        GATrackers.getInstance()
                .trackEvent(aboutUsGAC, aboutUsGAC + GAConstant.VIEW,
                        "ABOUT US VIEW");

        webview = (WebView) mView.findViewById(R.id.webview);
        startOrder = (Button) mView.findViewById(R.id.startOrder);

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);

        store = appDb.getStore(storeId);

        if (store != null && store.getAboutUs() != null) {
            String aboutus = store.getAboutUs().replaceAll("&nbsp;", " ").replaceAll("\\“", "\"").replaceAll("\\”", "\"");
            webview.loadData(aboutus, "text/html; charset=utf-8", "UTF-8");
        } else {
            getAboutUsStatus();
        }

        startOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ( getActivity()).onBackPressed();
            }
        });

        return mView;
    }

    private void getAboutUsStatus() {
        String deviceid = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceToken = prefManager.getSharedValue(AppConstant.DEVICE_TOKEN);

        Map<String, String> param = new HashMap<String, String>();
        param.put("device_id", deviceid);
        param.put("device_token", deviceToken);
        param.put("platform", AppConstant.PLATFORM);
        ProgressDialogUtil.showProgressDialog(getActivity());

        NetworkAdaper.getInstance().getNetworkServices().getStore(param, new Callback<GetStoreModel>() {


            @Override
            public void success(GetStoreModel getStoreModel, Response response) {
                if (getStoreModel.getSuccess()) {

                    webview.loadData(getStoreModel.getStore().getAboutUs(), "text/html", "UTF-8");
                    ProgressDialogUtil.hideProgressDialog();
                } else {
                    ProgressDialogUtil.hideProgressDialog();
                    Toast.makeText(getActivity(), getResources().getString(R.string.str_no_data_found), Toast.LENGTH_SHORT).show();
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
}
