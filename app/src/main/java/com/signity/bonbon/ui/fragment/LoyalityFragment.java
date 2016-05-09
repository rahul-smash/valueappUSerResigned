package com.signity.bonbon.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.model.LoyalityDataModel;
import com.signity.bonbon.model.LoyalityModel;
import com.signity.bonbon.network.NetworkAdaper;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by root on 29/3/16.
 */
public class LoyalityFragment extends Fragment {


    View rootView;
    PrefManager prefManager;
    TextView customerName, points,note;
    ListView offersList;

    ProgressDialogUtil progressDialogUtil;
    String userId, fullname, email;

    RelativeLayout bottomLayout,mainLayout;
    TextView bottomTxt;

    Adapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(getActivity());
        progressDialogUtil = new ProgressDialogUtil();
        userId = prefManager.getSharedValue(AppConstant.ID);
        fullname = prefManager.getSharedValue(AppConstant.NAME);
        email = prefManager.getSharedValue(AppConstant.EMAIL);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_loyality_points, container, false);

        mainLayout=(RelativeLayout)rootView.findViewById(R.id.mainLayout);
        note=(TextView)rootView.findViewById(R.id.note);
        offersList=(ListView)rootView.findViewById(R.id.offersList);
//        bottomLayout=(RelativeLayout)rootView.findViewById(R.id.bottomLayout);
        bottomTxt=(TextView)rootView.findViewById(R.id.bottomTxt);
        customerName = (TextView) rootView.findViewById(R.id.customerName);
        points = (TextView) rootView.findViewById(R.id.points);



        callNetworkForLoyalityPoints();

        return rootView;
    }

    private void callNetworkForLoyalityPoints() {

        try {

            ProgressDialogUtil.showProgressDialog(getActivity());

            Map<String, String> param = new HashMap<String, String>();
            param.put("user_id", userId);


            NetworkAdaper.getInstance().getNetworkServices().getLoyalityPoints(param, new Callback<LoyalityModel>() {

                @Override
                public void success(LoyalityModel loyalityModel, Response response) {

                    if(loyalityModel.getSuccess()){
                        ProgressDialogUtil.hideProgressDialog();

                        mainLayout.setVisibility(View.VISIBLE);
                        note.setVisibility(View.GONE);

                        if (loyalityModel.getLoyalityPoints().isEmpty()) {
                            points.setText("NIL");
                        } else {
                            points.setText("" + loyalityModel.getLoyalityPoints());
                        }



                        if (loyalityModel.getData() != null && loyalityModel.getData().size() != 0) {

                            mAdapter = new Adapter(getActivity(), loyalityModel.getData());
                            offersList.setAdapter(mAdapter);

                        }

                    }
                    else {
                        ProgressDialogUtil.hideProgressDialog();
                        Toast.makeText(getActivity(), "Record not found!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    ProgressDialogUtil.hideProgressDialog();
                }
            });
        } catch (Exception e) {

        }
    }



    class Adapter extends BaseAdapter {

        Activity context;
        LayoutInflater l;
        List<LoyalityDataModel> pointsList;
        ViewHolder holder;


        public Adapter(Activity context, List<LoyalityDataModel> pointsList) {
            this.pointsList = pointsList;
            this.context = context;
            l = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return pointsList.size();
        }

        @Override
        public Object getItem(int position) {
            return pointsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }



        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            holder = null;
            if (convertView == null) {
                convertView = l.inflate(R.layout.fragment_loyality_points_child, null);
                holder = new ViewHolder();
                holder.amountTxt = (TextView) convertView.findViewById(R.id.amountTxt);
                holder.pointsTxt = (TextView) convertView.findViewById(R.id.pointsTxt);
                holder.rupeeTxt=(TextView)convertView.findViewById(R.id.rupeeTxt);
//                holder.redeemNow = (TextView) convertView.findViewById(R.id.redeemNow);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            String currency = prefManager.getSharedValue(AppConstant.CURRENCY);


            if (currency.contains("\\")) {
                holder.rupeeTxt.setText(unescapeJavaString(currency));
            }
            else {
                holder.rupeeTxt.setText(currency);
            }

            try {

                holder.amountTxt.setText(""+pointsList.get(position).getAmount()+" OFF");
            }catch (Exception e){

            }

            try {
                holder.pointsTxt.setText("" + pointsList.get(position).getPoints()+" Pts.");
            }catch (Exception e){

            }

            return convertView;
        }

        class ViewHolder {
            TextView amountTxt,pointsTxt,rupeeTxt;
        }

    }
    public String unescapeJavaString(String st) {

        StringBuilder sb = new StringBuilder(st.length());

        for (int i = 0; i < st.length(); i++) {
            char ch = st.charAt(i);
            if (ch == '\\') {
                char nextChar = (i == st.length() - 1) ? '\\' : st
                        .charAt(i + 1);
// Octal escape?
                if (nextChar >= '0' && nextChar <= '7') {
                    String code = "" + nextChar;
                    i++;
                    if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                            && st.charAt(i + 1) <= '7') {
                        code += st.charAt(i + 1);
                        i++;
                        if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                                && st.charAt(i + 1) <= '7') {
                            code += st.charAt(i + 1);
                            i++;
                        }
                    }
                    sb.append((char) Integer.parseInt(code, 8));
                    continue;
                }
                switch (nextChar) {
                    case '\\':
                        ch = '\\';
                        break;
                    case 'b':
                        ch = '\b';
                        break;
                    case 'f':
                        ch = '\f';
                        break;
                    case 'n':
                        ch = '\n';
                        break;
                    case 'r':
                        ch = '\r';
                        break;
                    case 't':
                        ch = '\t';
                        break;
                    case '\"':
                        ch = '\"';
                        break;
                    case '\'':
                        ch = '\'';
                        break;
// Hex Unicode: u????
                    case 'u':
                        if (i >= st.length() - 5) {
                            ch = 'u';
                            break;
                        }
                        int code = Integer.parseInt(
                                "" + st.charAt(i + 2) + st.charAt(i + 3)
                                        + st.charAt(i + 4) + st.charAt(i + 5), 16);
                        sb.append(Character.toChars(code));
                        i += 5;
                        continue;
                }
                i++;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

}
