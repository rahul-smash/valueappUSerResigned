
package com.signity.bonbon.ui.shopcart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
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
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.signity.bonbon.Utilities.GsonHelper;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.Utilities.SimpleDividerItemDecoration;
import com.signity.bonbon.app.AppController;
import com.signity.bonbon.app.DataAdapter;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.app.ViewController;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.gcm.GCMClientManager;
import com.signity.bonbon.model.Category;
import com.signity.bonbon.model.GetSubCategory;
import com.signity.bonbon.model.Product;
import com.signity.bonbon.model.SelectedVariant;
import com.signity.bonbon.model.SubCategory;
import com.signity.bonbon.model.Variant;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.Delivery.DeliveryActivity;
import com.signity.bonbon.ui.Delivery.DeliveryPickupActivity;
import com.signity.bonbon.ui.RecommendedProduct.RecommendProductsActivity;
import com.signity.bonbon.ui.login.LoginScreenActivity;
import com.squareup.picasso.Picasso;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ShoppingCartActivity extends Activity implements View.OnClickListener {

    ListView listViewCart;
    ProductListAdapter adapter;

    TextView total, title, emptyCart, rupee,saving_rupee;
    Button placeorder,viewAllBtn;
    List<Product> listProduct;
    List<Product> listRecommendProduct;
    private GCMClientManager pushClientManager;
    public Typeface typeFaceRobotoRegular, typeFaceRobotoBold;
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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_cart_activity);
        appDb = DbAdapter.getInstance().getDb();
        prefManager = new PrefManager(this);
        gsonHelper = new GsonHelper();
        pushClientManager = new GCMClientManager(this, AppConstant.PROJECT_NUMBER);
        typeFaceRobotoRegular = FontUtil.getTypeface(this, FontUtil.FONT_ROBOTO_REGULAR);
        typeFaceRobotoBold = FontUtil.getTypeface(this, FontUtil.FONT_ROBOTO_BOLD);

        recommendedItemsList= (RecyclerView) findViewById(R.id.recommendedItemsList);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(ShoppingCartActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recommendedItemsList.setLayoutManager(horizontalLayoutManagaer);

        recommendItemLayout = (RelativeLayout) findViewById(R.id.recommendItemLayout);
        listViewCart = (ListView) findViewById(R.id.items_list);
        search = (ImageButton) findViewById(R.id.search);
        rupee = (TextView) findViewById(R.id.rupee);
        total = (TextView) findViewById(R.id.total);
        total.setTypeface(typeFaceRobotoRegular);
        title = (TextView) findViewById(R.id.textTitle);
        emptyCart = (TextView) findViewById(R.id.emptyCart);
        title.setText("My Cart");
        title.setTypeface(typeFaceRobotoRegular);
        placeorder = (Button) findViewById(R.id.placeorder);
        backButton = (Button) findViewById(R.id.backButton);
        viewAllBtn = (Button) findViewById(R.id.viewAllBtn);
        saving_rupee = (TextView) findViewById(R.id.saving_rupee);
        saving_rupee.setSelected(true);


        currency = prefManager.getSharedValue(AppConstant.CURRENCY);


        if (currency.contains("\\")) {
            rupee.setText(unescapeJavaString(currency));
        } else {
            rupee.setText(currency);
        }


        backButton.setOnClickListener(this);
        placeorder.setOnClickListener(this);
        viewAllBtn.setOnClickListener(this);
        search.setOnClickListener(this);
        ShoppingCartActivity.this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );


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
                dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), false);
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

            getRecommendProductList(productId.replace("[","").replace("]",""));
        } else {
            listViewCart.setVisibility(View.GONE);
            emptyCart.setVisibility(View.VISIBLE);
            recommendItemLayout.setVisibility(View.GONE);
        }
        updateCartPrice();
        updateSavingPrice();
    }

    private void updateSavingPrice() {

        String totalSaving = appDb.getCartTotalSaving();
        if(totalSaving.equalsIgnoreCase("0.00") || totalSaving.contains("-")){
            saving_rupee.setVisibility(View.GONE);
        }else {
            saving_rupee.setVisibility(View.VISIBLE);
            if (currency.contains("\\")) {
                saving_rupee.setText("Congratulations you save "+unescapeJavaString(currency)+totalSaving);
            } else {
                saving_rupee.setText("Congratulations you save "+currency+totalSaving);
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
                holder.items_name.setTypeface(typeFaceRobotoBold);
                holder.items_price = (TextView) convertView.findViewById(R.id.items_price);
                holder.items_price.setTypeface(typeFaceRobotoRegular);
                holder.btnVarient = (Button) convertView.findViewById(R.id.btnVarient);
                holder.add_button = (ImageButton) convertView.findViewById(R.id.add_button);
                holder.remove_button = (ImageButton) convertView.findViewById(R.id.remove_button);
                holder.number_text = (TextView) convertView.findViewById(R.id.number_text);
                holder.number_text.setTypeface(typeFaceRobotoRegular);
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
                holder.rupee.setText(unescapeJavaString(currency));
            } else {
                holder.rupee.setText(currency);
            }
            holder.items_name.setText(product.getTitle());
            holder.items_price.setText(productPrice);
            holder.variant.setText(txtQuant);


            holder.addBtn.setOnClickListener(new View.OnClickListener() {
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

            public MyViewHolder(View view) {
                super(view);
                items_name = (TextView) view.findViewById(R.id.items_name);
                items_price = (TextView) view.findViewById(R.id.items_price);
                rupee = (TextView) view.findViewById(R.id.rupee);
                variant = (TextView) view.findViewById(R.id.variant);
                addBtn = (Button) view.findViewById(R.id.addBtn);
                recommendLayout = (LinearLayout) view.findViewById(R.id.recommendLayout);

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
                    Toast.makeText(ShoppingCartActivity.this, "Please add items to your cart.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.search:
                startActivity(new Intent(ShoppingCartActivity.this, AppController.getInstance().getViewController().getSearchActivity()));
                AnimUtil.slideFromRightAnim(ShoppingCartActivity.this);
                break;

            case R.id.viewAllBtn:
                DataAdapter.getInstance().setProductList(listRecommendProduct);
                startActivity(new Intent(ShoppingCartActivity.this, RecommendProductsActivity.class));
                AnimUtil.slideFromRightAnim(ShoppingCartActivity.this);
                break;

        }

    }

    private void proceedToPlaceOrder() {

        PrefManager prefManager = new PrefManager(ShoppingCartActivity.this);
        String userId = prefManager.getSharedValue(AppConstant.ID);
        String pickUpStatus = prefManager.getPickupFacilityStatus();
        String deliveryStatus= prefManager.getDeliveryFacilityStatus();
        if (!userId.isEmpty()) {

            Intent intentDelivery = null;
            if(deliveryStatus.equalsIgnoreCase("1") && pickUpStatus.equalsIgnoreCase("1")){
                intentDelivery = new Intent(ShoppingCartActivity.this, DeliveryPickupActivity.class);
                intentDelivery.putExtra("title", "Deliver or PickUp");
            }
            else if(deliveryStatus.equalsIgnoreCase("1") && pickUpStatus.equalsIgnoreCase("0")){
                intentDelivery = new Intent(ShoppingCartActivity.this, DeliveryActivity.class);
            }
            else if(deliveryStatus.equalsIgnoreCase("0") && pickUpStatus.equalsIgnoreCase("1")){
                intentDelivery = new Intent(ShoppingCartActivity.this, DeliveryPickupActivity.class);
                intentDelivery.putExtra("title", "PickUp");
            }
            else {
                intentDelivery = new Intent(ShoppingCartActivity.this, DeliveryActivity.class);
            }


            /*if (pickUpStatus.equalsIgnoreCase("0")) {
                intentDelivery = new Intent(ShoppingCartActivity.this, DeliveryActivity.class);
            } else {
                intentDelivery = new Intent(ShoppingCartActivity.this, DeliveryPickupActivity.class);
            }*/
            intentDelivery.putExtra(AppConstant.FROM, "shop_cart");
            startActivity(intentDelivery);
            AnimUtil.slideFromRightAnim(ShoppingCartActivity.this);
        } else {
            Intent intentLogin = new Intent(ShoppingCartActivity.this, LoginScreenActivity.class);
            intentLogin.putExtra(AppConstant.FROM, "shop_cart");
            startActivity(intentLogin);
            AnimUtil.slideUpAnim(ShoppingCartActivity.this);
        }
    }


    public void showAlertDialog(Context context, String title,
                                String message) {
        new DialogHandler(context).setdialogForFinish(title, message, true);
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
}
