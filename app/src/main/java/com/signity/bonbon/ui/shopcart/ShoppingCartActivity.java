
package com.signity.bonbon.ui.shopcart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.GsonHelper;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.Utilities.SimpleDividerItemDecoration;
import com.signity.bonbon.Utilities.Util;
import com.signity.bonbon.app.AppController;
import com.signity.bonbon.app.DataAdapter;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.model.GetSubCategory;
import com.signity.bonbon.model.Product;
import com.signity.bonbon.model.SelectedVariant;
import com.signity.bonbon.model.SubCategory;
import com.signity.bonbon.model.Variant;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.Delivery.DeliveryActivity;
import com.signity.bonbon.ui.Delivery.DeliveryPickupActivity;
import com.signity.bonbon.ui.Delivery.DineInTableActivity;
import com.signity.bonbon.ui.RecommendedProduct.RecommendProductsActivity;
import com.signity.bonbon.ui.RecommendedProduct.RecommendProductsGroceryActivity;
import com.signity.bonbon.ui.login.LoginScreenActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ShoppingCartActivity extends Activity implements View.OnClickListener {

    private static final int REQUESTCODE_FOR_SUCESS_LOGIN = 330;
    ListView listViewCart;
    ProductListAdapter adapter;

    TextView total, title, emptyCart, rupee,saving_rupee;
    Button placeorder,viewAllBtn;
    List<Product> listProduct;
    List<Product> listRecommendProduct;
    private Button backButton;
    private AppDatabase appDb;
    private PrefManager prefManager;
    ImageButton search;
    String currency;
    private RecyclerView recommendedItemsList;
    private String productId="";
    private  RelativeLayout recommendItemLayout;
    private String[] productIds;
    GsonHelper gsonHelper;
    HorizontalAdapter mAdapter;
    private Context context;
    private RelativeLayout deliveryOptionLayout;
    private LinearLayout deliveryOption, pickupOption, dineinOption;
    String pickUpStatus, deliveryStatus, inStoreStatus;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_cart_activity);
        context = this;
        appDb = DbAdapter.getInstance().getDb();
        prefManager = new PrefManager(this);
        gsonHelper = new GsonHelper();
        currency = prefManager.getSharedValue(AppConstant.CURRENCY);

        pickUpStatus = prefManager.getPickupFacilityStatus();
        deliveryStatus= prefManager.getDeliveryFacilityStatus();
        inStoreStatus= prefManager.getInStoreFacilityStatus();


        init();

        initListeners();

        if (currency.contains("\\")) {
            rupee.setText(Util.unescapeJavaString(currency));
        } else {
            rupee.setText(currency);
        }



        ShoppingCartActivity.this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    private void initListeners() {
        backButton.setOnClickListener(this);
        placeorder.setOnClickListener(this);
        viewAllBtn.setOnClickListener(this);
        search.setOnClickListener(this);
        deliveryOption.setOnClickListener(this);
        dineinOption.setOnClickListener(this);
        pickupOption.setOnClickListener(this);
    }

    private void init() {

        recommendedItemsList= (RecyclerView) findViewById(R.id.recommendedItemsList);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(ShoppingCartActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recommendedItemsList.setLayoutManager(horizontalLayoutManagaer);

        recommendItemLayout = (RelativeLayout) findViewById(R.id.recommendItemLayout);
        listViewCart = (ListView) findViewById(R.id.items_list);
        search = (ImageButton) findViewById(R.id.search);
        rupee = (TextView) findViewById(R.id.rupee);
        total = (TextView) findViewById(R.id.total);
        title = (TextView) findViewById(R.id.textTitle);
        emptyCart = (TextView) findViewById(R.id.emptyCart);
        title.setText(getString(R.string.str_lbl_mycart));
        placeorder = (Button) findViewById(R.id.placeorder);
        backButton = (Button) findViewById(R.id.backButton);
        viewAllBtn = (Button) findViewById(R.id.viewAllBtn);
        saving_rupee = (TextView) findViewById(R.id.saving_rupee);
        saving_rupee.setSelected(true);
        deliveryOptionLayout = (RelativeLayout) findViewById(R.id.delivery_option_layout);
        deliveryOption = (LinearLayout) findViewById(R.id.delivery_option);
        dineinOption = (LinearLayout) findViewById(R.id.dinein_option);
        pickupOption = (LinearLayout) findViewById(R.id.pickup_option);
    }

    private void getRecommendProductList(String productId) {
        ProgressDialogUtil.showProgressDialog(ShoppingCartActivity.this);
        Map<String, String> param = new HashMap<String, String>();
//        Log.e("id", id);
        param.put("product_id", productId);
        NetworkAdaper.getInstance().getNetworkServices().getRecommendProductList(param, new Callback<GetSubCategory>() {

            @Override
            public void success(GetSubCategory getSubCategory, Response response) {
                if (getSubCategory.getSuccess()) {
                    ProgressDialogUtil.hideProgressDialog();
                    setupListProduct(getSubCategory.getData());
                } else {
                    ProgressDialogUtil.hideProgressDialog();
                    recommendItemLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                DialogHandler dialogHandler = new DialogHandler(ShoppingCartActivity.this);
                dialogHandler.setdialogForFinish(getResources().getString(R.string.dialog_title), getResources().getString(R.string.error_code_message), false);
            }
        });
    }

    private void setupListProduct(List<SubCategory> data) {
        listRecommendProduct = new ArrayList<>();
        for (SubCategory subCategory : data) {
            listRecommendProduct.addAll(subCategory.getProducts());
        }
        if (listRecommendProduct != null && listRecommendProduct.size()!=0) {
            recommendItemLayout.setVisibility(View.VISIBLE);
            mAdapter = new HorizontalAdapter( ShoppingCartActivity.this, listRecommendProduct);
            recommendedItemsList.setAdapter(mAdapter);
            recommendedItemsList.addItemDecoration(new SimpleDividerItemDecoration(this,LinearLayoutManager.HORIZONTAL));
        }else {
            recommendItemLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        listProduct = appDb.getCartListProduct();
        if (listProduct != null && listProduct.size() != 0) {
            adapter = new ProductListAdapter(ShoppingCartActivity.this, listProduct);
            listViewCart.setAdapter(adapter);
            listViewCart.setVisibility(View.VISIBLE);
            emptyCart.setVisibility(View.GONE);

            productIds=new String[listProduct.size()];

            for (int i=0; i<listProduct.size();i++){
                productIds[i]=listProduct.get(i).getId();
            }

            productId=Arrays.toString(productIds);

            checkRecommendedItems();

        } else {
            listViewCart.setVisibility(View.GONE);
            emptyCart.setVisibility(View.VISIBLE);
            recommendItemLayout.setVisibility(View.GONE);
        }
        updateCartPrice();
        updateSavingPrice();
    }

    private void checkRecommendedItems() {

        try {
            String itemsSwitch= prefManager.getSharedValue(AppConstant.RECOMMENDED_ITEMS);
            if(itemsSwitch!=null && !itemsSwitch.isEmpty() && itemsSwitch.equalsIgnoreCase("1")){
                getRecommendProductList(productId.replace("[","").replace("]",""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateSavingPrice() {

        String totalSaving = appDb.getCartTotalSaving();
        if(totalSaving.equalsIgnoreCase("0.00") || totalSaving.contains("-")){
            saving_rupee.setVisibility(View.GONE);
        }else {
            saving_rupee.setVisibility(View.VISIBLE);
            if (currency.contains("\\")) {
                saving_rupee.setText(getString(R.string.str_lbl_congrats_you_saved)+" "+Util.unescapeJavaString(currency)+totalSaving);
            } else {
                saving_rupee.setText(getString(R.string.str_lbl_congrats_you_saved)+" "+currency+totalSaving);
            }
        }
    }

    private void updateCartPrice() {
        String totalCartValue = appDb.getCartTotalPrice();
        total.setText(totalCartValue);
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
                holder.parent = (RelativeLayout) convertView.findViewById(R.id.parent);
                holder.items_name = (TextView) convertView.findViewById(R.id.items_name);
                holder.items_price = (TextView) convertView.findViewById(R.id.items_price);
                holder.btnVarient = (Button) convertView.findViewById(R.id.btnVarient);
                holder.add_button = (ImageButton) convertView.findViewById(R.id.add_button);
                holder.remove_button = (ImageButton) convertView.findViewById(R.id.remove_button);
                holder.number_text = (TextView) convertView.findViewById(R.id.number_text);
                holder.rupee = (TextView) convertView.findViewById(R.id.rupee);
                holder.rupee2 = (TextView) convertView.findViewById(R.id.rupee2);
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

            if (prefManager.getProjectType().equals(AppConstant.KEY_STORE_TYPE_GROCERY)) {
                holder.btnVarient.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                holder.btnVarient.setText(txtQuant);
                holder.btnVarient.setVisibility(View.VISIBLE);
            } else if(prefManager.getProjectType().equals(AppConstant.KEY_STORE_TYPE_RESTAURANT)){
                holder.btnVarient.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                holder.btnVarient.setText(txtQuant);
                holder.btnVarient.setVisibility(View.VISIBLE);
            }
            else {
                holder.btnVarient.setVisibility(View.GONE);
            }

            String currency = prefManager.getSharedValue(AppConstant.CURRENCY);


            if (currency.contains("\\")) {
                holder.rupee.setText(Util.unescapeJavaString(currency));
                holder.rupee2.setText(Util.unescapeJavaString(currency));
            } else {
                holder.rupee.setText(currency);
                holder.rupee2.setText(currency);
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


            holder.add_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quant = Integer.parseInt(selectedVariant.getQuantity());
                    quant = quant + 1;
                    selectedVariant.setQuantity(String.valueOf(quant));
                    holder.number_text.setText(quant + "");
                    notifyDataSetChanged();
                    appDb.updateProduct(product);
                    appDb.updateToCart(product);
                    updateCartPrice();
                    updateSavingPrice();
                }
            });

            holder.remove_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quant = Integer.parseInt(selectedVariant.getQuantity());
                    if (quant > 0) {
                        quant = quant - 1;
                        selectedVariant.setQuantity(String.valueOf(quant));
                        holder.number_text.setText(quant + "");
                        appDb.updateProduct(product);
                        appDb.updateToCart(product);
                        updateCartPrice();
                        updateSavingPrice();
                        if (!appDb.isProductInCart(selectedVariant.getVariantId())) {
                            listProduct.remove(product);
                            notifyDataSetChanged();
                        } else {
                            notifyDataSetChanged();
                        }
                    }

                }
            });


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
                convertView = l.inflate(R.layout.custom_spinner, parent, false);
                String weight = variant.getWeight();
                String prices = variant.getPrice();
                String unit = variant.getUnitType();
                TextView weights = (TextView) convertView.findViewById(R.id.weights);
                weights.setText(weight + " " + unit);
                TextView price = (TextView) convertView.findViewById(R.id.price);
                price.setText(prices);
                return convertView;
            }
        }

        class ViewHolder {
            RelativeLayout parent;
            Button btnVarient;
            TextView items_name, items_price, number_text, rupee, rupee2;
            public ImageButton add_button, remove_button;
            public RelativeLayout rel_mrp_offer_price;
            public TextView items_mrp_price;
        }
    }


    public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

        Activity context;
        LayoutInflater l;
        List<Product> list;
        private LayoutInflater mInflater;


        public HorizontalAdapter(Activity context, List<Product> list) {
            this.list = list;
            this.context = context;
            l = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recommend_product_child, parent, false);

            DisplayMetrics metrics=getDisplayMatric(context);
            ViewGroup.LayoutParams params=itemView.getLayoutParams();
            params.width=(metrics.widthPixels/3);
            itemView.setLayoutParams(params);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final Product product = list.get(position);
            final SelectedVariant selectedVariant = product.getSelectedVariant();

            String productPrice = "0.0";
            String mrpPrice = "0.0";
            String txtQuant = "";
            String txtQuantCount = "";

            if (selectedVariant != null && !selectedVariant.getVariantId().equals("0")) {
                txtQuant = String.valueOf(selectedVariant.getWeight() + " " + selectedVariant.getUnitType()).trim();
                productPrice = selectedVariant.getPrice();
                mrpPrice = selectedVariant.getMrpPrice();
                txtQuantCount = selectedVariant.getQuantity();
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
                mrpPrice = selectedVariant.getMrpPrice();
                txtQuantCount = selectedVariant.getQuantity();
            }

            String currency = prefManager.getSharedValue(AppConstant.CURRENCY);


            if (currency.contains("\\")) {
                holder.rupee.setText(Util.unescapeJavaString(currency));
            } else {
                holder.rupee.setText(currency);
            }
            holder.items_name.setText(product.getTitle());
            holder.items_name.setSelected(true);
            holder.items_price.setText(productPrice);
            holder.variant.setText(txtQuant);

            if(prefManager.getProjectType().equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_GROCERY)){

                holder.imageView.setVisibility(View.VISIBLE);
                if (product.getImageSmall() != null && !product.getImageSmall().isEmpty()) {
                    Picasso.with(ShoppingCartActivity.this).load(product.getImageSmall()).resize(50,50).centerInside()
                            .error(R.mipmap.ic_launcher).placeholder(R.drawable.placeholder).into(holder.imageView);
                } else {
                    holder.imageView.setImageResource(R.mipmap.ic_launcher);
                }
            }
            else {
                holder.imageView.setVisibility(View.GONE);
            }


            holder.recommendLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String productString = gsonHelper.getProduct(product);
                    prefManager.storeSharedValue(PrefManager.PREF_SEARCH_PRODUCT, productString);
                    Intent i = new Intent(ShoppingCartActivity.this, AppController.getInstance().getViewController().getProductViewActivity());
                    i.putExtra("product_id", product.getId());
                    startActivity(i);
                    AnimUtil.slideFromRightAnim(ShoppingCartActivity.this);
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder  {
            TextView items_name,items_price,rupee,variant;
            LinearLayout recommendLayout;
            Button addBtn;
            ImageView imageView;

            public MyViewHolder(View view) {
                super(view);
                items_name = (TextView) view.findViewById(R.id.items_name);
                items_price = (TextView) view.findViewById(R.id.items_price);
                rupee = (TextView) view.findViewById(R.id.rupee);
                variant = (TextView) view.findViewById(R.id.variant);
                addBtn = (Button) view.findViewById(R.id.addBtn);
                recommendLayout = (LinearLayout) view.findViewById(R.id.recommendLayout);
                imageView = (ImageView) view.findViewById(R.id.imageView);
            }

        }
    }



    public static DisplayMetrics getDisplayMatric(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        Log.e("Dimension", metrics.widthPixels + "*" + metrics.heightPixels);
        return metrics;
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(ShoppingCartActivity.this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.backButton:
                onBackPressed();
                break;

            case R.id.placeorder:
                if (appDb.getCartSize() != 0) {
                    proceedToPlaceOrder();
                } else {
                    Toast.makeText(ShoppingCartActivity.this, getString(R.string.msg_toast_add_item_to_cart), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.search:
                startActivity(new Intent(ShoppingCartActivity.this, AppController.getInstance().getViewController().getSearchActivity()));
                AnimUtil.slideFromRightAnim(ShoppingCartActivity.this);
                break;

            case R.id.viewAllBtn:
                DataAdapter.getInstance().setProductList(listRecommendProduct);
                if(prefManager.getProjectType().equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_GROCERY)){
                    startActivity(new Intent(ShoppingCartActivity.this, RecommendProductsGroceryActivity.class));
                    AnimUtil.slideFromRightAnim(ShoppingCartActivity.this);
                }else if(prefManager.getProjectType().equalsIgnoreCase(AppConstant.KEY_STORE_TYPE_RESTAURANT)){
                    startActivity(new Intent(ShoppingCartActivity.this, RecommendProductsActivity.class));
                    AnimUtil.slideFromRightAnim(ShoppingCartActivity.this);
                }

                break;

            case R.id.delivery_option:

                deliveryOptionLayout.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intentDelivery = new Intent(ShoppingCartActivity.this, DeliveryActivity.class);
                        intentDelivery.putExtra(AppConstant.FROM, "shop_cart");
                        startActivity(intentDelivery);
                        AnimUtil.slideFromRightAnim(ShoppingCartActivity.this);
                    }
                }, 100);

                break;

            case R.id.pickup_option:

                deliveryOptionLayout.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intentDelivery = new Intent(ShoppingCartActivity.this, DeliveryPickupActivity.class);
                        intentDelivery.putExtra(AppConstant.FROM, "shop_cart");
                        intentDelivery.putExtra("title", getResources().getString(R.string.str_select));
                        startActivity(intentDelivery);
                        AnimUtil.slideFromRightAnim(ShoppingCartActivity.this);
                    }
                }, 100);

                break;

            case R.id.dinein_option:

                deliveryOptionLayout.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intentDelivery = new Intent(ShoppingCartActivity.this, DineInTableActivity.class);
                        intentDelivery.putExtra(AppConstant.FROM, "shop_cart");
                        startActivity(intentDelivery);
                        AnimUtil.slideFromRightAnim(ShoppingCartActivity.this);
                    }
                }, 100);

                break;

        }

    }

    private void proceedToPlaceOrder() {
        PrefManager prefManager = new PrefManager(ShoppingCartActivity.this);
        String userId = prefManager.getSharedValue(AppConstant.ID);

        if (!userId.isEmpty()) {
            callProceedToPlaceOrder();
        } else {
            callLoginActivity();
        }
    }

    private void callProceedToPlaceOrder() {
        Intent intentDelivery = null;

        if(deliveryStatus.equalsIgnoreCase("0") && pickUpStatus.equalsIgnoreCase("0") && inStoreStatus.equalsIgnoreCase("0")){
            intentDelivery = new Intent(ShoppingCartActivity.this, DeliveryPickupActivity.class);
            intentDelivery.putExtra("title", getResources().getString(R.string.str_select));
            intentDelivery.putExtra(AppConstant.FROM, "shop_cart");
            startActivity(intentDelivery);
            AnimUtil.slideFromRightAnim(ShoppingCartActivity.this);
        }else if(deliveryStatus.equalsIgnoreCase("1") && pickUpStatus.equalsIgnoreCase("0") && inStoreStatus.equalsIgnoreCase("0")){
            intentDelivery = new Intent(ShoppingCartActivity.this, DeliveryActivity.class);
            intentDelivery.putExtra(AppConstant.FROM, "shop_cart");
            startActivity(intentDelivery);
            AnimUtil.slideFromRightAnim(ShoppingCartActivity.this);
        }
        else if(inStoreStatus.equalsIgnoreCase("1") && deliveryStatus.equalsIgnoreCase("0") && pickUpStatus.equalsIgnoreCase("0")){
            intentDelivery = new Intent(ShoppingCartActivity.this, DineInTableActivity.class);
            intentDelivery.putExtra(AppConstant.FROM, "shop_cart");
            startActivity(intentDelivery);
            AnimUtil.slideFromRightAnim(ShoppingCartActivity.this);
        }
        else if(pickUpStatus.equalsIgnoreCase("1") && inStoreStatus.equalsIgnoreCase("0") && deliveryStatus.equalsIgnoreCase("0")){
            intentDelivery = new Intent(ShoppingCartActivity.this, DeliveryPickupActivity.class);
            intentDelivery.putExtra("title", getResources().getString(R.string.str_select));
            intentDelivery.putExtra(AppConstant.FROM, "shop_cart");
            startActivity(intentDelivery);
            AnimUtil.slideFromRightAnim(ShoppingCartActivity.this);
        }else {
            chooseDeliveryOptions();
        }


    }

    private void chooseDeliveryOptions() {
        deliveryOptionLayout.setVisibility(View.VISIBLE);

        if(pickUpStatus.equalsIgnoreCase("1")){
            pickupOption.setVisibility(View.VISIBLE);
        }else {
            pickupOption.setVisibility(View.GONE);
        }

        if(deliveryStatus.equalsIgnoreCase("1")){
            deliveryOption.setVisibility(View.VISIBLE);
        }else {
            deliveryOption.setVisibility(View.GONE);
        }

        if(inStoreStatus.equalsIgnoreCase("1")){
            dineinOption.setVisibility(View.VISIBLE);
        }else {
            dineinOption.setVisibility(View.GONE);
        }

    }

    private void callLoginActivity() {
        Intent intentLogin = new Intent(context, LoginScreenActivity.class);
        startActivityForResult(intentLogin, REQUESTCODE_FOR_SUCESS_LOGIN);
    }


    public void showAlertDialog(Context context, String title,
                                String message) {
        new DialogHandler(context).setdialogForFinish(title, message, true);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUESTCODE_FOR_SUCESS_LOGIN:
                if (resultCode == Activity.RESULT_OK) {
//                    userId = prefManager.getSharedValue(AppConstant.ID);
//                    adapter.notifyDataSetChanged();
                    logUser();
                    callProceedToPlaceOrder();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(context, getString(R.string.lbl_login_error_msg), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        try {
            Crashlytics.setUserIdentifier(prefManager.getSharedValue(AppConstant.ID));
            Crashlytics.setUserEmail(prefManager.getSharedValue(AppConstant.EMAIL));
            Crashlytics.setUserName(prefManager.getSharedValue(AppConstant.NAME));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
