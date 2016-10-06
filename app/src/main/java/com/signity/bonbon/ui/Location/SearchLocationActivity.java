package com.signity.bonbon.ui.Location;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.signity.bonbon.R;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.listener.CallbackManager;
import com.signity.bonbon.model.AddressModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SearchLocationActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    EditText choose_location_edit;

    ImageButton location_fragment_clear_imgbtn;

//    String browserKey = "AIzaSyCkFTHz-KMRTpXv79uhejSpdP8Ylh8aBPU";
    String browserKey = "AIzaSyCpjiKUzlcsfoDwZBraxcGKqnHTKaV6Jfg";
    ImageButton mBack;

    View createdView;

    PrefManager mPrefManager;

    CallbackManager mCallbackManager;

    ArrayList<AddressModel> mainAl=new ArrayList<>();

    ListView choose_location_listview;

    ArrayAdapter<AddressModel> adapter;

    TextView use_my_location_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);
        mCallbackManager= (CallbackManager) this;


        init();
        setAllClickListeners();
    }

    private void setAllClickListeners() {
        mBack.setOnClickListener(this);
        use_my_location_text.setOnClickListener(this);
        location_fragment_clear_imgbtn.setOnClickListener(this);

        choose_location_listview.setOnItemClickListener(this);
        choose_location_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                new AutoCompleteAsync(s.toString()).execute();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void init() {

        mPrefManager=new PrefManager(SearchLocationActivity.this);
        location_fragment_clear_imgbtn=(ImageButton) createdView.findViewById(R.id.location_fragment_clear_imgbtn);

        use_my_location_text=(TextView)createdView.findViewById(R.id.use_my_location_text);

        choose_location_edit = (EditText) createdView.findViewById(R.id.choose_location_edit);
        mBack = (ImageButton) createdView.findViewById(R.id.backBtn);
        choose_location_listview=(ListView)createdView.findViewById(R.id.choose_location_listview);

        adapter=new ArrayAdapter<AddressModel>(SearchLocationActivity.this, android.R.layout.simple_list_item_1, mainAl){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view= super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setText(mainAl.get(position).location);
                return view;
            }
        };

        choose_location_listview.setAdapter(adapter);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.backBtn:
                onBackPressed();
                break;

            case R.id.location_fragment_clear_imgbtn:
                choose_location_edit.setText("");
                break;

            case R.id.use_my_location_text:
                mCallbackManager.useMyLocation();
                onBackPressed();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        new LatLongAsync(mainAl.get(position).place_id, position).execute();
        mCallbackManager.setLocation(mainAl.get(position).location);
        onBackPressed();
    }

    class AutoCompleteAsync extends AsyncTask<Void, Void, Void> {

        String input;
        public AutoCompleteAsync(String input){
            this.input=input;
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection conn = null;
            StringBuilder jsonResults = new StringBuilder();
            try {

                String urlString = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
                        + input +
                        "&sensor=true&key=" + browserKey;

                URL url = new URL(urlString.toString());
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());


                // Load the results into a StringBuilder

                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }

            } catch (Exception e) {

                e.printStackTrace();
            } finally {
                if (conn != null) {

                    conn.disconnect();
                }
            }
            try {


                JSONObject jsonObj = new JSONObject(jsonResults.toString());
                JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

                mainAl.clear();

                for (int i = 0; i < predsJsonArray.length(); i++) {
                    System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                    System.out.println("============================================================");

                    AddressModel mAddressModel=new AddressModel();
                    mAddressModel.location=predsJsonArray.getJSONObject(i).getString("description");
                    mAddressModel.place_id=predsJsonArray.getJSONObject(i).getString("place_id");

                    mainAl.add(mAddressModel);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
        }
    }

    class LatLongAsync extends AsyncTask<Void, Void, Void>{


        String place_id;

        public LatLongAsync(String place_id, int position){
            this.place_id=place_id;
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection conn = null;
            StringBuilder jsonResults = new StringBuilder();
            try {

                String urlString = "https://maps.googleapis.com/maps/api/place/details/json?key="+browserKey+"&placeid="+place_id;

                URL url = new URL(urlString.toString());
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());


                // Load the results into a StringBuilder

                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }

            } catch (Exception e) {

                e.printStackTrace();
            } finally {
                if (conn != null) {

                    conn.disconnect();
                }
            }
            try {


                JSONObject jsonObject = new JSONObject(jsonResults.toString());
                JSONObject jsonObject1= jsonObject.getJSONObject("result");
                JSONObject jsonObject2= jsonObject1.getJSONObject("geometry");
                JSONObject jsonObject3= jsonObject2.getJSONObject("location");

//                mPrefManager.storeSharedValue(PrefManager.LATITUDE_KEY, jsonObject3.getString("lat"));
//                mPrefManager.storeSharedValue(PrefManager.LONGITUDE_KEY, jsonObject3.getString("lng"));


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }





}
