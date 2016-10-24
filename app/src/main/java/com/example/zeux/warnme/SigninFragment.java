package com.example.zeux.warnme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class SigninFragment extends Fragment {

    EditText emailField, passField;
    Button signIn_btn, signUp_btn;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;

    private ProgressDialog mProgress;

    public SigninFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_signin, null);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);

        mProgress = new ProgressDialog(getActivity());

        emailField = (EditText) root.findViewById(R.id.emailField2);
        passField = (EditText) root.findViewById(R.id.passField2);

        signIn_btn = (Button) root.findViewById(R.id.signIn_btn2);
        signUp_btn = (Button) root.findViewById(R.id.signUp_btn2);


        signUp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntentActivity = new Intent(getContext(), RegisterActivity.class);
                mainIntentActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntentActivity);
            }
        });

        signIn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLogin();
            }
        });

        return root;
    }

    private void startLogin() {
        String email = emailField.getText().toString();
        String password = passField.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {

            Toast.makeText(getActivity(), "Please fill the fields", Toast.LENGTH_LONG).show();

        } else {

            mProgress.setMessage("Checking Login ..");
            mProgress.setCancelable(false);
            mProgress.show();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {

                        mProgress.dismiss();
                        Toast.makeText(getActivity(), "Problem in Loging in, Please try again !", Toast.LENGTH_LONG).show();

                    } else {

                        mProgress.dismiss();
                        checkUserExist();

                    }

                }
            });
        }
    }

    private void checkUserExist() {
       // if (mAuth.getCurrentUser() != null) {

            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(user_id)) {
                        Intent mainIntentActivity = new Intent(getContext(), MainActivity.class);
                        mainIntentActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntentActivity);
                        //Toast.makeText(getActivity(), "Logged in", Toast.LENGTH_LONG).show();


                    } else {
                        Intent setupIntent = new Intent(getContext(), SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                        Toast.makeText(getActivity(), "Please Setup ur account", Toast.LENGTH_LONG).show();
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

      //  }
    }


}
