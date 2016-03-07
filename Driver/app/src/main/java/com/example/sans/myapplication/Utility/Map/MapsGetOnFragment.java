package com.example.sans.myapplication.Utility.Map;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.sans.myapplication.Client.Client;
import com.example.sans.myapplication.R;
import com.example.sans.myapplication.Utility.Calendar.CalendarFragment;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapsGetOnFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapsGetOnFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapsGetOnFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView from_text;
    private TextView to_text;
    private TextView order_id;

    private Button gotOn;
    private Button cancel;
    private Button complete;

    private OnFragmentInteractionListener mListener;
    private SharedPreferences shares;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapsGetOnFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapsGetOnFragment newInstance(String param1, String param2) {
        MapsGetOnFragment fragment = new MapsGetOnFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MapsGetOnFragment() {
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

        shares = getActivity().getSharedPreferences("SHARES", 0);

        Bundle bundle = getActivity().getIntent().getExtras();
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_maps_get_on, container, false);

        from_text = (TextView) v.findViewById(R.id.from_text);
        to_text = (TextView) v.findViewById(R.id.to_text);
        order_id = (TextView) v.findViewById(R.id.order_id);
        JSONObject j = null;
        try {
            try {
                j = new JSONObject(bundle.getString("data", "ERROR"));
            }catch (JSONException e){
                j = new JSONObject(shares.getString("ORDER", "ERROR"));
            }
        cancel = (Button) v.findViewById(R.id.cancel);
            final JSONObject finalJ = j;
            cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Client client = new Client();

                client.addHeader("Authorization", shares.getString("ACCESS_TOKEN", "ERROR"));

                RequestParams params = new RequestParams();
                try {
                    params.put("order_id", finalJ.getJSONObject("order_data").getString("id"));
                    params.put("passenger_id", finalJ.getJSONObject("order_data").getString("passenger_id"));
                    params.put("driver_id", shares.getInt("ID", 0));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                client.post("order/cancelOrder", params, new JsonHttpResponseHandler());
                MapsGetOnFragment.this.getActivity().finish();
            }
        });
        complete = (Button) v.findViewById(R.id.complete);

        gotOn = (Button) v.findViewById(R.id.gotOn);

            gotOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsActivity mapsActivity = (MapsActivity) getActivity();
                mapsActivity.destination();
                final Client client = new Client();
                RequestParams params = new RequestParams();
                try {
                    params.put("order_id", finalJ.getJSONObject("order_data").getString("id"));
                    params.put("passenger_id", finalJ.getJSONObject("order_data").getString("passenger_id"));
                    params.put("driver_id", shares.getInt("ID", 0));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                client.addHeader("Authorization", shares.getString("ACCESS_TOKEN", "ERROR"));

                client.post("order/confirmOrder", params, new JsonHttpResponseHandler());
                cancel.setEnabled(false);
                cancel.setVisibility(View.INVISIBLE);
                gotOn.setEnabled(false);
                gotOn.setVisibility(View.INVISIBLE);
                complete.setEnabled(true);
                complete.setVisibility(View.VISIBLE);
                complete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RequestParams params = new RequestParams();
                        try {
                            client.addHeader("Authorization", shares.getString("ACCESS_TOKEN", "ERROR"));
                            params.put("order_id", finalJ.getJSONObject("order_data").getString("id"));
                            params.put("passenger_id", finalJ.getJSONObject("order_data").getString("passenger_id"));
                            params.put("driver_id", shares.getInt("ID", 0));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getActivity().finish();
                        client.post("order/completeOrder", params, new JsonHttpResponseHandler());

                        Intent intent = new Intent(getActivity(), CongratActivity.class);
                        startActivity(intent);

                    }
                });
            }
        });



            from_text.setText("" + j.getJSONObject("order_data").getJSONObject("start").getString("first_desc"));
            to_text.setText("" + j.getJSONObject("order_data").getJSONObject("end").getString("first_desc"));
            order_id.setText("" + j.getJSONObject("order_data").getString("id"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return v;
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
