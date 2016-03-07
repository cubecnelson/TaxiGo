package com.example.sans.myapplication.Utility.EditProfile;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sans.myapplication.Client.Client;
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
 * {@link EditProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SharedPreferences shares;


    private ImageView online_pp;

    private EditText phone;

    private EditText surname;

    private EditText given;

    private EditText license;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        shares = getActivity().getSharedPreferences("SHARES", 0);


        final Client client = new Client();



        RequestParams params = new RequestParams();

        try {
            JSONObject data = new JSONObject(shares.getString("DRIVER_DATA", "ERROR"));
            params.put("country_code", data.getString("country_code"));
            params.put("tel", data.getString("phone"));
            params.put("verification", data.getString("verification"));
        } catch (JSONException e) {
            e.printStackTrace();
        }



        final JSONObject[] driver_data = new JSONObject[1];

        client.addHeader("Authorization", shares.getString("ACCESS_TOKEN", "ERROR"));

        client.post("driver/verifyVerificationCode", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    try {

                        driver_data[0] = response.getJSONObject("data").getJSONObject("driver_data");

                        phone.setText(driver_data[0].getString("phone"));

                        surname.setText(driver_data[0].getString("family_name"));

                        given.setText(driver_data[0].getString("given_name"));

                        license.setText(driver_data[0].getString("license"));

                        client.get(driver_data[0].getString("image"), new FileAsyncHttpResponseHandler(EditProfileFragment.this.getActivity().getApplicationContext()) {
                            @Override
                            public void onFailure(int i, Header[] headers, Throwable throwable, File file) {

                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, final File response) {


                                EditProfileFragment.this.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Bitmap image = BitmapFactory.decodeFile(response.getPath());

                                        online_pp.setImageBitmap(image);

                                    }
                                });


                            }


                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                Toast.makeText(EditProfileFragment.this.getActivity(), "Status Code:" + statusCode + "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        online_pp = (ImageView) v.findViewById(R.id.online_pp);

        phone = (EditText) v.findViewById(R.id.phone);

        surname = (EditText) v.findViewById(R.id.surname);

        given = (EditText) v.findViewById(R.id.given);

        license = (EditText) v.findViewById(R.id.license);

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
