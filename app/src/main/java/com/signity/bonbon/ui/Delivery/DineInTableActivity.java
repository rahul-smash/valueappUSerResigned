package com.signity.bonbon.ui.Delivery;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.adapter.RvCategoryListAdapter;
import com.signity.bonbon.adapter.RvGridSpacesItemDecoration;
import com.signity.bonbon.model.Category;
import com.signity.bonbon.model.DineInModel;
import com.signity.bonbon.model.DineInTableList;
import com.signity.bonbon.model.GetStoreArea;
import com.signity.bonbon.model.GetStoreAreaModel;
import com.signity.bonbon.model.GetStoreModel;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.shopcart.ShoppingCartActivity2;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DineInTableActivity extends AppCompatActivity implements View.OnClickListener {


    ListView tableList;
    TextView no_record;
    ListTableAdapter adapter;

    List<DineInTableList> listTable;
    Button backButton;
    private String userId;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dine_in_table);
        prefManager = new PrefManager(DineInTableActivity.this);
        userId = prefManager.getSharedValue(AppConstant.ID);

        setUpList();

        initListners();

        getTableList();
    }

    private void initListners() {
        backButton.setOnClickListener(this);
    }

    private void setUpList() {


        tableList = (ListView) findViewById(R.id.listView);
        no_record=(TextView) findViewById(R.id.no_record);
        backButton = (Button)findViewById(R.id.backButton);
        adapter = new ListTableAdapter(DineInTableActivity.this, new ArrayList<DineInTableList>());
        tableList.setAdapter(adapter);
    }

    private void getTableList() {

        ProgressDialogUtil.showProgressDialog(DineInTableActivity.this);


        NetworkAdaper.getInstance().getNetworkServices().getDiningTables(new Callback<DineInModel>() {
            @Override
            public void success(DineInModel dineInModel, Response response) {
                ProgressDialogUtil.hideProgressDialog();

                if(dineInModel.getSuccess()){
                    try {
                        listTable=dineInModel.getData();
                        if(listTable!=null && listTable.size()!=0){
                            adapter = new ListTableAdapter(DineInTableActivity.this, listTable);
                            tableList.setAdapter(adapter);
                            tableList.setVisibility(View.VISIBLE);
                            no_record.setVisibility(View.GONE);
                        }
                        else {
                            tableList.setVisibility(View.GONE);
                            no_record.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    tableList.setVisibility(View.GONE);
                    no_record.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                try {
                    DialogHandler dialogHandler = new DialogHandler(DineInTableActivity.this);
                    dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), false);
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.backButton:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimUtil.slideFromLeftAnim(DineInTableActivity.this);
    }

    public class ListTableAdapter extends BaseAdapter {

        List<DineInTableList> storeModelList;
        Context context;
        LayoutInflater inflater;

        public ListTableAdapter(Context context, List<DineInTableList> storeModelList) {
            super();
            this.storeModelList = storeModelList;
            this.context = context;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void update(List<DineInTableList> city) {
            this.storeModelList = city;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return storeModelList.size();
        }


        @Override
        public Object getItem(int position) {
            return storeModelList.get(position);
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
                convertView = inflater.inflate(R.layout.adapter_dine_child_layout, null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.textView1);
                holder.parent = (RelativeLayout) convertView.findViewById(R.id.parent);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final DineInTableList model = storeModelList.get(position);
            final String table = model.getName();

            holder.name.setText(table.equalsIgnoreCase("") ? "" : table);

            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DineInTableActivity.this, ShoppingCartActivity2.class);
                    intent.putExtra("dine_in", "yes");
                    intent.putExtra("dining_table", ""+model.getId());
                    intent.putExtra("addressId", "363");
                    intent.putExtra("userId", userId);
                    intent.putExtra("isForPickup", "yes");
                    intent.putExtra("shiping_charges", "0");
                    intent.putExtra("minimum_charges", "0");
                    intent.putExtra("user_address","Post office Road, Near Fountain Chowk, Fateh Colony, Patiala");
                    startActivity(intent);
                    AnimUtil.slideFromRightAnim(DineInTableActivity.this);
                }
            });

            return convertView;
        }

        class ViewHolder {
            TextView name;
            RelativeLayout parent;
        }

    }


}
