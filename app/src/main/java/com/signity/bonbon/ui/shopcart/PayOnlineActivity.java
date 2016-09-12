package com.signity.bonbon.ui.shopcart;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.gcm.GCMClientManager;
import com.signity.bonbon.model.ResponseData;
import com.signity.bonbon.model.Store;
import com.signity.bonbon.network.NetworkAdaper;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PayOnlineActivity extends AppCompatActivity {


    WebView webview;
    AppDatabase appDb;
    PrefManager prefManager;
    private GCMClientManager pushClientManager;

    String storeId,url;
    private Store store;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_online);
        prefManager = new PrefManager(PayOnlineActivity.this);
        storeId = prefManager.getSharedValue(AppConstant.STORE_ID);
        pushClientManager = new GCMClientManager(this, AppConstant.PROJECT_NUMBER);
        appDb = DbAdapter.getInstance().getDb();

        url=getIntent().getStringExtra("url");

        webview = (WebView) findViewById(R.id.webview);

        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);


        ProgressDialogUtil.showProgressDialog(PayOnlineActivity.this);


        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                Log.e("Should overriding",url);

                if(url.contains("paymentReturn?") && url.contains("status")){
                    String[] separated = url.split("paymentReturn?");
                    separated[1] = separated[1].trim().substring(1,separated[1].trim().length());

                    String[] separated1 = separated[1].split("&");
                    String payment_request_id=separated1[0];

                    String[] payment_request=payment_request_id.trim().split("=");
                    String payment_request_id_value=payment_request[1];

                    String payment_id=separated1[1];
                    String[] payment=payment_id.trim().split("=");
                    String payment_id_value=payment[1];

                    String status=separated1[2];
                    String[] aStatus=status.trim().split("=");
                    String status_value=aStatus[1];

                    if(status_value.equalsIgnoreCase("success")){
                        Intent intent=new Intent();
                        intent.putExtra("payment_request_id",payment_request_id_value);
                        intent.putExtra("payment_id",payment_id_value);
                        setResult(2,intent);
                        finish();
                    }else if(status_value.equalsIgnoreCase("error")){
                        Intent intent=new Intent();
                        setResult(3,intent);
                        finish();
                    }

                }
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                ProgressDialogUtil.hideProgressDialog();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.e("onPageStarted", url);
            }
        });
        webview.loadUrl(url);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        AnimUtil.slideFromLeftAnim(PayOnlineActivity.this);
    }
}
