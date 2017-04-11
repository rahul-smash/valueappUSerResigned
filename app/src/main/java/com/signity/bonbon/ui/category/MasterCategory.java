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
import com.signity.bonbon.adapter.MasterCategoryListAdapter;
import com.signity.bonbon.adapter.RvGridSpacesItemDecoration;
import com.signity.bonbon.app.AppController;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.app.ViewController;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.model.Category;
import com.signity.bonbon.model.GetCategory;
import com.signity.bonbon.network.NetworkAdaper;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MasterCategory extends FragmentActivity implements View.OnClickListener {

    RelativeLayout menu_layout;
    Button btnBack, btnSearch;
    TextView title;
    TextView noRecord;

    public Typeface _ProximaNovaLight, _ProximaNovaSemiBold;

    List<Category> listCategory;
    List<Category> listMasterCategory;

    AppDatabase appDb;

    String appVersion, olderVersion;
    PrefManager prefManager;

    DisplayMetrics metrics;
    int screenHeight;
    int screenWidth;

    private boolean isTypeList = false;

    private RecyclerView recyclerView;

    private MasterCategoryListAdapter adapter;

    GridLayoutManager mGridLayoutManager;
    LinearLayoutManager mLinearLayoutManager;
    RvGridSpacesItemDecoration decoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_category);
        getDisplayMetrics();
        appDb = DbAdapter.getInstance().getDb();
        prefManager = new PrefManager(this);
        appVersion = prefManager.getSharedValue(AppConstant.APP_VERISON);
        olderVersion = prefManager.getSharedValue(AppConstant.APP_OLD_VERISON);

        isTypeList = prefManager.getSharedValue(AppConstant.CATEGORY_LAYOUT_TYPE).isEmpty() ? false :
                prefManager.getSharedValue(AppConstant.CATEGORY_LAYOUT_TYPE).equalsIgnoreCase("1") ? true : false;

        _ProximaNovaLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        _ProximaNovaSemiBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        btnBack = (Button) findViewById(R.id.backButton);
        btnBack.setOnClickListener(this);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        title = (TextView) findViewById(R.id.textTitle);
        noRecord = (TextView) findViewById(R.id.no_record);
        title.setText("Categories");
        title.setTypeface(_ProximaNovaLight);
        setUpRecylerView();

        getCategoryList();

        setUpRecylerViewItemClickListener();

    }


    private void setUpRecylerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new MasterCategoryListAdapter(MasterCategory.this, new ArrayList<Category>(), isTypeList);
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

        adapter.setOnItemClickListener(new MasterCategoryListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Category category = listMasterCategory.get(position);

                if (category.getSubCategoryList() != null && category.getSubCategoryList().size() != 0) {

                    getCategoryList(category.getId(), category.getTitle());

                } else {
                    showAlertDialog(MasterCategory.this, "Message", "No Category exists.");
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

        ProgressDialogUtil.showProgressDialog(MasterCategory.this);
        NetworkAdaper.getInstance().getNetworkServices().getCategoryList(new Callback<GetCategory>() {
            @Override
            public void success(GetCategory getCategory, Response response) {
                if (getCategory.getSuccess()) {
//                    appDb.addCategoryList(getCategory.getData());
                    listMasterCategory = getCategory.getData();
                    if (listMasterCategory != null && listMasterCategory.size() != 0) {
                        adapter = new MasterCategoryListAdapter(MasterCategory.this, listMasterCategory, isTypeList);
                        recyclerView.setAdapter(adapter);
                        noRecord.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        noRecord.setVisibility(View.VISIBLE);
                    }
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
                DialogHandler dialogHandler = new DialogHandler(MasterCategory.this);
                dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), false);
            }
        });
    }

    public void getCategoryList(final String categoryId, final String title) {

        ProgressDialogUtil.showProgressDialog(MasterCategory.this);
        NetworkAdaper.getInstance().getNetworkServices().getCategoryList(categoryId, new Callback<GetCategory>() {
            @Override
            public void success(GetCategory getCategory, Response response) {
                if (getCategory.getSuccess()) {
                    appDb.addCategoryList(getCategory.getData(), categoryId);
                    listCategory = appDb.getCategoryList(categoryId);
                    if (listCategory != null && listCategory.size() != 0) {

                        if (listCategory.size() == 1) {
                            Category category = listCategory.get(0);
                            if (category.getSubCategoryList() != null && category.getSubCategoryList().size() != 0) {
                                ViewController viewController = AppController.getInstance().getViewController();
                                Intent i = new Intent(MasterCategory.this, viewController.getCategoryDetailActivity());
                                i.putExtra("categoryId", category.getId());
                                i.putExtra("title", category.getTitle());
                                startActivity(i);
                                AnimUtil.slideFromRightAnim(MasterCategory.this);

                            }
                        } else {
                            Intent i = new Intent(MasterCategory.this, CategoryActivity.class);
                            i.putExtra("categoryId", categoryId);
                            i.putExtra("title", title);
                            startActivity(i);
                            AnimUtil.slideFromRightAnim(MasterCategory.this);
                        }
                    } else {
                    }
                    prefManager.storeSharedValue(AppConstant.APP_OLD_VERISON, appVersion);
                    ProgressDialogUtil.hideProgressDialog();
                } else {
                    ProgressDialogUtil.hideProgressDialog();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                DialogHandler dialogHandler = new DialogHandler(MasterCategory.this);
                dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), false);
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(MasterCategory.this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backButton:
                onBackPressed();
                break;
            case R.id.btnSearch:
                startActivity(new Intent(MasterCategory.this, AppController.getInstance().getViewController().getSearchActivity()));
                AnimUtil.slideFromRightAnim(MasterCategory.this);
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
