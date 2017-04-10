package com.signity.bonbon.ui.category;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.adapter.RvCategoryListAdapter;
import com.signity.bonbon.adapter.RvGridSpacesItemDecoration;
import com.signity.bonbon.app.AppController;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.app.ViewController;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.ga.GATrackers;
import com.signity.bonbon.model.Category;
import com.signity.bonbon.model.GetCategory;
import com.signity.bonbon.network.NetworkAdaper;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class CategoryActivity extends FragmentActivity implements View.OnClickListener {

    RelativeLayout menu_layout;
    Button btnBack, btnSearch;
    TextView title;
    TextView noRecord;

    public Typeface _ProximaNovaLight, _ProximaNovaSemiBold;

    List<Category> listCategory;

    AppDatabase appDb;

    String appVersion, olderVersion;
    PrefManager prefManager;

    DisplayMetrics metrics;
    int screenHeight;
    int screenWidth;

    private boolean isTypeList = false;

    private RecyclerView recyclerView;

    private RvCategoryListAdapter adapter;

    GridLayoutManager mGridLayoutManager;
    LinearLayoutManager mLinearLayoutManager;
    RvGridSpacesItemDecoration decoration;

    private String categoryId = "", titleString = "Categories";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_activity);
        GATrackers.getInstance().trackScreenView(getString(R.string.ga_screen_category));
        getDisplayMetrics();
        appDb = DbAdapter.getInstance().getDb();
        prefManager = new PrefManager(this);
        appVersion = prefManager.getSharedValue(AppConstant.APP_VERISON);
        olderVersion = prefManager.getSharedValue(AppConstant.APP_OLD_VERISON);

        isTypeList = prefManager.getSharedValue(AppConstant.CATEGORY_LAYOUT_TYPE).isEmpty() ? false :
                prefManager.getSharedValue(AppConstant.CATEGORY_LAYOUT_TYPE).equalsIgnoreCase("1") ? true : false;

        _ProximaNovaLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        _ProximaNovaSemiBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");


        try {
            categoryId = getIntent().getStringExtra("categoryId");
            titleString = getIntent().getStringExtra("title");
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnBack = (Button) findViewById(R.id.backButton);
        btnBack.setOnClickListener(this);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        title = (TextView) findViewById(R.id.textTitle);
        noRecord = (TextView) findViewById(R.id.no_record);
        title.setText(titleString);
        title.setTypeface(_ProximaNovaLight);
        setUpRecylerView();


        listCategory = appDb.getCategoryList(categoryId);

        if (appVersion.equals(olderVersion)) {


            if (listCategory != null && listCategory.size() != 0) {
                adapter = new RvCategoryListAdapter(CategoryActivity.this, listCategory, isTypeList);
//                adapter = new GridViewAdapter(CategoryActivity.this, listCategory);
                recyclerView.setAdapter(adapter);
            } else {
                getCategoryList();
            }
        } else {
            getCategoryList();
        }

        setUpRecylerViewItemClickListener();

    }


    private void setUpRecylerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new RvCategoryListAdapter(CategoryActivity.this, new ArrayList<Category>(), isTypeList);
        mGridLayoutManager = new GridLayoutManager(this, 2); // (Context context, int spanCount)
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_view_spacing);
        decoration = new RvGridSpacesItemDecoration(spacingInPixels);
        recyclerView.addItemDecoration(decoration);
        if (isTypeList) {
            recyclerView.setLayoutManager(mLinearLayoutManager);
        } else {
            recyclerView.setLayoutManager(mGridLayoutManager);
        }


    }

    private void setUpRecylerViewItemClickListener() {

        adapter.setOnItemClickListener(new RvCategoryListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Category category = listCategory.get(position);
                if (category.getSubCategoryList() != null && category.getSubCategoryList().size() != 0) {
                    ViewController viewController = AppController.getInstance().getViewController();
                    String cat = getString(R.string.ga_catagory_click);
                    String action = getString(R.string.app_name) + "-" + getString(R.string.ga_action_category_item_click);
                    GATrackers.getInstance().trackEvent(cat, action,
                            category.getTitle());
                    Intent i = new Intent(CategoryActivity.this, viewController.getCategoryDetailActivity());
                    i.putExtra("categoryId", category.getId());
                    i.putExtra("title", category.getTitle());
                    startActivity(i);
                    AnimUtil.slideFromRightAnim(CategoryActivity.this);
                } else {
                    showAlertDialog(CategoryActivity.this, "Message", "No Subcategories exists!");
                }
            }
        });
    }


    private void getDisplayMetrics() {
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;
        Log.e("Screen dimention ", screenWidth + " x " + screenHeight);
    }


    public void getCategoryList() {

        ProgressDialogUtil.showProgressDialog(CategoryActivity.this);
        NetworkAdaper.getInstance().getNetworkServices().getCategoryList(categoryId, new Callback<GetCategory>() {
            @Override
            public void success(GetCategory getCategory, Response response) {
                if (getCategory.getSuccess()) {
                    appDb.addCategoryList(getCategory.getData(), categoryId);
                    listCategory = appDb.getCategoryList(categoryId);
                    if (listCategory != null && listCategory.size() != 0) {
                        adapter = new RvCategoryListAdapter(CategoryActivity.this, listCategory, isTypeList);
                        recyclerView.setAdapter(adapter);
                        noRecord.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        noRecord.setVisibility(View.VISIBLE);
                    }
                    prefManager.storeSharedValue(AppConstant.APP_OLD_VERISON, appVersion);
                    ProgressDialogUtil.hideProgressDialog();
                } else {
                    ProgressDialogUtil.hideProgressDialog();
                    recyclerView.setVisibility(View.GONE);
                    noRecord.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                DialogHandler dialogHandler = new DialogHandler(CategoryActivity.this);
                dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(CategoryActivity.this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backButton:
                onBackPressed();
                break;
            case R.id.btnSearch:
                startActivity(new Intent(CategoryActivity.this, AppController.getInstance().getViewController().getSearchActivity()));
                AnimUtil.slideFromRightAnim(CategoryActivity.this);
                break;
        }
    }

    public void showAlertDialog(Context context, String title,
                                String message) {
        final DialogHandler dialogHandler = new DialogHandler(context);

        dialogHandler.setDialog(title, message);
        dialogHandler.setPostiveButton("OK", true)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogHandler.dismiss();
                    }
                });
    }
}
