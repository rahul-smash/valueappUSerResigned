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

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.FontUtil;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.gcm.GCMClientManager;
import com.signity.bonbon.model.GetOfferResponse;
import com.signity.bonbon.model.GetValidCouponResponse;
import com.signity.bonbon.model.LoyalityDataModel;
import com.signity.bonbon.model.LoyalityModel;
import com.signity.bonbon.model.OfferData;
import com.signity.bonbon.model.Product;
import com.signity.bonbon.model.ResponseData;
import com.signity.bonbon.model.SelectedVariant;
import com.signity.bonbon.model.Store;
import com.signity.bonbon.model.Variant;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.home.MainActivity;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ShoppingCartActivity2 extends Activity implements View.OnClickListener {
    public Typeface typeFaceRobotoRegular, typeFaceRobotoBold;
    ListView listViewCart;
    TextView items_price, discountVal, total, title,customerPts,note;
    Button placeorder;
    String userId;
    String addressId;
    String shippingChargeText, minmimumChargesText, user_address;
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
    Dialog dialog,redeemDialog;
    private GCMClientManager pushClientManager;
    private Button backButton, btnSearch;
    private TextView shipping_charges;
    private AppDatabase appDb;
    private Button applyCoupon, applyOffer, redeemPoints;
    private EditText editCoupon;
    private EditText edtBar;
    private String isForPickUpStatus = "no";
    private String coupenCode = "";

    ListView list_view;
    Adapter pointAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.signity.bonbon.R.layout.shopping_cart_activity2);
        ShoppingCartActivity2.this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        appDb = DbAdapter.getInstance().getDb();
        prefManager = new PrefManager(ShoppingCartActivity2.this);
        userId = getIntent().getStringExtra("userId");
        addressId = getIntent().getStringExtra("addressId");
        shippingChargeText = getIntent().getStringExtra("shiping_charges");
        minmimumChargesText = getIntent().getStringExtra("minimum_charges");
        user_address = getIntent().getStringExtra("user_address");
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
        homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(this);

        listViewCart = (ListView) findViewById(com.signity.bonbon.R.id.items_list);
        items_price = (TextView) findViewById(com.signity.bonbon.R.id.items_price);
        discountVal = (TextView) findViewById(R.id.discountVal);
        edtBar = (EditText) findViewById(R.id.edtBar);
        items_price.setTypeface(typeFaceRobotoRegular);
        total = (TextView) findViewById(com.signity.bonbon.R.id.total);
        total.setTypeface(typeFaceRobotoRegular);
        title = (TextView) findViewById(com.signity.bonbon.R.id.textTitle);
        shipping_charges = (TextView) findViewById(com.signity.bonbon.R.id.shipping_charges);
        title.setText("Confirm Order");
        title.setTypeface(typeFaceRobotoRegular);
        placeorder = (Button) findViewById(com.signity.bonbon.R.id.placeorder);
        backButton = (Button) findViewById(com.signity.bonbon.R.id.backButton);
        backButton.setOnClickListener(this);
        placeorder.setOnClickListener(this);
        editCoupon = (EditText) findViewById(R.id.editCoupon);
        applyCoupon = (Button) findViewById(R.id.applyCoupen);
        applyOffer = (Button) findViewById(R.id.applyOffer);
        redeemPoints = (Button) findViewById(R.id.redeemPoints);
        applyCoupon.setTag("apply");
        applyCoupon.setOnClickListener(this);
        applyOffer.setOnClickListener(this);
        redeemPoints.setOnClickListener(this);

        listProduct = appDb.getCartListProduct();

        if (listProduct != null && listProduct.size() != 0) {
            adapter = new ProductListAdapter(ShoppingCartActivity2.this, listProduct);
            listViewCart.setAdapter(adapter);
            listViewCart.setVisibility(View.VISIBLE);
            updateCartPrice();
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

    private void updateShippingCharges() {
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
        String note = edtBar.getText().toString();
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
        param.put("note", note);
        param.put("orders", order);
        param.put("coupon_code", coupenCode);
        param.put("checkout", orderPrice);
        param.put("discount", discount);
        param.put("total", amount);
        param.put("user_address", user_address);
//        param.put("coupon_code", coupon_code);
        Log.e("params", param.toString());
        NetworkAdaper.getInstance().getNetworkServices().placeOrder(param, new Callback<ResponseData>() {
            @Override
            public void success(ResponseData responseData, Response response) {
                ProgressDialogUtil.hideProgressDialog();
                if (responseData.getSuccess() != null ? responseData.getSuccess() : false) {
                    showAlertDialog(ShoppingCartActivity2.this, "Thank you!", "Thank you for placing the order. We will confirm your order soon.");
                } else {
                    DialogHandler dialogHandler = new DialogHandler(ShoppingCartActivity2.this);
                    dialogHandler.setdialogForFinish("Failure", "" + responseData.getMessage(), false);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                Toast.makeText(ShoppingCartActivity2.this, "Server not responding.", Toast.LENGTH_SHORT).show();
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

        Map<String, String> param = new HashMap<String, String>();
        param.put("device_id", deviceId);
        param.put("user_id", id);
        param.put("device_token", deviceToken);
        param.put("platform", AppConstant.PLATFORM);
        param.put("payment_method", "cod");
        param.put("user_address_id", addressId);
        param.put("shipping_charges", shippingcharge);
        param.put("note", note);
        param.put("orders", order);
        param.put("checkout", orderPrice);
        param.put("coupon_code", coupenCode);
        param.put("discount", discount);
        param.put("total", amount);
        param.put("user_address", user_address);
        Log.e("params", param.toString());
        NetworkAdaper.getInstance().getNetworkServices().placeOrder(param, new Callback<ResponseData>() {
            @Override
            public void success(ResponseData responseData, Response response) {
                ProgressDialogUtil.hideProgressDialog();
                if (responseData.getSuccess() != null ? responseData.getSuccess() : false) {
                    showAlertDialog(ShoppingCartActivity2.this, "Thank you!", "Thank you for placing the order. We will confirm your order soon.");
                } else {
                    DialogHandler dialogHandler = new DialogHandler(ShoppingCartActivity2.this);
                    dialogHandler.setdialogForFinish("Failure", "" + responseData.getMessage(), false);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                Toast.makeText(ShoppingCartActivity2.this, "Server not responding.", Toast.LENGTH_SHORT).show();
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
                if (appDb.getCartSize() != 0) {
                    if (isForPickUpStatus.equalsIgnoreCase("yes")) {
                        callNetworkServiceForPlaceOrderForPickup(userId, addressId);
                    } else {
                        callNetworkServiceForPlaceOrder(userId, addressId);
                    }
                } else {
//                    Toast.makeText(ShoppingCartActivity2.this, "Empty Cart", Toast.LENGTH_SHORT).show();
                    new DialogHandler(ShoppingCartActivity2.this).setdialogForFinish("Message", "You have a empty cart", false);
                }
                break;

            case R.id.applyCoupen:
                if (applyCoupon.getTag().equals("apply")) {
                    onApplyCoupon();
                } else if (applyCoupon.getTag().equals("remove")) {
                    onRemoveCoupon();
                }
                break;

            case R.id.applyOffer:
                if (dialog != null) {
                    dialog = null;
                }
                callNetworkForOffers();
                break;

            case R.id.redeemPoints:
                callNetworkForLoyalityPoints();
                break;

            case R.id.homeBtn:
                Intent intent = new Intent(ShoppingCartActivity2.this, MainActivity.class);
                startActivity(intent);
                break;
        }
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

                        dialog = new Dialog(ShoppingCartActivity2.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog.setContentView(R.layout.loyality_points_layout);

                        RelativeLayout header=(RelativeLayout)findViewById(R.id.header);
                        list_view = (ListView) dialog.findViewById(R.id.loyalityList);
                        customerPts = (TextView) dialog.findViewById(R.id.customerPts);
                        note=(TextView)dialog.findViewById(R.id.note);

                        if (loyalityModel.getLoyalityPoints().isEmpty()) {
                            customerPts.setText("You have NIL points.");
                        } else {
                            customerPts.setText("You have " + loyalityModel.getLoyalityPoints() + " points.");
                        }


                        if (loyalityModel.getData() != null && loyalityModel.getData().size() != 0) {
                            pointAdapter = new Adapter(ShoppingCartActivity2.this, loyalityModel.getData());
                            list_view.setAdapter(pointAdapter);
                        }

                        dialog.setCanceledOnTouchOutside(true);
                        dialog.show();

                    } else {
                        ProgressDialogUtil.hideProgressDialog();
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
//                showAlertDialog(getActivity(), "Error", error.getMessage());
            }
        });

    }

    private void onRemoveCoupon() {

        if (!discountVal.getText().toString().isEmpty() && !discountVal.getText().toString().equalsIgnoreCase("0")) {
            coupenCode = "";
            double discount = Double.parseDouble(discountVal.getText().toString());
            double totalval = Double.parseDouble(total.getText().toString());
            double finalVal = totalval + discount;
            DecimalFormat df = new DecimalFormat("###.##");
            total.setText(String.valueOf(df.format(finalVal)));
            discountVal.setText("0");
            applyCoupon.setText("Apply Coupon");
            applyCoupon.setTag("apply");
            editCoupon.setText("");
        }
    }


    private void applyDiscount(String discountPercent, String strOfferMinimumPrice) {
        double totalPrice = getTotalPrice();
        double discount = ((totalPrice * Double.parseDouble(discountPercent) / 100));
        double offerMinimumPrice = Double.parseDouble(strOfferMinimumPrice);

        if (offerMinimumPrice < totalPrice) {
            double finalPrice = totalPrice - discount + shippingCharge;
            DecimalFormat df = new DecimalFormat("###.##");
            total.setText(String.valueOf(df.format(finalPrice)));
            discountVal.setText(String.valueOf(discount));
            applyCoupon.setText("Remove Coupon");
            applyCoupon.setTag("remove");

        } else {
            Toast.makeText(ShoppingCartActivity2.this, "This offer is valid for minimum price order: "
                    + offerMinimumPrice, Toast.LENGTH_SHORT).show();
        }

    }

    private void onApplyCoupon() {

        final String couponCode = editCoupon.getText().toString();
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
                        String offerMinimumPrice = offerData.getMinimumOrderAmount();
                        applyDiscount(discountPercent, offerMinimumPrice);
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
                }
            });

        } else {
            Toast.makeText(ShoppingCartActivity2.this, "Coupon Code Empty", Toast.LENGTH_SHORT).show();
        }

    }

    public void showAlertDialog(Context context, String title,
                                String message) {
        final DialogHandler dialogHandler = new DialogHandler(ShoppingCartActivity2.this);
        dialogHandler.setDialog(title, message);
        dialogHandler.setPostiveButton("Ok", true).setOnClickListener(new View.OnClickListener() {
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
                holder.number_text = (TextView) convertView.findViewById(com.signity.bonbon.R.id.number_text);
                holder.number_text.setTypeface(typeFaceRobotoRegular);
                holder.rupee = (TextView) convertView.findViewById(com.signity.bonbon.R.id.rupee);
                holder.rupee.setTypeface(typeFaceRobotoRegular);
                holder.totalValue = (TextView) convertView.findViewById(R.id.totalValue);
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
                txtQuant = selectedVariant.getWeight();
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
                txtQuant = selectedVariant.getWeight();
                productPrice = selectedVariant.getPrice();
                txtQuantCount = selectedVariant.getQuantity();
                mrpPrice = selectedVariant.getMrpPrice();
            }

            holder.items_name.setText(product.getTitle());
            holder.items_price.setText(productPrice);
            holder.number_text.setText(txtQuantCount);

            if (productPrice.equalsIgnoreCase(mrpPrice)) {
                holder.rel_mrp_offer_price.setVisibility(View.GONE);
            } else {
                holder.rel_mrp_offer_price.setVisibility(View.VISIBLE);
            }
            holder.items_mrp_price.setText(mrpPrice);
            Double totalPrice = Double.parseDouble(txtQuantCount) * Double.parseDouble(productPrice);

            holder.totalValue.setText(String.valueOf(totalPrice));

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
            public TextView items_mrp_price, totalValue;
            RelativeLayout parent;
            TextView items_name, items_price, number_text, rupee;
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
            holder.discountValue.setText("" + data.getDiscount() + "% Off");
            holder.minValue.setText("Min Order " + data.getMinimumOrderAmount());

            double totalPrice = getTotalPrice();
            double minimumCharges = Double.parseDouble(data.getMinimumOrderAmount());
            if (minimumCharges > totalPrice) {
                holder.applyBtn.setVisibility(View.GONE);
                holder.needTxt.setVisibility(View.VISIBLE);
                holder.needTxt.setText("You have " + (minimumCharges - totalPrice) + " amount short for this offer.");
            } else {
                holder.applyBtn.setVisibility(View.VISIBLE);
                holder.needTxt.setVisibility(View.GONE);
            }

            holder.applyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editCoupon.setText("");
                    editCoupon.setText(data.getCouponCode());
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

            holder.rupees.setText("\u20B9"+pointsList.get(position).getAmount()+" OFF");
            holder.points.setText("" + pointsList.get(position).getPoints() + " Points");

           /* if(pointsList.get(position).getRedeemStatus()==1){
                holder.redeemNow.setVisibility(View.VISIBLE);
                holder.needTxt.setVisibility(View.GONE);
            }
            else {
                holder.redeemNow.setVisibility(View.GONE);
                holder.needTxt.setVisibility(View.VISIBLE);
                holder.needTxt.setText("You need "+(Integer.parseInt(pointsList.get(position).getPoints())-loyalityPts)+" more points.");
            }*/


            holder.redeemNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*point_id=pointsList.get(position).getId();
                    coupon_amount = pointsList.get(position).getAmount();
                    coupon_points=pointsList.get(position).getPoints();

                    callNetworkForRedeemMyPoints(pointsList.get(position).getId());*/
                }
            });



            return convertView;
        }

        class ViewHolder {
            TextView rupees,points,redeemNow,needTxt;
        }

    }


}
