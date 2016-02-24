package com.signity.bonbon.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.FontUtil;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.app.AppController;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.app.ViewController;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.gcm.GCMClientManager;
import com.signity.bonbon.model.GetStoreModel;
import com.signity.bonbon.model.SliderObject;
import com.signity.bonbon.model.Store;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.AboutUs.AboutUsFragment;
import com.signity.bonbon.ui.Delivery.DeliveryActivity;
import com.signity.bonbon.ui.fragment.Profile;
import com.signity.bonbon.ui.login.LoginScreenActivity;
import com.signity.bonbon.ui.order.OrderHistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    SlidingPaneLayout mSlidingPanel;
    ListView mMenuList;
    public TextView title, user;
    ImageButton search, shopingcart;
    Button menu;
    ImageView profilePic;
    public Typeface typeFaceRobotoRegular, typeFaceRobotoBold;
    String[] labels = {"Home", "My Profile", "Delivery Address", "My Order",
            "Book Now", "My Favorites", "About Us", "Share", "Log In"};

    Integer[] icons = {R.drawable.ic_home, R.drawable.profil_icon, R.drawable.address_icon,
            R.drawable.order_icon,
            R.drawable.my_shopping_list_icon, R.drawable.my_fav_icon,
            R.drawable.faq, R.drawable.ic_share, R.drawable.sign_out};
    ArrayList<SliderObject> viewList = new ArrayList<SliderObject>();

    AppDatabase appDb;
    Adapter adapter;
    PrefManager prefManager;
    private GCMClientManager pushClientManager;
    String userId;
    String storeId;

    String name;
    String phone;
    Context context;
    Store store;

    // Controller;
    ViewController viewController;

    boolean isActivityLoadsFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slider_pane);
        context = this;
        prefManager = new PrefManager(MainActivity.this);
        viewController = AppController.getInstance().getViewController();
        pushClientManager = new GCMClientManager(this, AppConstant.PROJECT_NUMBER);
        userId = prefManager.getSharedValue(AppConstant.ID);
        name = prefManager.getSharedValue(AppConstant.NAME);
        phone = prefManager.getSharedValue(AppConstant.PHONE);
        storeId = prefManager.getSharedValue(AppConstant.STORE_ID);

        typeFaceRobotoRegular = FontUtil.getTypeface(context, FontUtil.FONT_ROBOTO_REGULAR);
        typeFaceRobotoBold = FontUtil.getTypeface(context, FontUtil.FONT_ROBOTO_BOLD);

        appDb = DbAdapter.getInstance().getDb();

        mSlidingPanel = (SlidingPaneLayout) findViewById(R.id.SlidingPanel);
        menu = (Button) findViewById(R.id.menu);
        search = (ImageButton) findViewById(R.id.search);
        search.setVisibility(View.GONE);
        title = (TextView) findViewById(R.id.title);
        user = (TextView) findViewById(R.id.user);

        updateUserName();

        title.setTypeface(typeFaceRobotoBold);
        user.setTypeface(typeFaceRobotoRegular);
        mMenuList = (ListView) findViewById(R.id.menulist);
        shopingcart = (ImageButton) findViewById(R.id.shopingcart);
        profilePic = (ImageView) findViewById(R.id.profile_pic);
        labels[4] = AppController.getInstance().getViewController().getMenuTextBookNow();
        for (int i = 0; i < labels.length; i++) {
            SliderObject att = new SliderObject();
            att.labels = labels[i];
            att.icons = icons[i];
            viewList.add(att);
        }


        adapter = new Adapter(MainActivity.this);
        mMenuList.setAdapter(adapter);

        mSlidingPanel.setParallaxDistance(300);
        mSlidingPanel.setSliderFadeColor(200);

        menu.setOnClickListener(this);
        search.setOnClickListener(this);
        profilePic.setOnClickListener(this);
        store = appDb.getStore(storeId);
        updateStoreInfo(store);

        Fragment fragment = viewController.getHomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isActivityLoadsFirstTime) {
            getStoreServicesForUpdate();
        } else {
            isActivityLoadsFirstTime = false;
        }
    }

    private void updateUserName() {

        if (!name.isEmpty()) {
            user.setText(name);
        } else if (!phone.isEmpty()) {
            user.setText(phone);
        } else {
            user.setText("Guest");
        }
    }

    private void updateStoreInfo(Store store) {
        if (store != null) {
            if (store.getStoreName() != null && !store.getStoreName().isEmpty()) {
                title.setText("" + store.getStoreName());
            } else {
                title.setText("");
            }
        } else {
            title.setText("");
        }

    }

    public void toggleSlidingMenu() {
        if (!mSlidingPanel.isOpen()) {
            mSlidingPanel.openPane();
        } else {
            mSlidingPanel.closePane();
        }

        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu:
                toggleSlidingMenu();
                break;
            case R.id.search:
                startActivity(new Intent(MainActivity.this, AppController.getInstance().getViewController().getSearchActivity()));
                AnimUtil.slideFromRightAnim(MainActivity.this);
                break;
            case R.id.profile_pic:
//                clearBackStack();
                replaceFragment(0);
                mSlidingPanel.closePane();
                break;
        }
    }

    private void clearBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }


    private void replaceFragment(int position) {
        Fragment fragment;
        switch (position) {

            case 0:
                if (store != null) {
                    if (store.getStoreName() != null && !store.getStoreName().isEmpty()) {
                        title.setText("" + store.getStoreName());
                    } else {
                        title.setText("");
                    }
                }
                replace(viewController.getHomeFragment());
                break;
            case 1:

                if (userId.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, LoginScreenActivity.class);
                    intent.putExtra(AppConstant.FROM, "menu");
                    startActivity(intent);
                    AnimUtil.slideUpAnim(MainActivity.this);
                } else {
                    title.setText("My Profile");
                    fragment = new Profile();
                    replace(fragment);
                }
                break;

            case 2:
                if (userId.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, LoginScreenActivity.class);
                    intent.putExtra(AppConstant.FROM, "menu");
                    startActivity(intent);
                    AnimUtil.slideUpAnim(MainActivity.this);
                } else {
//                    title.setText("Delivery Address");
                    Intent intentDelivery = new Intent(MainActivity.this, DeliveryActivity.class);
                    intentDelivery.putExtra(AppConstant.FROM, "menu");
                    startActivity(intentDelivery);
                    AnimUtil.slideFromRightAnim(MainActivity.this);
                }
                break;

            case 3:

                if (userId.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, LoginScreenActivity.class);
                    intent.putExtra(AppConstant.FROM, "menu");
                    startActivity(intent);
                    AnimUtil.slideUpAnim(MainActivity.this);
                } else {
                    title.setText("Active Order");
                    fragment = new OrderHistory();
                    replace(fragment);
                }
                break;
            case 4:
                title.setText(AppController.getInstance().getViewController().getMenuTextBookNow());
                replace(AppController.getInstance().getViewController().getBookNowOrShoppinFragment());
                break;

            case 5:
                title.setText("My Favorites");
                replace(AppController.getInstance().getViewController().getFavouritesFragment());
                break;

            case 6:
                title.setText("About Us");
                replace(new AboutUsFragment());

                break;
            case 7:
                String shareContent =
                        "Kindly download " + store.getStoreName() + " app from " +
                                store.getAndroidAppShareLink() + "\nThanks and Regards\n " +
                                store.getStoreName() + "\n" + store.getCity() + " , " + store.getState();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, shareContent);
                intent.putExtra(Intent.EXTRA_SUBJECT, store.getStoreName());
                startActivity(Intent.createChooser(intent, "Share with"));
                break;
            case 8:
                title.setText(store.getStoreName());
                if (userId.isEmpty()) {
                    Intent intentLogin = new Intent(MainActivity.this, LoginScreenActivity.class);
                    intentLogin.putExtra(AppConstant.FROM, "menu");
                    startActivity(intentLogin);
                    AnimUtil.slideUpAnim(MainActivity.this);
                } else {
                    replace(viewController.getHomeFragment());
                    logOutUser();
                }
                adapter.notifyDataSetChanged();
                break;

        }
    }

    private void logOutUser() {
        PrefManager prefManager = new PrefManager(MainActivity.this);
        prefManager.clearSharedValue(AppConstant.ID);
        prefManager.clearSharedValue(AppConstant.PHONE);
        prefManager.clearSharedValue(AppConstant.NAME);
        prefManager.clearSharedValue(AppConstant.EMAIL);
        user.setText("Guest");
        userId = prefManager.getSharedValue(AppConstant.ID);
        appDb.deleteCartElement();

    }

    public void replace(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment).commit();
    }

    class Adapter extends BaseAdapter {
        Activity context;
        LayoutInflater l;
        boolean login = false;

        public Adapter(Activity context) {
            this.context = context;
            l = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        ViewHolder holder;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = l.inflate(R.layout.slider_child, null);
                holder = new ViewHolder();
                holder.icons = (ImageView) convertView.findViewById(R.id.icon);
                holder.labels = (TextView) convertView.findViewById(R.id.labels);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.icons.setImageResource(viewList.get(position).icons);
            holder.labels.setText(viewList.get(position).labels);

            SliderObject att = new SliderObject();

            if (userId.isEmpty()) {
                att.labels = "Log In";
                att.icons = icons[8];
                viewList.set(8, att);
                holder.labels.setText(viewList.get(position).labels);
                login = true;
            } else if (!userId.isEmpty()) {
                att.labels = "Log out";
                att.icons = icons[8];
                viewList.set(8, att);
                holder.labels.setText(viewList.get(position).labels);
                login = false;
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    replaceFragment(position);
                    toggleSlidingMenu();
                }
            });

            return convertView;
        }


        class ViewHolder {
            ImageView icons;
            TextView labels;

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        FragmentManager fm = getSupportFragmentManager();
//        fm.getFragments();
//        fm.getBackStackEntryCount();
        mSlidingPanel.closePane();
    }


    private void getStoreServicesForUpdate() {

        String deviceid = Settings.Secure.getString(MainActivity.this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceToken = pushClientManager.getRegistrationId(MainActivity.this);

        Map<String, String> param = new HashMap<String, String>();
        param.put("device_id", deviceid);
        param.put("device_token", deviceToken);
        param.put("platform", AppConstant.PLATFORM);

        NetworkAdaper.getInstance().getNetworkServices().getStore(param, new Callback<GetStoreModel>() {
            @Override
            public void success(GetStoreModel getStroeModel, Response response) {
                if (getStroeModel.getSuccess()) {
                    appDb.addStore(getStroeModel.getStore());
                    store = getStroeModel.getStore();
                    prefManager.storeSharedValue(AppConstant.STORE_ID, store.getId());
                    prefManager.storeSharedValue(AppConstant.APP_VERISON, store.getVersion());
                    prefManager.setProjectTheme(store.getTheme());
                    prefManager.setProjectType(store.getType());
                    prefManager.setOtoSkip(store.getOtpSkip());
                    String oldVerision = prefManager.getSharedValue(AppConstant.APP_OLD_VERISON);
                    if (oldVerision.isEmpty()) {
                        prefManager.storeSharedValue(AppConstant.APP_OLD_VERISON, store.getVersion());
                    }
                    Log.e("Store Version", store.getVersion());
                    if (!(store.getStoreStatus().equalsIgnoreCase("1"))) {
                        new DialogHandler(MainActivity.this).setdialogForFinish("Error", "Store Under maintainence, please try later", true);
                    }

                } else {
                    Log.e("Error", "Error success false");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("Error", error.getMessage());
            }
        });

    }

}
