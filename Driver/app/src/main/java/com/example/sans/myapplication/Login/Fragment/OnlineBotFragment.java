package com.example.sans.myapplication.Login.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sans.myapplication.Client.Client;
import com.example.sans.myapplication.Utility.MainActivity;
import com.example.sans.myapplication.R;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnlineBotFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OnlineBotFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnlineBotFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public JSONObject driver_data[];

    public Button online_button;

    public TextView online_name;

    public TextView online_id;

    public ImageView online_pp;

    private SharedPreferences shares;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OnlineBotFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OnlineBotFragment newInstance(String param1, String param2) {
        OnlineBotFragment fragment = new OnlineBotFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public OnlineBotFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_online_bot, container, false);
        online_button = (Button) view.findViewById(R.id.online_button);
        online_button.setOnClickListener(this);

        online_name = (TextView) view.findViewById(R.id.online_name);

        online_id = (TextView) view.findViewById(R.id.online_id);

        online_pp = (ImageView) view.findViewById(R.id.online_pp);

        final Client client = new Client();

        shares = getActivity().getSharedPreferences("SHARES", 0);

        RequestParams params = new RequestParams();
        params.put("driver_id", shares.getInt("ID", -1));

        driver_data = new JSONObject[1];

        client.addHeader("Authorization", shares.getString("ACCESS_TOKEN", "ERROR"));

        client.post("driver/getDriverData", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {


                    driver_data[0] = response.getJSONObject("data").getJSONObject("driver_data");

                    online_name.setText(driver_data[0].getString("name"));

                    online_id.setText(driver_data[0].getString("license"));


                    client.get(driver_data[0].getString("image"), new FileAsyncHttpResponseHandler(OnlineBotFragment.this.getActivity().getApplicationContext()) {
                        @Override
                        public void onFailure(int i, Header[] headers, Throwable throwable, File file) {

                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, final File response) {

                            try {
                                OnlineBotFragment.this.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Bitmap image = BitmapFactory.decodeFile(response.getPath());

                                        online_pp.setImageBitmap(image);

                                    }
                                });
                            }catch(NullPointerException e){

                            }

                        }


                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                Toast.makeText(OnlineBotFragment.this.getActivity(), "url: " + client.getAbsoluteUrl("driver/genVerificationCode"), Toast.LENGTH_LONG).show();
                Toast.makeText(OnlineBotFragment.this.getActivity(), "Status Code:" + statusCode + "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        final Client client = new Client();


        RequestParams params = new RequestParams();
        params.put("id", shares.getInt("ID", 0));
        params.put("status", 1);

        client.post("driver/setStatus", params, new JsonHttpResponseHandler());

        SharedPreferences.Editor editor = shares.edit();
        editor.putBoolean("ONLINE", true).commit();



        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivityForResult(i, 1);

        getActivity().finish();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
