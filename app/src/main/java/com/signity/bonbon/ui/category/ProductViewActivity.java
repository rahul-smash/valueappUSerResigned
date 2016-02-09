package com.signity.bonbon.ui.category;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.FontUtil;
import com.signity.bonbon.Utilities.GsonHelper;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.gcm.GCMClientManager;
import com.signity.bonbon.model.Product;
import com.signity.bonbon.model.SelectedVariant;
import com.signity.bonbon.model.Variant;
import com.signity.bonbon.ui.shopping.ShoppingListActivity;

import java.util.List;

public class ProductViewActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = ProductViewActivity.class.getSimpleName();
    private GCMClientManager pushClientManager;
    Button backButton, btnVarient, btnShopList;
    TextView description, item_name, price, number_text, title, price_text;
    TextView textTitle;
    public Typeface typeFaceRobotoRegular, typeFaceRobotoBold;
    private Product product;
    private ImageButton add_button, remove_button;
    private AppDatabase appDb;
    private GsonHelper gsonHelper;

    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.signity.bonbon.R.layout.activity_product_view);
        appDb = DbAdapter.getInstance().getDb();
        gsonHelper = new GsonHelper();
        prefManager = new PrefManager(ProductViewActivity.this);
        initProduct();
        typeFaceRobotoRegular = FontUtil.getTypeface(ProductViewActivity.this, FontUtil.FONT_ROBOTO_REGULAR);
        typeFaceRobotoBold = FontUtil.getTypeface(ProductViewActivity.this, FontUtil.FONT_ROBOTO_BOLD);
        pushClientManager = new GCMClientManager(this, AppConstant.PROJECT_NUMBER);
        backButton = (Button) findViewById(com.signity.bonbon.R.id.backButton);
        btnVarient = (Button) findViewById(com.signity.bonbon.R.id.btnVarient);
        textTitle = (TextView) findViewById(com.signity.bonbon.R.id.textTitle);
        btnShopList = (Button) findViewById(com.signity.bonbon.R.id.btnShopList);
        btnShopList.setOnClickListener(this);
        backButton.setOnClickListener(this);

        item_name = (TextView) findViewById(com.signity.bonbon.R.id.item_name);
        item_name.setTypeface(typeFaceRobotoRegular);
        price = (TextView) findViewById(com.signity.bonbon.R.id.price);
        description = (TextView) findViewById(com.signity.bonbon.R.id.description);
        price.setTypeface(typeFaceRobotoRegular);
        price_text = (TextView) findViewById(com.signity.bonbon.R.id.price_text);
        price_text.setTypeface(typeFaceRobotoRegular);
        number_text = (TextView) findViewById(com.signity.bonbon.R.id.number_text);
        number_text.setTypeface(typeFaceRobotoRegular);
        add_button = (ImageButton) findViewById(com.signity.bonbon.R.id.add_button);
        remove_button = (ImageButton) findViewById(com.signity.bonbon.R.id.remove_button);
        add_button.setOnClickListener(this);
        remove_button.setOnClickListener(this);
        textTitle.setText(product.getTitle());
        if (product.getVariants().size() > 1) {
            btnVarient.setCompoundDrawablesWithIntrinsicBounds(0, 0, com.signity.bonbon.R.drawable.arrow_spinner_down_24, 0);
            btnVarient.setOnClickListener(this);
        } else {
            btnVarient.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        setupProductUi();
    }

    // setup value for ui element
    private void setupProductUi() {
        SelectedVariant selectedVariant = product.getSelectedVariant();
        String txtQuant, productPrice, txtQuantCount;
        if (selectedVariant != null && !selectedVariant.getVariantId().equals("0")) {
            txtQuant = selectedVariant.getWeight();
            productPrice = selectedVariant.getPrice();
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
            txtQuant = selectedVariant.getWeight();
            productPrice = selectedVariant.getPrice();
            txtQuantCount = selectedVariant.getQuantity();
        }
        item_name.setText(product.getTitle());
        price.setText(productPrice);
        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            description.setText(product.getDescription());
        }
        number_text.setText(txtQuantCount);
        if (txtQuant != null && !txtQuant.isEmpty()) {
            btnVarient.setText(txtQuant);
        } else {
            btnVarient.setVisibility(View.GONE);
        }
    }

    private void initProduct() {
        String product_id = getIntent().getStringExtra("product_id");
        product = appDb.getProduct(product_id);
        if (product == null) {
            product = gsonHelper.getProduct(prefManager.getSharedValue(PrefManager.PREF_SEARCH_PRODUCT));
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case com.signity.bonbon.R.id.backButton:
                onBackPressed();
                break;
            case com.signity.bonbon.R.id.btnShopList:
                startActivity(new Intent(ProductViewActivity.this, ShoppingListActivity.class));
                AnimUtil.slideFromRightAnim(ProductViewActivity.this);
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

    }


    private void addCartVarient() {


        final Dialog dialog = new Dialog(ProductViewActivity.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(com.signity.bonbon.R.layout.layout_popup_window_varient);
        ListView lv = (ListView) dialog.findViewById(com.signity.bonbon.R.id.listViewCategory);
        Button btnCross = (Button) dialog.findViewById(com.signity.bonbon.R.id.btnCross);
        MyAdapter madapter = new MyAdapter(ProductViewActivity.this, com.signity.bonbon.R.layout.custom_spinner, product.getVariants());
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
                String txtQuant = selectedVariant.getWeight();
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
            TextView price = (TextView) convertView.findViewById(com.signity.bonbon.R.id.price);
            price.setText(prices);

            return convertView;
        }
    }

    @Override
    public void onBackPressed() {
//        updateCartOnBack();
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(ProductViewActivity.this);
    }
}
