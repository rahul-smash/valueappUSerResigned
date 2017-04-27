package com.signity.bonbon.ui.order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.FontUtil;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.Utilities.Util;
import com.signity.bonbon.app.DataAdapter;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.model.EmailResponse;
import com.signity.bonbon.model.GerOrderHistoryModel;
import com.signity.bonbon.model.OrderHistoryItemModel;
import com.signity.bonbon.model.OrderHistoryModel;
import com.signity.bonbon.model.Product;
import com.signity.bonbon.model.UpdateCartModel;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.shopcart.ShoppingCartActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OrderHistory extends Fragment implements View.OnClickListener {

    ListView order_history_list;
    TextView no_record;
    View mView;
    String userId;
    PrefManager prefManager;

    RelativeLayout listHeader;
    Button backButton;


    List<OrderHistoryModel> liseOrderHistoryModel;
    AppDatabase appDb;


    public Typeface typeFaceRobotoRegular, typeFaceRobotoBold;
    Adapter adapter;

    boolean isHeader;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(getActivity());
        appDb = DbAdapter.getInstance().getDb();
        Bundle bundle = getArguments();
        if (bundle != null) {
            isHeader = bundle.getBoolean(AppConstant.IS_HEADER);
        } else {
            isHeader = false;
        }
    }

    public static Fragment newInstance(Context context) {
        return Fragment.instantiate(context,
                OrderHistory.class.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.order_history, container, false);
        prefManager = new PrefManager(getActivity());
        userId = prefManager.getSharedValue(AppConstant.ID);
        typeFaceRobotoRegular = FontUtil.getTypeface(getActivity(), FontUtil.FONT_ROBOTO_REGULAR);
        typeFaceRobotoBold = FontUtil.getTypeface(getActivity(), FontUtil.FONT_ROBOTO_BOLD);
        order_history_list = (ListView) mView.findViewById(R.id.order_history_list);
        no_record = (TextView) mView.findViewById(R.id.no_record);
        listHeader = (RelativeLayout) mView.findViewById(R.id.listHeader);
        backButton = (Button) mView.findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

        if (isHeader) {
            listHeader.setVisibility(View.VISIBLE);
        } else {
            listHeader.setVisibility(View.GONE);
        }
        getOrderHistory();
        return mView;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.backButton:
                getActivity().onBackPressed();
                AnimUtil.slideFromLeftAnim(getActivity());
                break;

        }
    }

    class Adapter extends BaseAdapter {
        Activity context;
        LayoutInflater l;
        List<OrderHistoryModel> liseOrderHistoryModel;

        public Adapter(Activity context, List<OrderHistoryModel> liseOrderHistoryModel) {
            this.context = context;
            l = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.liseOrderHistoryModel = liseOrderHistoryModel;

        }

        @Override
        public int getCount() {
            return liseOrderHistoryModel.size();
        }

        @Override
        public Object getItem(int position) {
            return liseOrderHistoryModel.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        ViewHolder holder;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = l.inflate(R.layout.order_history_child, null);
                holder = new ViewHolder();
                holder.order_date = (TextView) convertView.findViewById(R.id.order_date);
                holder.order_date.setTypeface(typeFaceRobotoRegular);
                holder.order_number = (TextView) convertView.findViewById(R.id.order_number);
                holder.order_number.setTypeface(typeFaceRobotoRegular);
                holder.order_price = (TextView) convertView.findViewById(R.id.order_price);
                holder.order_price.setTypeface(typeFaceRobotoRegular);
                holder.view_order = (Button) convertView.findViewById(R.id.view_order);
                holder.mBtnReorder = (Button) convertView.findViewById(R.id.mBtnReorder);
                holder.mBtnCancel = (Button) convertView.findViewById(R.id.mBtnCancel);
                holder.status = (TextView) convertView.findViewById(R.id.status);
                holder.rupee = (TextView) convertView.findViewById(R.id.rupee);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final OrderHistoryModel orderHistoryModel = liseOrderHistoryModel.get(position);
//            holder.mBtnCancel.setEnabled(true);
            holder.mBtnCancel.setVisibility(View.VISIBLE);
            if (orderHistoryModel.getStatus().equals("0")) {
                holder.status.setText(getString(R.string.str_pending));
            } else if (orderHistoryModel.getStatus().equals("1")) {
                holder.status.setText(getString(R.string.str_approved));
            } else if (orderHistoryModel.getStatus().equals("2")) {
                holder.status.setText(getString(R.string.str_rejected));
            } else if (orderHistoryModel.getStatus().equals("3") || orderHistoryModel.getStatus().equals("6")) {
                holder.status.setText(getString(R.string.str_cancelled));
//                holder.mBtnCancel.setEnabled(false);
                holder.mBtnCancel.setVisibility(View.GONE);
            } else if (orderHistoryModel.getStatus().equals("4")) {
                holder.status.setText(getString(R.string.str_shipped));
            } else if (orderHistoryModel.getStatus().equals("5")) {
                holder.status.setText(getString(R.string.str_delivered));
            } else {
                holder.status.setVisibility(View.GONE);
            }


            String currency = prefManager.getSharedValue(AppConstant.CURRENCY);


            if (currency.contains("\\")) {
                holder.rupee.setText(Util.unescapeJavaString(currency));
            } else {
                holder.rupee.setText(currency);
            }


            holder.order_date.setText(orderHistoryModel.getOrderDate());
            try {
                holder.order_number.setText(""+orderHistoryModel.getDisplayOrderId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.order_price.setText(orderHistoryModel.getTotalOrderVal());

            holder.view_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataAdapter.getInstance().setOrderHistoryModel(orderHistoryModel);
                    Intent intent = new Intent(getActivity(), OrderDetail.class);
                    startActivity(intent);
                    AnimUtil.slideFromRightAnim(getActivity());
                }
            });
            holder.mBtnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (orderHistoryModel.getStatus().equalsIgnoreCase("0") ||
                            orderHistoryModel.getStatus().equals("1")) {
                        confirmCancelOrderDialog(orderHistoryModel.getOrderId());
                    } else {
                        String message = getString(R.string.str_order_alrady)+" " + holder.status.getText().toString();
                        showAlertDialog(context, getString(R.string.str_info), message, 0);
                    }
                }
            });

            holder.mBtnReorder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmReorderOrderDialog(orderHistoryModel);
                }
            });

            return convertView;
        }


        class ViewHolder {
            TextView order_date,rupee;
            TextView order_number;
            TextView order_price;
            TextView ship;
            TextView status;
            Button view_order, mBtnCancel, mBtnReorder;
        }
    }

    private void cancelOrder(String orderId) {
        ProgressDialogUtil.showProgressDialog(getActivity());
        Map<String, String> param = new HashMap<String, String>();
        param.put("user_id", userId);
        param.put("order_id", orderId);

        NetworkAdaper.getInstance().getNetworkServices().cancelOrder(param, new Callback<EmailResponse>() {
            @Override
            public void success(EmailResponse responseStatus, Response response) {
                ProgressDialogUtil.hideProgressDialog();
                if (responseStatus.getSuccess()) {
                    getOrderHistory();
                } else {
                    DialogHandler dialogHandler = new DialogHandler(getActivity());
                    dialogHandler.setdialogForFinish(getResources().getString(R.string.dialog_title), responseStatus.getMessage(), false);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                DialogHandler dialogHandler = new DialogHandler(getActivity());
                dialogHandler.setdialogForFinish(getResources().getString(R.string.dialog_title), getResources().getString(R.string.error_code_message), false);
            }
        });
    }

    private void reOrder(OrderHistoryModel orderHistoryModel) {

        int totalOrderItem = 0, addedtoCartItem = 0;
        List<OrderHistoryItemModel> ordersitems = orderHistoryModel.getOrderItems();
        if (ordersitems != null && ordersitems.size() != 0) {
            totalOrderItem = ordersitems.size();
            for (int i = 0; i < ordersitems.size(); i++) {
                OrderHistoryItemModel orderModel = ordersitems.get(i);
                Product product = appDb.getProduct(orderModel.getProductId());
                if (product != null) {
                    UpdateCartModel updateCartModel = new UpdateCartModel();
                    updateCartModel.setProductId(orderModel.getProductId());
                    updateCartModel.setVariantId(orderModel.getVariantId());
                    updateCartModel.setWeight(orderModel.getWeight());
                    updateCartModel.setMrpPrice(orderModel.getMrpPrice());
                    updateCartModel.setPrice(orderModel.getPrice());
                    updateCartModel.setDiscount(orderModel.getDiscount());
                    updateCartModel.setUnitType(orderModel.getUnitType());
                    updateCartModel.setQuantity(orderModel.getQuantity());
                    updateCartModel.setIsTaxEnable(orderModel.getIsTaxEnable());
                    appDb.addToCart(updateCartModel);
                    addedtoCartItem++;
                }
            }
            String message = "";

            if (totalOrderItem != addedtoCartItem) {
                if (addedtoCartItem == 0) {
                    message = getString(R.string.msg_toast_make_fresh_order);
                } else {
                    message = getString(R.string.msg_toast_items_not_availble);
                }
            } else {
                message = getString(R.string.msg_toast_order_added_to_cart);
            }
            showAlertDialog(getActivity(), getString(R.string.msg_toast_reorder), message, addedtoCartItem);
        }
    }


    public void getOrderHistory() {
        ProgressDialogUtil.showProgressDialog(getActivity());
        String deviceid = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceToken = prefManager.getSharedValue(AppConstant.DEVICE_TOKEN);
        Map<String, String> param = new HashMap<String, String>();
        param.put("device_id", deviceid);
        param.put("device_token", deviceToken);
        param.put("platform", AppConstant.PLATFORM);
        param.put("user_id", userId);

        NetworkAdaper.getInstance().getNetworkServices().getOrderHistoryList(param, new Callback<GerOrderHistoryModel>() {
            @Override
            public void success(GerOrderHistoryModel getOrderHistoryModel, Response response) {
                Log.e("Tab", getOrderHistoryModel.toString());
                if (getOrderHistoryModel.getSuccess()) {
                    liseOrderHistoryModel = getOrderHistoryModel.getData();
                    if (liseOrderHistoryModel != null && liseOrderHistoryModel.size() != 0) {
                        adapter = new Adapter(getActivity(), liseOrderHistoryModel);
                        order_history_list.setAdapter(adapter);
                    } else {
                        no_record.setVisibility(View.VISIBLE);
                        order_history_list.setVisibility(View.GONE);
                    }
                    ProgressDialogUtil.hideProgressDialog();
                } else {
                    no_record.setVisibility(View.VISIBLE);
                    order_history_list.setVisibility(View.GONE);
                    ProgressDialogUtil.hideProgressDialog();
                    /*DialogHandler dialogHandler = new DialogHandler(getActivity());
                    dialogHandler.setdialogForFinish("Message", getResources().getString(R.string.error_code_message), false);*/
                }

            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
                no_record.setVisibility(View.VISIBLE);
                order_history_list.setVisibility(View.GONE);
                Toast.makeText(getActivity(), getString(R.string.str_no_data_found), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void confirmCancelOrderDialog(final String orderId) {

        final DialogHandler dialogHandler = new DialogHandler(getActivity());
        dialogHandler.setDialog(getString(R.string.str_cancel_order), getResources().getString(R.string.cancel_order_txt));
        dialogHandler.setPostiveButton(getString(R.string.layout_notification_detail_yes), true).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelOrder(orderId);
                dialogHandler.dismiss();
            }
        });
        dialogHandler.setNegativeButton(getString(R.string.layout_notification_detail_no), true).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHandler.dismiss();
            }
        });
    }

    public void confirmReorderOrderDialog(final OrderHistoryModel orderHistoryModel) {

        final DialogHandler dialogHandler = new DialogHandler(getActivity());
        dialogHandler.setDialog(getString(R.string.str_reorder), getString(R.string.layout_reorder_items));
        dialogHandler.setPostiveButton(getString(R.string.layout_notification_detail_yes), true).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reOrder(orderHistoryModel);
                dialogHandler.dismiss();
            }
        });
        dialogHandler.setNegativeButton(getString(R.string.layout_notification_detail_no), true).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHandler.dismiss();
            }
        });
    }


    public void showAlertDialog(Context context, String title, String message, final int addedItem) {
        final DialogHandler dialogHandler = new DialogHandler(getActivity());
        dialogHandler.setDialog(title, message);
        dialogHandler.setPostiveButton(getString(R.string.str_lbl_ok), true).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addedItem != 0) {
                    Intent intentShopCartActivity = new Intent(getActivity(), ShoppingCartActivity.class);
                    startActivity(intentShopCartActivity);
                    AnimUtil.slideFromRightAnim(getActivity());
                }
                dialogHandler.dismiss();
            }
        });
    }


}
