package com.signity.bonbon.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.FontUtil;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.model.HomeObject;
import com.signity.bonbon.model.Store;
import com.signity.bonbon.ui.book.BookNowActivity;
import com.signity.bonbon.ui.category.CategoryActivity;
import com.signity.bonbon.ui.contacts.ContactActivity;
import com.signity.bonbon.ui.login.LoginScreenActivity;
import com.signity.bonbon.ui.offer.OfferListActivity;
import com.signity.bonbon.ui.order.OrderListActivity;
import com.signity.bonbon.ui.shopcart.ShoppingCartActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment_ extends Fragment implements View.OnClickListener {

    Button btnShopNow;
    ImageView imageView;
    GridView mGridView;
    GridViewAdapter adapter;
    Integer[] images = {R.drawable.categories, R.drawable.offers, R.drawable.shopping, R.drawable.order,
            R.drawable.contact, R.drawable.basket};
    String[] categories = {"CATEGORIES", "OFFERS", "BOOK NOW", "MY ORDERS", "CONTACT", "MY CART"};
    ArrayList<HomeObject> viewList = new ArrayList<HomeObject>();

    String storeId;
    String userId;
    Store store;
    AppDatabase appDb;
    PrefManager prefManager;
    public Typeface _ProximaNovaLight, _ProximaNovaSemiBold;
    View mView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDb = DbAdapter.getInstance().getDb();
        prefManager = new PrefManager(getActivity());
        storeId = prefManager.getSharedValue(AppConstant.STORE_ID);
        userId = prefManager.getSharedValue(AppConstant.ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.home_activity_, container, false);

        initialize();
        _ProximaNovaLight = FontUtil.
                getTypeface(getActivity(), FontUtil.FONT_ROBOTO_REGULAR);
        _ProximaNovaSemiBold = FontUtil.getTypeface(getActivity(), FontUtil.FONT_ROBOTO_BOLD);

        for (int i = 0; i < images.length; i++) {
            HomeObject att = new HomeObject();
            att.categories = categories[i];
            att.image = images[i];
            viewList.add(att);
        }

        adapter = new GridViewAdapter(getActivity(), viewList);
        mGridView.setAdapter(adapter);
        btnShopNow.setOnClickListener(this);
        return mView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnShopNow:
                startActivity(new Intent(getActivity(), CategoryActivity.class));
                AnimUtil.slideFromRightAnim(getActivity());
                break;

        }
    }


    private void initialize() {
        mGridView = (GridView) mView.findViewById(R.id.gridlist);
        imageView = (ImageView) mView.findViewById(R.id.imageView);
        store = appDb.getStore(storeId);
        if (store != null) {
            String banner = store.getBanner();
            if (store.getBanner() != null && !store.getBanner().isEmpty()) {
                Picasso.with(getActivity()).load(banner).error(R.drawable.no_image).into(imageView);
            } else {
                imageView.setImageResource(R.drawable.no_image);
            }
        } else {
            imageView.setImageResource(R.drawable.no_image);
        }
        btnShopNow = (Button) mView.findViewById(R.id.btnShopNow);
    }


    class GridViewAdapter extends BaseAdapter {
        Activity context;
        LayoutInflater l;
        List<HomeObject> list;

        public GridViewAdapter(Activity context, List<HomeObject> list) {
            this.list = list;
            this.context = context;
            l = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return list.size();
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
            holder = null;
            if (convertView == null) {
                convertView = l.inflate(R.layout.home_activity_child, null);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                holder.categories = (TextView) convertView.findViewById(R.id.category_name);
                holder.categories.setTypeface(_ProximaNovaLight);
                holder.coming = (TextView) convertView.findViewById(R.id.coming_soon);
                holder.coming.setTypeface(_ProximaNovaLight);
                holder.buttonCart = (Button) convertView.findViewById(R.id.buttonCart);
                holder.buttonCart.setTypeface(_ProximaNovaLight);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.categories.setText(list.get(position).categories);
            holder.image.setBackgroundResource(list.get(position).image);


//            if (position == 1) {
//                holder.coming.setVisibility(View.VISIBLE);
//            } else {
//                holder.coming.setVisibility(View.GONE);
//            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 0) {
                        startActivity(new Intent(getActivity(), CategoryActivity.class));
                        AnimUtil.slideFromRightAnim(getActivity());
                    } else if (position == 2) {
                        startActivity(new Intent(getActivity(), BookNowActivity.class));
                        AnimUtil.slideFromRightAnim(getActivity());
                    } else if (position == 3) {

                        if (userId.isEmpty()) {
                            Intent intent = new Intent(getActivity(), LoginScreenActivity.class);
                            intent.putExtra(AppConstant.FROM, "menu");
                            startActivity(intent);
                            AnimUtil.slideUpAnim(getActivity());
                        } else {
                            startActivity(new Intent(getActivity(), OrderListActivity.class));
                            AnimUtil.slideFromRightAnim(getActivity());
                        }


                    } else if (position == 1) {
                        startActivity(new Intent(getActivity(), OfferListActivity.class));
                        AnimUtil.slideFromRightAnim(getActivity());
                    } else if (position == 5) {
                        openShopCartActivity();
                    } else if (position == 4) {
                        startActivity(new Intent(getActivity(), ContactActivity.class));
                        AnimUtil.slideFromRightAnim(getActivity());
                    }

                }
            });


            return convertView;
        }


        class ViewHolder {
            ImageView image;
            TextView categories;
            TextView coming;
            Button buttonCart;

        }
    }

    public void openShopCartActivity() {
        Intent intentShopCartActivity = new Intent(getActivity(), ShoppingCartActivity.class);
        startActivity(intentShopCartActivity);
        AnimUtil.slideFromRightAnim(getActivity());
    }


}
