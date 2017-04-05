package com.signity.bonbon.ui.shopping;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.signity.bonbon.Utilities.AnimUtil;
import com.signity.bonbon.Utilities.AppConstant;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.app.AppController;

import java.util.ArrayList;
import java.util.List;


public class ShoppingList extends Fragment implements View.OnClickListener {

    RelativeLayout listHeader, btnBackLayout;
    ListView shoppingList;
    EditText searchBar;
    Button add_list, backButton, btnSearch;
    List<String> viewList = new ArrayList<String>();
    Adapter adapter;
    String temp = null;
    ListDatabase db;
    private PrefManager prefManager;
    String from;

    public Typeface _ProximaNovaLight, _ProximaNovaSemiBold;


    View mView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(getActivity());
    }

    public static Fragment newInstance(Context context) {
        return Fragment.instantiate(context,
                ShoppingList.class.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(com.signity.bonbon.R.layout.shopping_list, container, false);
        initialize();

        adapter = new Adapter(getActivity());
        shoppingList.setAdapter(adapter);

        add_list.setOnClickListener(this);
        backButton.setOnClickListener(this);

        return mView;
    }


    private void initialize() {


        listHeader = (RelativeLayout) mView.findViewById(com.signity.bonbon.R.id.listHeader);
        backButton = (Button) mView.findViewById(com.signity.bonbon.R.id.backButton);
//        btnSearch = (Button) mView.findViewById(R.id.btnSearch);
        shoppingList = (ListView) mView.findViewById(com.signity.bonbon.R.id.shopping_list);
        add_list = (Button) mView.findViewById(com.signity.bonbon.R.id.add_list);
        searchBar = (EditText) mView.findViewById(com.signity.bonbon.R.id.searchBar);

        if (getArguments() != null) {
            listHeader.setVisibility(View.VISIBLE);
        } else {
            listHeader.setVisibility(View.GONE);
        }

        _ProximaNovaLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        _ProximaNovaSemiBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Bold.ttf");

        db = new ListDatabase(getActivity());
        viewList = db.getAllContacts();

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case com.signity.bonbon.R.id.add_list:
                String name = searchBar.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Add List to Add Item", Toast.LENGTH_SHORT).show();
                } else {
                    if (name.equalsIgnoreCase(temp)) {
                        Toast.makeText(getActivity().getApplicationContext(), "Item Already Added", Toast.LENGTH_SHORT).show();
                    } else {
                        db.addContact(name);
                        viewList = db.getAllContacts();
                        adapter.notifyDataSetChanged();
                        temp = name;
                        searchBar.setText("");
                    }

                }
                View v = getActivity().getCurrentFocus();
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                break;

            case com.signity.bonbon.R.id.backButton:

                getActivity().onBackPressed();
                AnimUtil.slideFromLeftAnim(getActivity());

                break;

            case com.signity.bonbon.R.id.btnSearch:
                startActivity(new Intent(getActivity(), AppController.getInstance().getViewController().getSearchActivity()));
                AnimUtil.slideFromRightAnim(getActivity());

                break;
        }
    }

    class Adapter extends BaseAdapter {
        Activity context;
        LayoutInflater l;

        public Adapter(Activity context) {

            this.context = context;
            l = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return viewList.size();
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
                convertView = l.inflate(com.signity.bonbon.R.layout.shopping_list_child, null);
                holder = new ViewHolder();
                holder.itemName = (TextView) convertView.findViewById(com.signity.bonbon.R.id.itemName);
                holder.remove_button = (Button) convertView.findViewById(com.signity.bonbon.R.id.removebutton);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.itemName.setText(viewList.get(position));

            holder.remove_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.deleteContact(viewList.get(position));
                    viewList.remove(position);
                    notifyDataSetChanged();
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentSearch = new Intent(getActivity(), AppController.getInstance().getViewController().getSearchActivity());
                    intentSearch.putExtra("search_str", viewList.get(position));
                    startActivity(intentSearch);

                }
            });


            return convertView;
        }


        class ViewHolder {

            TextView itemName;
            Button remove_button;
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
