package com.signity.bonbon.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.Utilities.Util;
import com.signity.bonbon.app.AppController;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.listener.CartChangeListener;
import com.signity.bonbon.model.GetSubCategory;
import com.signity.bonbon.model.Product;
import com.signity.bonbon.model.SelectedVariant;
import com.signity.bonbon.model.SubCategory;
import com.signity.bonbon.model.Variant;
import com.signity.bonbon.network.NetworkAdaper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by root on 6/5/16.
 */
public class ProductListFragmentJewellers extends Fragment{

    private static final String TAG = ProductListFragment.class.getSimpleName();
    CartChangeListener cartChangeListener;
    ProductListAdapter adapter;
    ListView listView;
    TextView no_record;

    public static Activity context;
    public Typeface typeFaceRobotoRegular, typeFaceRobotoBold;

    public List<Product> listProduct;
    public SubCategory subCategory;

    String subCategoryId;
    private AppDatabase appDb;
    PrefManager prefManager;

    public static Fragment newInstance(Context context) {
        return Fragment.instantiate(context, ProductListFragment.class.getSimpleName());
    }

    public ProductListFragmentJewellers() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDb = DbAdapter.getInstance().getDb();
        cartChangeListener = (CartChangeListener) getActivity();
        prefManager = new PrefManager(getActivity());
        typeFaceRobotoRegular = FontUtil.getTypeface(getActivity(), FontUtil.FONT_ROBOTO_REGULAR);
        typeFaceRobotoBold = FontUtil.getTypeface(getActivity(), FontUtil.FONT_ROBOTO_BOLD);
        subCategoryId = getArguments().getString("subCategoryId");
        listProduct = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View convertView = inflater.inflate(R.layout.categories_item, null);
        listView = (ListView) convertView.findViewById(R.id.list);
        no_record = (TextView) convertView.findViewById(R.id.no_record);
//        listProduct = appDb.getProductList(subCategoryId);
        subCategory = appDb.getSubCategoryById(subCategoryId);
        adapter = new ProductListAdapter(getActivity(), listProduct);

        if (subCategory != null) {
            if ((subCategory.getVersion()).equals(subCategory.getOldVersion())) {
                listProduct = appDb.getProductList(subCategoryId);
                if (listProduct != null && listProduct.size() != 0) {
                    adapter = new ProductListAdapter(getActivity(), listProduct);
                    listView.setAdapter(adapter);
                    listView.setVisibility(View.VISIBLE);
                    no_record.setVisibility(View.GONE);
                } else {
                    getSubCategoryList(subCategoryId);
                }
            } else {
                getSubCategoryList(subCategoryId);
            }
        } else {
            getSubCategoryList(subCategoryId);
        }

        return convertView;
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

            if (layoutInflater == null) {
                layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.categories_item_child_jewellers, null);
                holder = new ViewHolder();
                holder.rel_mrp_offer_price = (RelativeLayout) convertView.findViewById(R.id.rel_mrp_offer_price);
                holder.items_mrp_price = (TextView) convertView.findViewById(R.id.items_mrp_price);
                holder.parent = (RelativeLayout) convertView.findViewById(R.id.parent);
                holder.block2 = (LinearLayout) convertView.findViewById(R.id.block2);
                holder.items = (ImageView) convertView.findViewById(R.id.items_image);
                holder.items_name = (TextView) convertView.findViewById(R.id.items_name);
                holder.items_name.setTypeface(typeFaceRobotoBold);
                holder.items_price = (TextView) convertView.findViewById(R.id.items_price);
                holder.items_price.setTypeface(typeFaceRobotoRegular);
                holder.items_mrp_price.setTypeface(typeFaceRobotoRegular);
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


            if (product.getImageMedium() != null && !product.getImageMedium().isEmpty()) {
                Picasso.with(getActivity()).load(product.getImageMedium()).error(R.mipmap.ic_launcher).into(holder.items);
            } else {
                holder.items.setImageResource(R.mipmap.ic_launcher);
            }


            String productPrice = "0.0";
            String mrpPrice = "0.0";
            String txtQuant = "";
            String txtQuantCount = "";
            String unitType = "";

            if (selectedVariant != null && !selectedVariant.getVariantId().equals("0")) {
                txtQuant = selectedVariant.getWeight();
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
                txtQuant = selectedVariant.getWeight();
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
                    cartChangeListener.onCartChangeListener();
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
                        cartChangeListener.onCartChangeListener();
                    }
                }
            });

            holder.block2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), AppController.getInstance().getViewController().getProductViewActivity());
                    i.putExtra("product_id", product.getId());
                    startActivity(i);
                    AnimUtil.slideFromRightAnim(getActivity());
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

            ImageView items;
            RelativeLayout parent, rel_mrp_offer_price;
            LinearLayout block2;
            TextView items_name, items_mrp_price, items_price, number_text, rupee, rupee2;
            public ImageButton add_button, remove_button, heart;
        }
    }


    // All networking call here


    public void getSubCategoryList(String id) {
        ProgressDialogUtil.showProgressDialog(getActivity());
        String deviceId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceToken = prefManager.getSharedValue(AppConstant.DEVICE_TOKEN);
        Map<String, String> param = new HashMap<String, String>();
//        Log.e("id", id);
        param.put("device_id", deviceId);
//        Log.e("device_id", deviceId);
        param.put("device_token", deviceToken);
//        Log.e("device_token", deviceToken);
        param.put("platform", AppConstant.PLATFORM);
//        Log.e("platform", AppConstant.PLATFORM);
        param.put("user_id", "");
        NetworkAdaper.getInstance().getNetworkServices().getSubCategoryProduct(param, id, new Callback<GetSubCategory>() {
            @Override
            public void success(GetSubCategory getCategory, Response response) {
                Log.e("Tab", getCategory.toString());

                if (getCategory.getSuccess()) {
                    List<SubCategory> list = getCategory.getData();
                    if (list != null && !list.isEmpty()) {
                        SubCategory subCategory = list.get(0);
                        appDb.addSubCategoryToDb(subCategory);
//                        listProduct = subCategory.getProducts();
                        listProduct = appDb.getProductList(subCategoryId);
                        if (listProduct != null && listProduct.size() != 0) {
                            adapter = new ProductListAdapter(getActivity(), listProduct);
                            listView.setAdapter(adapter);
                            listView.setVisibility(View.VISIBLE);
                            no_record.setVisibility(View.GONE);
                        } else {
                            listView.setVisibility(View.GONE);
                            no_record.setVisibility(View.VISIBLE);
                        }
                    }

                } else {
                    Toast.makeText(getActivity(), getString(R.string.lbl_no_response), Toast.LENGTH_SHORT).show();
                }

                ProgressDialogUtil.hideProgressDialog();

            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                DialogHandler dialogHandler = new DialogHandler(getActivity());
                dialogHandler.setdialogForFinish(getResources().getString(R.string.dialog_title), getResources().getString(R.string.error_code_message), false);
            }
        });

    }

}
