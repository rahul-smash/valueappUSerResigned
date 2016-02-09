package com.signity.bonbon.ui.Delivery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.model.GetStoreArea;
import com.signity.bonbon.model.GetStoreAreaModel;
import com.signity.bonbon.model.GetStoreAreaModel1;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.common.CityAreaActivitiy;

import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DeliveryAreaActivity  extends AppCompatActivity implements View.OnClickListener {

    private static final int CITY = 328;

    TextView textCity, noRecord, txtSelectCity;
    Button backButton;
    ListView listView;
    private String cityId;
    private String cityName;

    private HashMap<String, List<GetStoreAreaModel1>> mapStoreArea;

    private ListCityAreaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_area);
        txtSelectCity = (TextView) findViewById(R.id.city_name);
        listView = (ListView) findViewById(R.id.listView);
        noRecord = (TextView) findViewById(R.id.no_record);
        txtSelectCity.setOnClickListener(this);
        textCity = (TextView) findViewById(R.id.city);
        ((Button) findViewById(R.id.backButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mapStoreArea = new HashMap<>();
        getAllAreaDetail();
    }

    private void getAllAreaDetail() {

        ProgressDialogUtil.showProgressDialog(DeliveryAreaActivity.this);

        NetworkAdaper.getInstance().apiService.getStoreAreaList(new Callback<GetStoreArea>() {
            @Override
            public void success(GetStoreArea getStoreArea, Response response) {
                setUpStoreMap(getStoreArea);
                ProgressDialogUtil.hideProgressDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CITY) {
            if (resultCode == Activity.RESULT_OK) {
                String code = data.getExtras().getString("code");
                String title = data.getExtras().getString("title");
                if (code != null) {
                    cityId = code;
                    cityName = title;
                    txtSelectCity.setText(title);
                    textCity.setText(title);
                    if (mapStoreArea != null && mapStoreArea.size() != 0) {
                        List<GetStoreAreaModel1> list = mapStoreArea.get(code);
                        adapter = new ListCityAreaAdapter(DeliveryAreaActivity.this, list);
                        listView.setAdapter(adapter);
                        listView.setVisibility(View.VISIBLE);
                        noRecord.setVisibility(View.GONE);
                    } else {
                        listView.setVisibility(View.GONE);
                        noRecord.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.city_name:
                Intent intentCity = new Intent(DeliveryAreaActivity.this,
                        CityAreaActivitiy.class);
                intentCity.putExtra("key", "city");
                intentCity.putExtra("title", "City");
                startActivityForResult(intentCity, CITY);
                break;
        }
    }

    public void setUpStoreMap(GetStoreArea getStoreArea) {
        List<GetStoreAreaModel> list = getStoreArea.getData();
        for (GetStoreAreaModel getStoreAreaModel : list) {
            String cityId = getStoreAreaModel.getCity().getId();
            mapStoreArea.put(cityId, getStoreAreaModel.getArea());
        }
    }


    class ListCityAreaAdapter extends BaseAdapter {

        List<GetStoreAreaModel1> cityArea;
        Context context;
        LayoutInflater inflater;

        public ListCityAreaAdapter(Context context, List<GetStoreAreaModel1> cityArea) {
            super();
            this.cityArea = cityArea;
            this.context = context;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public int getCount() {
            return cityArea.size();
        }

        public void update(List<GetStoreAreaModel1> cityArea) {
            this.cityArea = cityArea;
            notifyDataSetChanged();
        }

        @Override
        public Object getItem(int position) {
            return cityArea.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.layout_row_country,
                        null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView
                        .findViewById(R.id.textView1);
                holder.parent = (RelativeLayout) convertView
                        .findViewById(R.id.parent);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final GetStoreAreaModel1 areaCity = cityArea.get(position);
            holder.name.setText(areaCity.getArea());
            return convertView;
        }

    }

    static class ViewHolder {
        TextView name;
        RelativeLayout parent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(DeliveryAreaActivity.this);
    }
}
