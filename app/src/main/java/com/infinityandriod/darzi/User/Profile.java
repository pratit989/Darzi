package com.infinityandriod.darzi.User;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.infinityandriod.darzi.Common.FirestoreMethods;
import com.infinityandriod.darzi.R;

import java.util.Map;

public class Profile extends AppCompatActivity {

    private static final String TAG = "Profile";
    FirestoreMethods method;
    StorageReference storageReference;
    FirebaseUser user;
    ImageView imageView;
    RequestOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile_page);

        method = new FirestoreMethods();

        ImageView backBtn = findViewById(R.id.general_back_btn);
        backBtn.setOnClickListener(view -> Profile.super.onBackPressed());

        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        // Hooks for assigning data
        TextView name = findViewById(R.id.full_name);
        TextView username = findViewById(R.id.username);
        TextView email = findViewById(R.id.email);
        TextView gender = findViewById(R.id.gender);
        TextView dateOfBirth = findViewById(R.id.date_of_birth);
        TextView phone_no = findViewById(R.id.phone_no);

        // Loading Screen
        ProgressBar progressBar = findViewById(R.id.progressBar);
        // Main content
        ScrollView scrollView = findViewById(R.id.mainContent);

        // Get Data
        DocumentReference docRef = method.db.collection("Users").document(user.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                Map<String, Object> data = document.getData();
                assert data != null;
                name.setText(user.getDisplayName());
                username.setText((String) data.get("username"));
                email.setText(user.getEmail());
                gender.setText((String) data.get("gender"));
                dateOfBirth.setText((String) data.get("dateOfBirth"));
                phone_no.setText(user.getPhoneNumber());
                scrollView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        // Reference to an image file in Cloud Storage
        storageReference = FirebaseStorage.getInstance().getReference();
        storageReference = storageReference.child("users/" + user.getUid() + "/profile.jpg");

        // ImageView in your Activity
        imageView = findViewById(R.id.profilePicture);

        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)
        options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.blank_profile)
                .error(R.drawable.blank_profile)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .priority(Priority.HIGH)
                .skipMemoryCache(true);
        GlideApp.with(this /* context */)
                .load(storageReference)
                .apply(options)
                .into(imageView);
    }

    public void editProfile(View view) {
        loadFile();
    }

    int gallery;

    public void loadFile()
    {
        gallery = 12;
        final String type="*/*";

        Intent i=new Intent(Intent.ACTION_GET_CONTENT);
        i.setType(type);
        startActivityForResult(Intent.createChooser(i,"select file"), gallery);
    }

    Uri uploadFileUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == gallery && resultCode == RESULT_OK && data != null) {
            uploadFileUri = data.getData();
            Uri file = uploadFileUri;
            storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference riversRef = storageReference.child("users/"+ user.getUid() + "/profile.jpg");
            UploadTask uploadTask = riversRef.putFile(file);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(exception -> {
                // Handle unsuccessful uploads
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                    recreate();
                }
            });
        }
    }
}
