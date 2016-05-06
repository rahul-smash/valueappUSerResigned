package com.signity.bonbon.ui.Jewellers.storetheme28;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.app.AppController;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.app.ViewController;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.model.Store;
import com.signity.bonbon.service.NotifyService;
import com.signity.bonbon.ui.category.CategoryActivity;
import com.signity.bonbon.ui.contacts.ContactActivity;
import com.signity.bonbon.ui.login.LoginScreenActivity;
import com.signity.bonbon.ui.offer.OfferListActivity;
import com.signity.bonbon.ui.order.OrderListActivity;
import com.signity.bonbon.ui.shopcart.ShoppingCartActivity;

import java.util.Calendar;

/**
 * Created by root on 5/5/16.
 */
public class HomeFragmentAppleTheme28 extends Fragment implements View.OnClickListener {

    RelativeLayout relCategory, relOffers, relFavorites, relMyOrders, relContact, relMyCart;
    String userId;
    ImageView imageView;
    GridView mGridView;
    String storeId;
    Store store;
    AppDatabase appDb;
    PrefManager prefManager;
    View mView;

    TextView goldPrice;
    Button buttonCart;
    private PendingIntent pendingIntent;
    ViewController viewController;
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
        relCategory = (RelativeLayout) mView.findViewById(R.id.relCategory);
        relOffers = (RelativeLayout) mView.findViewById(R.id.relOffers);
        relMyCart = (RelativeLayout) mView.findViewById(R.id.relMyCart);
        relFavorites = (RelativeLayout) mView.findViewById(R.id.relFavorites);
        relMyOrders = (RelativeLayout) mView.findViewById(R.id.relMyOrders);
        relContact = (RelativeLayout) mView.findViewById(R.id.relContact);
        goldPrice = (TextView) mView.findViewById(R.id.goldPrice);
        buttonCart=(Button)mView.findViewById(R.id.buttonCart);
        relCategory.setOnClickListener(this);
        relOffers.setOnClickListener(this);
        relFavorites.setOnClickListener(this);
        relMyOrders.setOnClickListener(this);
        relContact.setOnClickListener(this);
        relMyCart.setOnClickListener(this);
        store = appDb.getStore(storeId);
        if (!prefManager.isCartSetLocalNotification()) {
            setUpLocalNotificationForCart();
        }



        return mView;
    }

    private void checkCartSize() {
        int cartSize=appDb.getCartSize();
        if(cartSize!=0){
            buttonCart.setVisibility(View.VISIBLE);
            buttonCart.setText(""+cartSize);
        }
        else {
            buttonCart.setVisibility(View.GONE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        checkCartSize();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
            case R.id.relFavorites:
//                startActivity(new Intent(getActivity(), FavoriteFragmentActivity.class));
//                AnimUtil.slideFromRightAnim(getActivity());
//                startActivity(new Intent(getActivity(), BookNowActivity.class));
//                AnimUtil.slideFromRightAnim(getActivity());
                break;
            case R.id.relMyOrders:
                if (userId.isEmpty()) {
                    Intent intent = new Intent(getActivity(), LoginScreenActivity.class);
                    intent.putExtra(AppConstant.FROM, "menu");
                    startActivity(intent);
                    AnimUtil.slideUpAnim(getActivity());
                } else {
                    startActivity(new Intent(getActivity(), OrderListActivity.class));
                    AnimUtil.slideFromRightAnim(getActivity());
                }
//                Intent intent = new Intent(getActivity(), DeliveryAreaActivity.class);
//                startActivity(intent);
//                AnimUtil.slideFromRightAnim(getActivity());

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
