package com.example.sans.myapplication.Utility.OrderList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sans.myapplication.Client.Client;
import com.example.sans.myapplication.R;
import com.example.sans.myapplication.Utility.Map.MapsActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sans on 23/10/15.
 */public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {
    private ArrayList<String> mDataset;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout mLinearLayout;
        public Button mButton;
        public TextView from_text;
        public TextView to_text;
        public TextView remarks;
        public Context mContext;
        public LinearLayout remarks_area;
        public ViewHolder(LinearLayout v) {
            super(v);
            mLinearLayout = v;
            mContext = v.getContext();

            remarks_area = (LinearLayout) v.findViewById(R.id.remarks_area);
            remarks = (TextView) v.findViewById(R.id.remarks);
            mButton = (Button) v.findViewById(R.id.order_button);
            from_text = (TextView) v.findViewById(R.id.from_text);
            to_text = (TextView) v.findViewById(R.id.to_text);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public OrderListAdapter(ArrayList<String> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public OrderListAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orderlist_card, parent, false);
        // set the view's size, margins, paddings and layout parameters



        ViewHolder vh = new ViewHolder((LinearLayout) v);
        vh.mContext = parent.getContext();


        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element'
        holder.from_text.setText(mDataset.get(position));

        final SharedPreferences shares = holder.mContext.getSharedPreferences("SHARES", 0);

        try {
            final JSONObject order = new JSONObject(mDataset.get(position));
            holder.from_text.setText("" + order.getJSONObject("start").getString("first_desc"));
            holder.to_text.setText("" + order.getJSONObject("end").getString("first_desc"));
            final String order_id = order.getString("id");

            holder.mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                    public void onClick(View v) {
                    mDataset.clear();
                    OrderListAdapter.this.notifyDataSetChanged();
                    final Client client = new Client();

                    final RequestParams params = new RequestParams();


                    params.put("orderId", order_id);



                    client.addHeader("Authorization", shares.getString("ACCESS_TOKEN", "ERROR"));


                    client.post("OrderDistributor/takeOrder", params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                            Log.i("Status Code", "" + statusCode);
                        }
                    });
                }
            });
            if(order.getInt("require_vip") != 0){
                holder.remarks_area.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            Log.i("OrderList: ", e.getMessage());

        }


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}