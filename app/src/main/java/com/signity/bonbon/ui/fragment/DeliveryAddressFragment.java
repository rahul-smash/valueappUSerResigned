package com.signity.bonbon.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.DialogHandler;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.Utilities.ProgressDialogUtil;
import com.signity.bonbon.Utilities.Util;
import com.signity.bonbon.app.DbAdapter;
import com.signity.bonbon.db.AppDatabase;
import com.signity.bonbon.model.EmailResponse;
import com.signity.bonbon.model.UserAddressList;
import com.signity.bonbon.model.UserAddressModel;
import com.signity.bonbon.network.NetworkAdaper;
import com.signity.bonbon.ui.shopcart.ShoppingCartActivity2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by root on 14/10/15.
 */
public class DeliveryAddressFragment extends Fragment implements View.OnClickListener {

    public TextView address;
    RelativeLayout add_address;
    TextView mTxtProceed;
    ListView address_list;
    Adapter adapter;
    View mView;
    String from;
    String userId;
    UserAddressModel selectedUserAddress;
    int selectedPostion = 0;
    String currency;
    private List<UserAddressModel> listOfDeliveryAddress;
    private PrefManager prefManager;
    private AppDatabase appDb;
    private RelativeLayout mRelativeProceed;

    public static Fragment newInstance(Context context) {
        return Fragment.instantiate(context,
                DeliveryAddressFragment.class.getName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDb = DbAdapter.getInstance().getDb();
        prefManager = new PrefManager(getActivity());
        userId = prefManager.getSharedValue(AppConstant.ID);
        prefManager = new PrefManager(getActivity());
        from = getArguments().getString(AppConstant.FROM, "");
        currency = prefManager.getSharedValue(AppConstant.CURRENCY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.add_delivery_fragment, container, false);
        address = (TextView) mView.findViewById(R.id.address);
        mTxtProceed = (TextView) mView.findViewById(R.id.mTxtProceed);
        add_address = (RelativeLayout) mView.findViewById(R.id.add_address);
        mRelativeProceed = (RelativeLayout) mView.findViewById(R.id.mRelativeProceed);
        address_list = (ListView) mView.findViewById(R.id.address_list);
        address.setOnClickListener(this);
        mTxtProceed.setOnClickListener(this);

        if (from.equals("shop_cart")) {
            mRelativeProceed.setVisibility(View.VISIBLE);
        } else {
            mRelativeProceed.setVisibility(View.GONE);
        }
        getDeliveryAddress();
        return mView;
    }

    private void getDeliveryAddress() {
        ProgressDialogUtil.showProgressDialog(getActivity());
        Map<String, String> param = new HashMap<String, String>();
        param.put("user_id", userId);
        param.put("method", "GET");
        NetworkAdaper.getInstance().getNetworkServices().getAddressList(param, new Callback<UserAddressList>() {

            @Override
            public void success(UserAddressList userAddressList, Response response) {
                if (userAddressList.getSuccess()) {

                    listOfDeliveryAddress = userAddressList.getData();
                    if (listOfDeliveryAddress == null) {
                        listOfDeliveryAddress = new ArrayList<UserAddressModel>();
                    }
                    adapter = new Adapter(getActivity(), listOfDeliveryAddress);
                    address_list.setAdapter(adapter);
                    ProgressDialogUtil.hideProgressDialog();
                } else {
                    ProgressDialogUtil.hideProgressDialog();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ProgressDialogUtil.hideProgressDialog();
            }
        });
    }

    private void confirmUserForDeliveryAddress(UserAddressModel addressModel) {

        String totalPriceString = appDb.getCartTotalPrice();
        double cartprice = Double.parseDouble(totalPriceString);
        String minmumCartString = addressModel.getMinimumDeliveryAmout();
        String shipingCharges = addressModel.getAreaShipingCharges();
        String note = addressModel.getNote();
        String addressId = addressModel.getId();
        String areaId = addressModel.getAreaId();
        String userAddress = addressModel.getAddress() + ", "
                + addressModel.getAreaName() + ", " + addressModel.getCity() + ", " + addressModel.getZipcode();


        String noteText = (addressModel.getNote() != null && !addressModel.getNote().isEmpty())
                ? "\n" + addressModel.getNote() : "";


        String currencySymbol;

        if (currency.contains("\\")) {
            currencySymbol = Util.unescapeJavaString(currency);
        } else {
            currencySymbol = currency;
        }


        if (addressModel.getNotAllow()) {
            double minprice = Double.parseDouble(minmumCartString);
            if (minprice > cartprice) {

                double diff = minprice - cartprice;
                String shortPrice = String.format("%.2f", diff);

                String message = getString(R.string.lbl_you_currently)+" " + currencySymbol + "" + shortPrice +
                        " "+getString(R.string.lbl_you_add_items_tocart);
                showAlertDialogForMinAmount(getActivity(), getString(R.string.msg_dialog_alert), message
                );
            } else {
                String message = noteText.trim();
                if (message != null && !message.isEmpty()) {
                    message = message + "\n";
                } else {
                    message = "";
                }
                showAlertDialogForConfirm(getActivity(), getString(R.string.msg_dialog_confirmation), message, userId, addressId, "", minmumCartString, userAddress, areaId);
            }
        } else {
            if (minmumCartString != null && !minmumCartString.isEmpty() && !minmumCartString.equalsIgnoreCase("0")) {
                double minprice = Double.parseDouble(minmumCartString);
                if (shipingCharges != null
                        && !shipingCharges.isEmpty()) {
                    if (shipingCharges.equalsIgnoreCase("0")) {
                        if (minprice <= cartprice) {
                            String message = "";
                            if (noteText != null && !(noteText.isEmpty())) {
                                message = noteText + "\n";
                            } else {
                                message = "";
                            }
                            showAlertDialogForConfirm(getActivity(), getString(R.string.msg_dialog_confirmation), message, userId,
                                    addressId, shipingCharges, minmumCartString, userAddress, areaId
                            );
                        } else {
                            double diff = minprice - cartprice;
                            String shortPrice = String.format("%.2f", diff);

                            String message = getString(R.string.lbl_you_currently)+" " + currencySymbol + "" + shortPrice +
                                    " "+getString(R.string.lbl_you_add_items_tocart);;
                            showAlertDialogForMinAmount(getActivity(), getResources().getString(R.string.msg_dialog_alert), message
                            );
                        }
                    } else if (minprice > cartprice) {
                        String message = getString(R.string.msg_dialog_there_will_be)+" " + currencySymbol + "" + shipingCharges +
                                " "+getString(R.string.msg_dialog_shiping_charges) + ((noteText != null & (!(noteText.isEmpty()))) ? noteText + "\n" : "") +
                                "\n";
                        showAlertDialogForConfirm(getActivity(), getString(R.string.msg_dialog_confirmation), message, userId,
                                addressId, shipingCharges, minmumCartString, userAddress, areaId
                        );
                    } else {
                        String message = noteText.trim();
                        if (message != null && !message.isEmpty()) {
                            message = message + "\n";
                        } else {
                            message = "";
                        }
                        showAlertDialogForConfirm(getActivity(), getString(R.string.msg_dialog_confirmation), message, userId,
                                addressId, shipingCharges, minmumCartString, userAddress, areaId);
                    }
                }

            } else {
                if (shipingCharges != null
                        && !shipingCharges.isEmpty() && !(shipingCharges.equalsIgnoreCase("0"))) {
                    String message = getString(R.string.msg_dialog_there_will_be)+" " + currencySymbol + " " + shipingCharges +
                            " "+getString(R.string.msg_dialog_shiping_charges) + ((noteText != null & (!(noteText.isEmpty()))) ? noteText + "\n" : "") +
                            "\n";
                    showAlertDialogForConfirm(getActivity(), getString(R.string.msg_dialog_confirmation), message, userId, addressId, shipingCharges, "", userAddress, areaId);
                } else {
                    String message = noteText.trim();
                    if (message != null && !message.isEmpty()) {
                        message = message + "\n";
                    } else {
                        message = "";
                    }
                    showAlertDialogForConfirm(getActivity(), getString(R.string.msg_dialog_confirmation), message, userId, addressId, "", "", userAddress, areaId);
                }
            }
        }


    }

    private void callNetworkServiceForDelete(String id, String addressId, final int position) {
        ProgressDialogUtil.showProgressDialog(getActivity());

        String deviceId = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceToken = prefManager.getSharedValue(AppConstant.DEVICE_TOKEN);
        Map<String, String> param = new HashMap<String, String>();
        param.put("device_id", deviceId);
        param.put("user_id", id);
        param.put("method", "DELETE");
        param.put("address_id", addressId);
        param.put("device_token", deviceToken);
        param.put("platform", AppConstant.PLATFORM);
        NetworkAdaper.getInstance().getNetworkServices().deleteDeliveryAddress(param, new Callback<EmailResponse>() {
            @Override
            public void success(EmailResponse responseData, Response response) {
                ProgressDialogUtil.hideProgressDialog();
                if (responseData.getSuccess()) {
                    listOfDeliveryAddress.remove(position);
                    adapter.notifyDataSetChanged();
                    if(position==0){
                        selectedUserAddress=null;
                    }
                } else {
//                    Toast.makeText(getActivity(), responseData.getMessage(), Toast.LENGTH_SHORT).show();
                    DialogHandler dialogHandler = new DialogHandler(getActivity());
                    dialogHandler.setdialogForFinish(getResources().getString(R.string.dialog_title), "" + responseData.getMessage(), false);
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

    public void showAlertDialogForMinAmount(Context context, String title,
                                            String message) {

        final DialogHandler dialogHandler = new DialogHandler(context);
        dialogHandler.setDialog(title, message);
        dialogHandler.setPostiveButton(getResources().getString(R.string.str_lbl_ok), true)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogHandler.dismiss();
                        getActivity().onBackPressed();
                    }
                });
    }

    public void showAlertDialogForConfirm(Context context, String title,
                                          String message, final String userId, final String addressId,
                                          final String shipingCharges, final String minimumCharges, final String userAddress, final String areaId) {

        if(prefManager.getSharedValue(AppConstant.ONLINE_PAYMENT).equalsIgnoreCase("0")){
            final DialogHandler dialogHandler = new DialogHandler(context);
            dialogHandler.setDialog(title, message);
            dialogHandler.setPostiveButton(getString(R.string.str_lbl_proceed), true)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(getActivity(), ShoppingCartActivity2.class);
                            intent.putExtra("addressId", addressId);
                            intent.putExtra("userId", userId);
                            intent.putExtra("shiping_charges", shipingCharges);
                            intent.putExtra("minimum_charges", minimumCharges);
                            intent.putExtra("user_address", userAddress);
                            intent.putExtra("area_id", areaId);
                            intent.putExtra("payment_method", "2");
                            startActivity(intent);
                            AnimUtil.slideFromRightAnim(getActivity());
                            dialogHandler.dismiss();

                        }
                    });

            dialogHandler.setNegativeButton(getString(R.string.str_cancel), true).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogHandler.dismiss();
                }
            });
        }else if(prefManager.getSharedValue(AppConstant.ONLINE_PAYMENT).equalsIgnoreCase("1")){

            final Dialog dialog = new Dialog(getActivity());

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.custom_dialog);
            TextView positveButton = (TextView) dialog.findViewById(R.id.yesBtn);
            positveButton.setVisibility(View.VISIBLE);
            TextView negativeButton = (TextView) dialog.findViewById(R.id.noBtn);
            negativeButton.setVisibility(View.VISIBLE);
            TextView titleTxt = (TextView) dialog.findViewById(R.id.title);
            TextView messageText = (TextView) dialog.findViewById(R.id.message);
            titleTxt.setText(""+title);
            positveButton.setText("COD");
            negativeButton.setText(getString(R.string.lbl_you_online));
            messageText.setText(""+message+""+getString(R.string.lbl_you_select_payment));

            dialog.setCanceledOnTouchOutside(true);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            positveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ShoppingCartActivity2.class);
                    intent.putExtra("addressId", addressId);
                    intent.putExtra("userId", userId);
                    intent.putExtra("shiping_charges", shipingCharges);
                    intent.putExtra("minimum_charges", minimumCharges);
                    intent.putExtra("user_address", userAddress);
                    intent.putExtra("area_id", areaId);
                    intent.putExtra("payment_method", "2");
                    startActivity(intent);
                    AnimUtil.slideFromRightAnim(getActivity());
                    dialog.dismiss();
                }
            });

            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ShoppingCartActivity2.class);
                    intent.putExtra("addressId", addressId);
                    intent.putExtra("userId", userId);
                    intent.putExtra("shiping_charges", shipingCharges);
                    intent.putExtra("minimum_charges", minimumCharges);
                    intent.putExtra("user_address", userAddress);
                    intent.putExtra("area_id", areaId);
                    intent.putExtra("payment_method", "3");
                    startActivity(intent);
                    AnimUtil.slideFromRightAnim(getActivity());
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
        else {
            final DialogHandler dialogHandler = new DialogHandler(context);
            dialogHandler.setDialog(title, message);
            dialogHandler.setPostiveButton(getString(R.string.str_lbl_proceed), true)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(getActivity(), ShoppingCartActivity2.class);
                            intent.putExtra("addressId", addressId);
                            intent.putExtra("userId", userId);
                            intent.putExtra("shiping_charges", shipingCharges);
                            intent.putExtra("minimum_charges", minimumCharges);
                            intent.putExtra("user_address", userAddress);
                            intent.putExtra("area_id", areaId);
                            intent.putExtra("payment_method", "2");
                            startActivity(intent);
                            AnimUtil.slideFromRightAnim(getActivity());
                            dialogHandler.dismiss();

                        }
                    });

            dialogHandler.setNegativeButton(getString(R.string.str_cancel), true).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogHandler.dismiss();
                }
            });
        }



    }

    public void showAlertDialogForDelete(Context context, String title,
                                         String message, final String userId, final String addressId, final int position) {

        final DialogHandler dialogHandler = new DialogHandler(context);
        dialogHandler.setDialog(title, message);
        dialogHandler.setPostiveButton(getString(R.string.layout_notification_detail_yes), true)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callNetworkServiceForDelete(userId, addressId, position);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.address:
                Fragment fragment = AddAddressFragment.newInstance(getActivity());
                Bundle bundle = new Bundle();
                bundle.putString(AppConstant.FROM, from);
                bundle.putString(AppConstant.ACTION, "ADD");
                /*if (listOfDeliveryAddress.isEmpty()) {
                    bundle.putSerializable("object", null);
                } else {
                    bundle.putSerializable("object", listOfDeliveryAddress.get(0));
                }*/
                fragment.setArguments(bundle);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.right_to_center_slide,
                        R.anim.center_to_left_slide,
                        R.anim.left_to_center_slide,
                        R.anim.center_to_right_slide);
                ft.replace(R.id.container, fragment);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.mTxtProceed:
                if (selectedUserAddress != null) {
                    confirmUserForDeliveryAddress(selectedUserAddress);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.lbl_select_address), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        selectedUserAddress = null;
        selectedPostion = 0;
        getDeliveryAddress();

        InputMethodManager inputManager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) getActivity()).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    class Adapter extends BaseAdapter {
        public List<UserAddressModel> deliveryAddress;
        Activity context;
        LayoutInflater l;
        ViewHolder holder;

        public Adapter(Activity context, List<UserAddressModel> deliveryAddress) {
            this.deliveryAddress = deliveryAddress;
            this.context = context;
            l = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return deliveryAddress.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = l.inflate(R.layout.delivery_address_child, null);
                holder = new ViewHolder();
                holder.phone_number = (TextView) convertView.findViewById(R.id.phone_number1);
                holder.btnNext = (ImageView) convertView.findViewById(R.id.btnNext);
                holder.full_name = (TextView) convertView.findViewById(R.id.full_name);
                holder.location = (TextView) convertView.findViewById(R.id.location);
                holder.mail = (TextView) convertView.findViewById(R.id.mail);
                holder.edit_button = (Button) convertView.findViewById(R.id.edit_button);
                holder.remove_button = (Button) convertView.findViewById(R.id.remove_button);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.phone_number.setText(deliveryAddress.get(position).getMobile());
            holder.full_name.setText(deliveryAddress.get(position).getFirstName());
            holder.location.setText(deliveryAddress.get(position).getAddress());
            holder.mail.setText(deliveryAddress.get(position).getEmail());

            if (from.equals("shop_cart")) {
                holder.btnNext.setVisibility(View.VISIBLE);


                if (selectedPostion == position) {
                    holder.btnNext.setSelected(true);
                    selectedUserAddress = deliveryAddress.get(position);
                } else {
                    holder.btnNext.setSelected(false);
                }

               /* if (selectedPostion != -1) {
                    if (position == selectedPostion) {
                        holder.btnNext.setSelected(true);
                    } else {
                        holder.btnNext.setSelected(false);
                    }
                } else {
                    holder.btnNext.setSelected(false);
                }*/
            } else {
                holder.btnNext.setVisibility(View.GONE);
            }

            holder.edit_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Fragment fragment = AddAddressFragment.newInstance(getActivity());
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstant.FROM, from);
                    bundle.putString(AppConstant.ACTION, "EDIT");
                    bundle.putSerializable("object", deliveryAddress.get(position));
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.right_to_center_slide,
                            R.anim.center_to_left_slide,
                            R.anim.left_to_center_slide,
                            R.anim.center_to_right_slide);
                    ft.replace(R.id.container, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });

            holder.remove_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserAddressModel addressModel = deliveryAddress.get(position);
                    String userId = prefManager.getSharedValue(AppConstant.ID);
                    String addressId = addressModel.getId();
                    showAlertDialogForDelete(getActivity(), getString(R.string.msg_dialog_confirmation), getString(R.string.lbl_delete_address), userId, addressId, position);
//                    notifyDataSetChanged();
                }
            });


            if (from.equals("shop_cart")) {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedUserAddress = deliveryAddress.get(position);
                        selectedPostion = position;
                        notifyDataSetChanged();
                    }
                });
            }

            return convertView;
        }


        class ViewHolder {
            public TextView phone_number, full_name, location, mail;
            public Button edit_button, remove_button;
            ImageView btnNext;
        }
    }

}
