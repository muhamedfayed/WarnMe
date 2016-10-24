package com.example.zeux.warnme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

/**
 * A placeholder fragment containing a simple view.
 */
public class SetupActivityFragment extends Fragment {

    private ImageButton mSetupImage;
    private EditText mSetupName;
    private Button mSubmitBtn;

    private Uri mImageUri = null;
    private ProgressDialog mProgress;

    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;
    private StorageReference mStorageImage;

    private static final int GALLARY_REQUEST = 1;

    public SetupActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setup, null);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mStorageImage = FirebaseStorage.getInstance().getReference().child("ProfileImages");

        mProgress = new ProgressDialog(getActivity());

        mSetupImage = (ImageButton) root.findViewById(R.id.setupImage_btn);
        mSetupName = (EditText) root.findViewById(R.id.setupNameField);
        mSubmitBtn = (Button) root.findViewById(R.id.submit_btn);

        mSetupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent gallaryIntent = new Intent();
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                gallaryIntent.setType("image/*");
                startActivityForResult(gallaryIntent, GALLARY_REQUEST);

            }
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setupAccount();

            }
        });


        return root;
    }

    public void setupAccount() {
        mProgress.setMessage("Updating your account info..");
        mProgress.setCancelable(false);
        mProgress.show();
        final String name = mSetupName.getText().toString().trim();
        final String user_id = mAuth.getCurrentUser().getUid();


        if (!TextUtils.isEmpty(name) && mImageUri != null) {


            StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    mDatabaseUsers.child(user_id).child("name").setValue(name);
                    mDatabaseUsers.child(user_id).child("image").setValue(downloadUri);

                    Intent mainIntentActivity = new Intent(getContext(), MainActivity.class);
                    mainIntentActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntentActivity);

                    Toast.makeText(getActivity(),"Done !",Toast.LENGTH_LONG).show();
                    mProgress.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getActivity(),"Failed, Please try again.",Toast.LENGTH_LONG).show();
                    mProgress.dismiss();

                }
            });


        }else {

            Toast.makeText(getActivity(),"Please fill all the fields.",Toast.LENGTH_LONG).show();
            mProgress.dismiss();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLARY_REQUEST && resultCode == getActivity().RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(getContext(), this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == getActivity().RESULT_OK) {
                mImageUri = result.getUri();
                mSetupImage.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
