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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.signity.bonbon.R;
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
import com.signity.bonbon.ui.shopcart.ShoppingCartActivity;
import com.signity.bonbon.ui.shopping.ShoppingListActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductViewGroceryActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = ProductViewGroceryActivity.class.getSimpleName();
    private GCMClientManager pushClientManager;
    Button backButton, btnVarient, btnShopList,btnShopcart,shoppinglist_text;
    TextView description, item_name, price, number_text, title, price_text,rupee, items_mrp_price;
    TextView textTitle;
    public Typeface typeFaceRobotoRegular, typeFaceRobotoBold;
    private Product product;
    private ImageView item_image;
    private ImageButton add_button, remove_button;
    private AppDatabase appDb;
    private GsonHelper gsonHelper;
    private PrefManager prefManager;

    public int cartSize;

    String productViewTitle="";
    View divider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view_grocery);
        appDb = DbAdapter.getInstance().getDb();
        gsonHelper = new GsonHelper();
        prefManager = new PrefManager(ProductViewGroceryActivity.this);
        initProduct();
        typeFaceRobotoRegular = FontUtil.getTypeface(ProductViewGroceryActivity.this, FontUtil.FONT_ROBOTO_REGULAR);
        typeFaceRobotoBold = FontUtil.getTypeface(ProductViewGroceryActivity.this, FontUtil.FONT_ROBOTO_BOLD);
        pushClientManager = new GCMClientManager(this, AppConstant.PROJECT_NUMBER);
        backButton = (Button) findViewById(R.id.backButton);
        btnVarient = (Button) findViewById(R.id.btnVarient);
        textTitle = (TextView) findViewById(R.id.textTitle);
        btnShopList = (Button) findViewById(R.id.btnShopList);
        btnShopcart=(Button)findViewById(R.id.btnShopcart);
        shoppinglist_text=(Button)findViewById(R.id.shoppinglist_text);
        rupee=(TextView)findViewById(R.id.rupee);
        items_mrp_price = (TextView) findViewById(R.id.items_mrp_price);
        divider = (View) findViewById(R.id.divider);

        String currency = prefManager.getSharedValue(AppConstant.CURRENCY);


        if (currency.contains("\\")) {
            rupee.setText(unescapeJavaString(currency));
        }
        else {
            rupee.setText(currency);
        }
        btnShopList.setOnClickListener(this);
        backButton.setOnClickListener(this);
        btnShopcart.setOnClickListener(this);

        if(productViewTitle==null){
            textTitle.setText(product.getTitle());
        }else {
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
    }

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
            description.setText(product.getDescription());
        }
        number_text.setText(txtQuantCount);

        String variant=selectedVariant.getWeight().trim()+selectedVariant.getUnitType().trim();
        if (!variant.isEmpty()) {
            btnVarient.setVisibility(View.VISIBLE);
            btnVarient.setText(txtQuant);
        } else {
            btnVarient.setVisibility(View.GONE);
        }
        btnVarient.setText(txtQuant);

        if (product.getImage() != null && !product.getImage().isEmpty()) {
            Picasso.with(ProductViewGroceryActivity.this).load(product.getImage()).resize(400, 400).error(R.mipmap.ic_launcher).into(item_image);
        } else {
            item_image.setImageResource(R.mipmap.ic_launcher);
        }
    }

    private void initProduct() {
        String product_id = getIntent().getStringExtra("product_id");
        productViewTitle = getIntent().getStringExtra("productViewTitle");
        product = appDb.getProduct(product_id);
        if (product == null) {
            product = gsonHelper.getProduct(prefManager.getSharedValue(PrefManager.PREF_SEARCH_PRODUCT));
        }
    }


    public void checkCartValue() {
        cartSize = appDb.getCartSize();

        if(cartSize!=0){
            shoppinglist_text.setVisibility(View.VISIBLE);
            shoppinglist_text.setText(""+cartSize);
        }else {
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
            }
            else {
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
}
