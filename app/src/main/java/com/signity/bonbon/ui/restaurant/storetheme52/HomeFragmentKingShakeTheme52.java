package com.signity.bonbon.ui.restaurant.storetheme52;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.FixedSpeedScroller;
import com.signity.bonbon.Utilities.GsonHelper;
import com.signity.bonbon.Utilities.PageIndicator;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.app.AppController;
import com.signity.bonbon.app.DataAdapter;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.app.ViewController;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.model.Banner;
import com.signity.bonbon.model.Category;
import com.signity.bonbon.model.GetSubCategory;
import com.signity.bonbon.model.Product;
import com.signity.bonbon.model.Store;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.service.NotifyService;
import com.signity.bonbon.ui.Delivery.DeliveryAreaActivity;
import com.signity.bonbon.ui.book.BookNowActivity;
import com.signity.bonbon.ui.category.MasterCategory;
import com.signity.bonbon.ui.contacts.ContactActivity;
import com.signity.bonbon.ui.login.LoginScreenActivity;
import com.signity.bonbon.ui.offer.OfferListActivity;
import com.signity.bonbon.ui.shopcart.ShoppingCartActivity;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by root on 28/9/16.
 */
public class HomeFragmentKingShakeTheme52 extends Fragment implements View.OnClickListener{



    ImageButton callBtn, orderBtn, offersBtn;
    ImageView contactBtn, bookNowBtn;
    View mView;
    String storeId;
    String userId;
    Store store;
    AppDatabase appDb;
    PrefManager prefManager;
    private PendingIntent pendingIntent;
    ViewController viewController;
    ViewPager viewPager;
    CustomPagerAdapter customPagerAdapter;
    int currentPage = 0;
    GsonHelper gsonHelper;
    private List<Banner> banners;
    private PageIndicator pageIndicator;
    DisplayMetrics metrics;
    int screenHeight;
    int screenWidth;
    RelativeLayout bannerLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewController = AppController.getInstance().getViewController();
        appDb = DbAdapter.getInstance().getDb();
        prefManager = new PrefManager(getActivity());
        gsonHelper = new GsonHelper();
        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;
        storeId = prefManager.getSharedValue(AppConstant.STORE_ID);
        userId = prefManager.getSharedValue(AppConstant.ID);
        store = appDb.getStore(storeId);
        if (!prefManager.isCartSetLocalNotification()) {
            setUpLocalNotificationForCart();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(viewController.getHomeResourceLayout(), container, false);
        viewPager = (ViewPager) mView.findViewById(R.id.pager);
        orderBtn = (ImageButton) mView.findViewById(R.id.orderBtn);
        callBtn = (ImageButton) mView.findViewById(R.id.callBtn);
        offersBtn = (ImageButton) mView.findViewById(R.id.offersBtn);
        bookNowBtn = (ImageView) mView.findViewById(R.id.bookNowBtn);
        contactBtn = (ImageView) mView.findViewById(R.id.contactBtn);
        pageIndicator = (PageIndicator) mView.findViewById(R.id.pageIndicator);
        RelativeLayout.LayoutParams layoutParamsImageView = new RelativeLayout.LayoutParams((screenWidth ), (screenHeight / 3)+60);
        bannerLayout=(RelativeLayout)mView.findViewById(R.id.bannerLayout);
        bannerLayout.setLayoutParams(layoutParamsImageView);
        orderBtn.setOnClickListener(this);
        offersBtn.setOnClickListener(this);
        bookNowBtn.setOnClickListener(this);
        contactBtn.setOnClickListener(this);
        callBtn.setOnClickListener(this);





        banners = DataAdapter.getInstance().getBanners();
        if (banners != null && banners.size() != 0) {

//            viewPager.setPageMargin(-60);
            pageIndicator.setActiveDot(0);
            pageIndicator.setDotSpacing(10);
            pageIndicator.setTotalNoOfDots(banners.size());
            customPagerAdapter = new CustomPagerAdapter(getActivity(), banners);
            viewPager.setAdapter(customPagerAdapter);
//           viewPager.setPageTransformer(true,new ZoomOutPageTransformer());

            try {
                Field mScroller;
                mScroller = ViewPager.class.getDeclaredField("mScroller");
                mScroller.setAccessible(true);
                FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext(), new AccelerateInterpolator());
                // scroller.setFixedDuration(5000);
                mScroller.set(viewPager, scroller);
            } catch (NoSuchFieldException e) {
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }


            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {
                    if (viewPager.getCurrentItem() == (banners.size() - 1)) {
                        currentPage = 0;
                        viewPager.setCurrentItem(currentPage++, false);
                    } else {
                        viewPager.setCurrentItem(currentPage++, true);
                    }

                }
            };

            Timer swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    handler.post(Update);
                }
            }, 5000, 4000);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                pageIndicator.setActiveDot(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        store = appDb.getStore(storeId);



        store = appDb.getStore(storeId);
        return mView;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.orderBtn:
                startActivity(new Intent(getActivity(), MasterCategory.class));
                AnimUtil.slideFromRightAnim(getActivity());
                break;
            case R.id.offersBtn:
                startActivity(new Intent(getActivity(), OfferListActivity.class));
                AnimUtil.slideFromRightAnim(getActivity());
                break;
            case R.id.bookNowBtn:
                startActivity(new Intent(getActivity(), BookNowActivity.class));
                AnimUtil.slideFromRightAnim(getActivity());
                break;
            case R.id.relMyOrders:
                Intent intent = new Intent(getActivity(), DeliveryAreaActivity.class);
                startActivity(intent);
                AnimUtil.slideFromRightAnim(getActivity());

                break;
            case R.id.contactBtn:
                startActivity(new Intent(getActivity(), ContactActivity.class));
                AnimUtil.slideFromRightAnim(getActivity());
                break;
            case R.id.callBtn:
                PackageManager pm = getActivity().getBaseContext().getPackageManager();
                if (pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                    Intent callINtent = new Intent(Intent.ACTION_DIAL);
                    callINtent.setData(Uri.parse("tel:" + store.getContactNumber()));
                    startActivity(callINtent);
                    AnimUtil.slideFromRightAnim(getActivity());
                } else {
                    Toast.makeText(getActivity(), getString(R.string.msg_toast_calling_not_supported), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.relMyCart:
                openShopCartActivity();
                break;

        }
    }

    public int convertDpToPixels(float dp, Context context) {
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.getDisplayMetrics()
        );
    }

    public void showAlertDialog(Context context, String title,
                                String message) {
        final DialogHandler dialogHandler = new DialogHandler(context);

        dialogHandler.setDialog(title, message);
        dialogHandler.setPostiveButton(getString(R.string.str_lbl_ok), true)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogHandler.dismiss();
                    }
                });
    }


    private void callNetworkForProductDetail(String productId) {

        ProgressDialogUtil.showProgressDialog(getActivity());
        Map<String, String> param = new HashMap<String, String>();
//        Log.e("id", id);
        param.put("product_id", productId);

        NetworkAdaper.getInstance().getNetworkServices().getProductDetails(param, new Callback<GetSubCategory>() {

            @Override
            public void success(GetSubCategory getSubCategory, Response response) {
                if (getSubCategory.getSuccess()) {
                    ProgressDialogUtil.hideProgressDialog();
                    Product product = getSubCategory.getData().get(0).getProducts().get(0);

                    String productString = gsonHelper.getProduct(product);
                    prefManager.storeSharedValue(PrefManager.PREF_SEARCH_PRODUCT, productString);
                    Intent i = new Intent(getActivity(), AppController.getInstance().getViewController().getProductViewActivity());
                    i.putExtra("product_id", product.getId());
                    i.putExtra("productViewTitle", getSubCategory.getData().get(0).getTitle());
                    startActivity(i);
                    AnimUtil.slideFromRightAnim(getActivity());

                } else {
                    ProgressDialogUtil.hideProgressDialog();
//                    Toast.makeText(getActivity(), "No Data found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                DialogHandler dialogHandler = new DialogHandler(getActivity());
                dialogHandler.setdialogForFinish(getString(R.string.msg_dialog_error), getResources().getString(R.string.error_code_message), false);
            }
        });


    }

    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;
        List<Banner> banners;

        public CustomPagerAdapter(Context context, List<Banner> banners) {
            this.banners = banners;
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return banners.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

            final Banner banner = banners.get(position);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);


            try {
                Picasso.with(getActivity()).load(banners.get(position).getImage()).fit().error(R.mipmap.ic_launcher).into(imageView);
            } catch (Exception e) {
            }


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!banner.getLinkTo().isEmpty()) {
                        if (banner.getLinkTo().equalsIgnoreCase("category")) {

                            if (!banner.getCategoryId().equalsIgnoreCase("0")) {

                                if (!banner.getSubCategoryId().equalsIgnoreCase("0")) {

                                    if (!banner.getProductId().equalsIgnoreCase("0")) {
                                        callNetworkForProductDetail(banner.getProductId());
                                    } else {
                                        String catId = banner.getCategoryId();
                                        Category category = appDb.getCategoryById(catId);

                                        if (category != null) {

                                            if (category.getSubCategoryList() != null && category.getSubCategoryList().size() != 0) {
                                                ViewController viewController = AppController.getInstance().getViewController();
                                                Intent i = new Intent(getActivity(), viewController.getCategoryDetailActivity());
                                                i.putExtra("categoryId", category.getId());
                                                i.putExtra("title", category.getTitle());
                                                i.putExtra("subCategoryId", banner.getSubCategoryId());
                                                startActivity(i);
                                                AnimUtil.slideFromRightAnim(getActivity());
                                            } else {
//                                                    showAlertDialog(getActivity(), "Alert", "No Subcategories available");
                                            }
                                        }
                                    }

                                } else {

                                    String catId = banner.getCategoryId();
                                    Category category = appDb.getCategoryById(catId);

                                    if (category != null) {
                                        if (category.getSubCategoryList() != null && category.getSubCategoryList().size() != 0) {
                                            ViewController viewController = AppController.getInstance().getViewController();
                                            Intent i = new Intent(getActivity(), viewController.getCategoryDetailActivity());
                                            i.putExtra("categoryId", category.getId());
                                            i.putExtra("title", category.getTitle());
                                            startActivity(i);
                                            AnimUtil.slideFromRightAnim(getActivity());
                                        } else {
                                            showAlertDialog(getActivity(), getString(R.string.msg_dialog_alert), getString(R.string.msg_dialog_no_subcategories));
                                        }
                                    }
                                }
                            }
                        } else if (banner.getLinkTo().equalsIgnoreCase("product")) {
                            if (!banner.getProductId().equalsIgnoreCase("0")) {
                                callNetworkForProductDetail(banner.getProductId());
                            }
                        } else if (banner.getLinkTo().equalsIgnoreCase("offers")) {
                            if (!banner.getOfferId().equalsIgnoreCase("0")) {
                                if (userId.isEmpty()) {
                                    Intent intent = new Intent(getActivity(), LoginScreenActivity.class);
                                    intent.putExtra(AppConstant.FROM, "menu");
                                    startActivity(intent);
                                    AnimUtil.slideUpAnim(getActivity());
                                } else {
                                    startActivity(new Intent(getActivity(), OfferListActivity.class));
                                    AnimUtil.slideFromRightAnim(getActivity());
                                }
                            }
                        } else if (banner.getLinkTo().equalsIgnoreCase("pages")) {
                            /*if (!banner.getPagesId().equalsIgnoreCase("0")) {
                            }*/
                        } else if (banner.getLinkTo().equalsIgnoreCase("")) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(banner.getLink()));
                            startActivity(browserIntent);
                        }
                    } else {
                        if (!banner.getLink().isEmpty()) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(banner.getLink()));
                            startActivity(browserIntent);
                        }
                    }

                }
            });

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }



    public void openShopCartActivity() {
        Intent intentShopCartActivity = new Intent(getActivity(), ShoppingCartActivity.class);
        startActivity(intentShopCartActivity);
        AnimUtil.slideFromRightAnim(getActivity());
    }

    private void setUpLocalNotificationForCart() {
        Intent myIntent = new Intent(getActivity(), NotifyService.class);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Activity.ALARM_SERVICE);
        pendingIntent = PendingIntent.getService(getActivity(), 0, myIntent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        Log.e("Date", calendar.getTime().toString());
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);  //set repeating every 24 hours
        // Ask our service to set an alarm for that date, this activity talks to the client that talks to the service
        prefManager.setCartLocalNotification(true);
    }



}
