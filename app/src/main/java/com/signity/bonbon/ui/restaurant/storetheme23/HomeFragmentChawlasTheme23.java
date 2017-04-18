package com.signity.bonbon.ui.restaurant.storetheme23;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by root on 17/3/16.
 */
public class HomeFragmentChawlasTheme23 extends Fragment implements View.OnClickListener {

/*    RelativeLayout relCategory, relOffers, relHistory, relContact;
    Button buttonCart;
    ImageButton cartBtn;
    View mView;
    String storeId;
    String userId;
    Store store;
    AppDatabase appDb;
    PrefManager prefManager;
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
        relHistory = (RelativeLayout) mView.findViewById(R.id.relHistory);
        relContact = (RelativeLayout) mView.findViewById(R.id.relContact);
        cartBtn=(ImageButton)mView.findViewById(R.id.cartBtn);
        buttonCart = (Button) mView.findViewById(R.id.buttonCart);
        relCategory.setOnClickListener(this);
        relOffers.setOnClickListener(this);
        relHistory.setOnClickListener(this);
        relContact.setOnClickListener(this);
        cartBtn.setOnClickListener(this);
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
    }*/

    @Override
    public void onClick(View v) {
       /* switch (v.getId()) {

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
            case R.id.relHistory:

                if (userId.isEmpty()) {
                    Intent intent = new Intent(getActivity(), LoginScreenActivity.class);
                    intent.putExtra(AppConstant.FROM, "menu");
                    startActivity(intent);
                    AnimUtil.slideUpAnim(getActivity());
                } else {
                    Intent intent = new Intent(getActivity(), OrderListActivity.class);
                    intent.putExtra(AppConstant.IS_HEADER,true);
                    startActivity(intent);
                    AnimUtil.slideFromRightAnim(getActivity());
                }

                break;
            case R.id.relContact:
                startActivity(new Intent(getActivity(), ContactActivity.class));
                AnimUtil.slideFromRightAnim(getActivity());
                break;
            case R.id.relMyCart:
                openShopCartActivity();
                break;

            case R.id.cartBtn:
                openShopCartActivity();
                break;
        }*/
    }

    /*public void openShopCartActivity() {
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
    }*/


}
