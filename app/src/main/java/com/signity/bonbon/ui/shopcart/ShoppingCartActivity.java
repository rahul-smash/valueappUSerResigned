package com.signity.bonbon.ui.shopcart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.gcm.GCMClientManager;
import com.signity.bonbon.model.Product;
import com.signity.bonbon.model.SelectedVariant;
import com.signity.bonbon.model.Variant;
import com.signity.bonbon.ui.Delivery.DeliveryActivity;
import com.signity.bonbon.ui.login.LoginScreenActivity;
import com.signity.bonbon.ui.search.SearchActivity;

import java.util.List;

public class ShoppingCartActivity extends Activity implements View.OnClickListener {

    ListView listViewCart;
    ProductListAdapter adapter;

    TextView total, title, emptyCart;
    Button placeorder;
    ImageButton back;
    RelativeLayout menu_layout;
    List<Product> listProduct;
    private GCMClientManager pushClientManager;
    public Typeface typeFaceRobotoRegular, typeFaceRobotoBold;
    private Button backButton;
    private AppDatabase appDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_cart_activity);
        appDb = DbAdapter.getInstance().getDb();
        PrefManager prefManager = new PrefManager(this);
        pushClientManager = new GCMClientManager(this, AppConstant.PROJECT_NUMBER);
        typeFaceRobotoRegular = FontUtil.getTypeface(this, FontUtil.FONT_ROBOTO_REGULAR);
        typeFaceRobotoBold = FontUtil.getTypeface(this, FontUtil.FONT_ROBOTO_BOLD);
        listViewCart = (ListView) findViewById(R.id.items_list);
        total = (TextView) findViewById(R.id.total);
        total.setTypeface(typeFaceRobotoRegular);
        title = (TextView) findViewById(R.id.textTitle);
        emptyCart = (TextView) findViewById(R.id.emptyCart);
        title.setText("My Cart");
        title.setTypeface(typeFaceRobotoRegular);
        placeorder = (Button) findViewById(R.id.placeorder);
        backButton = (Button) findViewById(R.id.backButton);

        backButton.setOnClickListener(this);
        placeorder.setOnClickListener(this);
        ShoppingCartActivity.this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
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
        } else {
            listViewCart.setVisibility(View.GONE);
            emptyCart.setVisibility(View.VISIBLE);
        }
        updateCartPrice();
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
                holder.add_button = (ImageButton) convertView.findViewById(R.id.add_button);
                holder.remove_button = (ImageButton) convertView.findViewById(R.id.remove_button);
                holder.number_text = (TextView) convertView.findViewById(R.id.number_text);
                holder.number_text.setTypeface(typeFaceRobotoRegular);
                holder.rupee = (TextView) convertView.findViewById(R.id.rupee);
                holder.rupee.setTypeface(typeFaceRobotoRegular);
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
            TextView items_name, items_price, number_text, rupee;
            public ImageButton add_button, remove_button;
            public RelativeLayout rel_mrp_offer_price;
            public TextView items_mrp_price;
        }
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
                    Toast.makeText(ShoppingCartActivity.this, "Empty Cart", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btnSearch:
                startActivity(new Intent(ShoppingCartActivity.this, SearchActivity.class));
                AnimUtil.slideFromRightAnim(ShoppingCartActivity.this);
                break;
        }

    }

    private void proceedToPlaceOrder() {

        PrefManager prefManager = new PrefManager(ShoppingCartActivity.this);
        String userId = prefManager.getSharedValue(AppConstant.ID);


        if (!userId.isEmpty()) {
//            callNetworkServiceForPlaceOrder(userId);
            Intent intentDelivery = new Intent(ShoppingCartActivity.this, DeliveryActivity.class);
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
}
