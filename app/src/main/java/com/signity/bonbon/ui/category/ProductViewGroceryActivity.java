package com.signity.bonbon.ui.category;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.ga.GATrackers;
import com.signity.bonbon.model.GetSubCategory;
import com.signity.bonbon.model.Product;
import com.signity.bonbon.model.SelectedVariant;
import com.signity.bonbon.model.SubCategory;
import com.signity.bonbon.model.Variant;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.RecommendedProduct.RecommendProductsActivity;
import com.signity.bonbon.ui.RecommendedProduct.RecommendProductsGroceryActivity;
import com.signity.bonbon.ui.shopcart.ShoppingCartActivity;
import com.signity.bonbon.ui.shopping.ShoppingListActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProductViewGroceryActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = ProductViewGroceryActivity.class.getSimpleName();
    Button backButton, btnVarient, btnShopList, btnShopcart, shoppinglist_text, viewAllBtn;
    TextView description, item_name, price, number_text, title, price_text, rupee, items_mrp_price;
    TextView textTitle;
    public Typeface typeFaceRobotoRegular, typeFaceRobotoBold;
    private Product product;
    private ImageView item_image;
    private ImageButton add_button, remove_button;
    private AppDatabase appDb;
    private GsonHelper gsonHelper;
    private PrefManager prefManager;

    public int cartSize;

    String productViewTitle = "";
    View divider;

    List<Product> listProduct;
    List<Product> listRecommendProduct;
    private RecyclerView recommendedItemsList;
    private String productId = "";
    HorizontalAdapter mAdapter;
    private RelativeLayout recommendItemLayout;
    private String[] productIds;
    String product_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view_grocery);
        GATrackers.getInstance().trackScreenView(getString(R.string.ga_screen_product_view));
        appDb = DbAdapter.getInstance().getDb();
        gsonHelper = new GsonHelper();
        prefManager = new PrefManager(ProductViewGroceryActivity.this);
        initProduct();
        typeFaceRobotoRegular = FontUtil.getTypeface(ProductViewGroceryActivity.this, FontUtil.FONT_ROBOTO_REGULAR);
        typeFaceRobotoBold = FontUtil.getTypeface(ProductViewGroceryActivity.this, FontUtil.FONT_ROBOTO_BOLD);
        recommendedItemsList = (RecyclerView) findViewById(R.id.recommendedItemsList);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(ProductViewGroceryActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recommendedItemsList.setLayoutManager(horizontalLayoutManagaer);
        recommendItemLayout = (RelativeLayout) findViewById(R.id.recommendItemLayout);

        backButton = (Button) findViewById(R.id.backButton);
        btnVarient = (Button) findViewById(R.id.btnVarient);
        textTitle = (TextView) findViewById(R.id.textTitle);
        btnShopList = (Button) findViewById(R.id.btnShopList);
        btnShopcart = (Button) findViewById(R.id.btnShopcart);
        shoppinglist_text = (Button) findViewById(R.id.shoppinglist_text);
        rupee = (TextView) findViewById(R.id.rupee);
        items_mrp_price = (TextView) findViewById(R.id.items_mrp_price);
        divider = (View) findViewById(R.id.divider);
        viewAllBtn = (Button) findViewById(R.id.viewAllBtn);

        String currency = prefManager.getSharedValue(AppConstant.CURRENCY);


        if (currency.contains("\\")) {
            rupee.setText(unescapeJavaString(currency));
        } else {
            rupee.setText(currency);
        }
        btnShopList.setOnClickListener(this);
        backButton.setOnClickListener(this);
        btnShopcart.setOnClickListener(this);
        viewAllBtn.setOnClickListener(this);

        if (productViewTitle == null) {
            textTitle.setText(product.getTitle());
        } else {
            textTitle.setText(productViewTitle);
        }

        item_image = (ImageView) findViewById(com.signity.bonbon.R.id.item_image);
        item_name = (TextView) findViewById(com.signity.bonbon.R.id.item_name);
        item_name.setTypeface(typeFaceRobotoRegular);
        price = (TextView) findViewById(com.signity.bonbon.R.id.price);
        price.setTypeface(typeFaceRobotoRegular);
        description = (TextView) findViewById(com.signity.bonbon.R.id.description);
        price_text = (TextView) findViewById(com.signity.bonbon.R.id.price_text);
        price_text.setTypeface(typeFaceRobotoRegular);
        number_text = (TextView) findViewById(com.signity.bonbon.R.id.number_text);
        number_text.setTypeface(typeFaceRobotoRegular);
        add_button = (ImageButton) findViewById(com.signity.bonbon.R.id.add_button);
        remove_button = (ImageButton) findViewById(com.signity.bonbon.R.id.remove_button);
        add_button.setOnClickListener(this);
        remove_button.setOnClickListener(this);
        if (product.getVariants().size() > 1) {
            btnVarient.setCompoundDrawablesWithIntrinsicBounds(0, 0, com.signity.bonbon.R.drawable.arrow_spinner_down_24, 0);
            btnVarient.setOnClickListener(this);
        } else {
            btnVarient.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        setupProductUi();
        checkCartValue();

        getRecommendProductList(product_id);
//        setupRecommendedProducts();
    }

    private void setupRecommendedProducts() {
        listProduct = appDb.getCartListProduct();
        if (listProduct != null && listProduct.size() != 0) {

            productIds = new String[listProduct.size()];

            for (int i = 0; i < listProduct.size(); i++) {
                productIds[i] = listProduct.get(i).getId();
            }

            productId = Arrays.toString(productIds);

            getRecommendProductList(product_id);
        } else {
            recommendItemLayout.setVisibility(View.GONE);
        }
    }


    private void getRecommendProductList(String productId) {
        ProgressDialogUtil.showProgressDialog(ProductViewGroceryActivity.this);
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
                DialogHandler dialogHandler = new DialogHandler(ProductViewGroceryActivity.this);
                dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), false);
            }
        });
    }

    private void setupListProduct(List<SubCategory> data) {
        listRecommendProduct = new ArrayList<>();
        for (SubCategory subCategory : data) {
            listRecommendProduct.addAll(subCategory.getProducts());
        }
        if (listRecommendProduct != null && listRecommendProduct.size() != 0) {
            recommendItemLayout.setVisibility(View.VISIBLE);
            mAdapter = new HorizontalAdapter(ProductViewGroceryActivity.this, listRecommendProduct);
            recommendedItemsList.setAdapter(mAdapter);
            recommendedItemsList.addItemDecoration(new SimpleDividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));
        } else {
            recommendItemLayout.setVisibility(View.GONE);
        }
    }

    // setup value for ui element
    private void setupProductUi() {

        SelectedVariant selectedVariant = product.getSelectedVariant();
        String txtQuant, productPrice, txtQuantCount;
        String mrpPrice = "0.0";
        if (selectedVariant != null && !selectedVariant.getVariantId().equals("0")) {
            txtQuant = String.valueOf(selectedVariant.getWeight() + " " + selectedVariant.getUnitType()).trim();
            productPrice = selectedVariant.getPrice();
            mrpPrice = selectedVariant.getMrpPrice();
            txtQuantCount = appDb.getCartQuantity(selectedVariant.getVariantId());
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
        item_name.setText(product.getTitle());
        price.setText(productPrice);
        items_mrp_price.setText(mrpPrice);
        if (productPrice.equalsIgnoreCase(mrpPrice)) {
            divider.setVisibility(View.GONE);
            items_mrp_price.setVisibility(View.GONE);
        } else {
            divider.setVisibility(View.VISIBLE);
            items_mrp_price.setVisibility(View.VISIBLE);
        }

        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            description.setText(Html.fromHtml(product.getDescription()));
        }
        number_text.setText(txtQuantCount);

        String variant = selectedVariant.getWeight().trim() + selectedVariant.getUnitType().trim();
        if (!variant.isEmpty()) {
            btnVarient.setVisibility(View.VISIBLE);
            btnVarient.setText(txtQuant);
        } else {
            btnVarient.setVisibility(View.GONE);
        }
        btnVarient.setText(txtQuant);

        try {
            if (product.getImage() != null && !product.getImage().isEmpty()) {
                Picasso.with(ProductViewGroceryActivity.this).load(product.getImage()).fit().centerInside()
                        .error(R.mipmap.ic_launcher).placeholder(R.drawable.placeholder).into(item_image);
            } else {
                item_image.setImageResource(R.mipmap.ic_launcher);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initProduct() {
        product_id = getIntent().getStringExtra("product_id");
        productViewTitle = getIntent().getStringExtra("productViewTitle");
        productViewTitle = getIntent().getStringExtra("productViewTitle");
        product = appDb.getProduct(product_id);
        if (product == null) {
            product = gsonHelper.getProduct(prefManager.getSharedValue(PrefManager.PREF_SEARCH_PRODUCT));
        }
    }


    public void checkCartValue() {
        cartSize = appDb.getCartSize();

        if (cartSize != 0) {
            shoppinglist_text.setVisibility(View.VISIBLE);
            shoppinglist_text.setText("" + cartSize);
        } else {
            shoppinglist_text.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case com.signity.bonbon.R.id.backButton:
                onBackPressed();
                break;
            case com.signity.bonbon.R.id.btnShopList:
                startActivity(new Intent(ProductViewGroceryActivity.this, ShoppingListActivity.class));
                AnimUtil.slideFromRightAnim(ProductViewGroceryActivity.this);
                break;
            case R.id.btnShopcart:
                startActivity(new Intent(ProductViewGroceryActivity.this, ShoppingCartActivity.class));
                AnimUtil.slideFromRightAnim(ProductViewGroceryActivity.this);
                break;
            case com.signity.bonbon.R.id.btnVarient:
                addCartVarient();
                break;
            case com.signity.bonbon.R.id.add_button:
                addProductQuantity();
                break;
            case com.signity.bonbon.R.id.remove_button:
                removeProductQuantity();
                break;
            case R.id.viewAllBtn:
                DataAdapter.getInstance().setProductList(listRecommendProduct);
                if (prefManager.getProjectType().equalsIgnoreCase(AppConstant.APP_TYPE_GROCERY)) {
                    startActivity(new Intent(ProductViewGroceryActivity.this, RecommendProductsGroceryActivity.class));
                    AnimUtil.slideFromRightAnim(ProductViewGroceryActivity.this);
                } else if (prefManager.getProjectType().equalsIgnoreCase(AppConstant.APP_TYPE_RESTAURANT)) {
                    startActivity(new Intent(ProductViewGroceryActivity.this, RecommendProductsActivity.class));
                    AnimUtil.slideFromRightAnim(ProductViewGroceryActivity.this);
                }
                break;

        }

    }

    private void removeProductQuantity() {
        SelectedVariant selectedVariant = product.getSelectedVariant();
        int quant = Integer.parseInt(selectedVariant.getQuantity());
        if (quant > 0) {
            quant = quant - 1;
            selectedVariant.setQuantity(String.valueOf(quant));
            number_text.setText(quant + "");
            appDb.updateProduct(product);
            appDb.updateToCart(product);
            checkCartValue();
        }
    }


    private void addProductQuantity() {
        SelectedVariant selectedVariant = product.getSelectedVariant();
        int quant = Integer.parseInt(selectedVariant.getQuantity());
        quant = quant + 1;
        selectedVariant.setQuantity(String.valueOf(quant));
        number_text.setText(quant + "");
        appDb.updateProduct(product);
        appDb.updateToCart(product);
        checkCartValue();

    }

    private void addCartVarient() {
        final Dialog dialog = new Dialog(ProductViewGroceryActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(com.signity.bonbon.R.layout.layout_popup_window_varient);
        ListView lv = (ListView) dialog.findViewById(com.signity.bonbon.R.id.listViewCategory);
        Button btnCross = (Button) dialog.findViewById(com.signity.bonbon.R.id.btnCross);
        MyAdapter madapter = new MyAdapter(ProductViewGroceryActivity.this, com.signity.bonbon.R.layout.custom_spinner, product.getVariants());
        lv.setAdapter(madapter);
        dialog.setCanceledOnTouchOutside(true);
        btnCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                dialog.dismiss();
                SelectedVariant selectedVariant = product.getSelectedVariant();
                selectedVariant.setVarientPosition(pos);
                Variant variant = product.getVariants().get(pos);
                selectedVariant.setVariantId(variant.getId());
                selectedVariant.setSku(variant.getSku());
                selectedVariant.setWeight(variant.getWeight());
                selectedVariant.setMrpPrice(variant.getMrpPrice());
                selectedVariant.setPrice(variant.getPrice());
                selectedVariant.setDiscount(variant.getDiscount());
                selectedVariant.setUnitType(variant.getUnitType());
                selectedVariant.setQuantity(appDb.getCartQuantity(variant.getId()));
                price.setText(selectedVariant.getPrice());
                number_text.setText(selectedVariant.getQuantity());
                String txtQuant = selectedVariant.getWeight() + " " + selectedVariant.getUnitType();
                btnVarient.setText(txtQuant);
            }
        });
        dialog.show();

    }


    class MyAdapter extends ArrayAdapter<Variant> {
        LayoutInflater l;
        List<Variant> listVariant;

        public MyAdapter(Activity ctx, int txtViewResourceId, List<Variant> listVariant) {
            super(ctx, txtViewResourceId, listVariant);
            l = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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


            TextView rupee_tag = (TextView) convertView.findViewById(R.id.rupee_tag);
            String currency = prefManager.getSharedValue(AppConstant.CURRENCY);


            if (currency.contains("\\")) {
                rupee_tag.setText(unescapeJavaString(currency));
            } else {
                rupee_tag.setText(currency);
            }
            TextView price = (TextView) convertView.findViewById(com.signity.bonbon.R.id.price);
            price.setText(prices);

            return convertView;
        }
    }

    @Override
    public void onBackPressed() {
//        updateCartOnBack();
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(ProductViewGroceryActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkCartValue();
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

            DisplayMetrics metrics = getDisplayMatric(context);
            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            params.width = (metrics.widthPixels / 3);
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
            holder.items_name.setSelected(true);
            holder.items_price.setText(productPrice);
            holder.variant.setText(txtQuant);

            if (prefManager.getProjectType().equalsIgnoreCase(AppConstant.APP_TYPE_GROCERY)) {

                holder.imageView.setVisibility(View.VISIBLE);
                if (product.getImageSmall() != null && !product.getImageSmall().isEmpty()) {
                    try {
                        Picasso.with(ProductViewGroceryActivity.this).load(product.getImageSmall()).resize(50, 50).centerInside()
                                .error(R.mipmap.ic_launcher).placeholder(R.drawable.placeholder).into(holder.imageView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    holder.imageView.setImageResource(R.mipmap.ic_launcher);
                }
            } else {
                holder.imageView.setVisibility(View.GONE);
            }

            holder.recommendLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String productString = gsonHelper.getProduct(product);
                    prefManager.storeSharedValue(PrefManager.PREF_SEARCH_PRODUCT, productString);
                    Intent i = new Intent(ProductViewGroceryActivity.this, AppController.getInstance().getViewController().getProductViewActivity());
                    i.putExtra("product_id", product.getId());
                    startActivity(i);
                    AnimUtil.slideFromRightAnim(ProductViewGroceryActivity.this);
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView items_name, items_price, rupee, variant;
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
}
