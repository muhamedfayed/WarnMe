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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A placeholder fragment containing a simple view.
 */
public class RegisterActivityFragment extends Fragment {

    EditText mNameField,mEmailField,mPassField;
    Button mSignUp_btn,mSignIn_btn;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;

    public RegisterActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_register, null);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mNameField = (EditText) root.findViewById(R.id.nameField);
        mEmailField = (EditText) root.findViewById(R.id.emailField);
        mPassField = (EditText) root.findViewById(R.id.passField);

        mSignUp_btn = (Button) root.findViewById(R.id.signUp_button);
        mSignIn_btn = (Button) root.findViewById(R.id.signIn_button);


        mProgress = new ProgressDialog(getActivity());

        mSignUp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startSignUp();

            }
        });

        mSignIn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntentActivity =  new Intent(getContext(), Signin.class);
                mainIntentActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntentActivity);
            }
        });


        return root;
    }

    private void startSignUp() {
        final String name = mNameField.getText().toString().trim();
        String Email = mEmailField.getText().toString().trim();
        String Pass = mPassField.getText().toString().trim();



        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(Email) && !TextUtils.isEmpty(Pass) ){
            mProgress.setMessage("Creating an account..");
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(Email , Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        String user_id = mAuth.getCurrentUser().getUid();

                        DatabaseReference current_user_db = mDatabase.child(user_id);
                        current_user_db.child("name").setValue(name);
                        current_user_db.child("image").setValue("default");

                        mProgress.dismiss();
                        Toast.makeText(getActivity(),"Done !",Toast.LENGTH_LONG).show();
                        Intent mainIntentActivity =  new Intent(getContext(), MainActivity.class);
                        mainIntentActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntentActivity);


                    }
                    else {
                        mProgress.dismiss();
                        Toast.makeText(getActivity(),"Failed",Toast.LENGTH_LONG).show();
                    }

                }
            });

        }
    }
}
