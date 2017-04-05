package com.signity.bonbon.ui.Location;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.model.GetStoreArea;
import com.signity.bonbon.model.GetStoreAreaModel1;

import java.util.List;

public class SelectLocationActivity extends AppCompatActivity implements View.OnClickListener {


    Button submitBtn;
    RelativeLayout citySelect, areaSelect;
    ListView listView;

    Dialog dialog;

    PrefManager prefManager;
    AppDatabase appDb;

    public GetStoreArea storeArea;
    List<GetStoreAreaModel1> areas;
//    ListCityAdapter adapter;
//    ListAreaAdapter mAdapter;

    TextView cityName, areaName;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_select_location);
//        prefManager = new PrefManager(this);
//        appDb = DbAdapter.getInstance().getDb();
//        pushClientManager = new GCMClientManager(this, AppConstant.PROJECT_NUMBER);
//
//        storeArea= DataAdapter.getInstance().getStoreArea();
//
//
//
//        citySelect = (RelativeLayout) findViewById(R.id.citySelect);
//        areaSelect = (RelativeLayout) findViewById(R.id.areaSelect);
//        submitBtn = (Button) findViewById(R.id.submitBtn);
//        cityName=(TextView)findViewById(R.id.cityValue);
//        areaName=(TextView)findViewById(R.id.areaValue);
//
//
//        if(storeArea==null){
//            getAllAreaDetail();
//        }
//
//        citySelect.setOnClickListener(this);
//        areaSelect.setOnClickListener(this);
//        submitBtn.setOnClickListener(this);
//
//
//    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

//            case R.id.citySelect:
//
//                makeDialogLayout();
//
//                adapter = new ListCityAdapter(SelectLocationActivity.this, storeArea.getData());
//                listView.setAdapter(adapter);
//
//                break;
//
//            case R.id.areaSelect:
//
//                makeDialogLayout();
//                mAdapter = new ListAreaAdapter(SelectLocationActivity.this, areas);
//                listView.setAdapter(mAdapter);
//
//                break;
//
//            case R.id.submitBtn:
//                getMainActivity();
//                break;

        }

    }


//    private void getAllAreaDetail() {
//
//        ProgressDialogUtil.showProgressDialog(SelectLocationActivity.this);
//
//        NetworkAdaper.getInstance().getNetworkServices().getStoreAreaList(new Callback<GetStoreArea>() {
//            @Override
//            public void success(GetStoreArea getStoreArea, Response response) {
//                if (getStoreArea.getSuccess()) {
//
//                    if (getStoreArea.getData() != null && getStoreArea.getData().size() != 0) {
//                        ProgressDialogUtil.hideProgressDialog();
//                        storeArea=getStoreArea;
//
//                    } else {
//                        ProgressDialogUtil.hideProgressDialog();
//                        getMainActivity();
//                    }
//                } else {
//                    ProgressDialogUtil.hideProgressDialog();
//                    getMainActivity();
//                }
//
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                ProgressDialogUtil.hideProgressDialog();
//            }
//        });
//    }
//
//    private void getMainActivity() {
//        Intent intent_home = new Intent(SelectLocationActivity.this, MainActivity.class);
//        startActivity(intent_home);
//        finish();
//    }
//
//    private void makeDialogLayout() {
//
//        if (dialog == null) {
//            dialog = new Dialog(SelectLocationActivity.this);
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//            dialog.setContentView(R.layout.activity_city_select);
//            listView = (ListView) dialog.findViewById(R.id.listView);
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.show();
//        }
//
//    }
//
//    class ListCityAdapter extends BaseAdapter {
//
//        List<GetStoreAreaModel> storeModelList;
//        Context context;
//        LayoutInflater inflater;
//
//        public ListCityAdapter(Context context, List<GetStoreAreaModel> storeModelList) {
//            super();
//            this.storeModelList = storeModelList;
//            this.context = context;
//            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        }
//
//
//        @Override
//        public int getCount() {
//            return storeModelList.size();
//        }
//
//
//        @Override
//        public Object getItem(int position) {
//            return storeModelList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(final int position, View convertView,
//                            ViewGroup parent) {
//            ViewHolder holder = null;
//            if (convertView == null) {
//                convertView = inflater.inflate(R.layout.layout_row_country, null);
//                holder = new ViewHolder();
//                holder.name = (TextView) convertView.findViewById(R.id.textView1);
//                holder.parent = (RelativeLayout) convertView.findViewById(R.id.parent);
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//
//            final GetStoreAreaModel model = storeModelList.get(position);
//            final String city = model.getCity().getCity();
//
//            holder.name.setText(city.equalsIgnoreCase("") ? "":city);
//
//            holder.parent.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    areas = model.getArea();
//                    dialog.dismiss();
//                    dialog=null;
//                    cityName.setText(""+city);
//                }
//            });
//
//            return convertView;
//        }
//
//        class ViewHolder {
//            TextView name;
//            RelativeLayout parent;
//        }
//
//    }
//
//
//    class ListAreaAdapter extends BaseAdapter {
//
//        List<GetStoreAreaModel1> areas;
//        Context context;
//        LayoutInflater inflater;
//
//        public ListAreaAdapter(Context context, List<GetStoreAreaModel1> areas) {
//            super();
//            this.areas = areas;
//            this.context = context;
//            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        }
//
//
//        @Override
//        public int getCount() {
//            return areas.size();
//        }
//
//
//        @Override
//        public Object getItem(int position) {
//            return areas.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(final int position, View convertView,
//                            ViewGroup parent) {
//            ViewHolder holder = null;
//            if (convertView == null) {
//                convertView = inflater.inflate(R.layout.layout_row_country, null);
//                holder = new ViewHolder();
//                holder.name = (TextView) convertView.findViewById(R.id.textView1);
//                holder.parent = (RelativeLayout) convertView.findViewById(R.id.parent);
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//
//
//            final String area = areas.get(position).getArea();
//
//            holder.name.setText(area.equalsIgnoreCase("")? "":area);
//
//
//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//
//                    prefManager.storeBoolean(AppConstant.AREA_SELECTED, true);
//                    prefManager.storeSharedValue(AppConstant.SELECTED_STORE, areas.get(position).getStoreId());
//                    prefManager.storeSharedValue(AppConstant.AREA_NAME, areas.get(position).getArea());
//                    dialog.dismiss();
//                    dialog=null;
//                    areaName.setText(""+area);
//
//                }
//            });
//
//            return convertView;
//        }
//
//
//        class ViewHolder {
//            TextView name;
//            RelativeLayout parent;
//        }
//
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//    }
}
