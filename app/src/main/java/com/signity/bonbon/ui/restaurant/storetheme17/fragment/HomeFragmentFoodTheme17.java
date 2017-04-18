package com.signity.bonbon.ui.restaurant.storetheme17.fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ZoomOutPageTransformer;
import com.signity.bonbon.app.AppController;
import com.signity.bonbon.app.DataAdapter;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.app.ViewController;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.model.Banner;
import com.signity.bonbon.model.Store;
import com.signity.bonbon.service.NotifyService;
import com.signity.bonbon.ui.Delivery.DeliveryAreaActivity;
import com.signity.bonbon.ui.book.BookNowActivity;
import com.signity.bonbon.ui.category.CategoryActivity;
import com.signity.bonbon.ui.contacts.ContactActivity;
import com.signity.bonbon.ui.offer.OfferListActivity;
import com.signity.bonbon.ui.shopcart.ShoppingCartActivity;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by root on 29/2/16.
 */
public class HomeFragmentFoodTheme17 extends Fragment implements View.OnClickListener {


    RelativeLayout relCategory, relOffers, relBookNow, relMyOrders, relContact, relMyCart;
    Button buttonCart;
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
    private List<Banner> banners;
    int currentPage=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewController = AppController.getInstance().getViewController();
        appDb = DbAdapter.getInstance().getDb();
        prefManager = new PrefManager(getActivity());
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
        banners= DataAdapter.getInstance().getBanners();
        if(banners!=null && banners.size()!=0){

            customPagerAdapter = new CustomPagerAdapter(getActivity(), banners);
            viewPager.setAdapter(customPagerAdapter);
            viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {
                    if (viewPager.getCurrentItem() == (banners.size()-1)) {
                        currentPage = 0;
                        viewPager.setCurrentItem(currentPage++, false);
                    }
                    else
                    {
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
        relCategory = (RelativeLayout) mView.findViewById(R.id.relCategory);
        relOffers = (RelativeLayout) mView.findViewById(R.id.relOffers);
        relBookNow = (RelativeLayout) mView.findViewById(R.id.relBookNow);
        relMyOrders = (RelativeLayout) mView.findViewById(R.id.relMyOrders);
        relContact = (RelativeLayout) mView.findViewById(R.id.relContact);
        relMyCart = (RelativeLayout) mView.findViewById(R.id.relMyCart);
        buttonCart = (Button) mView.findViewById(R.id.buttonCart);
        relCategory.setOnClickListener(this);
        relOffers.setOnClickListener(this);
        relBookNow.setOnClickListener(this);
        relMyOrders.setOnClickListener(this);
        relContact.setOnClickListener(this);
        relMyCart.setOnClickListener(this);
        store = appDb.getStore(storeId);
        return mView;
    }

    private void checkCartCount() {

        int cartSize = appDb.getCartSize();
        if (cartSize != 0) {
            buttonCart.setVisibility(View.VISIBLE);
            buttonCart.setText(String.valueOf(cartSize));
        } else {
            buttonCart.setVisibility(View.GONE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        checkCartCount();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.relCategory:
                startActivity(new Intent(getActivity(), CategoryActivity.class));
                AnimUtil.slideFromRightAnim(getActivity());
                break;
            case R.id.relOffers:
                startActivity(new Intent(getActivity(), OfferListActivity.class));
                AnimUtil.slideFromRightAnim(getActivity());
                break;
            case R.id.relBookNow:
                startActivity(new Intent(getActivity(), BookNowActivity.class));
                AnimUtil.slideFromRightAnim(getActivity());
                break;
            case R.id.relMyOrders:
                Intent intent = new Intent(getActivity(), DeliveryAreaActivity.class);
                startActivity(intent);
                AnimUtil.slideFromRightAnim(getActivity());

                break;
            case R.id.relContact:
                startActivity(new Intent(getActivity(), ContactActivity.class));
                AnimUtil.slideFromRightAnim(getActivity());
                break;
            case R.id.relMyCart:
                openShopCartActivity();
                break;

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

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);




            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            try
            {
                Picasso.with(getActivity()).load(banners.get(position).getImage()).error(R.mipmap.ic_launcher).into(imageView);
            }
            catch (Exception e)
            {

            }

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }


}
