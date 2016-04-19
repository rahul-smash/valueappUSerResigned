package com.signity.bonbon.ui.offer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.GsonHelper;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.model.OfferData;
import com.squareup.picasso.Picasso;

/**
 * Created by rajesh on 10/12/15.
 */
public class OfferViewFragment extends Fragment {


    //ui element
    ImageView imageViewItem;
    TextView date1, date2, minimumAmout, discount, coupon, notification;

    //helper manager
    GsonHelper gsonHelper;
    PrefManager prefManager;

    //data object
    OfferData data;

    public static Fragment newInstance(Context context) {
        return Fragment.instantiate(context,
                OfferViewFragment.class.getName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gsonHelper = new GsonHelper();
        prefManager = new PrefManager(getActivity());

        String stringOfferData = prefManager.getSharedValue(AppConstant.OFFER_VIEW);

        data = gsonHelper.getOfferDataObject(stringOfferData);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;

        rootView = inflater.inflate(R.layout.fragment_offer_view, container, false);
        date1 = (TextView) rootView.findViewById(R.id.date1);
        date2 = (TextView) rootView.findViewById(R.id.date2);
//        usageLimit = (TextView) rootView.findViewById(R.id.txtUsageLimit);
        minimumAmout = (TextView) rootView.findViewById(R.id.txtMinimumOrder);
        discount = (TextView) rootView.findViewById(R.id.txtDiscount);
        coupon = (TextView) rootView.findViewById(R.id.txtCoupon);
        notification = (TextView) rootView.findViewById(R.id.notification);
        imageViewItem = (ImageView) rootView.findViewById(R.id.item_image);

        if (data != null) {
            loadDataElement();
        }

        return rootView;

    }

    private void loadDataElement() {

        String currency = prefManager.getSharedValue(AppConstant.CURRENCY);
        String rs = "";

        if (currency.contains("\\")) {
            rs=unescapeJavaString(currency);
        }
        else {
            rs=currency;
        }


        String percent = getString(R.string.text_percent);
        date1.setText(data.getValidFrom());
        date2.setText(data.getValidTo());
//        usageLimit.setText(data.getUsageLimit());
        minimumAmout.setText(rs + " " + data.getMinimumOrderAmount());
        discount.setText(data.getDiscount() + " " + percent);
        coupon.setText(data.getCouponCode());
        notification.setText(data.getOfferNotification());

        if (data.getImage() != null && !data.getImage().isEmpty()) {
            Picasso.with(getActivity()).load(data.getImage()).resize(400, 400).error(com.signity.bonbon.R.drawable.no_image).into(imageViewItem);
        } else {
            imageViewItem.setImageResource(com.signity.bonbon.R.drawable.no_image);
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
