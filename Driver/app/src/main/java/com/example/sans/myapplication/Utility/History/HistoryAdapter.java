package com.example.sans.myapplication.Utility.History;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sans.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by sans on 23/10/15.
 */public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private String[] mDataset;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout mLinearLayout;
        public TextView from_text;
        public TextView to_text;
        public TextView time_text;
        public ViewHolder(LinearLayout v) {
            super(v);
            mLinearLayout = v;
            from_text = (TextView) v.findViewById(R.id.from_text);
            to_text = (TextView) v.findViewById(R.id.to_text);
            time_text = (TextView) v.findViewById(R.id.time_text);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public HistoryAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_card, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder((LinearLayout) v);
        return vh;
    }



    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.mTextView.setText(mDataset[position]);

        try {
            final JSONObject data = new JSONObject(mDataset[position]);
            holder.from_text.setText(data.getJSONObject("start").getString("sec_desc"));
            holder.to_text.setText(data.getJSONObject("end").getString("sec_desc"));
            try {
                holder.time_text.setText(data.getString("complete_time").split("T")[0].replace("-", "/") + " " + data.getJSONObject("end").getString("date").split("T")[1].split(":")[0] + ":" + data.getJSONObject("end").getString("date").split("T")[1].split(":")[1]);
            }catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
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