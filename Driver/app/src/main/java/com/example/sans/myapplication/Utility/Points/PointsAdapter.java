package com.example.sans.myapplication.Utility.Points;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sans.myapplication.Client.Client;
import com.example.sans.myapplication.R;
import com.example.sans.myapplication.Utility.Calendar.CalendarFragment;
import com.example.sans.myapplication.Utility.MenuItemActivity;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sans on 23/10/15.
 */public class PointsAdapter extends RecyclerView.Adapter<PointsAdapter.ViewHolder> {
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
        public Button redeem_button;
        public ViewHolder(LinearLayout v) {
            super(v);
            mLinearLayout = v;
            mContext = v.getContext();
            gift_name = (TextView) v.findViewById(R.id.gift_name);
            gift_cost = (TextView) v.findViewById(R.id.gift_cost);
            gift_image = (ImageView) v.findViewById(R.id.gift_image);
            redeem_button = (Button) v.findViewById(R.id.redeem_button);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PointsAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PointsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.points_card, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder((LinearLayout) v);
        return vh;
    }



    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final SharedPreferences shares = holder.mContext.getSharedPreferences("SHARES", 0);

        try {
            final JSONObject data = new JSONObject(mDataset[position]);
            holder.gift_name.setText("" + data.getString("name"));
            holder.gift_cost.setText("" + data.getInt("cost"));

            final Client client = new Client();

            client.get(data.getString("image"), new FileAsyncHttpResponseHandler(holder.mLinearLayout.getContext()) {
                @Override
                public void onFailure(int i, Header[] headers, Throwable throwable, File file) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, final File response) {

                            Bitmap image = BitmapFactory.decodeFile(response.getPath());

                            holder.gift_image.setImageBitmap(image);


                }
            });

            holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = (Activity) holder.mContext;
                    Intent i = new Intent(activity, MenuItemActivity.class);
                    i.putExtra("MENU_ITEM", 1);
                    i.putExtra("GIFT_DATA", data.toString());
                    activity.startActivity(i);
                    activity.overridePendingTransition(R.anim.activity_slide_in1, R.anim.activity_slide_in2);
                }
            });

            holder.redeem_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Client client = new Client();

                    final RequestParams params = new RequestParams();


                    try {
                        params.put("giftId", data.getInt("id"));

                        client.addHeader("Authorization", shares.getString("ACCESS_TOKEN", "ERROR"));


                        client.post("gifts/redeem", params, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Toast.makeText(holder.mContext, "Redeemed", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                                Log.i("Status Code", "" + statusCode);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }
            });

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