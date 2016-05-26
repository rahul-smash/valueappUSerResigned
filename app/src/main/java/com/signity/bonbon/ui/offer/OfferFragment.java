package com.signity.bonbon.ui.offer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.GsonHelper;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.model.GetOfferResponse;
import com.signity.bonbon.model.OfferData;
import com.signity.bonbon.model.Store;
import com.signity.bonbon.network.ApiService;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.network.NetworkConstant;
import com.squareup.okhttp.OkHttpClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class OfferFragment extends Fragment implements View.OnClickListener {

    //Ui
    ListView listViewOffer;
    TextView mTextViewNoRecord;
    View mView;

    //app manager
    PrefManager prefManager;
    AppDatabase appDb;
    GsonHelper gsonHelper;

    //list and adapter
    List<OfferData> listOfferData;
    ListOfferAdapter adapter;


    //String constant
    String userId;

    //others
    public Typeface typeFaceRobotoRegular, typeFaceRobotoBold;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(getActivity());
        appDb = DbAdapter.getInstance().getDb();
        gsonHelper = new GsonHelper();

    }

    public static Fragment newInstance(Context context) {
        return Fragment.instantiate(context,
                OfferFragment.class.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_offer, container, false);
        prefManager = new PrefManager(getActivity());
        userId = prefManager.getSharedValue(AppConstant.ID);
        listViewOffer = (ListView) mView.findViewById(R.id.listView);
        mTextViewNoRecord = (TextView) mView.findViewById(R.id.no_record);
        getOffers();
        return mView;
    }


    // for temporary custom url
    public ApiService setupRetrofitClient() {
        String url = "http://app.grocersapp.com/1/api/storeOffers";
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(2, TimeUnit.MINUTES);
        client.setReadTimeout(2, TimeUnit.MINUTES);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient(client)).setEndpoint(NetworkConstant.BASE).setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        return restAdapter.create(ApiService.class);
    }


    @Override
    public void onClick(View view) {

    }


    class ListOfferAdapter extends BaseAdapter {
        Activity context;
        LayoutInflater l;
        List<OfferData> listOfferData;

        public ListOfferAdapter(Activity context, List<OfferData> listOfferData) {
            this.context = context;
            l = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.listOfferData = listOfferData;

        }

        @Override
        public int getCount() {
            return listOfferData.size();
        }

        @Override
        public Object getItem(int position) {
            return listOfferData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        ViewHolder holder;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = l.inflate(R.layout.view_offer_list_row, null);
                holder = new ViewHolder();
                holder.title = (TextView) (convertView.findViewById(R.id.txtOfferTitle));
                holder.codeVal = (TextView) (convertView.findViewById(R.id.txtCodeVal));
                holder.view = (RelativeLayout) (convertView.findViewById(R.id.root));
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final OfferData data = listOfferData.get(position);
            holder.title.setText(data.getName());
            holder.codeVal.setText(data.getCouponCode());
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String offerName = data.getName();
                    String offerDataString = gsonHelper.getOfferDataString(data);
                    prefManager.storeSharedValue(AppConstant.OFFER_VIEW, offerDataString);
                    Intent intentOfferView = new Intent(getActivity(), OfferViewActivity.class);
                    intentOfferView.putExtra("offerName", offerName);
                    startActivity(intentOfferView);
                    AnimUtil.slideFromRightAnim(getActivity());
                }
            });


            holder.codeVal.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        final android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) context
                                .getSystemService(Context.CLIPBOARD_SERVICE);
                        final android.content.ClipData clipData = android.content.ClipData
                                .newPlainText("text label", holder.codeVal.getText().toString());
                        clipboardManager.setPrimaryClip(clipData);
                        Toast.makeText(getActivity(), "Copied:" + holder.codeVal.getText().toString(), Toast.LENGTH_SHORT);
                    } else {
                        final android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) context
                                .getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboardManager.setText(holder.codeVal.getText().toString());
                        Toast.makeText(getActivity(), "Copied:" + holder.codeVal.getText().toString(), Toast.LENGTH_SHORT);
                    }
                    return true;
                }
            });
            return convertView;
        }


        class ViewHolder {
            TextView title;
            TextView codeVal;
            RelativeLayout view;
        }
    }


    public void getOffers() {
        ProgressDialogUtil.showProgressDialog(getActivity());

        Store store = appDb.getStore(prefManager.getSharedValue(AppConstant.STORE_ID));
        String storeId = store.getId();
        Map<String, String> param = new HashMap<String, String>();

        param.put("store_id", storeId);
        param.put("user_id", ""+userId);
        param.put("area_id", "");
        param.put("order_facility", "");

        NetworkAdaper.getInstance().getNetworkServices().getStoreOffer(param, new Callback<GetOfferResponse>() {
            @Override
            public void success(GetOfferResponse getOfferResponse, Response response) {

                if (getOfferResponse.getSuccess()) {
                    listOfferData = getOfferResponse.getData();
                    if (listOfferData != null && listOfferData.size() != 0) {
                        adapter = new ListOfferAdapter(getActivity(), listOfferData);
                        listViewOffer.setAdapter(adapter);
                        listViewOffer.setVisibility(View.VISIBLE);
                        mTextViewNoRecord.setVisibility(View.GONE);
                    } else {
                        listViewOffer.setVisibility(View.GONE);
                        mTextViewNoRecord.setVisibility(View.VISIBLE);
                    }
                } else {
                    listViewOffer.setVisibility(View.GONE);
                    mTextViewNoRecord.setVisibility(View.VISIBLE);
                }


                ProgressDialogUtil.hideProgressDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                DialogHandler dialogHandler = new DialogHandler(getActivity());
                dialogHandler.setdialogForFinish("Error", getResources().getString(R.string.error_code_message), false);
            }
        });

    }


    public void showAlertDialog(Context context, String title, String message) {
        new DialogHandler(context).setdialogForFinish(title, message, false);
    }

}
