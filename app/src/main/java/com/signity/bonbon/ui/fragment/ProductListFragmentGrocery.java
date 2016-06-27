package com.signity.bonbon.ui.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.signity.bonbon.app.AppController;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.gcm.GCMClientManager;
import com.signity.bonbon.listener.CartChangeListener;
import com.signity.bonbon.model.GetSubCategory;
import com.signity.bonbon.model.Product;
import com.signity.bonbon.model.SelectedVariant;
import com.signity.bonbon.model.ShoppingListObject;
import com.signity.bonbon.model.SubCategory;
import com.signity.bonbon.model.Variant;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.shopping.ListDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public final class ProductListFragmentGrocery extends Fragment {

    private static final String TAG = ProductListFragment.class.getSimpleName();
    CartChangeListener cartChangeListener;
    ProductListAdapter adapter;
    ListView listView;
    TextView no_record;
    ListDatabase db;

    public static Activity context;
    public Typeface typeFaceRobotoRegular, typeFaceRobotoBold;

    public List<Product> listProduct;
    public SubCategory subCategory;
    private GCMClientManager pushClientManager;

    String subCategoryId;
    private AppDatabase appDb;
    PrefManager prefManager;
    String productViewTitle;

    public static Fragment newInstance(Context context) {
        return Fragment.instantiate(context, ProductListFragment.class.getSimpleName());
    }

    public ProductListFragmentGrocery() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDb = DbAdapter.getInstance().getDb();
        db = new ListDatabase(getActivity());
        pushClientManager = new GCMClientManager(getActivity(), AppConstant.PROJECT_NUMBER);
        cartChangeListener = (CartChangeListener) getActivity();
        prefManager = new PrefManager(getActivity());
        typeFaceRobotoRegular = FontUtil.getTypeface(getActivity(), FontUtil.FONT_ROBOTO_REGULAR);
        typeFaceRobotoBold = FontUtil.getTypeface(getActivity(), FontUtil.FONT_ROBOTO_BOLD);
        subCategoryId = getArguments().getString("subCategoryId");
        productViewTitle = getArguments().getString("productViewTitle","");
        listProduct = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View convertView = inflater.inflate(R.layout.categories_item, null);
        listView = (ListView) convertView.findViewById(R.id.list);
        no_record = (TextView) convertView.findViewById(R.id.no_record);
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
                convertView = layoutInflater.inflate(R.layout.categories_item_child_grocery, null);
                holder = new ViewHolder();
                holder.rel_mrp_offer_price = (RelativeLayout) convertView.findViewById(R.id.rel_mrp_offer_price);
                holder.items_mrp_price = (TextView) convertView.findViewById(R.id.items_mrp_price);
                holder.items = (ImageView) convertView.findViewById(R.id.items_image);
                holder.imgBtnAddToShop = (ImageButton) convertView.findViewById(R.id.imgBtnAddToShop);
                holder.parent = (RelativeLayout) convertView.findViewById(R.id.parent);
                holder.items_name = (TextView) convertView.findViewById(R.id.items_name);
                holder.items_name.setTypeface(typeFaceRobotoBold);
                holder.items_price = (TextView) convertView.findViewById(R.id.items_price);
                holder.items_price.setTypeface(typeFaceRobotoRegular);
                holder.btnVarient = (Button) convertView.findViewById(R.id.btnVarient);
                holder.btnVarient.setClickable(true);
                holder.btnVarient.setFocusable(true);

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

            holder.imgBtnAddToShop.setVisibility(View.VISIBLE);

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
                holder.rupee2.setText(unescapeJavaString(currency));
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
                holder.btnVarient.setSelected(false);
            } else {
                holder.btnVarient.setSelected(true);
                holder.btnVarient.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_spinner_down_24, 0);
            }

            String variant=selectedVariant.getWeight().trim()+selectedVariant.getUnitType().trim();

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

            holder.imgBtnAddToShop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToShopList(product.getTitle());
                }
            });



            if (product.getVariants().size() > 1) {

                holder.btnVarient.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(getActivity());

                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog.setContentView(R.layout.layout_popup_window_varient);
                        ListView lv = (ListView) dialog.findViewById(R.id.listViewCategory);
                        Button btnCross = (Button) dialog.findViewById(R.id.btnCross);
                        madapter = new MyAdapter(getActivity(), R.layout.custom_spinner, product.getVariants());
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
                                appDb.updateProduct(product);
                                notifyDataSetChanged();
                            }
                        });
                        dialog.show();
                    }
                });
            } else {
                holder.btnVarient.setClickable(false);
                holder.btnVarient.setFocusable(false);
            }


            holder.items.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), AppController.getInstance().getViewController().getProductViewActivity());
                    i.putExtra("product_id", product.getId());
                    i.putExtra("productViewTitle", productViewTitle);
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
                    rupee_tag.setText(unescapeJavaString(currency));
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
            ImageButton imgBtnAddToShop;
            RelativeLayout parent;
            Button btnVarient;
            RelativeLayout rel_mrp_offer_price;
            TextView items_name, items_mrp_price, items_price, number_text, rupee, rupee2;
            public ImageButton add_button, remove_button, heart;
        }
    }

    private void addToShopList(final String title) {
        final DialogHandler dialogHandler = new DialogHandler(getActivity());

        dialogHandler.setDialog("Confirmation", "Are you sure to add this product to shopping list");
        dialogHandler.setPostiveButton("Yes", true).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShoppingListObject att = new ShoppingListObject();
                att.itemName = title;
                db.addContact(att);
                Toast.makeText(getActivity(), "Added to Shopping List", Toast.LENGTH_SHORT).show();
                dialogHandler.dismiss();

            }
        });
        dialogHandler.setNegativeButton("No", true)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogHandler.dismiss();
                    }
                });

    }
    // All networking call here

    public void getSubCategoryList(String id) {
        ProgressDialogUtil.showProgressDialog(getActivity());
        String deviceId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceToken = pushClientManager.getRegistrationId(getActivity());
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
                        listProduct = subCategory.getProducts();
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
                    Toast.makeText(getActivity(), "No response", Toast.LENGTH_SHORT).show();
                }

                ProgressDialogUtil.hideProgressDialog();

            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                DialogHandler dialogHandler = new DialogHandler(getActivity());
                dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), false);
            }
        });

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

