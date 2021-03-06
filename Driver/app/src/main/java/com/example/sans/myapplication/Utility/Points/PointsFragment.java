package com.example.sans.myapplication.Utility.Points;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sans.myapplication.Client.Client;
import com.example.sans.myapplication.R;
import com.example.sans.myapplication.Utility.Gift.GiftHistoryFragment;
import com.example.sans.myapplication.Utility.MenuItemActivity;
import com.example.sans.myapplication.Utility.Service.ServiceFragment;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link } interface
 * to handle interaction events.
 * Use the {@link } factory method to
 * create an instance of this fragment.
 */
public class PointsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String[] items;
    private SharedPreferences shares;

    private OnFragmentInteractionListener mListener;


    private FrameLayout giftHistory;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PointsFragment newInstance(String param1, String param2) {
        PointsFragment fragment = new PointsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PointsFragment() {
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

        final Client client = new Client();

        shares = getActivity().getSharedPreferences("SHARES", 0);

        RequestParams params = new RequestParams();

        client.addHeader("Authorization", shares.getString("ACCESS_TOKEN", "ERROR"));

        client.post("gifts/getAvailableGifts", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONObject("data").getJSONArray("available_gifts");
                    items = new String[jsonArray.length()];
                    for(int i = 0; i < jsonArray.length(); i++){
                        items[i] = jsonArray.get(i).toString();
                    }
                    mAdapter = new PointsAdapter(items);
                    mRecyclerView.setAdapter(mAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(PointsFragment.this.getActivity(), ""+e, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                Toast.makeText(PointsFragment.this.getActivity(), ""+error, Toast.LENGTH_SHORT).show();
            }
        });

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_points , container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.points_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)


        giftHistory = (FrameLayout) view.findViewById(R.id.gift_history);

        giftHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), MenuItemActivity.class);
                i.putExtra("MENU_ITEM", R.id.gift_history);
                getActivity().startActivity(i);
                getActivity().overridePendingTransition(R.anim.activity_slide_in1, R.anim.activity_slide_in2);
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event


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
