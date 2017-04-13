package com.signity.bonbon.ui.search;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.signity.bonbon.Utilities.GsonHelper;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.app.AppController;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.ga.GATrackers;
import com.signity.bonbon.model.GetSearchSubProducts;
import com.signity.bonbon.model.GetSubCategory;
import com.signity.bonbon.model.Product;
import com.signity.bonbon.model.SelectedVariant;
import com.signity.bonbon.model.ShoppingListObject;
import com.signity.bonbon.model.SubCategory;
import com.signity.bonbon.model.Variant;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.shopcart.ShoppingCartActivity;
import com.signity.bonbon.ui.shopping.ListDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by rajesh on 15/10/15.
 */
public class SearchForGroceryActivity extends Activity implements View.OnClickListener {

    TextView mHeaderText,shoppinglist_text;
    Button mBackButton,btnShopcart;
    EditText mSearchEdit;
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

    ListDatabase db;
    List<String> shoppingList = new ArrayList<String>();
    List<Product> favourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        GATrackers.getInstance().trackScreenView(getString(R.string.ga_screen_search));
        prefManager = new PrefManager(SearchForGroceryActivity.this);
        appDb = DbAdapter.getInstance().getDb();
        db = new ListDatabase(SearchForGroceryActivity.this);
        gsonHelper = new GsonHelper();
        prefManager = new PrefManager(SearchForGroceryActivity.this);
        shoppingList = db.getAllContacts();

        initialize();
        clickListeners();
        setTypeFaces();
        checkCartValue();

        mSearchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                String gaCategory = getString(R.string.ga_catagory_search);
                String action = getString(R.string.app_name) + "_" + getString(R.string.ga_action_search);
                String lbl = String.format(getString(R.string.ga_lbl_search), mSearchEdit.getText().toString(), getString(R.string.app_name));
                GATrackers.getInstance().trackEvent(gaCategory, action,
                        lbl);
                getSearchList(mSearchEdit.getText().toString());
                return false;
            }
        });

        Bundle bundle = getIntent().getExtras();
        try {
            if (bundle != null) {
                searchStr = bundle.getString("search_str");
            }
        } catch (Exception e) {

        }

        if (searchStr != null && !searchStr.isEmpty()) {
            mSearchEdit.setText(searchStr);
            getSearchList(mSearchEdit.getText().toString());
        }
    }


    private void initialize() {
        mHeaderText = (TextView) findViewById(R.id.headerText);
        mBackButton = (Button) findViewById(R.id.backButton);
        mSearchEdit = (EditText) findViewById(R.id.searchEdit);
        btnShopcart = (Button) findViewById(R.id.btnShopcart);
        shoppinglist_text = (Button) findViewById(R.id.shoppinglist_text);
        typeFaceRobotoRegular = FontUtil.getTypeface(SearchForGroceryActivity.this, FontUtil.FONT_ROBOTO_REGULAR);
        typeFaceRobotoBold = FontUtil.getTypeface(SearchForGroceryActivity.this, FontUtil.FONT_ROBOTO_BOLD);
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
                startActivity(new Intent(SearchForGroceryActivity.this, ShoppingCartActivity.class));
                AnimUtil.slideFromRightAnim(SearchForGroceryActivity.this);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(SearchForGroceryActivity.this);
    }


    public void getSearchList(String keyWords) {

        parentList = new ArrayList<List<GetSearchSubProducts>>();
        childList = new ArrayList<GetSearchSubProducts>();
        ProgressDialogUtil.showProgressDialog(SearchForGroceryActivity.this);
        String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
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
        param.put("keyword", keyWords);

        NetworkAdaper.getInstance().getNetworkServices().getSearchList(param, new Callback<GetSubCategory>() {

            @Override
            public void success(GetSubCategory getSubCategory, Response response) {
                if (getSubCategory.getSuccess()) {
                    ProgressDialogUtil.hideProgressDialog();
                    if(getSubCategory.getData().size()!=0){
                        setupListProduct(getSubCategory.getData());
                    }else {
                        Toast.makeText(SearchForGroceryActivity.this, getString(R.string.str_no_data_found), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    ProgressDialogUtil.hideProgressDialog();
                    Toast.makeText(SearchForGroceryActivity.this, getString(R.string.str_no_data_found), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                DialogHandler dialogHandler = new DialogHandler(SearchForGroceryActivity.this);
                dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), false);
            }
        });
    }

    private void setupListProduct(List<SubCategory> data) {
        listProduct = new ArrayList<>();
        for (SubCategory subCategory : data) {
            listProduct.addAll(subCategory.getProducts());
        }
        if (listProduct != null) {

            for(int i=0; i<favourite.size(); i++){
                for (int j=0; j<listProduct.size();j++){
                    if(favourite.get(i).getId().equalsIgnoreCase(listProduct.get(j).getId())){
                        listProduct.get(j).setFavorites(true);
                    }
                }
            }

            adapter = new ProductListAdapter(SearchForGroceryActivity.this, listProduct);
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
                Picasso.with(SearchForGroceryActivity.this).load(product.getImageMedium())
                        .error(R.mipmap.ic_launcher).placeholder(R.drawable.placeholder).into(holder.items);
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


            if(shoppingList.contains(product.getTitle())){
                holder.imgBtnAddToShop.setImageResource(R.drawable.list_on);
            }else {
                holder.imgBtnAddToShop.setImageResource(R.drawable.list_off);
            }

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


            holder.imgBtnAddToShop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(shoppingList.contains(product.getTitle())){
                        alreadyAddedToShopList();
                    }else {
                        holder.imgBtnAddToShop.setImageResource(R.drawable.list_on);
                        addToShopList(product.getTitle());
                    }
                }
            });

            if (product.getVariants().size() > 1) {
                holder.btnVarient.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(SearchForGroceryActivity.this);

                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog.setContentView(R.layout.layout_popup_window_varient);
                        ListView lv = (ListView) dialog.findViewById(R.id.listViewCategory);
                        Button btnCross = (Button) dialog.findViewById(R.id.btnCross);
                        madapter = new MyAdapter(SearchForGroceryActivity.this, R.layout.custom_spinner, product.getVariants());
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

            holder.items.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String productString = gsonHelper.getProduct(product);
                    prefManager.storeSharedValue(PrefManager.PREF_SEARCH_PRODUCT, productString);
                    Intent i = new Intent(SearchForGroceryActivity.this, AppController.getInstance().getViewController().getProductViewActivity());
                    i.putExtra("product_id", product.getId());
                    startActivity(i);
                    AnimUtil.slideFromRightAnim(SearchForGroceryActivity.this);
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
        final DialogHandler dialogHandler = new DialogHandler(SearchForGroceryActivity.this);

        dialogHandler.setDialog(getString(R.string.msg_dialog_confirmation), getString(R.string.msg_dialog_add_product_to_shoping_list));
        dialogHandler.setPostiveButton(getString(R.string.str_add), true).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShoppingListObject att = new ShoppingListObject();
                db.addContact(title);
                shoppingList = db.getAllContacts();
                adapter.notifyDataSetChanged();
                Toast.makeText(SearchForGroceryActivity.this, getString(R.string.str_added_shopping_list), Toast.LENGTH_SHORT).show();
                dialogHandler.dismiss();

            }
        });
        dialogHandler.setNegativeButton(getString(R.string.str_cancel), true)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogHandler.dismiss();
                        adapter.notifyDataSetChanged();
                    }
                });

    }

    private void alreadyAddedToShopList(){
        DialogHandler dialogHandler = new DialogHandler(SearchForGroceryActivity.this);
        dialogHandler.setdialogForFinish("Message", getString(R.string.str_already_added_to_list), false);
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
