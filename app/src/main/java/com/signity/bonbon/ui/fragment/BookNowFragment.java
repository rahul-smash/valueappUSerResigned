package com.signity.bonbon.ui.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.Utilities.Util;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.gcm.GCMClientManager;
import com.signity.bonbon.model.EmailResponse;
import com.signity.bonbon.model.EnquiryDataModel;
import com.signity.bonbon.model.EnquiryModel;
import com.signity.bonbon.model.GetStoreModel;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.view.DateTimePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by rajesh on 4/12/15.
 */
public class BookNowFragment extends Fragment implements View.OnClickListener {

    // edtName   city  message
    private DatePickerDialog toDatePickerDialog;
    private TimePickerDialog toTimePickerDialog;

    EditText edtName, phoneNumber, email, city, message,date;
    Button btnSubmit;
    private TextInputLayout input_layout_name, input_layout_phone, input_layout_email,input_layout_city,input_layout_message,input_layout_date;

    String storeId;

    private GCMClientManager pushClientManager;
    private PrefManager prefManager;
    private AppDatabase appDb;
    Bundle bundle;
    String from;

    boolean isFinsish = false;
    Calendar bookDateAndTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        if (bundle != null) {
            from = bundle.getString(AppConstant.FROM, "");
            if (from != null && from.equals("home")) {
                isFinsish = true;
            } else {
                isFinsish = false;
            }
        }
        appDb = DbAdapter.getInstance().getDb();
        prefManager = new PrefManager(getActivity());
        pushClientManager = new GCMClientManager(getActivity(), AppConstant.PROJECT_NUMBER);
        storeId = appDb.getStore(prefManager.getSharedValue(AppConstant.STORE_ID)).getId();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_now, container, false);
        input_layout_name = (TextInputLayout)rootView.findViewById(R.id.input_layout_name);
        input_layout_phone = (TextInputLayout) rootView.findViewById(R.id.input_layout_phone);
        input_layout_email = (TextInputLayout) rootView.findViewById(R.id.input_layout_email);
        input_layout_city = (TextInputLayout) rootView.findViewById(R.id.input_layout_city);
        input_layout_message = (TextInputLayout) rootView.findViewById(R.id.input_layout_message);
        input_layout_date = (TextInputLayout) rootView.findViewById(R.id.input_layout_date);
        edtName = (EditText) rootView.findViewById(R.id.edtName);
        phoneNumber = (EditText) rootView.findViewById(R.id.phoneNumber);
        email = (EditText) rootView.findViewById(R.id.email);
        city = (EditText) rootView.findViewById(R.id.city);
        message = (EditText) rootView.findViewById(R.id.message);
        btnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);
        date = (EditText) rootView.findViewById(R.id.date);

        btnSubmit.setOnClickListener(this);
        date.setOnClickListener(this);

        edtName.addTextChangedListener(new MyTextWatcher(edtName));
        phoneNumber.addTextChangedListener(new MyTextWatcher(phoneNumber));
        email.addTextChangedListener(new MyTextWatcher(email));
        city.addTextChangedListener(new MyTextWatcher(city));
        message.addTextChangedListener(new MyTextWatcher(message));

        setDateTimeField();

        callNetworkForEnquiryMessage();

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return rootView;
    }

    private void callNetworkForEnquiryMessage() {

        ProgressDialogUtil.showProgressDialog(getActivity());

        NetworkAdaper.getInstance().getNetworkServices().getEnquiryFormMessage(new Callback<EnquiryModel>() {

            @Override
            public void success(EnquiryModel enquiryModel, Response response) {
                ProgressDialogUtil.hideProgressDialog();

                if(enquiryModel.getSuccess()){

                    if(enquiryModel.getEnquiryDataModel()!=null){
                        EnquiryDataModel model=enquiryModel.getEnquiryDataModel();
                        DialogHandler dialogHandler = new DialogHandler(getActivity());
                        dialogHandler.setdialogForFinish("Message", ""+model.getMessage(), false);
                    }
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


    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();

        toDatePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        bookDateAndTime = Calendar.getInstance();
                        bookDateAndTime.set(year, monthOfYear, dayOfMonth);
                        toTimePickerDialog.show();

                    }
                }, newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));

        toTimePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                bookDateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                bookDateAndTime.set(Calendar.MINUTE, minute);
                String dateValue = Util.getTime(bookDateAndTime.getTime(), "dd-MM-yyyy hh:mm a");
                date.setText(dateValue);

            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), false);

    }

    private String getQueryJson() {

        String strJson;

        JSONObject jsonObject = new JSONObject();

        String name = edtName.getText().toString().trim();
        String emailstr = email.getText().toString().trim();
        String mobile = phoneNumber.getText().toString().trim();
        String cityStr = city.getText().toString().trim();
        String datetime = date.getText().toString().trim();
        String messageStr = message.getText().toString().trim();

        try {
            jsonObject.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            jsonObject.put("email", emailstr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            jsonObject.put("mobile", mobile);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            jsonObject.put("city", cityStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            jsonObject.put("datetime", datetime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jsonObject.put("message", messageStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonObject.toString();
    }


    private boolean validForm() {

        if (!validateName()) {
            return false;
        }

        if (!validatePhone()) {
            return false;
        }

        if (!validateEmail()) {
            return false;
        }

        if (!validateCity()) {
            return false;
        }
        if (!validateDate()) {
            return false;
        }
        if (!validateMessage()) {
            return false;
        }

        return true;

    }

    private boolean validateName() {
        if (edtName.getText().toString().trim().isEmpty()) {
            input_layout_name.setError(getString(R.string.str_booknow_fragment_enter_name));
            return false;
        } else {
            input_layout_name.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePhone() {
        if (phoneNumber.getText().toString().trim().isEmpty()) {
            input_layout_phone.setError(getString(R.string.str_booknow_fragment_enter_mobile));
            return false;
        }
        else if (phoneNumber.getText().toString().trim().length()!=10){
            input_layout_phone.setError(getString(R.string.str_booknow_fragment_mobile_valiation));
            return false;
        }
        else {
            input_layout_phone.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validateEmail() {
        String emailValue = email.getText().toString().trim();

        if (emailValue.isEmpty() || !Util.checkValidEmail(emailValue)) {
            input_layout_email.setError(getString(R.string.str_booknow_fragment_enter_valid_email));
            return false;
        } else {
            input_layout_email.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateCity() {
        if (city.getText().toString().trim().isEmpty()) {
            input_layout_city.setError(getString(R.string.str_booknow_fragment_enter_city));
            return false;
        } else {
            input_layout_city.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateDate() {
        if (date.getText().toString().trim().isEmpty()) {
            input_layout_date.setError(getString(R.string.str_booknow_fragment_enter_date));
            return false;
        }
        else {
            input_layout_date.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateMessage() {
        if (message.getText().toString().trim().isEmpty()) {
            input_layout_message.setError("Enter Your Message");
            return false;
        } else {
            input_layout_message.setErrorEnabled(false);
        }

        return true;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnSubmit:
                if (validForm()) {

                    String query = getQueryJson();

                    String userId = prefManager.getSharedValue(AppConstant.ID);
                    String deviceId = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                    String deviceToken = pushClientManager.getRegistrationId(getActivity());
                    Map<String, String> param = new HashMap<String, String>();

                    param.put("store_id", storeId);
                    param.put("device_id", deviceId);
                    param.put("device_token", deviceToken);
                    param.put("platform", AppConstant.PLATFORM);
                    param.put("user_id", userId);
                    param.put("query", query);

                    ProgressDialogUtil.showProgressDialog(getActivity());

                    NetworkAdaper.getInstance().getNetworkServices().query(param, new Callback<EmailResponse>() {
                        @Override
                        public void success(EmailResponse getValidCouponResponse, Response response) {
                            ProgressDialogUtil.hideProgressDialog();
                            if (getValidCouponResponse.getSuccess()) {
                                showSuccessFailureMessage(getActivity(), "Success", "Your request is successfully submitted",
                                        isFinsish);
                            } else {
                                showSuccessFailureMessage(getActivity(), "Message", "Sorry we are unable to submit your request. Please try later", isFinsish);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            ProgressDialogUtil.hideProgressDialog();
//                            Log.e("Log", error.getMessage());
                        }
                    });

                }
                break;
            case R.id.date:
                toDatePickerDialog.show();
//                showDateTimeDialog();
                break;
        }

    }

    private void showSuccessFailureMessage(Context activity, String title, String message, final boolean isFinish) {
        final DialogHandler dialog = new DialogHandler(activity);
        dialog.setDialog(title, message);
        dialog.setPostiveButton("OK", true).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFinish) {
                    getActivity().onBackPressed();
                }
                dialog.dismiss();
            }
        });
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.edtName:
                    validateName();

                    break;
                case R.id.phoneNumber:
                    validatePhone();

                    break;
                case R.id.email:
                    validateEmail();

                    break;
                case R.id.city:
                    validateCity();

                    break;
                case R.id.message:
                    validateMessage();

                    break;

            }
        }
    }


    private void showDateTimeDialog() {
        // Create the dialog
        final Dialog mDateTimeDialog = new Dialog(getActivity());
        // Inflate the root layout
        final RelativeLayout mDateTimeDialogView = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.date_time_dialog, null);
        // Grab widget instance
        final DateTimePicker mDateTimePicker = (DateTimePicker) mDateTimeDialogView.findViewById(R.id.DateTimePicker);
        // Check is system is set to use 24h time (this doesn't seem to work as expected though)
        final String timeS = android.provider.Settings.System.getString(getActivity().getContentResolver(), android.provider.Settings.System.TIME_12_24);
        final boolean is24h = !(timeS == null || timeS.equals("12"));

        // Update demo TextViews when the "OK" button is clicked
        ((Button) mDateTimeDialogView.findViewById(R.id.SetDateTime)).setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mDateTimePicker.clearFocus();
                String dateValue;
                Calendar newDate = Calendar.getInstance();
//                newDate.set(mDateTimePicker.get(Calendar.YEAR), (mDateTimePicker.get(Calendar.MONTH) + 1), mDateTimePicker.get(Calendar.DAY_OF_MONTH));

                dateValue = mDateTimePicker.get(Calendar.YEAR) + "/" + (mDateTimePicker.get(Calendar.MONTH) + 1) + "/"
                        + mDateTimePicker.get(Calendar.DAY_OF_MONTH);
//                ((TextView) v.findViewById(R.id.Date)).setText(mDateTimePicker.get(Calendar.YEAR) + "/" + (mDateTimePicker.get(Calendar.MONTH) + 1) + "/"
//                        + mDateTimePicker.get(Calendar.DAY_OF_MONTH));
                if (mDateTimePicker.is24HourView()) {
//                    newDate.set(Calendar.HOUR_OF_DAY, mDateTimePicker.get(Calendar.HOUR_OF_DAY));
//                    newDate.set(Calendar.MINUTE, mDateTimePicker.get(Calendar.MINUTE));
                    dateValue = dateValue.concat(mDateTimePicker.get(Calendar.HOUR_OF_DAY) + ":" + mDateTimePicker.get(Calendar.MINUTE));
//                    ((TextView) findViewById(R.id.Time)).setText(mDateTimePicker.get(Calendar.HOUR_OF_DAY) + ":" + mDateTimePicker.get(Calendar.MINUTE));
                } else {
                    newDate.set(Calendar.HOUR, mDateTimePicker.get(Calendar.HOUR));
                    newDate.set(Calendar.MINUTE, mDateTimePicker.get(Calendar.MINUTE));
                    newDate.set(Calendar.AM_PM, (mDateTimePicker.get(Calendar.AM_PM)));
                    dateValue = dateValue.concat(mDateTimePicker.get(Calendar.HOUR) + ":" + mDateTimePicker.get(Calendar.MINUTE) + " "
                            + (mDateTimePicker.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM"));
//                    ((TextView) findViewById(R.id.Time)).setText(mDateTimePicker.get(Calendar.HOUR) + ":" + mDateTimePicker.get(Calendar.MINUTE) + " "
//                            + (mDateTimePicker.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM"));
                }
//                String dateValue = Util.getTime(newDate.getTime(), "dd-MM-yyyy HH:mm");
                date.setText(dateValue);
                mDateTimeDialog.dismiss();
            }
        });

        // Cancel the dialog when the "Cancel" button is clicked
        ((Button) mDateTimeDialogView.findViewById(R.id.CancelDialog)).setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDateTimeDialog.cancel();
            }
        });

        // Reset Date and Time pickers when the "Reset" button is clicked
        ((Button) mDateTimeDialogView.findViewById(R.id.ResetDateTime)).setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDateTimePicker.reset();
            }
        });

        // Setup TimePicker
        mDateTimePicker.setIs24HourView(is24h);
        // No title on the dialog window
        mDateTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Set the dialog content view
        mDateTimeDialog.setContentView(mDateTimeDialogView);
        // Display the dialog
        mDateTimeDialog.show();
    }

}
