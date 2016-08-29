package au.com.jamesfrizelles.testdriveregister;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Koko on 29/08/2016.
 */
public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder> {
    private ArrayList<String> textList, imageList;
    private Context context;
    private String TAG;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView userTextView;
        public ImageView iconImageView;
        public LinearLayout layout;
        public ViewHolder(View v) {
            super(v);
            userTextView = (TextView) v.findViewById(R.id.userTextView);
            iconImageView = (ImageView) v.findViewById(R.id.iconImageView);
            layout = (LinearLayout) v.findViewById(R.id.profileListLayout);
        }
    }

    public CustomRecyclerAdapter(Context context, ArrayList<String> textList, ArrayList<String> imageList ) {
        this.textList = textList ;
        this.imageList = imageList;
        this.context = context;
        TAG = "CustomRecyclerAdapter";
    }

    @Override
    public CustomRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.activity_profile_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        // set the view's size, margins, paddings and layout parameters
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomRecyclerAdapter.ViewHolder holder, int position) {

        holder.userTextView.setText(textList.get(position));
        switch (imageList.get(position)){
            case "email":
                holder.iconImageView.setImageResource(R.drawable.ic_email);
                break;
            case "phone":
                holder.iconImageView.setImageResource(R.drawable.ic_phone);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return textList.size();
    }
}
