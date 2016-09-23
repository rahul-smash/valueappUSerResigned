package com.signity.bonbon.ui.restaurant.storetheme16;

import android.app.PendingIntent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.app.ViewController;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.model.Store;

/**
 * Created by root on 22/2/16.
 */
public class HomeFragmentTheme16 extends Fragment implements View.OnClickListener {


    ImageView imageViewCategories, imageViewOffers, imageViewDelivery, imageViewOrder, imageViewContact, imageViewCart,imageViewRing;
    Button buttonCart;
    View mView;
    String storeId;
    String userId;
    Store store;
    AppDatabase appDb;
    PrefManager prefManager;
    private PendingIntent pendingIntent;
    ViewController viewController;

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
//
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        mView = inflater.inflate(viewController.getHomeResourceLayout(), container, false);
//        imageViewCategories = (ImageView) mView.findViewById(R.id.imageViewCategories);
//        imageViewCategories.setVisibility(View.INVISIBLE);
//
//        imageViewOffers = (ImageView) mView.findViewById(R.id.imageViewOffers);
//        imageViewOffers.setVisibility(View.INVISIBLE);
//
//        imageViewDelivery = (ImageView) mView.findViewById(R.id.imageViewDelivery);
//        imageViewDelivery.setVisibility(View.INVISIBLE);
//
//        imageViewOrder = (ImageView) mView.findViewById(R.id.imageViewOrder);
//        imageViewOrder.setVisibility(View.INVISIBLE);
//
//        imageViewContact = (ImageView) mView.findViewById(R.id.imageViewContact);
//        imageViewContact.setVisibility(View.INVISIBLE);
//
//        imageViewCart = (ImageView) mView.findViewById(R.id.imageViewCart);
//        imageViewCart.setVisibility(View.INVISIBLE);
//        buttonCart = (Button) mView.findViewById(R.id.buttonCart);
//        buttonCart.setVisibility(View.INVISIBLE);
//        imageViewCategories.setOnClickListener(this);
//        imageViewOffers.setOnClickListener(this);
//        imageViewDelivery.setOnClickListener(this);
//        imageViewOrder.setOnClickListener(this);
//        imageViewContact.setOnClickListener(this);
//        imageViewCart.setOnClickListener(this);
//        store = appDb.getStore(storeId);
//        imageViewRing=(ImageView)mView.findViewById(R.id.imageViewRing);
//        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_with_bounce_anim);
//        animation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                buttonCart.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                buttonCart.setVisibility(View.GONE);
//                homeIconAnimation();
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        imageViewRing.startAnimation(animation);
//
//
//        return mView;
//    }
//
//    private void homeIconAnimation() {
//        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_with_bounce_anim);
//        animation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                imageViewCategories.setVisibility(View.VISIBLE);
//                imageViewOffers.setVisibility(View.VISIBLE);
//                imageViewDelivery.setVisibility(View.VISIBLE);
//                imageViewOrder.setVisibility(View.VISIBLE);
//                imageViewContact.setVisibility(View.VISIBLE);
//                imageViewCart.setVisibility(View.VISIBLE);
//                buttonCart.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                int cartSize = appDb.getCartSize();
//                if (cartSize != 0) {
//                    buttonCart.setVisibility(View.VISIBLE);
//                    buttonCart.setText(String.valueOf(cartSize));
//                } else {
//                    buttonCart.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        imageViewCategories.startAnimation(animation);
//        imageViewOffers.startAnimation(animation);
//        imageViewDelivery.startAnimation(animation);
//        imageViewOrder.startAnimation(animation);
//        imageViewContact.startAnimation(animation);
//        imageViewCart.startAnimation(animation);
//    }
//
//
//    private void checkCartCount() {
//
//        int cartSize = appDb.getCartSize();
//        if (cartSize != 0) {
//            buttonCart.setVisibility(View.VISIBLE);
//            buttonCart.setText(String.valueOf(cartSize));
//        } else {
//            buttonCart.setVisibility(View.GONE);
//        }
//    }
//
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        checkCartCount();
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

//            case R.id.imageViewCategories:
//                startActivity(new Intent(getActivity(), CategoryActivity.class));
//                AnimUtil.slideFromRightAnim(getActivity());
//                break;
//            case R.id.imageViewOffers:
//                startActivity(new Intent(getActivity(), OfferListActivity.class));
//                AnimUtil.slideFromRightAnim(getActivity());
//                break;
//            case R.id.imageViewDelivery:
//                Intent intent = new Intent(getActivity(), DeliveryAreaActivity.class);
//                startActivity(intent);
//                AnimUtil.slideFromRightAnim(getActivity());
//
//                break;
//            case R.id.imageViewOrder:
////                Intent intent = new Intent(getActivity(), DeliveryAreaActivity.class);
////                startActivity(intent);
////                AnimUtil.slideFromRightAnim(getActivity());
//                if (userId.isEmpty()) {
//                    Intent intentLogin = new Intent(getActivity(), LoginScreenActivity.class);
//                    intentLogin.putExtra(AppConstant.FROM, "menu");
//                    startActivity(intentLogin);
//                    AnimUtil.slideUpAnim(getActivity());
//                } else {
//                    Intent intentOrder = new Intent(getActivity(), OrderListActivity.class);
//                    intentOrder.putExtra(AppConstant.IS_HEADER, true);
//                    startActivity(intentOrder);
//                    AnimUtil.slideFromRightAnim(getActivity());
//                }
//
//                break;
//            case R.id.imageViewContact:
//                startActivity(new Intent(getActivity(), ContactActivity.class));
//                AnimUtil.slideFromRightAnim(getActivity());
//                break;
//            case R.id.imageViewCart:
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
