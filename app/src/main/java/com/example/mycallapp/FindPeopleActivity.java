package com.example.mycallapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class FindPeopleActivity extends AppCompatActivity {
    private RecyclerView findFriemdList;
    private EditText searchET;
    private String str="";
    private DatabaseReference usersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        findFriemdList = findViewById(R.id.find_friend_list);
        searchET = findViewById(R.id.search_user_text);
        findFriemdList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                   if(searchET.getText().toString().equals("")){
                       Toast.makeText(FindPeopleActivity.this,"Please write a name to search",Toast.LENGTH_LONG).show();
                   }
                   else{
                       str = charSequence.toString();
                       onStart();
                   }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = null;
        if(str.equals("")){
            options = new FirebaseRecyclerOptions.Builder<Contacts>()
                    .setQuery(usersRef,Contacts.class)
                    .build();
        }
        else{
            options = new FirebaseRecyclerOptions.Builder<Contacts>()
                    .setQuery(usersRef.orderByChild("name").startAt(str).endAt(str+"\uf8ff"),Contacts.class)
                    .build();
        }

        FirebaseRecyclerAdapter<Contacts,FindFriendViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull final Contacts model){
                        holder.UserNameText.setText(model.getName());
                        Picasso.get().load(model.getImage()).into(holder.profileImageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visit_user_id = getRef(position).getKey();
                                Intent intent = new Intent(FindPeopleActivity.this,ProfileActivity.class);
                                intent.putExtra("visit_user_id",visit_user_id);
                                intent.putExtra("profile_image",model.getImage());
                                intent.putExtra("profile_name",model.getName());
                                startActivity(intent);

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_design,parent,false);
                        FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                        return viewHolder;

                    }
                };
        findFriemdList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder {
        TextView UserNameText;
        Button vediocallbtn;
        ImageView profileImageView;
        RelativeLayout cardview;

        public FindFriendViewHolder(@NonNull View itemView){
            super(itemView);

            UserNameText = itemView.findViewById(R.id.name_contact);
            vediocallbtn = itemView.findViewById(R.id.call_btn);
            profileImageView = itemView.findViewById(R.id.image_contact);
            cardview = itemView.findViewById(R.id.Card_view1);

            vediocallbtn.setVisibility(View.GONE);
        }
    }



}
