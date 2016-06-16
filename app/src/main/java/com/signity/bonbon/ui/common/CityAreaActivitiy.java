package com.signity.bonbon.ui.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.model.GetStoreArea;
import com.signity.bonbon.model.GetStoreAreaModel;
import com.signity.bonbon.model.GetStoreAreaModel1;
import com.signity.bonbon.model.GetStoreAreaModel2;
import com.signity.bonbon.network.NetworkAdaper;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CityAreaActivitiy extends FragmentActivity {

    EditText editInputSearch;
    ListView listView;
    List<GetStoreAreaModel> areasmain;
    List<GetStoreAreaModel1> cityArea;
    List<GetStoreAreaModel> areamain;
//    ListCountryAdapter adapter;

//    public ApiService apiService;

    String key, title;
    String cityId;
    String cityName;

    ListCityAdapter cityAdapter;
    ListCityAreaAdapter cityAreaAdapter;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_activity_city_area);
//        setupRetrofitClient();
        listView = (ListView) findViewById(R.id.list_view);
        editInputSearch = (EditText) findViewById(R.id.search_view);
        key = getIntent().getStringExtra("key");
        title = getIntent().getStringExtra("title");
        if (key.equals("area")) {
            cityId = getIntent().getStringExtra("city_id");
            cityName = getIntent().getStringExtra("city_name");
        }


        findViewById(R.id.backButton).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ((TextView) findViewById(R.id.textTitle)).setText(title);

        getStoreArea();

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );


    }

//    public void setupRetrofitClient() {
//        OkHttpClient client = new OkHttpClient();
//        client.setConnectTimeout(2, TimeUnit.MINUTES);
//        client.setReadTimeout(2, TimeUnit.MINUTES);
//        RestAdapter restAdapter = new RestAdapter.Builder()
//                .setClient(new OkClient(client)).setEndpoint("http://app.grocersapp.com/2/api").setLogLevel(RestAdapter.LogLevel.FULL)
//                .build();
//        apiService = restAdapter.create(ApiService.class);
//    }

    private void getStoreArea() {

        ProgressDialogUtil.showProgressDialog(CityAreaActivitiy.this);
        areamain = new ArrayList<>();
        NetworkAdaper.getInstance().apiService.getStoreAreaList(new Callback<GetStoreArea>() {

            @Override
            public void success(GetStoreArea getStoreArea, Response response) {

                if (getStoreArea.getSuccess()) {

                    for (int i = 0; i < getStoreArea.getData().size(); i++) {

                        GetStoreAreaModel getStoreAreaModel = new GetStoreAreaModel();
                        getStoreAreaModel = getStoreArea.getData().get(i);
//                        areamain= (List<GetStoreAreaModel>) getStoreArea.getData().get(i);
                        areamain.add(getStoreAreaModel);

                    }
                    setupList(areamain);
                }
                ProgressDialogUtil.hideProgressDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
            }
        });
    }

    private void setupList(List<GetStoreAreaModel> areas) {

        if (key.equals("city")) {
            areasmain = areas;
            cityAdapter = new ListCityAdapter(CityAreaActivitiy.this, areas);
            listView.setAdapter(cityAdapter);
            addSearchTextChangeListnerForCity();
        } else if (key.equals("area")) {
            for (int i = 0; i < areas.size(); i++) {
                if (areas.get(i).getCity().getId().equals(cityId)) {
                    cityArea = areas.get(i).getArea();
                    break;
                }
            }

            if (cityArea != null && cityArea.size() != 0) {
                cityAreaAdapter = new ListCityAreaAdapter(CityAreaActivitiy.this, cityArea);
                listView.setAdapter(cityAreaAdapter);
                addSearchTextChangeListnerForCityArea();
            }


        }


    }

    public void addSearchTextChangeListnerForCity() {
        editInputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

                List<GetStoreAreaModel> filterList = new ArrayList<GetStoreAreaModel>();
                for (int i = 0; i < areasmain.size(); i++) {

                    String city = areasmain.get(i).getCity().getCity().toLowerCase();
                    if (city.contains(s.toString().toLowerCase())) {
                        filterList.add(areasmain.get(i));
                    }

                }

                if (s.length() == 0) {
                    cityAdapter.update(areasmain);
                } else {
                    if(filterList!=null && filterList.size()!=0){
                        cityAdapter.update(filterList);
                    }
                    else {
                        GetStoreAreaModel model=new GetStoreAreaModel();
                        GetStoreAreaModel2 model2=new GetStoreAreaModel2();
                        model2.setCity("No City Found");
                        model.setCity(model2);
                        filterList.add(model);
                        cityAdapter.update(filterList);
                    }


                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub



            }
        });
    }

    public void addSearchTextChangeListnerForCityArea() {
        editInputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

                List<GetStoreAreaModel1> filterList = new ArrayList<GetStoreAreaModel1>();
                for (int i = 0; i < cityArea.size(); i++) {

                    String area = cityArea.get(i).getArea().toLowerCase();
                    if (area.contains(s.toString().toLowerCase())) {
                        filterList.add(cityArea.get(i));
                    }
                }

                if (s.length() == 0) {
                    cityAreaAdapter.update(cityArea);
                } else {

                    if(filterList!=null && filterList.size()!=0){
                        cityAreaAdapter.update(filterList);
                    }else {
                        GetStoreAreaModel1 model1=new GetStoreAreaModel1();
                        model1.setArea("No Area found");
                        filterList.add(model1);
                        cityAreaAdapter.update(filterList);
                    }


                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }


    class ListCityAdapter extends BaseAdapter {

        List<GetStoreAreaModel> areas;
        Context context;
        LayoutInflater inflater;

        public ListCityAdapter(Context context, List<GetStoreAreaModel> areas) {
            super();
            this.areas = areas;
            this.context = context;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public int getCount() {
            return areas.size();
        }

        public void update(List<GetStoreAreaModel> areas) {
            this.areas = areas;
            notifyDataSetChanged();
        }

        @Override
        public Object getItem(int position) {
            return areas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
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

            GetStoreAreaModel areaModel = areas.get(position);
            final GetStoreAreaModel2 city = areaModel.getCity();
            holder.name.setText(city.getCity());
            holder.parent.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    String code = city.getId();
                    String title = city.getCity();
                    Bundle b = new Bundle();
                    b.putString("code", code);
                    b.putString("title", title);
                    Intent i = getIntent(); // gets the intent that called this
                    i.putExtras(b);
                    setResult(Activity.RESULT_OK, i);
                    finish();
                    AnimUtil.slidDownAnim(CityAreaActivitiy.this);
                }
            });
            return convertView;
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
            // TODO Auto-generated method stub
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
            holder.parent.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    String code = areaCity.getAreaId();
                    String title = areaCity.getArea();
                    Bundle b = new Bundle();
                    b.putString("code", code);
                    b.putString("title", title);
                    Intent i = getIntent(); // gets the intent that called this
                    i.putExtras(b);
                    setResult(Activity.RESULT_OK, i);
                    finish();
                    AnimUtil.slidDownAnim(CityAreaActivitiy.this);
                }
            });
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
//        AnimUtil.slidDownAnim(CityAreaActivitiy.this);
    }

}
