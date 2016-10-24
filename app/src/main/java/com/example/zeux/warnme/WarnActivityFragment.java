package com.example.zeux.warnme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

/**
 * A placeholder fragment containing a simple view.
 */
public class WarnActivityFragment extends Fragment {

    private ImageButton mSelectImage;
    private EditText mWarnTitle, mWarnPlace, mWarnDes;
    private Button mSubmit;


    private Uri resultUri = null;

    public static final int GALLARY_INTENT = 1;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUsers;

    private ProgressDialog mProgress;

    public WarnActivityFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_warn, null);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("WarnMe_Strings");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());


        mSelectImage = (ImageButton) root.findViewById(R.id.imageSelect);
        mWarnTitle = (EditText) root.findViewById(R.id.titleField);
        mWarnPlace = (EditText) root.findViewById(R.id.placeField);
        mWarnDes = (EditText) root.findViewById(R.id.descriptionField);
        mSubmit = (Button) root.findViewById(R.id.submitButton);

        mProgress = new ProgressDialog(getActivity());

        try {
            mSelectImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLARY_INTENT);

                }
            });

            mSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startWarning();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        return root;
    }

    private void startWarning() {

        mProgress.setMessage("Warning others...");
        mProgress.setCancelable(false);
        mProgress.show();

        final String warnTitle = mWarnTitle.getText().toString().trim();
        final String warnPlace = mWarnPlace.getText().toString().trim();
        final String warnDes = mWarnDes.getText().toString().trim();

        if (!TextUtils.isEmpty(warnTitle) &&
                !TextUtils.isEmpty(warnPlace) &&
                !TextUtils.isEmpty(warnDes) &&
                resultUri != null) {

            StorageReference filepath = mStorage.child("WarnMe_Images").child(resultUri.getLastPathSegment());

            filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    final DatabaseReference newWarn = mDatabase.push();


                    mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newWarn.child("title").setValue(warnTitle);
                            newWarn.child("place").setValue(warnPlace);
                            newWarn.child("description").setValue(warnDes);
                            newWarn.child("image").setValue(downloadUrl.toString());
                            newWarn.child("uid").setValue(mCurrentUser.getUid());
                            newWarn.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(getContext(), MainActivity.class));
                                        Toast.makeText(getActivity(), "Thanks for being positive.", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getActivity(), "Warining Failed.", Toast.LENGTH_LONG).show();
                        }
                    });


                    mProgress.dismiss();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgress.dismiss();
                    Toast.makeText(getActivity(), "Failed to upload data", Toast.LENGTH_LONG).show();
                }
            });


        } else {

            mProgress.dismiss();
            Toast.makeText(getActivity(), "Please check all the fields above!", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLARY_INTENT && resultCode == Activity.RESULT_OK) {


            Uri imageURI = data.getData();

            CropImage.activity(imageURI)
                    .setAspectRatio(1, 1)
                    .start(getContext(), this);

//            mSelectImage.setImageURI(imageURI);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                resultUri = result.getUri();
                mSelectImage.setImageURI(resultUri);
                Log.i("here", "heree");

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.i("here2", "heree");
            }
        }
    }
}
