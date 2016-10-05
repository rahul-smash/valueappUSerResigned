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
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.model.AddressSelectData;
import com.signity.bonbon.model.AddressSelectModel;
import com.signity.bonbon.model.GetStoreArea;
import com.signity.bonbon.model.GetStoreAreaModel;
import com.signity.bonbon.model.GetStoreAreaModel1;
import com.signity.bonbon.model.GetStoreAreaModel2;
import com.signity.bonbon.network.NetworkAdaper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AddressSelectActivity extends FragmentActivity {


    EditText editInputSearch;
    ListView listView;
    List<AddressSelectData> list;
    String typeNameValue, typeName, id;
    String cityId, stateId, areaId;
    String cityName, stateName, areaName;

    ListCityAdapter cityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_address_select);
//        setupRetrofitClient();
        listView = (ListView) findViewById(R.id.list_view);
        editInputSearch = (EditText) findViewById(R.id.search_view);
        typeName = getIntent().getStringExtra("typeName");
        typeNameValue = getIntent().getStringExtra("typeNameValue");
        id = getIntent().getStringExtra("id");


        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ((TextView) findViewById(R.id.textTitle)).setText(typeName);

        getStoreArea();

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    private void getStoreArea() {

        ProgressDialogUtil.showProgressDialog(AddressSelectActivity.this);

        Map<String, String> param = new HashMap<String, String>();

        param.put("typeName", typeNameValue);
        param.put("id", id);
        list = new ArrayList<>();
        NetworkAdaper.getInstance().apiService.getAddressData(param, typeName, new Callback<AddressSelectModel>() {

            @Override
            public void success(AddressSelectModel addressSelectModel, Response response) {

                if (addressSelectModel.getSuccess()) {

                    if (addressSelectModel.getData() != null && addressSelectModel.getData().size() != 0) {
                        setupList(addressSelectModel.getData());
                    }
                } else {
                    DialogHandler dialogHandler = new DialogHandler(AddressSelectActivity.this);
                    dialogHandler.setdialogForFinish("Message", "" + addressSelectModel.getMessage(), false);
                }

                ProgressDialogUtil.hideProgressDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
            }
        });
    }

    private void setupList(List<AddressSelectData> list) {

        cityAdapter = new ListCityAdapter(AddressSelectActivity.this, list);
        listView.setAdapter(cityAdapter);
        addSearchTextChangeListnerForCity();

    }

    public void addSearchTextChangeListnerForCity() {
        editInputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

                List<AddressSelectData> filterList = new ArrayList<AddressSelectData>();
                for (int i = 0; i < list.size(); i++) {

                    String city = filterList.get(i).getTypeName().toLowerCase();
                    if (city.contains(s.toString().toLowerCase())) {
                        filterList.add(list.get(i));
                    }

                }

                if (s.length() == 0) {
                    cityAdapter.update(list);
                } else {
                    if (filterList != null && filterList.size() != 0) {
                        cityAdapter.update(filterList);
                    } else {
                        AddressSelectData model = new AddressSelectData();
                        model.setTypeName("No Data Found");
                        model.setId("");
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        AnimUtil.slidDownAnim(CityAreaActivitiy.this);
    }


    class ListCityAdapter extends BaseAdapter {

        List<AddressSelectData> list;
        Context context;
        LayoutInflater inflater;

        public ListCityAdapter(Context context, List<AddressSelectData> list) {
            super();
            this.list = list;
            this.context = context;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public int getCount() {
            return list.size();
        }

        public void update(List<AddressSelectData> areas) {
            this.list = areas;
            notifyDataSetChanged();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
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

            final AddressSelectData model = list.get(position);
            holder.name.setText(""+model.getTypeName());
            holder.parent.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    String id = model.getId();
                    String typeName = model.getTypeName();
                    String areaName = null;
                    try {
                        areaName = (!model.getArea().isEmpty()? model.getArea():"");
                    } catch (Exception e) {
                        e.printStackTrace();
                        areaName="";
                    }
                    Bundle b = new Bundle();
                    b.putString("id", id);
                    b.putString("typeName", typeName);
                    b.putString("areaName", areaName);
                    Intent i = getIntent(); // gets the intent that called this
                    i.putExtras(b);
                    setResult(Activity.RESULT_OK, i);
                    finish();
                    AnimUtil.slidDownAnim(AddressSelectActivity.this);
                }
            });
            return convertView;
        }

        public class ViewHolder {
            TextView name;
            RelativeLayout parent;
        }

    }


}
