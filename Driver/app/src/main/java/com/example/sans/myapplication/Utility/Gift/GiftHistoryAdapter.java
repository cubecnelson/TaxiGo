package com.example.sans.myapplication.Utility.Gift;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sans.myapplication.Client.Client;
import com.example.sans.myapplication.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sans on 23/10/15.
 */public class GiftHistoryAdapter extends RecyclerView.Adapter<GiftHistoryAdapter.ViewHolder> {
    private String[] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout mLinearLayout;
        public Context mContext;
        public TextView gift_name;
        public TextView gift_cost;
        public ImageView gift_image;
        public TextView gift_exchanged;
        public TextView time;
        public ViewHolder(LinearLayout v) {
            super(v);
            mLinearLayout = v;
            mContext = v.getContext();
            gift_name = (TextView) v.findViewById(R.id.gift_name);
            gift_cost = (TextView) v.findViewById(R.id.gift_cost);
            gift_image = (ImageView) v.findViewById(R.id.gift_image);
            gift_exchanged = (TextView) v.findViewById(R.id.gift_exchanged);
            time = (TextView) v.findViewById(R.id.time);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public GiftHistoryAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GiftHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gift_history_card, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder((LinearLayout) v);
        return vh;
    }



    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.mTextView.setText(mDataset[position]);

        SharedPreferences shares = holder.mContext.getSharedPreferences("SHARES",0);

        try {
            JSONObject data = new JSONObject(mDataset[position]);
            //holder.gift_name.setText("" + data.getString("name"));
            holder.gift_cost.setText("- " + data.getInt("cost") + "點");

            String date_string = data.getString("redeem_datetime").split("T")[0];

            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");

            try {
                Date date = ft.parse(date_string);
                Date curr = new Date();
                long diff = curr.getTime() - date.getTime();
                int days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                if(days > 1){
                    if(days < 30){
                        date_string = days + "日";
                    }else if(days < 365){
                        date_string = days/30 + "月";
                    }else{
                        date_string = days/365 + "年";
                    }
                    holder.time.setText(date_string+"前");
                }else{
                    holder.time.setText("今天");
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }




            final Client client = new Client();

            RequestParams params = new RequestParams();


            client.get("gifts/"+data.getInt("gift_id")+"?access_token="+shares.getString("ACCESS_TOKEN", "ERROR"), params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        holder.gift_name.setText(response.getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(holder.mContext, ""+e, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                    holder.gift_name.setText("" + error);
                }
            });

            if (data.getString("exchange_datetime").compareTo("null") == 0){
                holder.gift_image.setImageResource(R.color.white);
                holder.gift_exchanged.setText("未領取");
            }else{
                holder.gift_image.setImageResource(R.color.orange);
                holder.gift_exchanged.setTextColor(Color.WHITE);
                holder.gift_exchanged.setText("已領取");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }


}