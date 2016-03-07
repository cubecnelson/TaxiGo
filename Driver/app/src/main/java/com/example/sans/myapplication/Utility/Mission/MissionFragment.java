package com.example.sans.myapplication.Utility.Mission;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sans.myapplication.Client.Client;
import com.example.sans.myapplication.R;
import com.example.sans.myapplication.Utility.MenuItemActivity;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MissionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MissionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MissionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView require_count;
    private TextView progress_count;

    private FrameLayout missionHistory;

    private OnFragmentInteractionListener mListener;


    private SharedPreferences shares;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MissionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MissionFragment newInstance(String param1, String param2) {
        MissionFragment fragment = new MissionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MissionFragment() {
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
        View v = inflater.inflate(R.layout.fragment_mission, container, false);

        //TextViews
        require_count = (TextView) v.findViewById(R.id.require_count);
        progress_count = (TextView) v.findViewById(R.id.progress_count);

        //Progress Bar
        final LinearLayout progress_fore = (LinearLayout) v.findViewById(R.id.progress_fore);
        final LinearLayout progress_back = (LinearLayout) v.findViewById(R.id.progress_back);
        final ViewGroup.LayoutParams foreParams = progress_fore.getLayoutParams();
        ViewGroup.LayoutParams backParams = progress_back.getLayoutParams();
        final ImageView car = (ImageView) v.findViewById(R.id.icon);



        shares = getActivity().getSharedPreferences("SHARES", 0);

        final Client client = new Client();



        RequestParams params = new RequestParams();
        int user_id = 0;

        try {
            JSONObject data = new JSONObject(shares.getString("DRIVER_DATA", "ERROR"));
            user_id = data.getInt("userId");
            params.put("id", user_id);
            params.put("access_token", shares.getString("ACCESS_TOKEN", "ERROR"));
        } catch (JSONException e) {
            e.printStackTrace();
        }



        final JSONObject[] driver_data = new JSONObject[1];

        client.addHeader("Authorization", shares.getString("ACCESS_TOKEN", "ERROR"));

        client.get("mission_progresses/", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray res) {
                try {
                    JSONObject response = (JSONObject) res.get(res.length()-1);
                    int progress_c = response.getInt("progress_count");
                    int require_c = response.getInt("required_count");
                    require_count.setText(""+require_c);
                    progress_count.setText(""+progress_c);

                    final double progress = progress_c;

                    final int length = progress_back.getLayoutParams().width;

                    Thread t = new Thread(new Runnable() {
                        int add = 2;

                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                                for (int i = 160; i <= length * progress; i += add * progress) {
                                    foreParams.width = (int) (i);
                                    final int num = i;
                                    if(getActivity() != null)
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override

                                        public void run() {
                                            progress_fore.setLayoutParams(new FrameLayout.LayoutParams(foreParams));
                                            if (num == length) {
                                                car.setImageResource(R.drawable.menu_mission);
                                                car.setColorFilter(R.color.orange);
                                            }
                                        }
                                    });
                                    try {
                                        Thread.sleep(1);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    );
                    t.start();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                Toast.makeText(MissionFragment.this.getActivity(), "Status Code:" + statusCode + "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });





        //Mission History Button
        missionHistory = (FrameLayout) v.findViewById(R.id.mission_history);
        missionHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MenuItemActivity.class);
                i.putExtra("MENU_ITEM", R.id.gift_history);
                getActivity().startActivity(i);
                getActivity().overridePendingTransition(R.anim.activity_slide_in1, R.anim.activity_slide_in2);

            }
        });


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
