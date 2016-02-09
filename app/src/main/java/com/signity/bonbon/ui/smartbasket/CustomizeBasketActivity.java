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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.signity.bonbon.ui.shopcart.ShoppingCartActivity;

import java.util.ArrayList;
import java.util.List;


public class CustomizeBasketActivity extends Activity {

    ImageButton search, shopingcart, back;
    TextView title, done_button;
    ListView items_list;
    Adapter adapter;
    Integer[] items = {};
    String[] items_name = {"Amul Butter", "Saffola Masala Oats", "Fun Foods Peanut Butter", "Saffola Masala Oats", "Saffola Masala Oats", "Saffola Masala Oats", "Saffola Masala Oats"};
    String[] items_price = {"95.00", "95.00", "95.00", "95.00", "95.00", "95.00", "95.00"};
    ArrayList<CustomizeBasketObject> viewList = new ArrayList<CustomizeBasketObject>();

    public Typeface _ProximaNovaLight, _ProximaNovaSemiBold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.signity.bonbon.R.layout.customize_basket);
        initialize();

        _ProximaNovaLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        _ProximaNovaSemiBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        for (int i = 0; i < items.length; i++) {
            CustomizeBasketObject att = new CustomizeBasketObject();
            att.items = items[i];
            att.items_name = items_name[i];
            att.items_price = items_price[i];
            viewList.add(att);
        }

        adapter = new Adapter(CustomizeBasketActivity.this, viewList);
        items_list.setAdapter(adapter);


        shopingcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {

                    Intent i = new Intent(CustomizeBasketActivity.this, ShoppingCartActivity.class);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                }
            }
        });

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomizeBasketActivity.this, SmartBasketActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    private void initialize() {
        items_list = (ListView) findViewById(com.signity.bonbon.R.id.items_list);
        search = (ImageButton) findViewById(com.signity.bonbon.R.id.search);
        shopingcart = (ImageButton) findViewById(com.signity.bonbon.R.id.shopingcart);
        done_button = (TextView) findViewById(com.signity.bonbon.R.id.done_button);
        done_button.setTypeface(_ProximaNovaLight);
        title = (TextView) findViewById(com.signity.bonbon.R.id.title);
        title.setTypeface(_ProximaNovaSemiBold);
        back = (ImageButton) findViewById(com.signity.bonbon.R.id.menu);
        back.setBackgroundResource(com.signity.bonbon.R.drawable.left);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dp2px(16), dp2px(25));
        layoutParams.setMargins(dp2px(10), 0, 0, 0);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        back.setLayoutParams(layoutParams);
        title.setText("Customize Basket");
        shopingcart.setVisibility(View.VISIBLE);

    }

    class Adapter extends BaseAdapter {
        Activity context;
        LayoutInflater l;
        List<CustomizeBasketObject> list;
        public int count = 0;
        String[] weights = new String[]{"40 gms", "80 gms"};

        public Adapter(Activity context, List<CustomizeBasketObject> list) {
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
                convertView = l.inflate(com.signity.bonbon.R.layout.customize_basket_child, null);
                holder = new ViewHolder();
                holder.items = (ImageView) convertView.findViewById(com.signity.bonbon.R.id.items_image);
                holder.items_name = (TextView) convertView.findViewById(com.signity.bonbon.R.id.items_name);
                holder.items_name.setTypeface(_ProximaNovaSemiBold);
                holder.items_price = (TextView) convertView.findViewById(com.signity.bonbon.R.id.items_price);
                holder.items_price.setTypeface(_ProximaNovaLight);
                holder.spinner = (Spinner) convertView.findViewById(com.signity.bonbon.R.id.spinner);
                holder.add_button = (ImageButton) convertView.findViewById(com.signity.bonbon.R.id.add_button);
                holder.remove_button = (ImageButton) convertView.findViewById(com.signity.bonbon.R.id.remove_button);
                holder.number_text = (TextView) convertView.findViewById(com.signity.bonbon.R.id.number_text);
                holder.number_text.setTypeface(_ProximaNovaLight);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(CustomizeBasketActivity.this, android.R.layout.simple_spinner_item, weights);
            holder.spinner.setAdapter(adapter);

            holder.items.setBackgroundResource(list.get(position).items);
            holder.items_name.setText(list.get(position).items_name);
            holder.items_price.setText(list.get(position).items_price);
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

            return convertView;
        }


        class ViewHolder {
            ImageView items;
            Spinner spinner;
            TextView items_name, items_price, number_text;
            ImageButton add_button, remove_button;
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CustomizeBasketActivity.this, SmartBasketActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }

}
