package com.example.sans.myapplication.Login.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;


import com.example.sans.myapplication.AppController;
import com.example.sans.myapplication.Client.Client;
import com.example.sans.myapplication.Login.OnlineActivity;
import com.example.sans.myapplication.R;
import com.example.sans.myapplication.Utility.MainActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginBotFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginBotFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginBotFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Boolean IsGen;

    private Button key_button;
    private Spinner country_code;
    private EditText tel_no;
    private EditText verifyCode;
    private AppController controller;
    private SharedPreferences shares;
    private FrameLayout login_top;
    private FrameLayout login_bot;


    private OnFragmentInteractionListener mListener;
    public Button login_button;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginBotFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginBotFragment newInstance(String param1, String param2) {
        LoginBotFragment fragment = new LoginBotFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public LoginBotFragment() {
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
        View v = inflater.inflate(R.layout.fragment_login_bot, container, false);

        IsGen = false;

        shares = LoginBotFragment.this.getActivity().getSharedPreferences("SHARES", 0);


        country_code = (Spinner) v.findViewById(R.id.spinner);
        country_code.setSelection(1);

        tel_no = (EditText) v.findViewById(R.id.phone);

        verifyCode = (EditText) v.findViewById(R.id.password);

        login_button = (Button) v.findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(IsGen) {
                    verifyLogin(country_code.getSelectedItem().toString().substring(1), tel_no.getText().toString(), verifyCode.getText().toString());
                }else{

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                            View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_login, null);
                            builder.setView(view);

                            final Dialog dialog = builder.create();
                            ((TextView) view.findViewById(R.id.text)).setText("請先獲取驗證碼");
                            Button yes = (Button) view.findViewById(R.id.confirm);
                            yes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    dialog.dismiss();
                                }
                            });

                dialog.show();
                }
            }
        });

        key_button = (Button) v.findViewById(R.id.key_button);
        key_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                key_button.setEnabled(false);

                IsGen = true;

                new CountDownTimer(60000, 1000){

                    @Override
                    public void onTick(long millisUntilFinished) {
                        key_button.setText(""+millisUntilFinished/1000 + "秒後再獲取");
                    }

                    @Override
                    public void onFinish() {
                        key_button.setText("獲取驗證碼");
                        key_button.setEnabled(true);
                        IsGen = false;
                    }
                }.start();

                String countryCode[] = {"852", "853", "86"};

                RequestParams params = new RequestParams();
                params.put("country_code", countryCode[country_code.getSelectedItemPosition()]);
                params.put("tel", tel_no);

                final Client client = new Client();


                client.post("driver/genVerificationCode", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            String msg = response.getString("msg");

                       } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {

                    }
                });


            }
        });


        if(shares.getBoolean("LOGIN", false)){
            if(shares.getBoolean("ONLINE", false)){
             //   Intent i = new Intent(LoginBotFragment.this.getActivity(), MainActivity.class);
             //   startActivity(i);
            }else {
                OnlineBotFragment newFragment = new OnlineBotFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.setCustomAnimations(R.anim.fadein,
                        R.anim.fadeout);
                transaction.replace(R.id.login_bot, newFragment);
                transaction.addToBackStack(null);

                transaction.commit();
            }
        }


        return v;
    }


    public boolean verifyLogin(final String country_code, final String tel_no, final String verifyCode){
        final boolean[] valid = {false};

        RequestParams params = new RequestParams();
        params.put("country_code", country_code);
        params.put("tel", tel_no);
        params.put("verification", verifyCode);

        final Client client = new Client();

        client.post("driver/verifyVerificationCode", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String msg = response.getString("msg");
                    if (msg.compareTo("Success") == 0) {

                        SharedPreferences.Editor editor = shares.edit();
                        editor.putString("ACCESS_TOKEN", response.getJSONObject("data").getString("user_token"));
                        editor.putInt("ID", response.getJSONObject("data").getInt("id"));
                        editor.putBoolean("LOGIN", true);
                        editor.putBoolean("ONLINE", true);
                        editor.putString("DRIVER_DATA", response.getJSONObject("data").getJSONObject("driver_data").toString());
                        editor.commit();

                        final Client client = new Client();


                        RequestParams params = new RequestParams();
                        params.put("id", shares.getInt("ID", 0));
                        params.put("status", 1);

                        client.post("driver/setStatus", params, new JsonHttpResponseHandler());



                        Intent i = new Intent(getActivity(), MainActivity.class);
                        startActivityForResult(i, 1);

                    } else if(msg.compareTo("Driver not found") == 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_login, null);
                        builder.setView(view);

                        final Dialog dialog = builder.create();
                        ((TextView) view.findViewById(R.id.text)).setText("電話並沒有登記");
                        Button yes = (Button) view.findViewById(R.id.confirm);
                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    } else  {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_login, null);
                        builder.setView(view);

                        final Dialog dialog = builder.create();
                        ((TextView) view.findViewById(R.id.text)).setText("驗證碼錯誤");
                        Button yes = (Button) view.findViewById(R.id.confirm);
                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
            }
        });

        return valid[0];
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
