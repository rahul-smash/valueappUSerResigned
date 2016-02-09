package com.signity.bonbon.ui.fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
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


public class HomeFragment extends Fragment implements View.OnClickListener {

    RelativeLayout relCategory, relOffers, relBookNow, relMyOrders, relContact, relMyCart;

    Button buttonCart;
    //Ui Element
    ImageView imageView;
    View mView;
    String storeId;
    String userId;
    Store store;
    AppDatabase appDb;
    PrefManager prefManager;
     PendingIntent pendingIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mView = inflater.inflate(R.layout.home_activity, container, false);
        imageView = (ImageView) mView.findViewById(R.id.imageView);
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
        if (store != null) {
            String banner = store.getBanner();
            if (store.getBanner() != null && !store.getBanner().isEmpty()) {
                Picasso.with(getActivity()).load(banner).resize(500, 500).error(R.drawable.no_image).into(imageView);
            } else {
                imageView.setImageResource(R.drawable.no_image);
            }
        } else {
            imageView.setImageResource(R.drawable.no_image);
        }

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
                Intent intentBookmark = new Intent(getActivity(), BookNowActivity.class);
                intentBookmark.putExtra(AppConstant.FROM, "home");
                startActivity(intentBookmark);
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


}
