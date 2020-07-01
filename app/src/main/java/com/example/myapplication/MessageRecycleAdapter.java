package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MessageRecycleAdapter extends RecyclerView.Adapter<MessageRecycleAdapter.ViewHolder> {
    List<String> messages;
    private static int received_message_type=0;
    private static int sent_message_type=1;
    ActionMode action_mode;
    FragmentActivity ctx;
    Integer item_id=0;
    final ArrayList<String>selected_messages=new ArrayList<>();

    MessageRecycleAdapter(FragmentActivity ctx, List<String> messages){
        this.messages=messages;
        this.ctx=ctx;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder view_holder = null;
        View view;
        if(viewType==0){
            view=LayoutInflater.from(parent.getContext()).inflate(R.layout.message_text_container,parent,false);
            view_holder=new ViewHolder(view);
        }
        else {
            view=LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_message_text_container,parent,false);
            view_holder=new ViewHolder(view);
        }

        return view_holder;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final String str_position=String.valueOf(position);
        holder.time_field.setText("19:30");
        holder.message.setText(messages.get(position));
        if(selected_messages.contains(String.valueOf(position))) {
            holder.message_text_container.setBackgroundResource(R.color.message_selected_background);
        }
        else{
            holder.message_text_container.setBackgroundResource(R.color.chat_background);
        }
        //holder.(R.color.chat_background);


       /*holder.rowLayout.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View view) {

                     if(selected_messages.size()==0){
                view.setBackgroundResource(R.color.message_selected_background);
                Toast.makeText(ctx, "you are choking me"+String.valueOf(position), Toast.LENGTH_SHORT).show();
                selected_messages.add(String.valueOf(position));
                control_action_menu();
                return true;
            }
            return  false;

           }
       });











        holder.rowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected_messages.size()>=1){
                    if(selected_messages.contains(String.valueOf(position))){
                        view.setBackgroundColor(view.getSolidColor());
                        selected_messages.remove(String.valueOf(position));
                        if(selected_messages.size()==0){
                            action_mode.finish();
                        }
                        Toast.makeText(ctx, "you released me", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        selected_messages.add(String.valueOf(position));
                        Toast.makeText(ctx, "you are adding me", Toast.LENGTH_SHORT).show();
                        view.setBackgroundResource(R.color.message_selected_background);
                    }
                }

            }
        });*/











    }

    public boolean control_action_menu(){
        if(action_mode!=null){
            Log.i(TAG, "onLongClick: "+"i was clicked loooooong");
            return false;
        }

        action_mode=ctx.startActionMode(action_view_menu);
        return true;
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position%2==0){
            return 0;
        }
        else{
            return 1;
        }
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView time_field;
        TextView message;
        ConstraintLayout rowLayout;
        ConstraintLayout message_text_container;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //itemView.setId(item_id);
            //item_id++;
            time_field=itemView.findViewById(R.id.time_field);
            message=itemView.findViewById(R.id.received_message);
            rowLayout=itemView.findViewById(R.id.message_container);
            message_text_container=itemView.findViewById(R.id.message_text_container);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(selected_messages.size()>=1){
                if(selected_messages.contains(String.valueOf(getAdapterPosition()))){
                    view.setBackgroundColor(view.getSolidColor());
                    selected_messages.remove(String.valueOf(getAdapterPosition()));
                    if(selected_messages.size()==0){
                        action_mode.finish();
                    }
                    Toast.makeText(ctx, "you released me", Toast.LENGTH_SHORT).show();
                }
                else {
                    selected_messages.add(String.valueOf(getAdapterPosition()));
                    Toast.makeText(ctx, "you are adding me", Toast.LENGTH_SHORT).show();
                    view.setBackgroundResource(R.color.message_selected_background);
                }
            }
        }


        @Override
        public boolean onLongClick(View view) {
            if(selected_messages.size()==0){
                Log.i(TAG, "onLongClick: "+((TextView)view.findViewById(R.id.received_message)).getText().toString());
                view.setBackgroundResource(R.color.message_selected_background);
                Toast.makeText(ctx, "you are choking me"+String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
                selected_messages.add(String.valueOf(getAdapterPosition()));
                Log.i(TAG, "onLongClick: item id"+String.valueOf(getItemId()));
                Log.i(TAG, "onLongClick position: "+String.valueOf(getAdapterPosition()));
                try {
                    control_action_menu();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                return true;
            }
            return  false;
        }
    }


    //action mode callback
    ActionMode.Callback action_view_menu=new ActionMode.Callback(){

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {

            ctx.getMenuInflater().inflate(R.menu.message_selected_menu,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if(menuItem.getItemId()==R.id.delete_item){

                ArrayList<String>msgs_to_delete=new ArrayList<>();
                msgs_to_delete.addAll(selected_messages);
                Log.i(TAG, "onActionItemClicked: "+"size od fdeting"+String.valueOf(msgs_to_delete.size()));

                for(String str_pos:msgs_to_delete){
                    messages.remove(Integer.parseInt(str_pos));
                    notifyItemRemoved(Integer.parseInt(str_pos));
                    //msgs_to_delete.remove(str_pos);
                    Log.i(TAG, "onActionItemClicked: "+"deleting item"+str_pos);
                }




                //notifyDataSetChanged();
                selected_messages.clear();

                action_mode.finish();
                Toast.makeText(ctx,selected_messages.size()+"messages deleted",Toast.LENGTH_LONG);
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            action_mode=null;

        }


    };




}
