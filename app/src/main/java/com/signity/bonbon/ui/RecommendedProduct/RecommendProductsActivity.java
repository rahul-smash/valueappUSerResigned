package com.signity.bonbon.ui.RecommendedProduct;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.GsonHelper;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.Util;
import com.signity.bonbon.app.AppController;
import com.signity.bonbon.app.DataAdapter;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.ga.GATrackers;
import com.signity.bonbon.model.GetSearchSubProducts;
import com.signity.bonbon.model.Product;
import com.signity.bonbon.model.SelectedVariant;
import com.signity.bonbon.model.Variant;
import com.signity.bonbon.ui.shopcart.ShoppingCartActivity;

import java.util.List;

public class RecommendProductsActivity extends Activity implements View.OnClickListener {

    TextView mHeaderText, shoppinglist_text;
    Button mBackButton, btnShopcart;
    public Typeface typeFaceRobotoRegular, typeFaceRobotoBold;
    ProductListAdapter adapter;
    ListView mSearchList;
    List<Product> listProduct;
    List<List<GetSearchSubProducts>> parentList;
    List<GetSearchSubProducts> childList;
    AppDatabase appDb;
    GsonHelper gsonHelper;
    PrefManager prefManager;
    String searchStr;

    public int cartSize;
    List<Product> favourite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_products);
        GATrackers.getInstance().trackScreenView(getString(R.string.ga_screen_recommended));
        prefManager = new PrefManager(RecommendProductsActivity.this);
        appDb = DbAdapter.getInstance().getDb();
        gsonHelper = new GsonHelper();
        initialize();
        clickListeners();
        setTypeFaces();
        checkCartValue();

        listProduct= DataAdapter.getInstance().getProductList();

        setupListProduct(listProduct);

    }


    private void initialize() {
        mHeaderText = (TextView) findViewById(R.id.headerText);
        mBackButton = (Button) findViewById(R.id.backButton);
        btnShopcart = (Button) findViewById(R.id.btnShopcart);
        shoppinglist_text = (Button) findViewById(R.id.shoppinglist_text);
        mSearchList = (ListView) findViewById(R.id.searchList);

        favourite = appDb.getUserFavProductList();
    }

    private void clickListeners() {
        mBackButton.setOnClickListener(this);
        btnShopcart.setOnClickListener(this);

    }

    private void setTypeFaces() {

        mHeaderText.setTypeface(typeFaceRobotoRegular);
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
            case R.id.backButton:
                onBackPressed();
                break;

            case R.id.btnShopcart:
                startActivity(new Intent(RecommendProductsActivity.this, ShoppingCartActivity.class));
                AnimUtil.slideFromRightAnim(RecommendProductsActivity.this);
                break;


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(RecommendProductsActivity.this);
    }




    private void setupListProduct(List<Product> listProduct) {

        if (listProduct != null) {

            for(int i=0; i<favourite.size(); i++){
                for (int j=0; j<listProduct.size();j++){
                    if(favourite.get(i).getId().equalsIgnoreCase(listProduct.get(j).getId())){
                        listProduct.get(j).setFavorites(true);
                    }
                }
            }
            adapter = new ProductListAdapter(RecommendProductsActivity.this, listProduct);
            mSearchList.setAdapter(adapter);
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
                convertView = layoutInflater.inflate(R.layout.categories_item_child, null);
                holder = new ViewHolder();
                holder.rel_mrp_offer_price = (RelativeLayout) convertView.findViewById(R.id.rel_mrp_offer_price);
                holder.items_mrp_price = (TextView) convertView.findViewById(R.id.items_mrp_price);
                holder.parent = (RelativeLayout) convertView.findViewById(R.id.parent);
                holder.block2 = (LinearLayout) convertView.findViewById(R.id.block2);
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
                holder.heart = (ImageButton) convertView.findViewById(R.id.heart);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final Product product = listProduct.get(position);
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
                holder.rupee2.setText(Util.unescapeJavaString(currency));
            } else {
                holder.rupee.setText(currency);
                holder.rupee2.setText(currency);
            }


            if (product.isFavorites()) {
                holder.heart.setSelected(true);
            } else {
                holder.heart.setSelected(false);
            }

            if (product.getVariants().size() <= 1) {
                holder.btnVarient.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            } else {
                holder.btnVarient.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_spinner_down_24, 0);
            }
            String variant = selectedVariant.getWeight().trim() + selectedVariant.getUnitType().trim();

            if (!variant.isEmpty()) {
                holder.btnVarient.setVisibility(View.VISIBLE);
                holder.btnVarient.setText(txtQuant);
            } else {
                holder.btnVarient.setVisibility(View.GONE);
            }

            holder.items_name.setText(product.getTitle());
            holder.items_price.setText(productPrice);

            if (productPrice.equalsIgnoreCase(mrpPrice)) {
                holder.rupee.setVisibility(View.VISIBLE);
                holder.rel_mrp_offer_price.setVisibility(View.GONE);
            } else {
                holder.rupee.setVisibility(View.GONE);
                holder.rel_mrp_offer_price.setVisibility(View.VISIBLE);
            }
            holder.items_mrp_price.setText(mrpPrice);
            holder.number_text.setText(txtQuantCount);


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
                    checkCartValue();
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
                        notifyDataSetChanged();
                        appDb.updateProduct(product);
                        appDb.updateToCart(product);
                        checkCartValue();
                    }
                }
            });
            if (product.getVariants().size() > 1) {
                holder.btnVarient.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(RecommendProductsActivity.this);

                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog.setContentView(R.layout.layout_popup_window_varient);
                        ListView lv = (ListView) dialog.findViewById(R.id.listViewCategory);
                        Button btnCross = (Button) dialog.findViewById(R.id.btnCross);
                        madapter = new MyAdapter(RecommendProductsActivity.this, R.layout.custom_spinner, product.getVariants());
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
                                notifyDataSetChanged();
                            }
                        });
                        dialog.show();
                    }
                });
            }


            holder.block2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String productString = gsonHelper.getProduct(product);
                    prefManager.storeSharedValue(PrefManager.PREF_SEARCH_PRODUCT, productString);
                    Intent i = new Intent(RecommendProductsActivity.this, AppController.getInstance().getViewController().getProductViewActivity());
                    i.putExtra("product_id", product.getId());
                    startActivity(i);
                    AnimUtil.slideFromRightAnim(RecommendProductsActivity.this);
                }
            });


            holder.heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!product.isFavorites()) {
                        product.setFavorites(true);
                        appDb.updateProduct(product);
                    } else {
                        product.setFavorites(false);
                        appDb.updateProduct(product);
                    }
                    notifyDataSetChanged();
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

                TextView rupee_tag = (TextView) convertView.findViewById(R.id.rupee_tag);
                String currency = prefManager.getSharedValue(AppConstant.CURRENCY);


                if (currency.contains("\\")) {
                    rupee_tag.setText(Util.unescapeJavaString(currency));
                } else {
                    rupee_tag.setText(currency);
                }

                TextView price = (TextView) convertView.findViewById(R.id.price);
                price.setText(prices);

                return convertView;
            }


        }

        class ViewHolder {
            RelativeLayout parent;
            LinearLayout block2;
            Button btnVarient;
            RelativeLayout rel_mrp_offer_price;
            TextView items_name, items_price, items_mrp_price, number_text, rupee, rupee2;
            public ImageButton add_button, remove_button, heart;
        }
    }

}
