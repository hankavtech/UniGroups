package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Constraints;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class GroupChatFragment extends Fragment {

    ArrayList<String> messages;
    //ListView lview;
    RecyclerView recycle_view;
    LinearLayoutManager linear_manager;
    ImageView m_sender;
    EditText input_message;
    ItemTouchHelper touch_helper;
    MessageRecycleAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messages=new ArrayList<>();
        messages.add("i am 1");
        messages.add("i M 2");
        messages.add("i am 3");
        messages.add("i am 4");
        messages.add("i am 5");
        messages.add("i am 6");
        messages.add("i am 7");
        messages.add("i am 8");
        messages.add("i am 9");
        messages.add("i am 10");
        messages.add("i am 11");
        messages.add("i am 12");
        messages.add("i am 13");
        messages.add("i am 14");
        messages.add("i am 15");
        messages.add("i am 16");
        messages.add("i am 17");
        messages.add("i am 18");
        messages.add("i am 19");
        messages.add("i am 20");
        messages.add("i am 21");
        messages.add("i am 22");
        messages.add("i am 23");
        messages.add("i am 24");
        messages.add("i am 25");
        messages.add("i am 26");
        messages.add("i am 27");
        messages.add("i am 28");
        messages.add("i am 29");
        View rootView=getLayoutInflater().inflate(R.layout.fragment_group_chat,null);
        recycle_view= rootView.findViewById(R.id.messages_recycle_view);
        //recycle_view.offsetChildrenVertical(10);
        input_message=rootView.findViewById(R.id.input_message);
        adapter=new MessageRecycleAdapter(getActivity(),messages);
        recycle_view.setAdapter(adapter);
        linear_manager=new LinearLayoutManager(getContext());
        //linear_manager.setReverseLayout(false);
        linear_manager.scrollToPosition(recycle_view.getAdapter().getItemCount()-1);
        //linear_manager.setStackFromEnd(false);
        recycle_view.setLayoutManager(linear_manager);

        m_sender=rootView.findViewById(R.id.message_sender);
        input_message=rootView.findViewById(R.id.input_message);
        touch_helper=new ItemTouchHelper(remove_message_callback);
        touch_helper.attachToRecyclerView(recycle_view);


        m_sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message= input_message.getText().toString();
                messages.add(message);
                adapter.notifyDataSetChanged();
                linear_manager.scrollToPosition(recycle_view.getAdapter().getItemCount()-1);
                input_message.setText("");
            }
        });


        recycle_view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    recycle_view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recycle_view.scrollToPosition(recycle_view.getAdapter().getItemCount()-1);
                        }
                    }, 100);
                }


            }
        });


        return rootView;
    }

    ItemTouchHelper.SimpleCallback remove_message_callback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position=viewHolder.getAdapterPosition();
            final String deleted_text=messages.get(position);
            switch (direction){
                case ItemTouchHelper.RIGHT:
                    messages.remove(position);
                    adapter.notifyItemRemoved(position);
                    Log.i(TAG, "onSwiped: "+String.valueOf(position));
                    break;
                case ItemTouchHelper.LEFT:
                    Toast.makeText(getContext(),"reply to mesage",Toast.LENGTH_LONG);
                    break;
            }


            Snackbar.make(recycle_view,"Message deleted",Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    messages.add(position,deleted_text);
                    adapter.notifyItemInserted(position);
                    linear_manager.scrollToPosition(recycle_view.getAdapter().getItemCount()-1);
                    Log.i(TAG, "onClick: "+String.valueOf(position));
                }
            }).show();


        }
    };


    //callback for Action view on hold message






    //create options for message selected


    /* class MyAdapter extends ArrayAdapter<String> {
        ArrayList<String> msgs;

        public MyAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<String> objects) {
            super(context, resource, textViewResourceId, objects);
            this.msgs= (ArrayList<String>) objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if(convertView==null){
                if(position%2==0) {
                    convertView = getLayoutInflater().inflate(R.layout.message_text_container, null);
                }
                else{
                    convertView = getLayoutInflater().inflate(R.layout.sent_message_text_container, null);
                }
            }

            TextView r_message=convertView.findViewById(R.id.received_message);
            TextView time_view=convertView.findViewById(R.id.time_field);;
            int width=r_message.getWidth();
            int height= r_message.getHeight();
            r_message.setText(msgs.get(position));
            time_view.setText("11:30");

            //ConstraintSet constraints=new ConstraintSet();
            //ConstraintLayout layout=(ConstraintLayout)time_view.getParent();
            //constraints.clone(layout);
            //constraints.connect(R.id.time_field,ConstraintSet.TOP,R.id.received_message_container,ConstraintSet.BOTTOM,2);
            //constraints.connect(R.id.time_field,ConstraintSet.START,ConstraintSet.PARENT_ID,ConstraintSet.START,width);
            //ViewGroup.MarginLayoutParams params= (ViewGroup.MarginLayoutParams) layout.getLayoutParams();
            //constraints.setPa(R.id.time_field,ConstraintSet.LEFT,width);

            //.applyTo(layout);

            return convertView;

        }
    }*/


}