package com.signity.bonbon.ui.shopcart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.signity.bonbon.model.GetValidCouponResponse;
import com.signity.bonbon.model.OfferData;
import com.signity.bonbon.model.Product;
import com.signity.bonbon.model.ResponseData;
import com.signity.bonbon.model.SelectedVariant;
import com.signity.bonbon.model.Variant;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.home.MainActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ShoppingCartActivity2 extends Activity implements View.OnClickListener {
    ListView listViewCart;
    TextView items_price, discountVal, total, title;
    Button placeorder;
    private GCMClientManager pushClientManager;

    public Typeface typeFaceRobotoRegular, typeFaceRobotoBold;
    private Button backButton, btnSearch;
    private TextView shipping_charges;

    String userId;
    String addressId;
    String shippingChargeText, minmimumChargesText, user_address;
    private AppDatabase appDb;
    ProductListAdapter adapter;
    List<Product> listProduct;
    double shippingCharge = 0.0;
    double minimumCharges = 0.0;
    private Button applyCoupon;
    private EditText editCoupon;
    private EditText edtBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.signity.bonbon.R.layout.shopping_cart_activity2);
        ShoppingCartActivity2.this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        appDb = DbAdapter.getInstance().getDb();
        userId = getIntent().getStringExtra("userId");
        addressId = getIntent().getStringExtra("addressId");
        shippingChargeText = getIntent().getStringExtra("shiping_charges");
        minmimumChargesText = getIntent().getStringExtra("minimum_charges");
        user_address = getIntent().getStringExtra("user_address");
        if (shippingChargeText != null && !shippingChargeText.isEmpty()) {
            shippingCharge = Double.parseDouble(shippingChargeText);
        }
        if (minmimumChargesText != null && !minmimumChargesText.isEmpty()) {
            minimumCharges = Double.parseDouble(minmimumChargesText);
        }
        pushClientManager = new GCMClientManager(this, AppConstant.PROJECT_NUMBER);
        typeFaceRobotoRegular = FontUtil.getTypeface(this, FontUtil.FONT_ROBOTO_REGULAR);
        typeFaceRobotoBold = FontUtil.getTypeface(this, FontUtil.FONT_ROBOTO_BOLD);
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
        applyCoupon.setTag("apply");
        applyCoupon.setOnClickListener(this);


        listProduct = appDb.getCartListProduct();

        if (listProduct != null && listProduct.size() != 0) {
            adapter = new ProductListAdapter(ShoppingCartActivity2.this, listProduct);
            listViewCart.setAdapter(adapter);
            listViewCart.setVisibility(View.VISIBLE);
            updateCartPrice();
        }
    }

    private void updateShippingCharges() {
        double totalPrice = getTotalPrice();

        if (shippingCharge != 0.0) {
            if (minimumCharges != 0.0) {
                if (minimumCharges > totalPrice) {
                    shipping_charges.setText(String.valueOf(shippingCharge));
                    total.setText(String.valueOf(totalPrice + shippingCharge));
                } else {
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
        return Double.valueOf(totalCartValue);
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
        param.put("checkout", orderPrice);
        param.put("discount", discount);
        param.put("total", amount);
        param.put("user_address", user_address);
        Log.e("params", param.toString());
        NetworkAdaper.getInstance().getNetworkServices().placeOrder(param, new Callback<ResponseData>() {
            @Override
            public void success(ResponseData responseData, Response response) {
                ProgressDialogUtil.hideProgressDialog();
                if (responseData.getSuccess()) {
                    showAlertDialog(ShoppingCartActivity2.this, "Thank you!", "Thank you for placing the order. We will confirm your order soon.");
                } else {
                    Toast.makeText(ShoppingCartActivity2.this, responseData.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                Log.e("Error", error.getMessage());
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
                    callNetworkServiceForPlaceOrder(userId, addressId);
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
        }
    }

    private void onRemoveCoupon() {

        if (!discountVal.getText().toString().isEmpty() && !discountVal.getText().toString().equalsIgnoreCase("0")) {
            double discount = Double.parseDouble(discountVal.getText().toString());
            double totalval = Double.parseDouble(total.getText().toString());
            double finalVal = totalval + discount;
            total.setText(String.valueOf(finalVal));
            discountVal.setText("0");
            applyCoupon.setText("Apply Coupon");
            applyCoupon.setTag("apply");
        }


    }


    private void applyDiscount(String discountPercent, String strOfferMinimumPrice) {
        double totalPrice = getTotalPrice();
        double discount = ((totalPrice * Double.parseDouble(discountPercent) / 100));
        double offerMinimumPrice = Double.parseDouble(strOfferMinimumPrice);

        if (offerMinimumPrice < totalPrice) {
            double finalPrice = totalPrice - discount + shippingCharge;
            total.setText(String.valueOf(finalPrice));
            discountVal.setText(String.valueOf(discount));
            applyCoupon.setText("Remove Coupon");
            applyCoupon.setTag("remove");
        } else {
            Toast.makeText(ShoppingCartActivity2.this, "This offer is valid for minimum price order: "
                    + offerMinimumPrice, Toast.LENGTH_SHORT).show();
        }

    }

    private void onApplyCoupon() {

        String couponCode = editCoupon.getText().toString();
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
                        OfferData offerData = getValidCouponResponse.getData();
                        String discountPercent = offerData.getDiscount();
                        String offerMinimumPrice = offerData.getMinimumOrderAmount();
                        applyDiscount(discountPercent, offerMinimumPrice);

                    } else {
                        Log.e("Tag", getValidCouponResponse.getMessage());
                        Toast.makeText(ShoppingCartActivity2.this, getValidCouponResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    ProgressDialogUtil.hideProgressDialog();
                }
            });

        } else {
            Toast.makeText(ShoppingCartActivity2.this, "Coupon Code Empty", Toast.LENGTH_SHORT).show();
        }

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
                convertView = layoutInflater.inflate(R.layout.view_shopcart_item_child, null);
                holder = new ViewHolder();
                holder.rel_mrp_offer_price = (RelativeLayout) convertView.findViewById(R.id.rel_mrp_offer_price);
                holder.items_mrp_price = (TextView) convertView.findViewById(R.id.items_mrp_price);
                holder.parent = (RelativeLayout) convertView.findViewById(com.signity.bonbon.R.id.parent);
                holder.items_name = (TextView) convertView.findViewById(com.signity.bonbon.R.id.items_name);
                holder.items_name.setTypeface(typeFaceRobotoBold);
                holder.items_price = (TextView) convertView.findViewById(com.signity.bonbon.R.id.items_price);
                holder.items_price.setTypeface(typeFaceRobotoRegular);
                holder.add_button = (ImageButton) convertView.findViewById(com.signity.bonbon.R.id.add_button);
                holder.remove_button = (ImageButton) convertView.findViewById(com.signity.bonbon.R.id.remove_button);
                holder.number_text = (TextView) convertView.findViewById(com.signity.bonbon.R.id.number_text);
                holder.number_text.setTypeface(typeFaceRobotoRegular);
                holder.rupee = (TextView) convertView.findViewById(com.signity.bonbon.R.id.rupee);
                holder.rupee.setTypeface(typeFaceRobotoRegular);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final Product product = listProduct.get(position);
            final SelectedVariant selectedVariant = product.getSelectedVariant();



            holder.add_button.setVisibility(View.GONE);
            holder.remove_button.setVisibility(View.GONE);

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
            RelativeLayout parent;
            TextView items_name, items_price, number_text, rupee;
            public ImageButton add_button, remove_button;
            public RelativeLayout rel_mrp_offer_price;
            public TextView items_mrp_price;
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
}
