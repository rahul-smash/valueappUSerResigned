package com.signity.bonbon.ui.restaurant.storetheme11.fragment;

import android.app.PendingIntent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.app.ViewController;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.model.Store;

/**
 * Created by root on 5/2/16.
 */
public class HomeFragmentTheme11 extends Fragment implements View.OnClickListener {


    TextView imgCategory,imgBookNow,imgContact,imgOffers,imgMyOrder;

    RelativeLayout relCategory, relOffers, relBookNow, relMyOrders, relContact;
//    Button buttonCart;
    View mView;
    String storeId;
    String userId;
    Store store;
    AppDatabase appDb;
    PrefManager prefManager;
    private PendingIntent pendingIntent;

    ViewController viewController;

    Typeface font_icon;

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        viewController = AppController.getInstance().getViewController();
//        appDb = DbAdapter.getInstance().getDb();
//        prefManager = new PrefManager(getActivity());
//        storeId = prefManager.getSharedValue(AppConstant.STORE_ID);
//        userId = prefManager.getSharedValue(AppConstant.ID);
//        store = appDb.getStore(storeId);
//        if (!prefManager.isCartSetLocalNotification()) {
//            setUpLocalNotificationForCart();
//        }
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        mView = inflater.inflate(viewController.getHomeResourceLayout(), container, false);
//        relCategory = (RelativeLayout) mView.findViewById(R.id.relCategory);
//        relOffers = (RelativeLayout) mView.findViewById(R.id.relOffers);
//        relBookNow = (RelativeLayout) mView.findViewById(R.id.relBookNow);
//        relMyOrders = (RelativeLayout) mView.findViewById(R.id.relMyOrders);
//        relContact = (RelativeLayout) mView.findViewById(R.id.relContact);
//
//        initialize();
//
////        relMyCart = (RelativeLayout) mView.findViewById(R.id.relMyCart);
////        buttonCart = (Button) mView.findViewById(R.id.buttonCart);
//        relCategory.setOnClickListener(this);
//        relOffers.setOnClickListener(this);
//        relBookNow.setOnClickListener(this);
//        relMyOrders.setOnClickListener(this);
//        relContact.setOnClickListener(this);
////        relMyCart.setOnClickListener(this);
//        store = appDb.getStore(storeId);
//        return mView;
//    }
//
//    private void initialize() {
//        font_icon = FontUtil.getTypeface(getActivity(),FontUtil.ICON_SET);
//        imgCategory=(TextView)mView.findViewById(R.id.imgCategory);
//        imgBookNow=(TextView)mView.findViewById(R.id.imgBookNow);
//        imgContact=(TextView)mView.findViewById(R.id.imgContact);
//        imgOffers=(TextView)mView.findViewById(R.id.imgOffers);
//        imgMyOrder=(TextView)mView.findViewById(R.id.imgMyOrder);
//
//        imgCategory.setTypeface(font_icon);
//        imgBookNow.setTypeface(font_icon);
//        imgContact.setTypeface(font_icon);
//        imgOffers.setTypeface(font_icon);
//        imgMyOrder.setTypeface(font_icon);
//
//        imgCategory.setText("\uE900");
//        imgBookNow.setText("\uE904");
//        imgContact.setText("\uE905");
//        imgOffers.setText("\uE903");
//        imgMyOrder.setText("\uE901");
//    }
//
//    private void checkCartCount() {
//        int cartSize = appDb.getCartSize();
//    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        checkCartCount();
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

//            case R.id.relCategory:
//                startActivity(new Intent(getActivity(), CategoryActivity.class));
//                AnimUtil.slideFromRightAnim(getActivity());
//                break;
//            case R.id.relOffers:
//                startActivity(new Intent(getActivity(), OfferListActivity.class));
//                AnimUtil.slideFromRightAnim(getActivity());
//                break;
//            case R.id.relBookNow:
//                Intent intentBookmark = new Intent(getActivity(), BookNowActivity.class);
//                intentBookmark.putExtra(AppConstant.FROM, "home");
//                startActivity(intentBookmark);
//                AnimUtil.slideFromRightAnim(getActivity());
//                break;
//            case R.id.relMyOrders:
//                Intent intent = new Intent(getActivity(), DeliveryAreaActivity.class);
//                startActivity(intent);
//                AnimUtil.slideFromRightAnim(getActivity());
//                break;
//            case R.id.relContact:
//                startActivity(new Intent(getActivity(), ContactActivity.class));
//                AnimUtil.slideFromRightAnim(getActivity());
//                break;
//            case R.id.relMyCart:
//                openShopCartActivity();
//                break;

        }
    }

//    public void openShopCartActivity() {
//        Intent intentShopCartActivity = new Intent(getActivity(), ShoppingCartActivity.class);
//        startActivity(intentShopCartActivity);
//        AnimUtil.slideFromRightAnim(getActivity());
//    }
//
//    private void setUpLocalNotificationForCart() {
//        Intent myIntent = new Intent(getActivity(), NotifyService.class);
//        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Activity.ALARM_SERVICE);
//        pendingIntent = PendingIntent.getService(getActivity(), 0, myIntent, 0);
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, 20);
//        calendar.set(Calendar.MINUTE, 00);
//        calendar.set(Calendar.SECOND, 00);
//        Log.e("Date", calendar.getTime().toString());
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);  //set repeating every 24 hours
//        // Ask our service to set an alarm for that date, this activity talks to the client that talks to the service
//        prefManager.setCartLocalNotification(true);
//    }


}
