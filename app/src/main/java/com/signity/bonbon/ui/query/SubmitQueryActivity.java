package com.signity.bonbon.ui.query;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.model.EmailResponse;
import com.signity.bonbon.network.ApiService;
import com.signity.bonbon.network.NetworkConstant;
import com.squareup.okhttp.OkHttpClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * Created by Rajinder on 25/11/15.
 */
public class SubmitQueryActivity extends AppCompatActivity implements View.OnClickListener {


    TextView productName;
    EditText name, phone, email, city, message;
    Button buttonSubmit, buttonBack;
    String productTilte, productId, varientId;

    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_submit_query);
        productName = (TextView) findViewById(R.id.productName);
        name = (EditText) findViewById(R.id.edtName);
        phone = (EditText) findViewById(R.id.phoneNumber);
        email = (EditText) findViewById(R.id.email);
        city = (EditText) findViewById(R.id.city);
        message = (EditText) findViewById(R.id.message);
        buttonSubmit = (Button) findViewById(R.id.btnSubmit);
        buttonBack = (Button) findViewById(R.id.backButton);
        buttonSubmit.setOnClickListener(this);
        buttonBack.setOnClickListener(this);
        getIntentData();
    }

    private void getIntentData() {

        productTilte = getIntent().getStringExtra("product_name");
        productId = getIntent().getStringExtra("product_id");
        varientId = getIntent().getStringExtra("varient_id");

        productName.setText(productTilte);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnSubmit:
                submitUserQuery();
                break;

            case R.id.backButton:
                onBackPressed();
                break;

        }

    }


    public ApiService setupCustomRetrofitClient() {
        ApiService apiService = null;
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(2, TimeUnit.MINUTES);
        client.setReadTimeout(2, TimeUnit.MINUTES);
       /* RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient(client)).setEndpoint(NetworkConstant.ROOT + "/" + NetworkConstant.STORE_ID).setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        apiService = restAdapter.create(ApiService.class);*/
        return apiService;
    }


    private void submitUserQuery() {

        if (isValidationTrue()) {

            ApiService apiService=setupCustomRetrofitClient();

            ProgressDialogUtil.showProgressDialog(SubmitQueryActivity.this);
            String nameVal = name.getText().toString();
            String phoneVal = phone.getText().toString();
            String emailVal = email.getText().toString();
            String cityVal = city.getText().toString();
            String messageVal = message.getText().toString();

            Map<String, String> param = new HashMap<String, String>();

            param.put("store_id", "7");
            param.put("email", emailVal);
            param.put("city", cityVal);
            param.put("name", nameVal);
            param.put("phone", phoneVal);
            param.put("message", messageVal);
            param.put("variant_id", varientId);
            param.put("product_id", productId);


           apiService.submitQuery(param, new Callback<EmailResponse>() {
                @Override
                public void success(EmailResponse emailResponse, Response response) {

                    if (emailResponse.getSuccess()) {
                        Toast.makeText(SubmitQueryActivity.this, "Thank you for posting your query", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    ProgressDialogUtil.hideProgressDialog();
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(SubmitQueryActivity.this, "Failed to post your query \n Message: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    ProgressDialogUtil.hideProgressDialog();
                }
            });


        }

    }

    private boolean isValidationTrue() {

        if (name.getText().toString().isEmpty()) {
            Toast.makeText(SubmitQueryActivity.this, "Please Enter your name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phone.getText().toString().isEmpty()) {
            Toast.makeText(SubmitQueryActivity.this, "Please Enter your Number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (message.getText().toString().isEmpty()) {
            Toast.makeText(SubmitQueryActivity.this, "Please Enter your Message", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(SubmitQueryActivity.this);
    }
}
