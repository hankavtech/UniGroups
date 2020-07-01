package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  {
    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<UniItem> uniItems;
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<UniItem> uniItems, Context mContext) {
        this.uniItems = uniItems;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: ");
        Glide.with(mContext)
                .asBitmap()
                .load(uniItems.get(position).getUniImage())
                .into(holder.uniImage);
        holder.uniName.setText(uniItems.get(position).getUniName());
        holder.uniType.setText(uniItems.get(position).getUniType());
        holder.state.setText(uniItems.get(position).getState());
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on  "+ uniItems.get(position));
                Intent intent=new Intent(mContext,UniTabbedActivity.class);
                intent.putExtra("tab",1);
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return uniItems.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView uniImage;
        TextView uniName;
        TextView state;
        TextView uniType;
        ConstraintLayout parentLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            uniImage=itemView.findViewById(R.id.uni_image);
            uniName=itemView.findViewById(R.id.uni_name);
            state=itemView.findViewById(R.id.state);
            uniType=itemView.findViewById(R.id.uniType);
            parentLayout=itemView.findViewById(R.id.uni_element_view);
        }
    }



}
