package com.signity.bonbon.ui.smartbasket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.signity.bonbon.ui.restaurant.storetangerine9.fragment.HomeFragmentTangerineTheme1;
import com.signity.bonbon.ui.shopcart.ShoppingCartActivity;

import java.util.ArrayList;
import java.util.List;


public class SmartBasketActivity extends Activity {

    ImageButton search, shopingcart, back;
    TextView title;
    ListView items_list;
    Adapter adapter;
    Integer[] items = {};
    String[] items_name = {"Basket Name", "Basket Name", "Basket Name", "Basket Name", "Basket Name", "Basket Name", "Basket Name",};
    double[] items_price = {1200.00, 1200.00, 1200.00, 1200.00, 1200.00, 1200.00, 1200.00};
    ArrayList<SmartBasketObject> viewList = new ArrayList<SmartBasketObject>();

    public Typeface _ProximaNovaLight, _ProximaNovaSemiBold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.signity.bonbon.R.layout.smart_basket);

        initialize();

        _ProximaNovaLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        _ProximaNovaSemiBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        for (int i = 0; i < items.length; i++) {
            SmartBasketObject att = new SmartBasketObject();
            att.items = items[i];
            att.items_name = items_name[i];
            att.items_price = items_price[i];
            viewList.add(att);
        }

        adapter = new Adapter(SmartBasketActivity.this, viewList);
        items_list.setAdapter(adapter);

        shopingcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {

                    Intent i = new Intent(SmartBasketActivity.this, ShoppingCartActivity.class);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                }
            }
        });


    }

    private void initialize() {
        items_list = (ListView) findViewById(com.signity.bonbon.R.id.items_list);
        search = (ImageButton) findViewById(com.signity.bonbon.R.id.search);
        shopingcart = (ImageButton) findViewById(com.signity.bonbon.R.id.shopingcart);
        title = (TextView) findViewById(com.signity.bonbon.R.id.title);
        title.setTypeface(_ProximaNovaLight);
        back = (ImageButton) findViewById(com.signity.bonbon.R.id.menu);
        back.setBackgroundResource(com.signity.bonbon.R.drawable.left);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dp2px(16), dp2px(25));
        layoutParams.setMargins(dp2px(10), 0, 0, 0);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        back.setLayoutParams(layoutParams);
        title.setText("Smart Basket");
        shopingcart.setVisibility(View.VISIBLE);

    }


    class Adapter extends BaseAdapter {
        Activity context;
        LayoutInflater l;
        List<SmartBasketObject> list;
        public int count = 0;


        public Adapter(Activity context, List<SmartBasketObject> list) {
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

            if (convertView == null) {
                convertView = l.inflate(com.signity.bonbon.R.layout.smart_basket_child, null);
                holder = new ViewHolder();
                holder.items = (ImageView) convertView.findViewById(com.signity.bonbon.R.id.items_image);
                holder.items_name = (TextView) convertView.findViewById(com.signity.bonbon.R.id.items_name);
                holder.items_name.setTypeface(_ProximaNovaSemiBold);
                holder.items_price = (TextView) convertView.findViewById(com.signity.bonbon.R.id.items_price);
                holder.items_price.setTypeface(_ProximaNovaLight);
                holder.add_button = (ImageButton) convertView.findViewById(com.signity.bonbon.R.id.add_button);
                holder.remove_button = (ImageButton) convertView.findViewById(com.signity.bonbon.R.id.remove_button);
                holder.number_text = (TextView) convertView.findViewById(com.signity.bonbon.R.id.number_text);
                holder.number_text.setTypeface(_ProximaNovaLight);
                holder.customize_basket = (TextView) convertView.findViewById(com.signity.bonbon.R.id.customize_basket);
                holder.customize_basket.setTypeface(_ProximaNovaLight);
                holder.addtocart = (TextView) convertView.findViewById(com.signity.bonbon.R.id.addtocart);
                holder.addtocart.setTypeface(_ProximaNovaLight);
                holder.heart = (ImageButton) convertView.findViewById(com.signity.bonbon.R.id.heart);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (list.get(position).isSelected) {
                holder.heart.setBackgroundResource(com.signity.bonbon.R.drawable.heart_pink);
            } else {
                holder.heart.setBackgroundResource(com.signity.bonbon.R.drawable.heart);
            }

            holder.items.setBackgroundResource(list.get(position).items);
            holder.number_text.setText("" + list.get(position).count);

            holder.add_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.get(position).count == 0 || list.get(position).count > 0) {
                        list.get(position).count++;
                        holder.number_text.setText("" + list.get(position).count);
                        notifyDataSetChanged();
                    }
                }
            });


            holder.remove_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.get(position).count > 0) {
                        list.get(position).count--;
                        holder.number_text.setText("" + list.get(position).count);
                        notifyDataSetChanged();
                    }
                }
            });

            holder.customize_basket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SmartBasketActivity.this, CustomizeBasketActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                }
            });

            holder.heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.get(position).isSelected) {
                        holder.heart.setBackgroundResource(com.signity.bonbon.R.drawable.heart);
                        list.get(position).isSelected = false;
                        notifyDataSetChanged();
                    } else {
                        holder.heart.setBackgroundResource(com.signity.bonbon.R.drawable.heart_pink);
                        list.get(position).isSelected = true;
                    }
                }
            });

            return convertView;
        }


        class ViewHolder {
            ImageView items;

            TextView items_name, items_price, number_text, customize_basket, addtocart;
            public ImageButton add_button, remove_button, heart;
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SmartBasketActivity.this, HomeFragmentTangerineTheme1.class));
        overridePendingTransition(0, 0);
        finish();
    }
}
