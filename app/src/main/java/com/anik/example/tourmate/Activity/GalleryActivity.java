package com.anik.example.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.anik.example.tourmate.Adapter.MomentAdapter;
import com.anik.example.tourmate.ModelClass.Moment;
import com.anik.example.tourmate.R;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {
    private RecyclerView galleryRV;
    private FloatingActionButton addGalleryFABTN;
    private FloatingActionMenu actionMenu;
    private int request_camera = 1, select_file = 0;
    private String imageDownloadUrl;
    private String uid;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Moment> momentArrayList;
    private MomentAdapter momentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        init();
        uid = firebaseAuth.getCurrentUser().getUid();

        getAllMomentGallery();
        configRV();

        addGalleryFABTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewMoment();
            }
        });
    }

    private void configRV() {
        galleryRV.setLayoutManager(new GridLayoutManager(this,2));
    }

    private void getAllMomentGallery() {
        DatabaseReference allMomentsRef = databaseReference.child("User(TourMateApp)").child(uid).child("All Tour Moments");
        allMomentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    momentArrayList.clear();

                    for(DataSnapshot momentData: dataSnapshot.getChildren()){
                        Moment newMoment = momentData.getValue(Moment.class);
                        momentArrayList.add(newMoment);
                        galleryRV.setAdapter(momentAdapter);
                        momentAdapter.notifyDataSetChanged();
                    }
                }
                else {
                    Toast.makeText(GalleryActivity.this, "Tour Moment is empty", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNewMoment() {
        final CharSequence[] optionItems = {"Camera", "Gallery"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(GalleryActivity.this);
        builder.setTitle("Choose Image Source");
        builder.setCancelable(false);
        builder.setItems(optionItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (optionItems[i].equals("Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, request_camera);

                } else if (optionItems[i].equals("Gallery")) {
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
                Bundle bundle = data.getExtras();
                final Bitmap bmp = (Bitmap) bundle.get("data");
                Uri imageURI = getImageUri(this,bmp);
                uploadImage(imageURI);
            }
            else if (requestCode == select_file) {
                if(data != null){
                    Uri imageUri = data.getData();
                    uploadImage(imageUri);
                }
            }
        }
    }

    private void uploadImage(Uri imageUri) {
        actionMenu.close(true);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String timeInMS = String.valueOf(System.currentTimeMillis());
        final StorageReference imageRef = storageReference.child("TourMate images").child(timeInMS);
        final String imageName = imageRef.getName();
        imageRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    Toast.makeText(GalleryActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageDownloadUrl = uri.toString();
                            DatabaseReference tourImageRef = databaseReference.child("User(TourMateApp)").child(uid).child("All Tour Moments");
                            String newImageID = tourImageRef.push().getKey();
                            Moment newMoment = new Moment(imageName,imageDownloadUrl);
                            tourImageRef.child(newImageID).setValue(newMoment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(GalleryActivity.this, "Image saved", Toast.LENGTH_SHORT).show();
                                        //startActivity(new Intent(GalleryActivity.this,MomentsActivity.class).putExtra("tourID",tourID));
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

    private void init() {
        galleryRV = findViewById(R.id.galleryRV);
        addGalleryFABTN = findViewById(R.id.addGalleryMomentFABTN);
        actionMenu = findViewById(R.id.addGalleryMomentFAM);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        momentArrayList = new ArrayList<>();
        momentAdapter = new MomentAdapter(momentArrayList,this);

    }

    public void goToMain(View view) {
        Intent intent = new Intent(GalleryActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
