package com.signity.bonbon.ui.contacts;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.model.Store;
import com.signity.bonbon.ui.search.SearchActivity;

public class ContactActivity_ extends Activity implements View.OnClickListener {

    Button btnBack, btnSearch, btnCall;
    TextView title, txtStorename, txtAddress, txtName;
    AppDatabase appDb;
    WebView mesWebView;
    public ProgressDialog progress;
    public String webUrl = "https://www.google.co.in/maps/@30.7192015,76.8476775,14z?hl=en";
    Store store;
//    Adapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.signity.bonbon.R.layout.contact_activity);

        appDb = DbAdapter.getInstance().getDb();
        store = appDb.getStore("");

        initialize();

        if (store != null) {
            txtStorename.setText(store.getStoreName());
            txtAddress.setText(store.getLocation() + ", " + store.getCity() + ", " + store.getState());
            txtName.setText(store.getContactPerson());
        }

        try {

            WebSettings webSettings = mesWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setBuiltInZoomControls(true);

            try {
                if (!progress.isShowing()) {

                    progress.setMessage("Loading...");
                    progress.show();
                }
            } catch (Exception e) {
            }

            mesWebView.setWebViewClient(new WebViewClient() {


                public void onLoadResource(WebView view, String url) {


                }

                public void onPageFinished(WebView view, String url) {
                    try {
                        if (progress.isShowing()) {
                            progress.dismiss();
                        }
                    } catch (Exception e) {
                    }
                }
            });
            mesWebView.loadUrl(webUrl);

            // for open the keypad of phone..
            mesWebView.requestFocus(View.FOCUS_DOWN);

        } catch (Exception e) {
        }

    }

    private void initialize() {

        progress = new ProgressDialog(ContactActivity_.this);
        btnBack = (Button) findViewById(com.signity.bonbon.R.id.backButton);
        btnCall = (Button) findViewById(com.signity.bonbon.R.id.btnCall);
        btnCall.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnSearch = (Button) findViewById(com.signity.bonbon.R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        title = (TextView) findViewById(com.signity.bonbon.R.id.textTitle);
        txtStorename = (TextView) findViewById(com.signity.bonbon.R.id.txtStorename);
        txtAddress = (TextView) findViewById(com.signity.bonbon.R.id.txtAddress);
//        txtName = (TextView) findViewById(com.signity.bonbon.R.id.txtName);
        title.setText("Contacts");

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case com.signity.bonbon.R.id.backButton:
                onBackPressed();
                break;
            case com.signity.bonbon.R.id.btnSearch:
                startActivity(new Intent(ContactActivity_.this, SearchActivity.class));
                AnimUtil.slideFromRightAnim(ContactActivity_.this);
                break;

            case com.signity.bonbon.R.id.btnCall:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + store.getContactNumber()));
                startActivity(intent);
                AnimUtil.slideFromRightAnim(ContactActivity_.this);
                break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(ContactActivity_.this);
    }
}
