package com.example.sans.myapplication.Utility.OrderList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sans.myapplication.AppController;
import com.example.sans.myapplication.Client.Client;
import com.example.sans.myapplication.Login.OnlineActivity;
import com.example.sans.myapplication.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrderListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrderListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderListFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MyReceiver receiver;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<String> items;

    private Button offline_button;

    private LinearLayout no_order;

    private OnFragmentInteractionListener mListener;

    private SharedPreferences shares;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderListFragment newInstance(String param1, String param2) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public OrderListFragment() {
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
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("taxigo.api.aquireOrderService");
        getActivity().registerReceiver(receiver, filter);
        //shares
        shares = getActivity().getSharedPreferences("SHARES", 0);

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        final Client client = new Client();

        final RequestParams params = new RequestParams();
        params.put("id", shares.getInt("ID", 0));

        final JSONObject[] driver_data = new JSONObject[1];

        final int status_code[] = new int[1];

        status_code[0] = 689;

        items = new ArrayList<String>();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.order_recycler_view);


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);


        // specify an adapter (see also next example)
        mAdapter = new OrderListAdapter(items);
        mRecyclerView.setAdapter(mAdapter);

        client.addHeader("Authorization", shares.getString("ACCESS_TOKEN", "ERROR"));

        client.post("OrderDistributor/aquireOrders", params, new JsonHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray order_array = response.getJSONObject("data").getJSONArray("order_list");

                    for (int i = 0; i < order_array.length(); i++) {
                        items.add(order_array.get(i).toString());
                    }
                } catch (JSONException e) {
                    Toast.makeText(OrderListFragment.this.getActivity(), "" + e, Toast.LENGTH_SHORT).show();
                }
                mAdapter = new OrderListAdapter(items);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mAdapter);

                no_order = (LinearLayout) view.findViewById(R.id.no_order);
                if (items.size() == 0) {

                    no_order.setVisibility(View.VISIBLE);
                } else {
                    no_order.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {

            }
        });




        offline_button = (Button) view.findViewById(R.id.offline_button);

        offline_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Client client = new Client();
                RequestParams params = new RequestParams();
                params.put("id", shares.getInt("ID", 0));
                params.put("status", 999);
                client.post("driver/setStatus", params, new JsonHttpResponseHandler());

                Intent i = new Intent(getActivity(), OnlineActivity.class);
                startActivity(i);
                SharedPreferences.Editor editor = shares.edit();
                editor.putBoolean("ONLINE", false);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("login", 1);
                getActivity().setResult(Activity.RESULT_OK, resultIntent);
                getActivity().finish();

            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        Toast.makeText(getActivity(), "Offline", Toast.LENGTH_LONG).show();
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
        getActivity().unregisterReceiver(receiver);
        mListener = null;
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

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

    public class MyReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {


            try {
                //Toast.makeText(getActivity(), "Updating OrderList", Toast.LENGTH_SHORT).show();

                JSONArray order_array = (new JSONObject(intent.getStringExtra("order_data"))).getJSONArray("order_list");
                items = new ArrayList<String>();
                for (int i = 0; i < order_array.length(); i++) {
                    items.add(order_array.get(i).toString());
                }
                mAdapter = new OrderListAdapter(items);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mAdapter);

                if (items.size() == 0) {
                    no_order.setVisibility(View.VISIBLE);

                } else {
                    no_order.setVisibility(View.INVISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

}
