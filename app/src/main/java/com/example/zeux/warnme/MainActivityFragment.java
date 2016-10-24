package com.example.zeux.warnme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private RecyclerView mWarnList;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers;

    public static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListner;

    private ProgressDialog mProgress;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, null);


        mAuth = FirebaseAuth.getInstance();
        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {

                    try {
                        Intent loginInt = new Intent(getActivity(), Signin.class);
                        loginInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginInt);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                } else {

                }

            }
        };


        mDatabase = FirebaseDatabase.getInstance().getReference().child("WarnMe_Strings");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mDatabaseUsers.keepSynced(true);

        mWarnList = (RecyclerView) root.findViewById(R.id.warn_list);
        mWarnList.setHasFixedSize(true);
        mWarnList.setLayoutManager(new LinearLayoutManager(getActivity()));

        checkUserExist();
        return root;
    }


    @Override
    public void onStart() {
        super.onStart();


        mAuth.addAuthStateListener(mAuthListner);

        FirebaseRecyclerAdapter<Warn, WarnViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Warn, WarnViewHolder>(
                Warn.class,
                R.layout.warn_row,
                WarnViewHolder.class,
                mDatabase

        ) {
            @Override
            protected void populateViewHolder(WarnViewHolder viewHolder, Warn model, int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setPlace(model.getPlace());
                viewHolder.setDes(model.getDescription());
                viewHolder.setImage(getContext(), model.getImage());
                viewHolder.setUsername(model.getUsername());


            }
        };

        mWarnList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class WarnViewHolder extends RecyclerView.ViewHolder {

        View mView;


        public WarnViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setTitle(String title) {
            TextView warn_title = (TextView) mView.findViewById(R.id.warn_title);
            warn_title.setText(title);
        }

        public void setPlace(String place) {
            TextView place_title = (TextView) mView.findViewById(R.id.warn_place);
            place_title.setText(place);
        }

        public void setDes(String description) {
            TextView warn_des = (TextView) mView.findViewById(R.id.warn_des);
            warn_des.setText(description);
        }

        public void setImage(Context mContext, String image) {
            ImageView warn_image = (ImageView) mView.findViewById(R.id.warn_image);
            Picasso.with(mContext).load(image).into(warn_image);
        }

        public void setUsername(String username) {
            TextView warn_by = (TextView) mView.findViewById(R.id.warn_by);
            warn_by.setText("By : " + username);

        }

    }

    private void checkUserExist() {

        if (mAuth.getCurrentUser() != null) {


            final String user_id = mAuth.getCurrentUser().getUid();
            // if (user_id != null) {
            mProgress = new ProgressDialog(getActivity());
            mProgress.setMessage("Checking User information..");
            mProgress.setCancelable(false);
            mProgress.show();


            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(user_id)) {
                        Intent mainIntentActivity = new Intent(getContext(), SetupActivity.class);
                        mainIntentActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntentActivity);
                        //Toast.makeText(getActivity(), "Logged in", Toast.LENGTH_LONG).show();

                        mProgress.dismiss();
                    } else {
                        mProgress.dismiss();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
//        }else {
//            Intent mainIntentActivity = new Intent(getContext(), Signin.class);
//            mainIntentActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(mainIntentActivity);
//        }

    }
}
