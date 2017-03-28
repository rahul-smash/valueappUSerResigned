package com.signity.bonbon.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.model.Category;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by rajesh on 26/4/16.
 */
public class RvCategoryListAdapter extends RecyclerView.Adapter<RvCategoryListAdapter.MyViewHolder> {


    // Define listener member variable
    private static OnItemClickListener listener;

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    private static final String TAG = RvCategoryListAdapter.class.getSimpleName();

    private List<Category> mData;
    private LayoutInflater mInflater;
    RelativeLayout.LayoutParams layoutParamsImageView;

    DisplayMetrics metrics;
    int screenHeight;
    int screenWidth;

    Context context;
    boolean isType;

    public RvCategoryListAdapter(Context context, List<Category> data, boolean isType) {
        this.mData = data;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.isType = isType;


        metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;
        Log.e("Screen Dimen", screenWidth + "*" + screenHeight);
        layoutParamsImageView = new RelativeLayout.LayoutParams(isType ? RelativeLayout.LayoutParams.MATCH_PARENT : (screenWidth / 2) - 10, isType ? screenHeight / 4 : (screenHeight / 4));

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(isType ? R.layout.categories_activity_list_child : R.layout.categories_activity_child, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Category category = mData.get(position);
        holder.text.setText(category.getTitle());
//        holder.image.setLayoutParams(layoutParamsImageView);
//            holder.image.setImageResource(images[0]);
        try {
            if (category.getImageMedium() != null && !category.getImageMedium().isEmpty()) {
                Picasso.with(context).load(category.getImage()).
                        resize(300, 300).error(R.mipmap.ic_launcher).placeholder(R.drawable.placeholder).into(holder.image);
            } else {
                holder.image.setImageResource(R.mipmap.ic_launcher);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView text;

        public MyViewHolder(final View convertView) {
            super(convertView);
            image = (ImageView) convertView.findViewById(R.id.image);
            text = (TextView) convertView.findViewById(R.id.text_name);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null)
                        listener.onItemClick(convertView, getLayoutPosition());
                }
            });
//            text.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // Triggers click upwards to the adapter on click
//                    if (listener != null)
//                        listener.onItemClick(text, getLayoutPosition());
//                }
//            });
        }

    }

}
