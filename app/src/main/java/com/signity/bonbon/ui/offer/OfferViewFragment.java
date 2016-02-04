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

        String rs = getString(R.string.text_rs);
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
}
