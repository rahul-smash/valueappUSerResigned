package com.signity.bonbon.ui.loginwithemail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.model.LoginData;
import com.signity.bonbon.model.LoginModel;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.fragment.SignupFragment;
import com.signity.bonbon.ui.login.ForgotPassActivity;
import com.signity.bonbon.ui.login.LoginScreenActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Rajinder on 12/10/15.
 */
public class LoginWithEmailFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;
    Button btnNext, signUpBtn, forgotPassBtn;
    EditText edtPass, edtEmail;
    ImageButton backButton;
    //    String from;
    String id;
    boolean isEmailExist = false, isNameExist = false;
    PrefManager prefManager;
    ImageView loginButton;
    CallbackManager callbackManager;
    ImageView signInButton;
    GoogleSignInOptions gso;
    Context context;
    private GoogleApiClient mGoogleApiClient;

    public static Fragment newInstance(Context context) {

        return Fragment.instantiate(context,
                LoginWithEmailFragment.class.getName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGmailComponents();
        prefManager = new PrefManager(getActivity());
    }

    private void initGmailComponents() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken("1054972669581-2b660msm4k1947lrfhrooqmbfj3ana41.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_layout_login_with_email, container, false);
        btnNext = (Button) rootView.findViewById(com.signity.bonbon.R.id.btnNext);
        signUpBtn = (Button) rootView.findViewById(R.id.signUpBtn);
        forgotPassBtn = (Button) rootView.findViewById(R.id.forgotPassBtn);
        edtEmail = (EditText) rootView.findViewById(R.id.edtEmail);
        edtPass = (EditText) rootView.findViewById(com.signity.bonbon.R.id.edtPass);
        backButton = (ImageButton) rootView.findViewById(com.signity.bonbon.R.id.backButton);


        //Facebook Login Button intialization
        loginButton = (ImageView) rootView.findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);


        //Google Login Button initialization
        signInButton = (ImageView) rootView.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);


        btnNext.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);
        backButton.setOnClickListener(this);
        forgotPassBtn.setOnClickListener(this);
        addActionDoneEvet(edtPass);


        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            if (callbackManager == null) {
                return;
            }
            callbackManager.onActivityResult(requestCode, resultCode, data);

        }
    }


    private void addActionDoneEvet(EditText edtPhone) {

        edtPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnNext.performClick();
                }

                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnNext:


                if (edtEmail.getText().toString().isEmpty() && edtPass.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Email and password are required to continue.", Toast.LENGTH_SHORT).show();
//                    textLayout1.setError("You need to enter your email");
//                    textLayout2.setError("You need to enter your password");
                } else {

                    if (edtPass.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), "Your password is required .", Toast.LENGTH_SHORT).show();
                    } else {
                        if (checkValidEmail(edtEmail.getText().toString())) {
                            callNetworkServiceForLogin();
                        } else {
                            Toast.makeText(getActivity(), "Your email is required and must be in a valid email format.", Toast.LENGTH_SHORT).show();
                        }

                    }


                }


                break;
            case R.id.backButton:
                getActivity().onBackPressed();
                break;

            case R.id.signUpBtn:
                callSignUpFragment("","","");
                break;

            case R.id.forgotPassBtn:
                Intent intent = new Intent(getActivity(), ForgotPassActivity.class);
                startActivity(intent);
                break;

            case R.id.sign_in_button:
                signIn();
                break;

            case R.id.login_button:
                initFacebookComponents();
                break;

        }

    }

    private void callSignUpFragment(String name, String email, String type) {

        Fragment fragment = SignupFragment.newInstance(getActivity());
        Bundle bundle = new Bundle();
        bundle.putString("name",name);
        bundle.putString("email",email);
        bundle.putString("type",type);
        fragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(com.signity.bonbon.R.anim.right_to_center_slide,
                com.signity.bonbon.R.anim.center_to_left_slide,
                com.signity.bonbon.R.anim.left_to_center_slide,
                com.signity.bonbon.R.anim.center_to_right_slide);
        ft.replace(com.signity.bonbon.R.id.container, fragment);
        ft.addToBackStack(null);
        ft.commit();

    }


    private void initFacebookComponents() {

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        AppEventsLogger.activateApp(getActivity());
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile,email,user_birthday,user_friends,user_location"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {


                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("LoginActivity", response.toString());
                        // Get facebook data from login
//                        Bundle bFacebookData = getFacebookData(object);

                        String name = "", email = "";
                        try {
                            name = object.getString("first_name") + " " + object.getString("last_name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            email = object.getString("email");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        try {
//                            callNetworkServiceForRegister(name, email, "facebook");
                            callSignUpFragment(name, email, "facebook");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            LoginManager.getInstance().logOut();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                try {
                    LoginManager.getInstance().logOut();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("facebook", "error");
            }

        });


    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("TAG", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            String name = acct.getDisplayName();
            String email = acct.getEmail();

            try {
                callSignUpFragment(name, email, "google");
            } catch (Exception e) {
                e.printStackTrace();
            }
            signOut();
        } else {
            Toast.makeText(getActivity(), "Not able to Login. Please try later.", Toast.LENGTH_LONG).show();
        }
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
    }


    private void callNetworkServiceForRegister(final String name, final String email,final String type) {
        ProgressDialogUtil.showProgressDialog(getActivity());


        String deviceId = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceToken = prefManager.getSharedValue(AppConstant.DEVICE_TOKEN);
        Map<String, String> param = new HashMap<String, String>();
        param.put("full_name", name);
        param.put("email", email);
        param.put("password", "");
        param.put("phone", "");
        param.put("device_id", deviceId);
        param.put("device_token", deviceToken);
        param.put("platform", AppConstant.PLATFORM);
        param.put("type",type);

        NetworkAdaper.getInstance().getNetworkServices().userSignup(param, new Callback<LoginModel>() {


            @Override
            public void success(LoginModel model, Response response) {

                if (model.getSuccess()) {
                    ProgressDialogUtil.hideProgressDialog();
                    if (model.getData() != null) {
                        saveUserIdToPref(model.getData());
                        proceedtoActivity();
                    }
                    Toast.makeText(getActivity(), "" + model.getMessage(), Toast.LENGTH_SHORT).show();

                } else {
                    ProgressDialogUtil.hideProgressDialog();
                    DialogHandler dialogHandler = new DialogHandler(getActivity());
                    dialogHandler.setdialogForFinish("Message", "" + model.getMessage(), false);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
            }
        });
    }

    private void callNetworkForForgotPassword(String email) {

        ProgressDialogUtil.showProgressDialog(getActivity());

        Map<String, String> param = new HashMap<String, String>();
        param.put("email_id", email);
        NetworkAdaper.getInstance().getNetworkServices().forgetPassword(param, new Callback<LoginModel>() {


            @Override
            public void success(LoginModel loginModel, Response response) {

                if (loginModel.getSuccess()) {
                    ProgressDialogUtil.hideProgressDialog();
                    DialogHandler dialogHandler = new DialogHandler(getActivity());
                    dialogHandler.setdialogForFinish("Success", "" + loginModel.getMessage(), false);
                } else {
                    ProgressDialogUtil.hideProgressDialog();
                    DialogHandler dialogHandler = new DialogHandler(getActivity());
                    dialogHandler.setdialogForFinish("Message", "" + loginModel.getMessage(), false);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
            }
        });

    }

    private void callNetworkServiceForLogin() {
        ProgressDialogUtil.showProgressDialog(getActivity());

        String email = edtEmail.getText().toString();
        String pass = edtPass.getText().toString();
        String deviceId = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceToken = prefManager.getSharedValue(AppConstant.DEVICE_TOKEN);
        Map<String, String> param = new HashMap<String, String>();
        param.put("email", email);
        param.put("password", pass);
        param.put("device_id", deviceId);
        param.put("device_token", deviceToken);
        param.put("platform", AppConstant.PLATFORM);
        NetworkAdaper.getInstance().getNetworkServices().loginVerification(param, new Callback<LoginModel>() {


            @Override
            public void success(LoginModel loginModel, Response response) {

                if (loginModel.getSuccess()) {
                    ProgressDialogUtil.hideProgressDialog();
                    if (loginModel.getData() != null) {
                        saveUserIdToPref(loginModel.getData());
                        proceedtoActivity();
                    }


                } else {
                    ProgressDialogUtil.hideProgressDialog();
                    DialogHandler dialogHandler = new DialogHandler(getActivity());
                    dialogHandler.setdialogForFinish("Message", "" + loginModel.getMessage(), false);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
            }
        });
    }


    private void proceedtoActivity() {
        if (getActivity().getCurrentFocus() != null) {
            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
        ((LoginScreenActivity) getActivity()).setResultForActivity(Activity.RESULT_OK);
    }


    private void saveUserIdToPref(LoginData data) {
        PrefManager prefManager = new PrefManager(getActivity());
        prefManager.storeSharedValue(AppConstant.ID, data.getId());
        prefManager.storeSharedValue(AppConstant.PHONE, data.getPhone());
        if (data.getFullName() != null && !data.getFullName().isEmpty()) {
            prefManager.storeSharedValue(AppConstant.NAME, data.getFullName());
            isNameExist = true;
        }
        if (data.getEmail() != null && !data.getEmail().isEmpty()) {
            prefManager.storeSharedValue(AppConstant.EMAIL, data.getEmail());
            isEmailExist = true;
        }
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
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
