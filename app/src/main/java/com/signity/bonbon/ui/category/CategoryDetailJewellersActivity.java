package com.signity.bonbon.ui.category;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.Util;
import com.signity.bonbon.app.AppController;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.listener.CartChangeListener;
import com.signity.bonbon.model.Category;
import com.signity.bonbon.model.SubCategory;
import com.signity.bonbon.ui.fragment.ProductListFragmentJewellers;
import com.signity.bonbon.ui.shopcart.ShoppingCartActivity;
import com.signity.bonbon.ui.shopping.ShoppingListActivity;

import java.util.List;

public class CategoryDetailJewellersActivity extends FragmentActivity implements View.OnClickListener, CartChangeListener {

    public static final String TAG = CategoryDetailJewellersActivity.class.getSimpleName();
    private AppDatabase appDb;
    private LinearLayout linearShopCart;
    Button backButton, btnSearch, btnShopCart, btnCartCount, proceed, btnShopList;
    TextView textTitle;
    TextView cartTotalPrice,rupee;
    TabLayout tabLayout;
    ViewPager viewPager;
    PagerAdapter adapter;
    String title;
    String id;
    List<SubCategory> subCategoryList;
    boolean isActivityFirstTime = true;
    PrefManager prefManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail_jewellers);
        appDb = DbAdapter.getInstance().getDb();
        prefManager= new PrefManager(CategoryDetailJewellersActivity.this);
        title = getIntent().getStringExtra("title");
        id = getIntent().getStringExtra("categoryId");
        rupee=(TextView)findViewById(R.id.rupee);
        backButton = (Button) findViewById(R.id.backButton);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnShopCart = (Button) findViewById(R.id.shopingcart);
        linearShopCart = (LinearLayout) findViewById(R.id.linearShopCart);
        proceed = (Button) findViewById(R.id.proceed);
        textTitle = (TextView) findViewById(R.id.textTitle);
        btnCartCount = (Button) findViewById(R.id.shoppingcart_text);
        btnShopList = (Button) findViewById(R.id.btnShopList);
        btnShopList.setVisibility(View.VISIBLE);
        cartTotalPrice = (TextView) findViewById(R.id.price);
        textTitle.setText(title);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(1);
        backButton.setOnClickListener(this);
        btnShopList.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        linearShopCart.setOnClickListener(this);


        String currency = prefManager.getSharedValue(AppConstant.CURRENCY);


        if (currency.contains("\\")) {
            rupee.setText(Util.unescapeJavaString(currency));
        }
        else {
            rupee.setText(currency);
        }

        checkCartValue();
        Category category = appDb.getCategoryById(id);
        subCategoryList = category.getSubCategoryList();
        if (subCategoryList != null && subCategoryList.size() != 0) {
            setupTab(subCategoryList);
        }

        if (subCategoryList.size() <= 1) {
            tabLayout.setVisibility(View.GONE);
        }
    }

    public void setupTab(List<SubCategory> subCategoryList) {

        for (int i = 0; i < subCategoryList.size(); i++) {
            SubCategory subCategory = subCategoryList.get(i);
            tabLayout.addTab(tabLayout.newTab().setTag(subCategory.getId()).setText(subCategory.getTitle()));
        }
        if (subCategoryList.size() > 2) {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        }

        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), subCategoryList);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Category category = appDb.getCategoryById(id);
        subCategoryList = category.getSubCategoryList();
        if (subCategoryList != null && subCategoryList.size() != 0) {
            adapter.update(subCategoryList);
        }
        adapter.notifyDataSetChanged();
        onCartChangeListener();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(CategoryDetailJewellersActivity.this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.backButton:
                onBackPressed();
                break;
            case R.id.linearShopCart:
                openShopCartActivity();
                break;
            case R.id.proceed:
//                openShopCartActivity();
                break;

            case R.id.btnSearch:
                startActivity(new Intent(CategoryDetailJewellersActivity.this, AppController.getInstance().getViewController().getSearchActivity()));
                AnimUtil.slideFromRightAnim(CategoryDetailJewellersActivity.this);
                break;

            case R.id.btnShopList:
                startActivity(new Intent(CategoryDetailJewellersActivity.this, ShoppingListActivity.class));
                AnimUtil.slideFromRightAnim(CategoryDetailJewellersActivity.this);
                break;
        }

    }


    //Pager for the viewpager
    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;
        int currentPosition;
        List<SubCategory> subCategoryList;

        public PagerAdapter(FragmentManager fm, int NumOfTabs, List<SubCategory> subCategoryList) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
            this.subCategoryList = subCategoryList;
        }

        public void update(List<SubCategory> subCategoryList) {
            this.subCategoryList = subCategoryList;
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {

            String subCategoryId = subCategoryList.get(position).getId();
            currentPosition = position;
            Bundle arg = new Bundle();
            arg.putInt("position", position);
            arg.putString("subCategoryId", subCategoryId);
            ProductListFragmentJewellers fragment = new ProductListFragmentJewellers();
            fragment.setArguments(arg);
            return fragment;
        }

        public int getCurrentPosition() {
            return currentPosition;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    public void checkCartValue() {
        onCartChangeListener();
    }

    @Override
    public void onCartChangeListener() {
        int cartSize = appDb.getCartSize();
        if (cartSize != 0) {
            btnCartCount.setVisibility(View.VISIBLE);
            btnCartCount.setText(String.valueOf(cartSize));
        } else {
            btnCartCount.setVisibility(View.GONE);
        }
        String totalCartValue = appDb.getCartTotalPrice();
        cartTotalPrice.setText(totalCartValue);
    }

    public void openShopCartActivity() {
        Intent intentShopCartActivity = new Intent(CategoryDetailJewellersActivity.this, ShoppingCartActivity.class);
        startActivity(intentShopCartActivity);
        AnimUtil.slideFromRightAnim(CategoryDetailJewellersActivity.this);
    }

}
