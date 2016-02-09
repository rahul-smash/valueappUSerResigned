package com.signity.bonbon.ui.FAQ;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.signity.bonbon.R;

import java.util.ArrayList;
import java.util.List;

public class Faq extends Fragment {


    ListView faq_list;
    String[] questions = {"How do i verify my number in GrocerApp?", "How can i tell if someone has read my message?", "Why can't I connect to GrocerApp?", "How do i update GrocerApp?", "Question Number 5",};
    public TextView title;
    Adapter adapter;

    ArrayList<FaqObject> viewList = new ArrayList<FaqObject>();

    public Typeface _ProximaNovaLight, _ProximaNovaSemiBold;

    View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.faq, container, false);
        initialize();

        _ProximaNovaLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        _ProximaNovaSemiBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Bold.ttf");

        for (int i = 0; i < questions.length; i++) {
            FaqObject att = new FaqObject();
            att.question = questions[i];
            viewList.add(att);
        }

        adapter = new Adapter(getActivity(), viewList);
        faq_list.setAdapter(adapter);


        return mView;
    }


    private void initialize() {
        faq_list = (ListView) mView.findViewById(R.id.faq_list);
    }

    class Adapter extends BaseAdapter {
        Activity context;
        LayoutInflater l;
        List<FaqObject> list;

        public Adapter(Activity context, List<FaqObject> list) {
            this.list = list;
            this.context = context;
            l = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        ViewHolder holder;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = l.inflate(R.layout.faq_child, null);
                holder = new ViewHolder();
                holder.question = (TextView) convertView.findViewById(R.id.question);
                holder.question.setTypeface(_ProximaNovaSemiBold);
                holder.arrow_down = (ImageButton) convertView.findViewById(R.id.arrow_down);
                holder.answer = (TextView) convertView.findViewById(R.id.answer);
                holder.answer.setTypeface(_ProximaNovaLight);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (list.get(position).click == false) {
                holder.arrow_down.setBackgroundResource(R.drawable.arrow_down);
                holder.answer.setVisibility(View.GONE);
            } else {
                holder.arrow_down.setBackgroundResource(R.drawable.arrow_up);
                holder.answer.setVisibility(View.VISIBLE);
            }

            holder.question.setText(list.get(position).question);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.get(position).click == false) {
                        holder.arrow_down.setBackgroundResource(R.drawable.arrow_up);
                        list.get(position).click = true;
                        notifyDataSetChanged();
                    } else if (list.get(position).click == true) {
                        holder.arrow_down.setBackgroundResource(R.drawable.arrow_down);
                        list.get(position).click = false;
                        notifyDataSetChanged();
                    }
                }
            });
            return convertView;
        }


        class ViewHolder {
            TextView question, answer;
            ImageButton arrow_down;
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
