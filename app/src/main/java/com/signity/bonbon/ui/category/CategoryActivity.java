package com.signity.bonbon.ui.category;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.app.AppController;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.app.ViewController;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.model.Category;
import com.signity.bonbon.model.GetCategory;
import com.signity.bonbon.network.NetworkAdaper;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class CategoryActivity extends FragmentActivity implements View.OnClickListener {

    RelativeLayout menu_layout;
    Button btnBack, btnSearch;
    TextView title;
    TextView noRecord;
    GridView mGridView;

    GridViewAdapter adapter;
    public Typeface _ProximaNovaLight, _ProximaNovaSemiBold;

    List<Category> listCategory;

    AppDatabase appDb;

    String appVersion, olderVersion;
    PrefManager prefManager;

    DisplayMetrics metrics;
    int screenHeight;
    int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_activity);
        getDisplayMetrics();
        appDb = DbAdapter.getInstance().getDb();
        prefManager = new PrefManager(this);
        appVersion = prefManager.getSharedValue(AppConstant.APP_VERISON);
        olderVersion = prefManager.getSharedValue(AppConstant.APP_OLD_VERISON);

        _ProximaNovaLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        _ProximaNovaSemiBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        mGridView = (GridView) findViewById(R.id.gridlist);
        btnBack = (Button) findViewById(R.id.backButton);
        btnBack.setOnClickListener(this);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        title = (TextView) findViewById(R.id.textTitle);
        noRecord = (TextView) findViewById(R.id.no_record);
        title.setText("Categories");
        title.setTypeface(_ProximaNovaLight);
        listCategory = appDb.getCategoryList();

        Log.e("Version", appVersion + " " + olderVersion);
        if (appVersion.equals(olderVersion)) {
            if (listCategory != null && listCategory.size() != 0) {
                adapter = new GridViewAdapter(CategoryActivity.this, listCategory);
                mGridView.setAdapter(adapter);
            } else {
                getCategoryList();
            }
        } else {
            getCategoryList();
        }

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

        NetworkAdaper.getInstance().getNetworkServices().getCategoryList(new Callback<GetCategory>() {
            @Override
            public void success(GetCategory getCategory, Response response) {

                if (getCategory.getSuccess()) {
                    appDb.addCategoryList(getCategory.getData());
                    listCategory = appDb.getCategoryList();
                    if (listCategory != null && listCategory.size() != 0) {
                        adapter = new GridViewAdapter(CategoryActivity.this, listCategory);
                        mGridView.setAdapter(adapter);
                        noRecord.setVisibility(View.GONE);
                        mGridView.setVisibility(View.VISIBLE);
                    } else {
                        mGridView.setVisibility(View.GONE);
                        noRecord.setVisibility(View.VISIBLE);
                    }
                    prefManager.storeSharedValue(AppConstant.APP_OLD_VERISON, appVersion);
                    ProgressDialogUtil.hideProgressDialog();
                } else {
                    ProgressDialogUtil.hideProgressDialog();
                    mGridView.setVisibility(View.GONE);
                    noRecord.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
            }
        });
    }

    class GridViewAdapter extends BaseAdapter {
        Activity context;
        LayoutInflater l;
        List<Category> listCategory;
        LinearLayout.LayoutParams layoutParamsImageView;

        public GridViewAdapter(Activity context, List<Category> listCategory) {
            this.listCategory = listCategory;
            this.context = context;
            l = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layoutParamsImageView = new LinearLayout.LayoutParams((screenWidth / 2) - 10, (screenHeight / 4));
            Log.e("Image dimenstion ", layoutParamsImageView.width + " x " + layoutParamsImageView.height);
        }

        @Override
        public int getCount() {
            return listCategory.size();
        }

        @Override
        public Object getItem(int position) {
            return listCategory.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        ViewHolder holder;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            holder = null;
            if (convertView == null) {
                convertView = l.inflate(R.layout.categories_activity_child, null);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                holder.image.setLayoutParams(layoutParamsImageView);
                holder.text = (TextView) convertView.findViewById(R.id.text_name);
                holder.text.setTypeface(_ProximaNovaLight);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Category category = listCategory.get(position);

            holder.text.setText(category.getTitle());

//            holder.image.setImageResource(images[0]);

            if (category.getImageMedium() != null && !category.getImageMedium().isEmpty()) {
                Picasso.with(CategoryActivity.this).load(category.getImage()).
                        resize(300, 300).error(R.drawable.no_image).into(holder.image);
            } else {
                holder.image.setImageResource(R.drawable.no_image);
            }
//            holder.image.setImageResource(images[new Random(images.length).nextInt() - 1]);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (category.getSubCategoryList() != null && category.getSubCategoryList().size() != 0) {
                        ViewController viewController = AppController.getInstance().getViewController();
                        Intent i = new Intent(CategoryActivity.this, viewController.getCategoryDetailActivity());
                        i.putExtra("categoryId", category.getId());
                        i.putExtra("title", category.getTitle());
                        startActivity(i);
                        AnimUtil.slideFromRightAnim(CategoryActivity.this);
                    } else {
                        showAlertDialog(CategoryActivity.this, "Alert", "No Subcategories available");
                    }
                }
            });
            return convertView;
        }

        class ViewHolder {
            ImageView image;
            TextView text;
        }
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
        dialogHandler.setPostiveButton("Ok", true)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogHandler.dismiss();
                    }
                });
    }
}
