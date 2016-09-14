package com.signity.bonbon.ui.shopcart;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.FontUtil;
import com.signity.bonbon.Utilities.GsonHelper;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.app.DataAdapter;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.ga.GAConstant;
import com.signity.bonbon.ga.GATrackers;
import com.signity.bonbon.gcm.GCMClientManager;
import com.signity.bonbon.model.FixedTaxDetail;
import com.signity.bonbon.model.GetOfferResponse;
import com.signity.bonbon.model.GetValidCouponResponse;
import com.signity.bonbon.model.LoyalityDataModel;
import com.signity.bonbon.model.LoyalityModel;
import com.signity.bonbon.model.OfferData;
import com.signity.bonbon.model.OnlinePaymentModel;
import com.signity.bonbon.model.Product;
import com.signity.bonbon.model.ResponseData;
import com.signity.bonbon.model.SelectedVariant;
import com.signity.bonbon.model.Store;
import com.signity.bonbon.model.TaxCalculationModel;
import com.signity.bonbon.model.TaxDataModel;
import com.signity.bonbon.model.TaxDetail;
import com.signity.bonbon.model.TaxDetails;
import com.signity.bonbon.model.ValidAllCouponData;
import com.signity.bonbon.model.ValidAllCouponsModel;
import com.signity.bonbon.model.Variant;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.Delivery.DeliveryPickupActivity;
import com.signity.bonbon.ui.home.MainActivity;
import com.signity.bonbon.ui.offer.OfferViewActivity;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ShoppingCartActivity2 extends Activity implements View.OnClickListener {
    public Typeface typeFaceRobotoRegular, typeFaceRobotoBold;
    ListView listViewCart;
    TextView items_price, discountVal, total, title, customerPts, note, rs1, rs2, rs3, rs4, shipping_charges_text, discountLblText, taxTag;
    Button placeorder;
    String userId,payment_method;
    String addressId;
    String shippingChargeText, minmimumChargesText, user_address, areaId;
    ProductListAdapter adapter;
    List<Product> listProduct;
    double shippingCharge = 0.0;
    double minimumCharges = 0.0;
    ImageButton homeBtn;
    RelativeLayout relativeLayout;
    boolean keyboardOpen = false;
    PrefManager prefManager;
    //list and adapter
    List<OfferData> listOfferData;
    ListOfferAdapter mAdapter;
    ListView offerList;
    TextView messageTxt;
    Dialog dialog, redeemDialog;
    ListView list_view;
    Adapter pointAdapter;
    RelativeLayout normalOfferScreen, shipping_layout, discount_layout;
    LinearLayout loyalityScreen;
    String isTaxEnable, taxLabel, taxRate;
    String discount = "0", fixed_discount_amount = "0";
    ViewGroup footerView;
    LinearLayout layout, linearFixedTaxLayout,linearFixedTaxLayoutDisable;
    private GCMClientManager pushClientManager;
    private Button backButton, btnSearch;
    private TextView shipping_charges;
    private AppDatabase appDb;
    private Button applyCoupon, applyCoupon_1, applyOffer, applyOffer_1, redeemPoints;
    private EditText editCoupon, editCoupon_1;
    private EditText edtBar;
    private String isForPickUpStatus = "no";
    private String coupenCode = "";
    private double loyalityPoints;
    private String taxDetailsJson,taxLabelJson,taxFixedTaxJson;
    private String loyalityStatus = "0"; // If it will be 1 then we will show loyality points screen otherwise normal screen
    private String couponCode = "";    // variable used to store couponcode applied by the User.
    private String tax="0" ;
    GsonHelper gsonHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.signity.bonbon.R.layout.shopping_cart_activity2);
        ShoppingCartActivity2.this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        appDb = DbAdapter.getInstance().getDb();
        gsonHelper = new GsonHelper();
        prefManager = new PrefManager(ShoppingCartActivity2.this);

        GATrackers.getInstance().trackScreenView(GAConstant.CHECKOUT_SCREEN);
        userId = getIntent().getStringExtra("userId");
        addressId = getIntent().getStringExtra("addressId");
        shippingChargeText = getIntent().getStringExtra("shiping_charges");
        minmimumChargesText = getIntent().getStringExtra("minimum_charges");
        user_address = getIntent().getStringExtra("user_address");
        areaId = getIntent().getStringExtra("area_id");
        try {
            payment_method = getIntent().getStringExtra("payment_method");
        } catch (Exception e) {
            e.printStackTrace();
            payment_method="2";
        }
        isForPickUpStatus = getIntent().getStringExtra("isForPickup") != null ? getIntent().getStringExtra("isForPickup") : "no";

        if (shippingChargeText != null && !shippingChargeText.isEmpty()) {
            shippingCharge = Double.parseDouble(shippingChargeText);
        }
        if (minmimumChargesText != null && !minmimumChargesText.isEmpty()) {
            minimumCharges = Double.parseDouble(minmimumChargesText);
        }
        pushClientManager = new GCMClientManager(this, AppConstant.PROJECT_NUMBER);
        typeFaceRobotoRegular = FontUtil.getTypeface(this, FontUtil.FONT_ROBOTO_REGULAR);
        typeFaceRobotoBold = FontUtil.getTypeface(this, FontUtil.FONT_ROBOTO_BOLD);


        final View activityRootView = findViewById(R.id.activityRoot);
        relativeLayout = (RelativeLayout) findViewById(R.id.listLayout);

        loyalityScreen = (LinearLayout) findViewById(R.id.loyalityScreen);  // getting id of loyality screen
        normalOfferScreen = (RelativeLayout) findViewById(R.id.normalOfferScreen); // getting id of normal screen

        loyalityStatus = prefManager.getSharedValue(AppConstant.LOYALITY);

        if (loyalityStatus.equalsIgnoreCase("0")) {
            loyalityScreen.setVisibility(View.GONE);
            normalOfferScreen.setVisibility(View.VISIBLE);
        } else if (loyalityStatus.equalsIgnoreCase("1")) {
            loyalityScreen.setVisibility(View.VISIBLE);
            normalOfferScreen.setVisibility(View.GONE);
        }

        homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(this);


        listViewCart = (ListView) findViewById(com.signity.bonbon.R.id.items_list);

        prepareFooter();

        edtBar = (EditText) findViewById(R.id.edtBar);
        total = (TextView) findViewById(com.signity.bonbon.R.id.total);
        title = (TextView) findViewById(com.signity.bonbon.R.id.textTitle);

        title.setText("Confirm Order");
        placeorder = (Button) findViewById(com.signity.bonbon.R.id.placeorder);
        backButton = (Button) findViewById(com.signity.bonbon.R.id.backButton);
        backButton.setOnClickListener(this);
        placeorder.setOnClickListener(this);
        editCoupon = (EditText) findViewById(R.id.editCoupon);
        editCoupon_1 = (EditText) findViewById(R.id.editCoupon_1);
        applyCoupon = (Button) findViewById(R.id.applyCoupen);
        applyCoupon_1 = (Button) findViewById(R.id.applyCoupen_1);
        applyOffer = (Button) findViewById(R.id.applyOffer);
        applyOffer_1 = (Button) findViewById(R.id.applyOffer_1);
        redeemPoints = (Button) findViewById(R.id.redeemPoints);


        taxTag = (TextView) findViewById(R.id.taxTag);
        applyCoupon.setTag("apply");
        applyCoupon_1.setTag("apply");
        applyCoupon.setOnClickListener(this);
        applyCoupon_1.setOnClickListener(this);
        applyOffer.setOnClickListener(this);
        applyOffer_1.setOnClickListener(this);
        redeemPoints.setOnClickListener(this);


        listProduct = appDb.getCartListProduct();

        if (listProduct != null && listProduct.size() != 0) {
            adapter = new ProductListAdapter(ShoppingCartActivity2.this, listProduct);
            listViewCart.setAdapter(adapter);
            listViewCart.setVisibility(View.VISIBLE);
            updateCartPrice();
            updateTaxPrice();
        }


        relativeLayout.post(new Runnable() {
            public void run() {
                int height = relativeLayout.getHeight();
                LinearLayout.LayoutParams mParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, height);
                relativeLayout.setLayoutParams(mParam);
            }
        });


        listViewCart.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (keyboardOpen) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touapplyOfferapplyOfferapplyOfferapplyOfferch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;

                    }
                }
                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });


        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //r will be populated with the coordinates of your view that area still visible.
                activityRootView.getWindowVisibleDisplayFrame(r);

                int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > 100) {
                    // if more than 100 pixels, its probably a keyboard...
                    keyboardOpen = true;
                } else {
                    keyboardOpen = false;
                }
            }
        });

        listViewCart.setSelection(0);

    }

    private void initializeFooterView(TaxDataModel model) {

        prepareFooter();


        double itemsPrice = Double.parseDouble(model.getItemSubTotal());
        double tax = Double.parseDouble(model.getTax());
        double discount = Double.parseDouble(model.getDiscount());
        double shippingCharge = Double.parseDouble(model.getShipping());


//        tax_value.setText(""+String.format("%.2f", tax));
        items_price.setText("" + String.format("%.2f", itemsPrice));
        discountVal.setText("" + String.format("%.2f", discount));
        shipping_charges.setText("" + String.format("%.2f", shippingCharge));

        if (shippingCharge == 0.0) {
            shipping_charges_text.setVisibility(View.GONE);
            shipping_layout.setVisibility(View.GONE);
        } else {
            shipping_charges_text.setVisibility(View.VISIBLE);
            shipping_layout.setVisibility(View.VISIBLE);
        }

        if (discount == 0.0) {
            discountLblText.setVisibility(View.GONE);
            discount_layout.setVisibility(View.GONE);
        } else {
            discountLblText.setVisibility(View.VISIBLE);
            discount_layout.setVisibility(View.VISIBLE);
        }

        addTaxRow(model.getTaxDetail());

        List<FixedTaxDetail> listWithTaxEnable=new ArrayList<>();
        List<FixedTaxDetail> listWithTaxdisable=new ArrayList<>();

        if(model.getFixedTaxDetail()!=null && model.getFixedTaxDetail().size()!=0){

            for(FixedTaxDetail fixedTaxDetail:model.getFixedTaxDetail()){
                if(fixedTaxDetail.getIsTaxEnable().equalsIgnoreCase("1")){
                    listWithTaxEnable.add(fixedTaxDetail);
                }
                else {
                    listWithTaxdisable.add(fixedTaxDetail);
                }
            }

        }
        addFixedTaxRowEnable(listWithTaxEnable);
        addFixedTaxRowDisable(listWithTaxdisable);

    }

    private void addFixedTaxRowEnable(List<FixedTaxDetail> fixedTaxDetail) {

        /*View namebar = (LinearLayout)findViewById(R.id.linearFixedTaxLayout);
        ((ViewGroup) namebar.getParent()).removeView(namebar);*/


        if (!fixedTaxDetail.isEmpty()) {

            for (int i = 0; i < fixedTaxDetail.size(); i++) {
                View child = getLayoutInflater().inflate(R.layout.tax_row_layout, null);
                TextView tax_label = (TextView) child.findViewById(R.id.tax_label);
                TextView tax_value = (TextView) child.findViewById(R.id.tax_value);
                TextView rs5 = (TextView) child.findViewById(R.id.rs5);

                String currency = prefManager.getSharedValue(AppConstant.CURRENCY);
                if (currency.contains("\\")) {
                    rs5.setText(unescapeJavaString(currency));
                } else {
                    rs5.setText(currency);
                }
                double tax= Double.parseDouble(fixedTaxDetail.get(i).getFixedTaxAmount());
                tax_label.setText("" + fixedTaxDetail.get(i).getFixedTaxLabel());
                tax_value.setText("" + String.format("%.2f", tax));

                if(tax!=0.0){
                    linearFixedTaxLayout.addView(child);
                }


            }
        }

    }

    private void addFixedTaxRowDisable(List<FixedTaxDetail> fixedTaxDetail) {

        /*View namebar = (LinearLayout)findViewById(R.id.linearFixedTaxLayout);
        ((ViewGroup) namebar.getParent()).removeView(namebar);*/


        if (!fixedTaxDetail.isEmpty()) {

            for (int i = 0; i < fixedTaxDetail.size(); i++) {
                View child = getLayoutInflater().inflate(R.layout.tax_row_layout, null);
                TextView tax_label = (TextView) child.findViewById(R.id.tax_label);
                TextView tax_value = (TextView) child.findViewById(R.id.tax_value);
                TextView rs5 = (TextView) child.findViewById(R.id.rs5);

                String currency = prefManager.getSharedValue(AppConstant.CURRENCY);
                if (currency.contains("\\")) {
                    rs5.setText(unescapeJavaString(currency));
                } else {
                    rs5.setText(currency);
                }
                double tax= Double.parseDouble(fixedTaxDetail.get(i).getFixedTaxAmount());
                tax_label.setText("" + fixedTaxDetail.get(i).getFixedTaxLabel());
                tax_value.setText("" + String.format("%.2f", tax));
                if(tax!=0.0){
                    linearFixedTaxLayoutDisable.addView(child);
                }
            }
        }

    }


    private void addTaxRow(List<TaxDetails> detailsList) {


/*
        View namebar = (LinearLayout)findViewById(R.id.linearTaxLayout);
        ((ViewGroup) namebar.getParent()).removeView(namebar);*/

        if (!detailsList.isEmpty()) {

            for (int i = 0; i < detailsList.size(); i++) {
                View child = getLayoutInflater().inflate(R.layout.tax_row_layout, null);
                TextView tax_label = (TextView) child.findViewById(R.id.tax_label);
                TextView tax_value = (TextView) child.findViewById(R.id.tax_value);
                TextView rs5 = (TextView) child.findViewById(R.id.rs5);

                String currency = prefManager.getSharedValue(AppConstant.CURRENCY);
                if (currency.contains("\\")) {
                    rs5.setText(unescapeJavaString(currency));
                } else {
                    rs5.setText(currency);
                }
                double tax= Double.parseDouble(detailsList.get(i).getTax());

                tax_label.setText("" + detailsList.get(i).getLabel() + "(" + detailsList.get(i).getRate() + "%)");
                tax_value.setText("" + String.format("%.2f", tax));

                if(tax!=0.0){
                    layout.addView(child);
                }
            }
        }

    }


    private void updateTaxPrice() {

        isTaxEnable = prefManager.getSharedValue(AppConstant.istaxenable);
        taxLabel = prefManager.getSharedValue(AppConstant.tax_label_name);

        if (isTaxEnable.equalsIgnoreCase("0")) {
            taxTag.setVisibility(View.VISIBLE);
            taxRate = "0";
        } else {
            taxTag.setVisibility(View.GONE);
            taxRate = prefManager.getSharedValue(AppConstant.tax_rate);
        }

        callNetworkForTaxCalculations();

    }


    private void updateShippingCharges() {
//        double taxValue = Double.parseDouble(getTaxAmount());
        double totalPrice = getTotalPrice();

        if (shippingCharge != 0.0) {
            if (minimumCharges != 0.0) {
                if (minimumCharges > totalPrice) {
                    shipping_charges.setText(String.valueOf(shippingCharge));
                    total.setText(String.valueOf(totalPrice + shippingCharge));
                } else {
                    shippingCharge = 0.0;
                    shipping_charges.setText(String.valueOf(0.0));
                    total.setText(String.valueOf(totalPrice));
                }
            } else {
                shipping_charges.setText(String.valueOf(shippingCharge));
                total.setText(String.valueOf(totalPrice + shippingCharge));
            }
        } else {
            shipping_charges.setText(String.valueOf(shippingCharge));
            total.setText(String.valueOf(totalPrice + shippingCharge));
        }
    }


    private void updateCartPrice() {
        String totalCartValue = String.valueOf(getTotalPrice());
        items_price.setText(totalCartValue);
        updateShippingCharges();
    }


    private double getTotalPrice() {
        String totalCartValue = appDb.getCartTotalPrice();
        DecimalFormat df = new DecimalFormat("###.##");
        return Double.valueOf(df.format(Double.parseDouble(totalCartValue)));
    }

    private void callNetworkServiceForPlaceOrder(String id, String addressId) {


        ProgressDialogUtil.showProgressDialog(ShoppingCartActivity2.this);
        String deviceId = Settings.Secure.getString(ShoppingCartActivity2.this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceToken = pushClientManager.getRegistrationId(ShoppingCartActivity2.this);
//        String order = appDb.getOrderStringForSubmit();
        PrefManager prefManager = new PrefManager(this);
        String shippingcharge = shipping_charges.getText().toString();
        String orderPrice = items_price.getText().toString();
        String discount = discountVal.getText().toString();
        String amount = total.getText().toString();
        String order = appDb.getCartListStringJson();
        String note = "cod "+edtBar.getText().toString();
//        String tax = tax_value.getText().toString();
        String coupon_code = "" + editCoupon.getText().toString();


        Log.e("Order", order);
        Map<String, String> param = new HashMap<String, String>();
        param.put("device_id", deviceId);
        param.put("user_id", id);
        param.put("device_token", deviceToken);
        param.put("platform", AppConstant.PLATFORM);
        param.put("payment_method", "cod");
        param.put("user_address_id", addressId);
        param.put("shipping_charges", shippingcharge);
        param.put("tax", tax);
        param.put("tax_rate", taxRate);
        param.put("note", note);
        param.put("orders", order);
        param.put("coupon_code", coupenCode);
        param.put("checkout", orderPrice);
        param.put("discount", discount);
        param.put("total", amount);
        param.put("user_address", user_address);

        param.put("store_tax_rate_detail", taxLabelJson);
        param.put("store_fixed_tax_detail", taxFixedTaxJson);
        param.put("calculated_tax_detail", taxDetailsJson);

//        param.put("coupon_code", coupon_code);
        Log.e("params", param.toString());
        NetworkAdaper.getInstance().getNetworkServices().placeOrder(param, new Callback<ResponseData>() {
            @Override
            public void success(ResponseData responseData, Response response) {
                ProgressDialogUtil.hideProgressDialog();
                if (responseData.getSuccess() != null ? responseData.getSuccess() : false) {
                    String orderGAC = getString(R.string.app_name) + GAConstant.ORDER;
                    GATrackers.getInstance().trackEvent(orderGAC, orderGAC + GAConstant.PLACED,
                            "There is one order of amount " + items_price.getText().toString() + " is placed for the address " + user_address);
                    showAlertDialog(ShoppingCartActivity2.this, "Thank you!", "Thank you for placing the order. We will confirm your order soon.");
                } else {
                    DialogHandler dialogHandler = new DialogHandler(ShoppingCartActivity2.this);
                    dialogHandler.setdialogForFinish("Message", ""+responseData.getMessage(), false);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                DialogHandler dialogHandler = new DialogHandler(ShoppingCartActivity2.this);
                dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), false);
            }
        });


    }

    private void callNetworkServiceForPlaceOrderForPickup(String id, String addressId) {
        ProgressDialogUtil.showProgressDialog(ShoppingCartActivity2.this);
        String deviceId = Settings.Secure.getString(ShoppingCartActivity2.this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceToken = pushClientManager.getRegistrationId(ShoppingCartActivity2.this);
//        String order = appDb.getOrderStringForSubmit();
        String shippingcharge = shipping_charges.getText().toString();
        String orderPrice = items_price.getText().toString();
        String discount = discountVal.getText().toString();
        String amount = total.getText().toString();
        String order = appDb.getCartListStringJson();
        String note = edtBar.getText().toString();
//        String tax = tax_value.getText().toString();


        Map<String, String> param = new HashMap<String, String>();
        param.put("device_id", deviceId);
        param.put("user_id", id);
        param.put("device_token", deviceToken);
        param.put("platform", AppConstant.PLATFORM);
        param.put("payment_method", "cod");
        param.put("user_address_id", addressId);
        param.put("shipping_charges", shippingcharge);
        param.put("tax", tax);
        param.put("tax_rate", taxRate);
        param.put("note", note);
        param.put("orders", order);
        param.put("checkout", orderPrice);
        param.put("coupon_code", coupenCode);
        param.put("discount", discount);
        param.put("total", amount);
        param.put("user_address", user_address);

        param.put("store_tax_rate_detail", taxLabelJson);
        param.put("store_fixed_tax_detail", taxFixedTaxJson);
        param.put("calculated_tax_detail", taxDetailsJson);
        Log.e("params", param.toString());
        NetworkAdaper.getInstance().getNetworkServices().placeOrder(param, new Callback<ResponseData>() {
            @Override
            public void success(ResponseData responseData, Response response) {
                ProgressDialogUtil.hideProgressDialog();
                if (responseData.getSuccess() != null ? responseData.getSuccess() : false) {
                    String orderGAC = getString(R.string.app_name) + GAConstant.ORDER;
                    GATrackers.getInstance().trackEvent(orderGAC, orderGAC + GAConstant.PLACED,
                            "There is one order of amount " + items_price.getText().toString() + " is placed for the address " + user_address);

                    showAlertDialogwithPickUp(ShoppingCartActivity2.this, "Thank you!", "Thank you for placing the order. We will confirm your order soon.");
                } else {
                    DialogHandler dialogHandler = new DialogHandler(ShoppingCartActivity2.this);
                    dialogHandler.setdialogForFinish("Message", ""+responseData.getMessage(), false);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                DialogHandler dialogHandler = new DialogHandler(ShoppingCartActivity2.this);
                dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), false);
            }
        });
    }






    private void callNetworkServiceForPlaceOrderForOnline(String id, String addressId,String payment_request_id,String payment_id) {

        ProgressDialogUtil.showProgressDialog(ShoppingCartActivity2.this);
        String deviceId = Settings.Secure.getString(ShoppingCartActivity2.this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceToken = pushClientManager.getRegistrationId(ShoppingCartActivity2.this);
//        String order = appDb.getOrderStringForSubmit();
        PrefManager prefManager = new PrefManager(this);
        String shippingcharge = shipping_charges.getText().toString();
        String orderPrice = items_price.getText().toString();
        String discount = discountVal.getText().toString();
        String amount = total.getText().toString();
        String order = appDb.getCartListStringJson();
        String note = "Online "+edtBar.getText().toString();
        String coupon_code = "" + editCoupon.getText().toString();
        Log.e("Order", order);
        Map<String, String> param = new HashMap<String, String>();

        param.put("payment_request_id", payment_request_id);
        param.put("payment_id", payment_id);
        param.put("device_id", deviceId);
        param.put("user_id", id);
        param.put("device_token", deviceToken);
        param.put("platform", AppConstant.PLATFORM);
        param.put("payment_method", "online");
        param.put("user_address_id", addressId);
        param.put("shipping_charges", shippingcharge);
        param.put("tax", tax);
        param.put("tax_rate", taxRate);
        param.put("note", note);
        param.put("orders", order);
        param.put("coupon_code", coupenCode);
        param.put("checkout", orderPrice);
        param.put("discount", discount);
        param.put("total", amount);
        param.put("user_address", user_address);


        param.put("store_tax_rate_detail", taxLabelJson);
        param.put("store_fixed_tax_detail", taxFixedTaxJson);
        param.put("calculated_tax_detail", taxDetailsJson);
//        param.put("coupon_code", coupon_code);
        Log.e("params", param.toString());
        NetworkAdaper.getInstance().getNetworkServices().placeOrder(param, new Callback<ResponseData>() {
            @Override
            public void success(ResponseData responseData, Response response) {
                ProgressDialogUtil.hideProgressDialog();
                if (responseData.getSuccess() != null ? responseData.getSuccess() : false) {
                    String orderGAC = getString(R.string.app_name) + GAConstant.ORDER;
                    GATrackers.getInstance().trackEvent(orderGAC, orderGAC + GAConstant.PLACED,
                            "There is one order of amount " + items_price.getText().toString() + " is placed for the address " + user_address);

                    showAlertDialog(ShoppingCartActivity2.this, "Thank you!", "Thank you for placing the order. We will confirm your order soon.");
                } else {
                    DialogHandler dialogHandler = new DialogHandler(ShoppingCartActivity2.this);
                    dialogHandler.setdialogForFinish("Message", ""+responseData.getMessage(), false);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                DialogHandler dialogHandler = new DialogHandler(ShoppingCartActivity2.this);
                dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), false);
            }
        });


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(ShoppingCartActivity2.this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case com.signity.bonbon.R.id.backButton:
                onBackPressed();
                break;
            case com.signity.bonbon.R.id.placeorder:
                if (openTime()) {
                    if (appDb.getCartSize() != 0) {
                        if (isForPickUpStatus.equalsIgnoreCase("yes")) {
                            callNetworkServiceForPlaceOrderForPickup(userId, addressId);
                        } else {

                            if(payment_method.equalsIgnoreCase("2")){
                                callNetworkServiceForPlaceOrder(userId, addressId);
                            }else if(payment_method.equalsIgnoreCase("3")){
                                callNetworkForOnlinepayment();
                            }else {
                                callNetworkServiceForPlaceOrder(userId, addressId);
                            }
                            /*String paymentOption=prefManager.getSharedValue(AppConstant.ONLINE_PAYMENT);

                            if(paymentOption.equalsIgnoreCase("0")){
                                callNetworkServiceForPlaceOrder(userId, addressId);
                            }else if(paymentOption.equalsIgnoreCase("1")){
                                callForPaymentOptions();
                            }*/
//                            callNetworkServiceForPlaceOrder(userId, addressId);
                        }
                    } else {
//                    Toast.makeText(ShoppingCartActivity2.this, "Empty Cart", Toast.LENGTH_SHORT).show();
                        new DialogHandler(ShoppingCartActivity2.this).setdialogForFinish("Message", "You have a empty cart", false);
                    }
                } else {
                    String message = prefManager.getSharedValue(AppConstant.MESSAGE);
                    new DialogHandler(ShoppingCartActivity2.this).setdialogForFinish("Message", message, false);
                }
                break;

            case R.id.applyCoupen:
                if (applyCoupon.getTag().equals("apply")) {
                    onApplyCoupon();
                } else if (applyCoupon.getTag().equals("remove")) {
                    onRemoveCoupon();
                }
                break;

            case R.id.applyCoupen_1:
                if (applyCoupon_1.getTag().equals("apply")) {
                    onApplyCoupon();
                } else if (applyCoupon_1.getTag().equals("remove")) {
                    onRemoveCoupon();
                }
                break;

            case R.id.applyOffer:
                if (dialog != null) {
                    dialog = null;
                }
                callNetworkForOffers();
                break;


            case R.id.applyOffer_1:
                if (dialog != null) {
                    dialog = null;
                }
                callNetworkForOffers();
                break;


            case R.id.redeemPoints:
                if (redeemDialog != null) {
                    redeemDialog = null;
                }
                callNetworkForLoyalityPoints();
                break;

            case R.id.homeBtn:
                Intent intent = new Intent(ShoppingCartActivity2.this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void callForPaymentOptions() {
        final Dialog dialog = new Dialog(ShoppingCartActivity2.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.choose_payment_options);

        Button codBtn=(Button)dialog.findViewById(R.id.codBtn);
        Button onlineBtn=(Button)dialog.findViewById(R.id.onlineBtn);

        codBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callNetworkServiceForPlaceOrder(userId, addressId);
            }
        });

        onlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callNetworkForOnlinepayment();
            }
        });

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }



    private void callNetworkForOnlinepayment(){
        ProgressDialogUtil.showProgressDialog(ShoppingCartActivity2.this);
        String amount = total.getText().toString();
        String user_name=prefManager.getSharedValue(AppConstant.NAME);
        String email=prefManager.getSharedValue(AppConstant.EMAIL);
        String phone=prefManager.getSharedValue(AppConstant.PHONE);
        Map<String, String> param = new HashMap<String, String>();
        param.put("user_name", user_name);
        param.put("email", email);
        param.put("phone", phone);
        param.put("total", amount);
//        param.put("coupon_code", coupon_code);
        Log.e("params", param.toString());
        NetworkAdaper.getInstance().getNetworkServices().onlinePayment(param, new Callback<OnlinePaymentModel>() {


            @Override
            public void success(OnlinePaymentModel onlinePaymentModel, Response response) {

                if (onlinePaymentModel.getSuccess()) {
                    ProgressDialogUtil.hideProgressDialog();
                    Intent intent = new Intent(ShoppingCartActivity2.this, PayOnlineActivity.class);
                    intent.putExtra("url", onlinePaymentModel.getPaymentUrl());
                    startActivityForResult(intent, 2);
                    AnimUtil.slideFromRightAnim(ShoppingCartActivity2.this);
                } else {
                    ProgressDialogUtil.hideProgressDialog();
                    Toast.makeText(ShoppingCartActivity2.this, "" + onlinePaymentModel.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                DialogHandler dialogHandler = new DialogHandler(ShoppingCartActivity2.this);
                dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), false);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(resultCode==2)
        {
            String payment_request_id=data.getStringExtra("payment_request_id");
            String payment_id=data.getStringExtra("payment_id");
            callNetworkServiceForPlaceOrderForOnline(userId, addressId, payment_request_id, payment_id);
        }
        else if(resultCode==3){
            showAlertDialog(ShoppingCartActivity2.this, "Message", "There is problem in processing your payment. Please try after some time. In case your money has been deducted from your account then please contact your respective bank.");
        }
    }


    private boolean openTime() {
        boolean status = false;
        String is24x7_open = prefManager.getSharedValue(AppConstant.is24x7_open);
        String openTime = prefManager.getSharedValue(AppConstant.OPEN_TIME);
        String closeTime = prefManager.getSharedValue(AppConstant.CLOSE_TIME);
        String openDays = prefManager.getSharedValue(AppConstant.OPEN_DAYS);

        if (is24x7_open.equalsIgnoreCase("1")) {
            return true;
        } else if (openTime.isEmpty() || closeTime.isEmpty()) {
            return true;
        } else {
            if (isStoreClosedToday(openDays)) {
                return false;
            }

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mmaa");
            String currentDate = formatter.format(calendar.getTime());

            String strOpenHrs = openTime;
            String strCloseHrs = closeTime;

            SimpleDateFormat format = new SimpleDateFormat("hh:mmaa");
            Date openDate = null, closeDate = null, currentTime = null;
            try {
                openDate = format.parse(strOpenHrs);
                closeDate = format.parse(strCloseHrs);
                currentTime = format.parse(currentDate);

                if (currentTime.compareTo(openDate) > 0 && currentTime.compareTo(closeDate) < 0) {
                    status = true;
                } else {
                    status = false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
//            format = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
//            String date = format.format(newDate);
        }
        return status;
    }

    private boolean isStoreClosedToday(String openDays) {
        Calendar calendar = Calendar.getInstance();
        boolean result = false;
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                if (!openDays.contains("sun")) {
                    result = true;
                }
                break;
            case 2:
                if (!openDays.contains("mon")) {
                    result = true;
                }
                break;
            case 3:
                if (!openDays.contains("tue")) {
                    result = true;
                }
                break;
            case 4:
                if (!openDays.contains("wed")) {
                    result = true;
                }
                break;
            case 5:
                if (!openDays.contains("thu")) {
                    result = true;
                }
                break;
            case 6:
                if (!openDays.contains("fri")) {
                    result = true;
                }
                break;
            case 7:
                if (!openDays.contains("sat")) {
                    result = true;
                }
                break;
        }
        return result;
    }

    private void callNetworkForLoyalityPoints() {
        try {
            ProgressDialogUtil.showProgressDialog(ShoppingCartActivity2.this);
            Map<String, String> param = new HashMap<String, String>();
            param.put("user_id", userId);
            NetworkAdaper.getInstance().getNetworkServices().getLoyalityPoints(param, new Callback<LoyalityModel>() {
                @Override
                public void success(LoyalityModel loyalityModel, Response response) {

                    if (loyalityModel.getSuccess()) {
                        ProgressDialogUtil.hideProgressDialog();

                        redeemDialog = new Dialog(ShoppingCartActivity2.this);
                        redeemDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        redeemDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        redeemDialog.setContentView(R.layout.loyality_points_layout);

                        RelativeLayout header = (RelativeLayout) findViewById(R.id.header);
                        list_view = (ListView) redeemDialog.findViewById(R.id.loyalityList);
                        customerPts = (TextView) redeemDialog.findViewById(R.id.customerPts);
                        note = (TextView) redeemDialog.findViewById(R.id.note);

                        if (loyalityModel.getLoyalityPoints().isEmpty()) {
                            customerPts.setText("You have NIL points.");
                            loyalityPoints = Double.parseDouble(loyalityModel.getLoyalityPoints());
                        } else {
                            loyalityPoints = Double.parseDouble(loyalityModel.getLoyalityPoints());
                            customerPts.setText("You have " + loyalityModel.getLoyalityPoints() + " points.");
                        }


                        if (loyalityModel.getData() != null && loyalityModel.getData().size() != 0) {
                            pointAdapter = new Adapter(ShoppingCartActivity2.this, loyalityModel.getData());
                            list_view.setAdapter(pointAdapter);
                        }

                        redeemDialog.setCanceledOnTouchOutside(true);
                        redeemDialog.show();

                    } else {
                        ProgressDialogUtil.hideProgressDialog();
                        Toast.makeText(ShoppingCartActivity2.this, "There are no coupons to redeem.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    ProgressDialogUtil.hideProgressDialog();
                }
            });
        } catch (Exception e) {

        }
    }


    private void callNetworkForOffers() {
        ProgressDialogUtil.showProgressDialog(ShoppingCartActivity2.this);

        Store store = appDb.getStore(prefManager.getSharedValue(AppConstant.STORE_ID));
        String storeId = store.getId();
        Map<String, String> param = new HashMap<String, String>();

        param.put("store_id", storeId);
        param.put("user_id", userId);


        if (isForPickUpStatus.equalsIgnoreCase("yes")) {
            param.put("order_facility", "Pickup");
            param.put("area_id", addressId);
        } else if (isForPickUpStatus.equalsIgnoreCase("no")) {
            param.put("order_facility", "Delivery");
            param.put("area_id", areaId);
        }


        NetworkAdaper.getInstance().getNetworkServices().getStoreOffer(param, new Callback<GetOfferResponse>() {
            @Override
            public void success(GetOfferResponse getOfferResponse, Response response) {
                ProgressDialogUtil.hideProgressDialog();
                if (getOfferResponse.getSuccess() != null ? getOfferResponse.getSuccess() : false) {
                    listOfferData = getOfferResponse.getData();
                }
                dialog = new Dialog(ShoppingCartActivity2.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.offers_screen);

                offerList = (ListView) dialog.findViewById(R.id.offerList);
                messageTxt = (TextView) dialog.findViewById(R.id.messageTxt);
                if (listOfferData != null && listOfferData.size() != 0) {
                    mAdapter = new ListOfferAdapter(ShoppingCartActivity2.this, listOfferData);
                    offerList.setAdapter(mAdapter);
                    offerList.setVisibility(View.VISIBLE);
                    messageTxt.setVisibility(View.GONE);
                } else {
                    offerList.setVisibility(View.GONE);
                    messageTxt.setVisibility(View.VISIBLE);
                }

                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                DialogHandler dialogHandler = new DialogHandler(ShoppingCartActivity2.this);
                dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), false);
            }
        });

    }

    private void onRemoveCoupon() {

        if (!discountVal.getText().toString().isEmpty() && !discountVal.getText().toString().equalsIgnoreCase("0")) {
            coupenCode = "";
            discount = "0";
            fixed_discount_amount = "0";
            double discount = Double.parseDouble(discountVal.getText().toString());
            double totalval = Double.parseDouble(total.getText().toString());
            double finalVal = totalval + discount;
            DecimalFormat df = new DecimalFormat("###.##");
            total.setText(String.valueOf(df.format(finalVal)));
            discountVal.setText("0");

            if (loyalityStatus.equalsIgnoreCase("0")) {
                applyCoupon_1.setText("Apply Coupon");
                applyCoupon_1.setTag("apply");
                editCoupon_1.setText("");
            } else if (loyalityStatus.equalsIgnoreCase("1")) {
                applyCoupon.setVisibility(View.GONE);
                editCoupon.setVisibility(View.GONE);
                applyOffer.setVisibility(View.VISIBLE);
                redeemPoints.setVisibility(View.VISIBLE);
                applyCoupon.setText("Apply Coupon");
                applyCoupon.setTag("apply");
                editCoupon.setText("");
                applyCoupon_1.setText("Apply Coupon");
                applyCoupon_1.setTag("apply");
                editCoupon_1.setText("");
            }

            callNetworkForTaxCalculations();
        }
    }

    /*private void onRemoveCoupon1() {

        if (!discountVal.getText().toString().isEmpty() && !discountVal.getText().toString().equalsIgnoreCase("0")) {
            coupenCode = "";
            discount = "0";
            fixed_discount_amount = "0";
            double discount = Double.parseDouble(discountVal.getText().toString());
            double totalval = Double.parseDouble(total.getText().toString());
            double finalVal = totalval + discount;
            DecimalFormat df = new DecimalFormat("###.##");
            total.setText(String.valueOf(df.format(finalVal)));
            discountVal.setText("0");
            applyCoupon_1.setText("Apply Coupon");
            applyCoupon_1.setTag("apply");
            editCoupon_1.setText("");


            callNetworkForTaxCalculations();
        }
    }*/


    private void applyDiscount(String discountPercent, String strOfferMinimumPrice) {
        double totalPrice = getTotalPrice();
        double discount = ((totalPrice * Double.parseDouble(discountPercent) / 100));
        double offerMinimumPrice = Double.parseDouble(strOfferMinimumPrice);

        if (offerMinimumPrice <= totalPrice) {
            double finalPrice = totalPrice - discount + shippingCharge;
            DecimalFormat df = new DecimalFormat("###.##");
            total.setText(String.valueOf(df.format(finalPrice)));
            discountVal.setText(String.valueOf(df.format(discount)));

            if (loyalityStatus.equalsIgnoreCase("0")) {
                applyCoupon_1.setText("Remove Coupon");
                applyCoupon_1.setTag("remove");
            } else if (loyalityStatus.equalsIgnoreCase("1")) {
                applyCoupon.setText("Remove Coupon");
                applyCoupon.setTag("remove");
                applyCoupon_1.setText("Remove Coupon");
                applyCoupon_1.setTag("remove");
                applyCoupon.setVisibility(View.VISIBLE);
                editCoupon.setVisibility(View.VISIBLE);
                applyOffer.setVisibility(View.GONE);
                redeemPoints.setVisibility(View.GONE);
            }

            callNetworkForTaxCalculations();

        } else {
            Toast.makeText(ShoppingCartActivity2.this, "This offer is valid for minimum price order."
                    + offerMinimumPrice, Toast.LENGTH_SHORT).show();
        }

    }


    /*private void applyDiscountForNormalScreen(String discountPercent, String strOfferMinimumPrice) {
        double totalPrice = getTotalPrice();
        double discount = ((totalPrice * Double.parseDouble(discountPercent) / 100));
        double offerMinimumPrice = Double.parseDouble(strOfferMinimumPrice);

        if (offerMinimumPrice <= totalPrice) {
            double finalPrice = totalPrice - discount + shippingCharge;
            DecimalFormat df = new DecimalFormat("###.##");
            total.setText(String.valueOf(df.format(finalPrice)));
            discountVal.setText(String.valueOf(df.format(discount)));
            applyCoupon_1.setText("Remove Coupon");
            applyCoupon_1.setTag("remove");

            callNetworkForTaxCalculations();

        } else {
            Toast.makeText(ShoppingCartActivity2.this, "This offer is valid for minimum price order."
                    + offerMinimumPrice, Toast.LENGTH_SHORT).show();
        }

    }*/

    private void applyPointsDiscount(String discountAmount) {
        fixed_discount_amount = discountAmount;
        double totalPrice = getTotalPrice();
        double discount = Double.parseDouble(discountAmount);


        double finalPrice = totalPrice - discount + shippingCharge;
        DecimalFormat df = new DecimalFormat("###.##");
        total.setText(String.valueOf(df.format(finalPrice)));
        discountVal.setText(String.valueOf(df.format(discount)));
        applyCoupon.setText("Remove Coupon");
        applyCoupon.setTag("remove");
        applyCoupon.setVisibility(View.VISIBLE);
        editCoupon.setVisibility(View.VISIBLE);
        applyOffer.setVisibility(View.GONE);
        redeemPoints.setVisibility(View.GONE);

        callNetworkForTaxCalculations();

    }


   /* private void onApplyCoupon1() {

        final String couponCode = editCoupon_1.getText().toString();
        if (!couponCode.isEmpty()) {
            String deviceId = Settings.Secure.getString(ShoppingCartActivity2.this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            String deviceToken = pushClientManager.getRegistrationId(ShoppingCartActivity2.this);
            String plaform = AppConstant.PLATFORM;
            String uId = userId;
            Map<String, String> params = new HashMap<>();
            params.put("device_id", deviceId);
            params.put("user_id", uId);
            params.put("device_token", deviceToken);
            params.put("platform", plaform);
            params.put("coupon_code", couponCode);
            ProgressDialogUtil.showProgressDialog(ShoppingCartActivity2.this);
            NetworkAdaper.getInstance().getNetworkServices().validateCoupon(params, new Callback<GetValidCouponResponse>() {
                @Override
                public void success(GetValidCouponResponse getValidCouponResponse, Response response) {

                    ProgressDialogUtil.hideProgressDialog();
                    if (getValidCouponResponse.getSuccess()) {
                        coupenCode = couponCode;
                        OfferData offerData = getValidCouponResponse.getData();
                        String discountPercent = offerData.getDiscount();
                        discount = offerData.getDiscount();
                        String offerMinimumPrice = offerData.getMinimumOrderAmount();
                        applyDiscount_2(discountPercent, offerMinimumPrice);
                    } else {
                        coupenCode = "";
                        Log.e("Tag", getValidCouponResponse.getMessage());
                        Toast.makeText(ShoppingCartActivity2.this, getValidCouponResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    coupenCode = "";
                    ProgressDialogUtil.hideProgressDialog();
                    DialogHandler dialogHandler = new DialogHandler(ShoppingCartActivity2.this);
                    dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), false);
                }
            });

        } else {
            Toast.makeText(ShoppingCartActivity2.this, "Coupon Code Empty", Toast.LENGTH_SHORT).show();
        }

    }*/

    private void onApplyCoupon() {

        if (loyalityStatus.equalsIgnoreCase("0")) {
            couponCode = editCoupon_1.getText().toString();
        } else if (loyalityStatus.equalsIgnoreCase("1")) {
            couponCode = editCoupon.getText().toString();
        }

        if (!couponCode.isEmpty()) {
            String deviceId = Settings.Secure.getString(ShoppingCartActivity2.this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            String deviceToken = pushClientManager.getRegistrationId(ShoppingCartActivity2.this);
            String plaform = AppConstant.PLATFORM;
            String uId = userId;
            String orders = appDb.getCartItemsListStringJson();

            Map<String, String> params = new HashMap<>();
            params.put("device_id", deviceId);
            params.put("user_id", uId);
            params.put("device_token", deviceToken);
            params.put("platform", plaform);
            params.put("coupon_code", couponCode);
            params.put("payment_method", payment_method);
            params.put("orders", orders);
            ProgressDialogUtil.showProgressDialog(ShoppingCartActivity2.this);
            /*NetworkAdaper.getInstance().getNetworkServices().validateCoupon(params, new Callback<GetValidCouponResponse>() {
                @Override
                public void success(GetValidCouponResponse getValidCouponResponse, Response response) {

                    ProgressDialogUtil.hideProgressDialog();
                    if (getValidCouponResponse.getSuccess()) {
                        coupenCode = couponCode;
                        OfferData offerData = getValidCouponResponse.getData();
                        String discountPercent = offerData.getDiscount();
                        discount = offerData.getDiscount();
                        String offerMinimumPrice = offerData.getMinimumOrderAmount();

                        applyDiscount(discountPercent, offerMinimumPrice);
//                        applyDiscountForNormalScreen(discountPercent, offerMinimumPrice);
                    } else {
                        coupenCode = "";
                        Log.e("Tag", getValidCouponResponse.getMessage());
                        Toast.makeText(ShoppingCartActivity2.this, getValidCouponResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    coupenCode = "";
                    ProgressDialogUtil.hideProgressDialog();
                    DialogHandler dialogHandler = new DialogHandler(ShoppingCartActivity2.this);
                    dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), false);
                }
            });*/


            NetworkAdaper.getInstance().getNetworkServices().validateCoupon(params, new Callback<ValidAllCouponsModel>() {
                @Override
                public void success(final ValidAllCouponsModel validAllCouponsModel, Response response) {

                    ProgressDialogUtil.hideProgressDialog();
                    if (validAllCouponsModel.getSuccess()) {

                        if(validAllCouponsModel.getDiscountAmount()!=0.00){
                            coupenCode = couponCode;
                            ValidAllCouponData data=validAllCouponsModel.getData();
                            String discountPercent = data.getDiscount();
                            fixed_discount_amount = String.valueOf(validAllCouponsModel.getDiscountAmount());
                            discount="0";
                            String offerMinimumPrice = data.getMinimumOrderAmount();

                            applyDiscount(discountPercent, offerMinimumPrice);

                        }else {
                            coupenCode = couponCode;
                            ValidAllCouponData data=validAllCouponsModel.getData();
                            String discountPercent = data.getDiscount();
                            discount = data.getDiscount();
                            fixed_discount_amount="0";
                            String offerMinimumPrice = data.getMinimumOrderAmount();

                            applyDiscount(discountPercent, offerMinimumPrice);
                        }

                    } else {
                        coupenCode = "";
                        final DialogHandler dialogHandler = new DialogHandler(ShoppingCartActivity2.this);
                        dialogHandler.setDialog("Message", validAllCouponsModel.getMessage());
                        dialogHandler.setPostiveButton("Details", true)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogHandler.dismiss();

                                        String offerName = validAllCouponsModel.getData().getName();
                                        String offerDataString = gsonHelper.getOfferDataString(validAllCouponsModel.getData());
                                        prefManager.storeSharedValue(AppConstant.OFFER_VIEW, offerDataString);
                                        Intent intentOfferView = new Intent(ShoppingCartActivity2.this, OfferViewActivity.class);
                                        intentOfferView.putExtra("offerName", offerName);
                                        startActivity(intentOfferView);
                                        finish();
                                        AnimUtil.slideFromRightAnim(ShoppingCartActivity2.this);

                                    }
                                });

                        dialogHandler.setNegativeButton("Cancel", true).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogHandler.dismiss();
                            }
                        });
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    coupenCode = "";
                    ProgressDialogUtil.hideProgressDialog();
                    DialogHandler dialogHandler = new DialogHandler(ShoppingCartActivity2.this);
                    dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), false);
                }
            });


        } else {
            Toast.makeText(ShoppingCartActivity2.this, "Coupon Code Empty", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    private void callNetworkForTaxCalculations() {

        ProgressDialogUtil.showProgressDialog(ShoppingCartActivity2.this);
        PrefManager prefManager = new PrefManager(this);
//        taxRate=prefManager.getSharedValue(AppConstant.tax_rate); //getting tax rate from prefrencees

        String shippingcharge = shipping_charges.getText().toString();

        String order = appDb.getCartItemsListStringJson();

        Map<String, String> param = new HashMap<String, String>();
        param.put("shipping", shippingcharge);
        param.put("discount", discount);
        param.put("tax", taxRate);
        param.put("fixed_discount_amount", fixed_discount_amount);
        param.put("order_detail", order);

        NetworkAdaper.getInstance().getNetworkServices().getTaxCalculations(param, new Callback<TaxCalculationModel>() {

            @Override
            public void success(TaxCalculationModel taxCalculationModel, Response response) {
                ProgressDialogUtil.hideProgressDialog();
                if (taxCalculationModel.getSuccess() != null ? taxCalculationModel.getSuccess() : false) {
//                    showAlertDialog(ShoppingCartActivity2.this, "Thank you!", "Thank you for placing the order. We will confirm your order soon.");

                    double totalPrice = Double.parseDouble(taxCalculationModel.getData().getTotal());
                    total.setText("" + String.format("%.2f", totalPrice));

                    if (taxCalculationModel.getData().getTaxDetail() != null && taxCalculationModel.getData().getTaxDetail().size() != 0) {
                        taxDetailsJson = getTaxDetail(taxCalculationModel.getData().getTaxDetail());
                    } else {
                        taxDetailsJson = "";
                    }

                    if (taxCalculationModel.getData().getFixedTaxDetail() != null && taxCalculationModel.getData().getFixedTaxDetail().size() != 0) {
                        taxFixedTaxJson=getFixedTaxDetailJson(taxCalculationModel.getData().getFixedTaxDetail());
                    }
                    else {
                        taxFixedTaxJson="";
                    }

                    if (taxCalculationModel.getData().getTaxLabel() != null && taxCalculationModel.getData().getTaxLabel().size() != 0) {
                        taxLabelJson=getTaxDetailJson(taxCalculationModel.getData().getTaxLabel());
                    }
                    else {
                        taxLabelJson="";
                    }

                    TaxDataModel model = taxCalculationModel.getData();

                    if(!model.getTax().isEmpty()){
                        tax=model.getTax();
                    }

                    int i = listViewCart.getFooterViewsCount();

                    if (i > 0) {
                        for (int j = 0; j < i; j++) {
                            listViewCart.removeFooterView(footerView);
                        }
                    }

                    initializeFooterView(model);


                } else {

                    showAlertDialogOnTaxFailure(ShoppingCartActivity2.this, "Message", "" + taxCalculationModel.getMessage());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                showAlertDialogOnTaxFailure(ShoppingCartActivity2.this, "Message", getResources().getString(R.string.error_code_message));
//                DialogHandler dialogHandler = new DialogHandler(ShoppingCartActivity2.this);
//                dialogHandler.setdialogForFinish("Error", getResources().getString(R.string.error_code_message), false);
            }
        });

    }


    public void showAlertDialogwithPickUp(Context context, String title,
                                          String message) {
        final DialogHandler dialogHandler = new DialogHandler(ShoppingCartActivity2.this);
        dialogHandler.setDialog(title, message);
        dialogHandler.setPostiveButton("OK", true).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHandler.dismiss();
                appDb.deleteCartElement();
                Intent intent_home = new Intent(ShoppingCartActivity2.this, MainActivity.class);
                intent_home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_home);
                finish();
                AnimUtil.slideFromLeftAnim(ShoppingCartActivity2.this);
            }
        });

        dialogHandler.setNegativeButton("Guide Me", true).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHandler.dismiss();
                appDb.deleteCartElement();
                Intent intent_home = new Intent(ShoppingCartActivity2.this, DeliveryPickupActivity.class);
                intent_home.putExtra(AppConstant.FROM, "shopping_cart2");
                intent_home.putExtra("title", "Guide Me");
                startActivity(intent_home);
                finish();
                AnimUtil.slideFromLeftAnim(ShoppingCartActivity2.this);
            }
        });
    }

    public void showAlertDialog(Context context, String title,
                                String message) {
        final DialogHandler dialogHandler = new DialogHandler(ShoppingCartActivity2.this);
        dialogHandler.setDialog(title, message);
        dialogHandler.setPostiveButton("OK", true).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHandler.dismiss();
                appDb.deleteCartElement();
                Intent intent_home = new Intent(ShoppingCartActivity2.this, MainActivity.class);
                intent_home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_home);
                finish();
                AnimUtil.slideFromLeftAnim(ShoppingCartActivity2.this);
//                onBackPressed();
            }
        });

    }


    public void showAlertDialogOnTaxFailure(Context context, String title,
                                            String message) {
        final DialogHandler dialogHandler = new DialogHandler(ShoppingCartActivity2.this);
        dialogHandler.setDialog(title, message);
        dialogHandler.setPostiveButton("OK", true).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHandler.dismiss();
                appDb.deleteCartElement();
                Intent intent_home = new Intent(ShoppingCartActivity2.this, MainActivity.class);
                intent_home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent_home);
                finish();
                AnimUtil.slideFromLeftAnim(ShoppingCartActivity2.this);
            }
        });

    }


    private String getTaxDetail(List<TaxDetails> detailsList) {
        String jsonString = "";
        List<TaxDetails> list = detailsList;

        Gson gson = new Gson();
        Type type = new TypeToken<List<TaxDetails>>() {
        }.getType();
        jsonString = gson.toJson(list, type);
        Log.i("TAG", jsonString);

        if (list==null){
            return "";
        }

        return jsonString;
    }

    private String getTaxDetailJson(List<TaxDetail> list) {
        String jsonString = "";
        List<TaxDetail> taxDetails = list;
        Gson gson = new Gson();
        Type type = new TypeToken<List<TaxDetail>>() {
        }.getType();
        jsonString = gson.toJson(taxDetails, type);
        Log.i("TAG", jsonString);

        if (taxDetails==null){
            return "";
        }

        return jsonString;
    }

    private String getFixedTaxDetailJson(List<FixedTaxDetail> list) {
        String jsonString = "";
        List<FixedTaxDetail> fixedTaxDetails = list;

        Gson gson = new Gson();
        Type type = new TypeToken<List<FixedTaxDetail>>() {
        }.getType();
        jsonString = gson.toJson(fixedTaxDetails, type);
        Log.i("TAG", jsonString);

        if (fixedTaxDetails==null){
            return "";
        }

        return jsonString;
    }

    public String unescapeJavaString(String st) {

        StringBuilder sb = new StringBuilder(st.length());

        for (int i = 0; i < st.length(); i++) {
            char ch = st.charAt(i);
            if (ch == '\\') {
                char nextChar = (i == st.length() - 1) ? '\\' : st
                        .charAt(i + 1);
// Octal escape?
                if (nextChar >= '0' && nextChar <= '7') {
                    String code = "" + nextChar;
                    i++;
                    if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                            && st.charAt(i + 1) <= '7') {
                        code += st.charAt(i + 1);
                        i++;
                        if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                                && st.charAt(i + 1) <= '7') {
                            code += st.charAt(i + 1);
                            i++;
                        }
                    }
                    sb.append((char) Integer.parseInt(code, 8));
                    continue;
                }
                switch (nextChar) {
                    case '\\':
                        ch = '\\';
                        break;
                    case 'b':
                        ch = '\b';
                        break;
                    case 'f':
                        ch = '\f';
                        break;
                    case 'n':
                        ch = '\n';
                        break;
                    case 'r':
                        ch = '\r';
                        break;
                    case 't':
                        ch = '\t';
                        break;
                    case '\"':
                        ch = '\"';
                        break;
                    case '\'':
                        ch = '\'';
                        break;
// Hex Unicode: u????
                    case 'u':
                        if (i >= st.length() - 5) {
                            ch = 'u';
                            break;
                        }
                        int code = Integer.parseInt(
                                "" + st.charAt(i + 2) + st.charAt(i + 3)
                                        + st.charAt(i + 4) + st.charAt(i + 5), 16);
                        sb.append(Character.toChars(code));
                        i += 5;
                        continue;
                }
                i++;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    public void prepareFooter() {
        LayoutInflater inflater = getLayoutInflater();
        footerView = (ViewGroup) inflater.inflate(R.layout.list_footer_final_screen, null);
        items_price = (TextView) footerView.findViewById(com.signity.bonbon.R.id.items_price);
        shipping_charges_text = (TextView) footerView.findViewById(R.id.shipping_charges_text);
        discountLblText = (TextView) footerView.findViewById(R.id.discountLblText);
        shipping_charges = (TextView) footerView.findViewById(com.signity.bonbon.R.id.shipping_charges);
        discountVal = (TextView) footerView.findViewById(R.id.discountVal);
        shipping_layout = (RelativeLayout) footerView.findViewById(R.id.shipping_layout);
        discount_layout = (RelativeLayout) footerView.findViewById(R.id.discount_layout);


        rs1 = (TextView) footerView.findViewById(R.id.rs1);
        rs2 = (TextView) footerView.findViewById(R.id.rs2);
        rs3 = (TextView) footerView.findViewById(R.id.rs3);
//        rs5 = (TextView) footerView.findViewById(R.id.rs5);
        rs4 = (TextView) findViewById(R.id.rs4);


        String currency = prefManager.getSharedValue(AppConstant.CURRENCY);


        if (currency.contains("\\")) {
            rs1.setText(unescapeJavaString(currency));
            rs2.setText(unescapeJavaString(currency));
            rs3.setText("- " + unescapeJavaString(currency));
            rs4.setText(unescapeJavaString(currency));
//            rs5.setText(unescapeJavaString(currency));
        } else {
            rs1.setText(currency);
            rs2.setText(currency);
            rs3.setText("- " + currency);
            rs4.setText(currency);
//            rs5.setText(currency);
        }

        layout = (LinearLayout) footerView.findViewById(R.id.linearTaxLayout);
        linearFixedTaxLayout = (LinearLayout) footerView.findViewById(R.id.linearFixedTaxLayout);
        linearFixedTaxLayoutDisable = (LinearLayout) footerView.findViewById(R.id.linearFixedTaxLayoutDisable);

        listViewCart.addFooterView(footerView, null, false);
    }

    class ProductListAdapter extends BaseAdapter {
        Activity context;
        LayoutInflater layoutInflater;
        MyAdapter madapter;
        List<Product> listProduct;
        ViewHolder holder;

        public ProductListAdapter(Activity context, List<Product> listProduct) {
            this.listProduct = listProduct;
            this.context = context;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return listProduct.size();
        }

        @Override
        public Object getItem(int position) {
            return listProduct.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.view_shoppingcart_item_child, null);
                holder = new ViewHolder();
                holder.rel_mrp_offer_price = (RelativeLayout) convertView.findViewById(R.id.rel_mrp_offer_price);
                holder.items_mrp_price = (TextView) convertView.findViewById(R.id.items_mrp_price);
                holder.parent = (RelativeLayout) convertView.findViewById(com.signity.bonbon.R.id.parent);
                holder.items_name = (TextView) convertView.findViewById(com.signity.bonbon.R.id.items_name);
                holder.items_name.setTypeface(typeFaceRobotoBold);
                holder.items_price = (TextView) convertView.findViewById(com.signity.bonbon.R.id.items_price);
                holder.items_price.setTypeface(typeFaceRobotoRegular);
                holder.btnVarient = (Button) convertView.findViewById(R.id.btnVarient);
                holder.number_text = (TextView) convertView.findViewById(com.signity.bonbon.R.id.number_text);
                holder.number_text.setTypeface(typeFaceRobotoRegular);
                holder.rupee = (TextView) convertView.findViewById(R.id.rupee);
                holder.rupee2 = (TextView) convertView.findViewById(R.id.rupee2);
                holder.totalValue = (TextView) convertView.findViewById(R.id.totalValue);
                holder.rupeeTxt = (TextView) convertView.findViewById(R.id.rupeeTxt);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final Product product = listProduct.get(position);
            final SelectedVariant selectedVariant = product.getSelectedVariant();


            String productPrice = "";
            String mrpPrice = "";
            String txtQuant = "";
            String txtQuantCount = "";

            if (selectedVariant != null && !selectedVariant.getVariantId().equals("0")) {
                txtQuant = String.valueOf(selectedVariant.getWeight() + " " + selectedVariant.getUnitType()).trim();
                productPrice = selectedVariant.getPrice();
                txtQuantCount = selectedVariant.getQuantity();
                mrpPrice = selectedVariant.getMrpPrice();
            } else {
                Variant variant = product.getVariants().get(0);
                selectedVariant.setVariantId(variant.getId());
                selectedVariant.setSku(variant.getSku());
                selectedVariant.setWeight(variant.getWeight());
                selectedVariant.setMrpPrice(variant.getMrpPrice());
                selectedVariant.setPrice(variant.getPrice());
                selectedVariant.setDiscount(variant.getDiscount());
                selectedVariant.setUnitType(variant.getUnitType());
                selectedVariant.setQuantity(appDb.getCartQuantity(variant.getId()));
                txtQuant = String.valueOf(selectedVariant.getWeight() + " " + selectedVariant.getUnitType()).trim();
                productPrice = selectedVariant.getPrice();
                txtQuantCount = selectedVariant.getQuantity();
                mrpPrice = selectedVariant.getMrpPrice();
            }


            if (prefManager.getProjectType().equals(AppConstant.APP_TYPE_GROCERY)) {
                holder.btnVarient.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                holder.btnVarient.setText(txtQuant);
                holder.btnVarient.setVisibility(View.VISIBLE);
            } else if(prefManager.getProjectType().equals(AppConstant.APP_TYPE_RESTAURANT)){
                holder.btnVarient.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                holder.btnVarient.setText(txtQuant);
                holder.btnVarient.setVisibility(View.VISIBLE);
            }
            else {
                holder.btnVarient.setVisibility(View.GONE);
            }

            String currency = prefManager.getSharedValue(AppConstant.CURRENCY);


            if (currency.contains("\\")) {
                holder.rupee.setText(unescapeJavaString(currency));
                holder.rupee2.setText(unescapeJavaString(currency));
                holder.rupeeTxt.setText(unescapeJavaString(currency));
            } else {
                holder.rupee.setText(currency);
                holder.rupee2.setText(currency);
                holder.rupeeTxt.setText(currency);
            }


            holder.items_name.setText(Html.fromHtml(product.getTitle()));
            holder.items_price.setText(productPrice);
            holder.number_text.setText(txtQuantCount);

            if (productPrice.equalsIgnoreCase(mrpPrice)) {
                holder.rupee.setVisibility(View.VISIBLE);
                holder.rel_mrp_offer_price.setVisibility(View.GONE);
            } else {
                holder.rupee.setVisibility(View.GONE);
                holder.rel_mrp_offer_price.setVisibility(View.VISIBLE);
            }

            holder.items_mrp_price.setText(mrpPrice);
            Double totalPrice = Double.parseDouble(txtQuantCount) * Double.parseDouble(productPrice);
            holder.totalValue.setText("" + String.format("%.2f", totalPrice));

            return convertView;
        }

        class MyAdapter extends ArrayAdapter<Variant> {
            LayoutInflater l;
            List<Variant> listVariant;

            public MyAdapter(Activity ctx, int txtViewResourceId, List<Variant> listVariant) {
                super(ctx, txtViewResourceId, listVariant);
                l = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                this.listVariant = listVariant;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Variant variant = listVariant.get(position);
                convertView = l.inflate(com.signity.bonbon.R.layout.custom_spinner, parent, false);
                String weight = variant.getWeight();
                String prices = variant.getPrice();
                String unit = variant.getUnitType();
                TextView weights = (TextView) convertView.findViewById(com.signity.bonbon.R.id.weights);
                weights.setText(weight + " " + unit);
                TextView price = (TextView) convertView.findViewById(com.signity.bonbon.R.id.price);
                price.setText(prices);

                return convertView;
            }

        }

        class ViewHolder {
            public RelativeLayout rel_mrp_offer_price;
            Button btnVarient;
            public TextView items_mrp_price, totalValue;
            RelativeLayout parent;
            TextView items_name, items_price, number_text, rupee, rupee2, rupeeTxt;
        }
    }

    class ListOfferAdapter extends BaseAdapter {
        Activity context;
        LayoutInflater l;
        List<OfferData> listOfferData;
        ViewHolder holder;

        public ListOfferAdapter(Activity context, List<OfferData> listOfferData) {
            this.context = context;
            l = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.listOfferData = listOfferData;

        }

        @Override
        public int getCount() {
            return listOfferData.size();
        }

        @Override
        public Object getItem(int position) {
            return listOfferData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = l.inflate(R.layout.offers_screen_child, null);
                holder = new ViewHolder();
                holder.discountValue = (TextView) (convertView.findViewById(R.id.discountValue));
                holder.minValue = (TextView) (convertView.findViewById(R.id.minValue));
                holder.applyBtn = (Button) (convertView.findViewById(R.id.applyBtn));
                holder.needTxt = (TextView) (convertView.findViewById(R.id.needTxt));
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final OfferData data = listOfferData.get(position);
//            holder.discountValue.setText("" + data.getDiscount() + "% Off");
            holder.discountValue.setText("" + data.getName());
            holder.discountValue.setSelected(true);
            if(data.getMinimumOrderAmount().equalsIgnoreCase("0")){
                holder.minValue.setText(""+data.getName());
            }else {
                holder.minValue.setText("Min Order " + data.getMinimumOrderAmount());
            }
            double totalPrice = getTotalPrice();
            double minimumCharges = Double.parseDouble(data.getMinimumOrderAmount());
            if (minimumCharges > totalPrice) {
                holder.applyBtn.setVisibility(View.GONE);
                holder.needTxt.setVisibility(View.VISIBLE);

                holder.needTxt.setText("You have " + String.format("%.2f", (minimumCharges - totalPrice)) + " amount short for this offer.");
            } else {
                holder.applyBtn.setVisibility(View.VISIBLE);
                holder.needTxt.setVisibility(View.GONE);
            }

            holder.applyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editCoupon.setText("");
                    editCoupon.setText(data.getCouponCode());
                    editCoupon_1.setText("");
                    editCoupon_1.setText(data.getCouponCode());
                    onApplyCoupon();
                    dialog.dismiss();
                    dialog = null;
                }
            });

            return convertView;
        }


        class ViewHolder {
            TextView discountValue, minValue, needTxt;
            Button applyBtn;
        }
    }

    class Adapter extends BaseAdapter {

        Activity context;
        LayoutInflater l;
        List<LoyalityDataModel> pointsList;
        ViewHolder holder;


        public Adapter(Activity context, List<LoyalityDataModel> pointsList) {
            this.pointsList = pointsList;
            this.context = context;
            l = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return pointsList.size();
        }

        @Override
        public Object getItem(int position) {
            return pointsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            holder = null;
            if (convertView == null) {
                convertView = l.inflate(R.layout.loyality_points_child, null);
                holder = new ViewHolder();
                holder.rupees = (TextView) convertView.findViewById(R.id.rupees);
                holder.points = (TextView) convertView.findViewById(R.id.points);
                holder.redeemNow = (TextView) convertView.findViewById(R.id.redeemNow);
                holder.needTxt = (TextView) convertView.findViewById(R.id.needTxt);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            String currency = prefManager.getSharedValue(AppConstant.CURRENCY);


            if (currency.contains("\\")) {
                holder.rupees.setText(unescapeJavaString(currency) + "" + pointsList.get(position).getAmount() + " OFF");
            } else {
                holder.rupees.setText("" + currency + "" + pointsList.get(position).getAmount() + " OFF");
            }

            holder.points.setText("" + pointsList.get(position).getPoints() + " Points");
            final double points = Double.parseDouble(pointsList.get(position).getPoints());

            if (points >= loyalityPoints) {
                holder.redeemNow.setVisibility(View.GONE);
                holder.needTxt.setVisibility(View.VISIBLE);
                holder.needTxt.setText("You need " + String.format("%.2f", (points - loyalityPoints)) + " more points.");

            } else {
                holder.redeemNow.setVisibility(View.VISIBLE);
                holder.needTxt.setVisibility(View.GONE);
            }


            holder.redeemNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    editCoupon.setText("");
                    editCoupon.setText(pointsList.get(position).getCouponCode());
//                    onApplyCoupon();
                    coupenCode=pointsList.get(position).getCouponCode();
                    applyPointsDiscount(pointsList.get(position).getAmount());
                    redeemDialog.dismiss();
                    redeemDialog = null;
                }
            });


            return convertView;
        }

        class ViewHolder {
            TextView rupees, points, redeemNow, needTxt;
        }

    }

}
