package com.anik.example.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.anik.example.tourmate.ModelClass.Moment;
import com.anik.example.tourmate.R;
import com.anik.example.tourmate.ModelClass.User;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private TextView nameTV, emailTV, locationTV;
    private CircleImageView profileImage;
    private ImageView addImage, coverImage;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private SharedPreferences sharedPreferences;
    private String uid;
    private final int request_camera = 1;
    private final int select_file = 0;
    private String imageDownloadUrl=null;
    private int typePic = 0;
    private Uri picURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
        uid = firebaseAuth.getCurrentUser().getUid();
        fireBaseStateListener();
        getUserInfoFromDB();

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImages();
            }
        });
    }

    private void addImages() {
        PopupMenu popupMenu = new PopupMenu(this, addImage);
        popupMenu.inflate(R.menu.profile_add_image_menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.addProfilePic) {
                    typePic = 1;
                    imageSourceDialog();
                }
                else if (menuItem.getItemId() == R.id.addCoverPic) {
                    typePic = 2;
                    imageSourceDialog();
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void imageSourceDialog() {
        final CharSequence[] optionItems = {"Camera", "Gallery"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Choose Image Source");
        builder.setCancelable(false);
        builder.setItems(optionItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (optionItems[i].equals("Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, request_camera);

                }
                else if (optionItems[i].equals("Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, select_file);
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == request_camera) {
                if (data != null) {
                    if(typePic == 1){
                        Bundle bundle = data.getExtras();
                        final Bitmap bmp = (Bitmap) bundle.get("data");
                        picURI = getImageUri(this, bmp);
                        uploadProfilePic(picURI);
                    }
                    else if(typePic == 2){
                        Bundle bundle = data.getExtras();
                        final Bitmap bmp = (Bitmap) bundle.get("data");
                        picURI = getImageUri(this, bmp);
                        uploadCoverPic(picURI);
                    }
                }
            }
            else if (requestCode == select_file) {
                if (data != null) {
                    if(typePic == 1){
                        picURI = data.getData();
                        uploadProfilePic(picURI);
                    }
                    else if(typePic == 2){
                        picURI = data.getData();
                        uploadCoverPic(picURI);
                    }
                }
            }
        }
    }

    private void uploadCoverPic(Uri picURI) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String timeInMS = String.valueOf(System.currentTimeMillis());
        final StorageReference imageRef = storageReference.child("TourMate images").child(timeInMS);
        imageRef.putFile(picURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageDownloadUrl = uri.toString();
                            DatabaseReference coverImageRef = databaseReference.child("User(TourMateApp)").child(uid).child("user image").child("cover image");
                            User userCoverImage = new User(imageDownloadUrl);
                            coverImageRef.setValue(userCoverImage).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ProfileActivity.this, "Image saved", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    private void uploadProfilePic(Uri picURI) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String timeInMS = String.valueOf(System.currentTimeMillis());
        final StorageReference imageRef = storageReference.child("TourMate images").child(timeInMS);
        imageRef.putFile(picURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageDownloadUrl = uri.toString();
                            DatabaseReference profileImageRef = databaseReference.child("User(TourMateApp)").child(uid).child("user image").child("profile image");
                            User userProfileImage = new User(imageDownloadUrl);
                            profileImageRef.setValue(userProfileImage).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ProfileActivity.this, "Image saved", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    private void getUserInfoFromDB() {
        DatabaseReference userRef = databaseReference.child("User(TourMateApp)").child(uid).child("user information");
        DatabaseReference userProfileImgRef = databaseReference.child("User(TourMateApp)").child(uid).child("user image").child("profile image");
        DatabaseReference userCoverImgRef = databaseReference.child("User(TourMateApp)").child(uid).child("user image").child("cover image");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User currentUser = dataSnapshot.getValue(User.class);
                    nameTV.setText(currentUser.getUserName());
                    emailTV.setText(currentUser.getUserEmail());
                    locationTV.setText(currentUser.getUserLocation());
                }
                else {
                    Toast.makeText(ProfileActivity.this, "No User Data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        userProfileImgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User currentUserProfileImage = dataSnapshot.getValue(User.class);
                    Picasso.with(ProfileActivity.this).load(currentUserProfileImage.getUserImageDownloadURL()).fit().into(profileImage);
                }
                else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        userCoverImgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User currentUserCoverImage = dataSnapshot.getValue(User.class);
                    Picasso.with(ProfileActivity.this).load(currentUserCoverImage.getUserImageDownloadURL()).fit().into(coverImage);
                }
                else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        nameTV = findViewById(R.id.userNameTV);
        emailTV = findViewById(R.id.userEmailTV);
        locationTV = findViewById(R.id.userLocationTV);
        addImage = findViewById(R.id.addImageClickIV);
        profileImage = findViewById(R.id.profileImageCIV);
        coverImage = findViewById(R.id.coverImageIV);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void goBack(View view) {
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        finish();
    }

    public void logOut(View view) {
        FirebaseAuth.getInstance().signOut();
    }

    private void fireBaseStateListener() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("Signed in", "user ID: " + user.getUid());
                } else {
                    Toast.makeText(ProfileActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        }
    }

    public Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
