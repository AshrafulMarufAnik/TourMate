package com.anik.example.tourmate.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anik.example.tourmate.ModelClass.Moment;
import com.anik.example.tourmate.R;
import com.anik.example.tourmate.ModelClass.Tour;
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

import java.io.ByteArrayOutputStream;

public class TourDetailsActivity extends AppCompatActivity {
    private LinearLayout routeClick,expenseClick,circleClick,tourMomentsClick,addMomentsClick;
    private TextView tourNameTV,tourLocationTV,tourBudgetTV,tourTotalExpenseTV,tourReturnDateTV;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Tour currentTour;
    private String tourID;
    private String uid;
    private int request_camera = 1, select_file = 0, imageType = 0;
    private String momentImage;
    private String imageDownloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_details);

        init();
        tourID = getIntent().getStringExtra("tourID");
        uid = firebaseAuth.getCurrentUser().getUid();
        getTourDetailsFromDB();

        routeClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TourDetailsActivity.this,RoutePointsActivity.class);
                intent.putExtra("tourID",tourID);
                startActivity(intent);
            }
        });

        expenseClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TourDetailsActivity.this,ExpenseListActivity.class);
                intent.putExtra("tourID",tourID);
                startActivity(intent);
            }
        });

        tourMomentsClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TourDetailsActivity.this,MomentsActivity.class);
                intent.putExtra("tourID",tourID);
                startActivity(intent);
            }
        });

        addMomentsClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTourImage();
            }
        });

    }

    private void addTourImage() {
        final CharSequence[] optionItems = {"Camera", "Gallery"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(TourDetailsActivity.this);
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
                momentImage = encodeToBase64(bmp, Bitmap.CompressFormat.JPEG, 100);
                uploadImageViaCamera(momentImage);
                imageType = 1;

            }
            else if (requestCode == select_file) {
                if(data != null){
                    Uri imageUri = data.getData();
                    uploadImageViaGallery(imageUri);
                    imageType = 2;
                }
            }
        }
    }

    private void uploadImageViaCamera(String momentImage) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String timeInMS = String.valueOf(System.currentTimeMillis());
        final StorageReference imageRef = storageReference.child("TourMate images").child(uid).child(tourID).child(timeInMS);
        final String imageName = imageRef.getName();
        //imageRef.putBytes();
        imageRef.putFile(Uri.parse(momentImage)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    Toast.makeText(TourDetailsActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageDownloadUrl = uri.toString();
                            DatabaseReference tourImageRef = databaseReference.child("User(TourMateApp)").child(uid).child("Tour information").child(tourID).child("Tour Moments");
                            String newImageID = tourImageRef.push().getKey();
                            Moment newMoment = new Moment(imageName,imageDownloadUrl);
                            tourImageRef.child(newImageID).setValue(newMoment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(TourDetailsActivity.this, "Successfully saved", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

    }

    private void uploadImageViaGallery(Uri imageUri) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String timeInMS = String.valueOf(System.currentTimeMillis());
        final StorageReference imageRef = storageReference.child("TourMate images").child(uid).child(tourID).child(timeInMS);
        final String imageName = imageRef.getName();
        imageRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    Toast.makeText(TourDetailsActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageDownloadUrl = uri.toString();
                            DatabaseReference tourImageRef = databaseReference.child("User(TourMateApp)").child(uid).child("Tour information").child(tourID).child("Tour Moments");
                            String newImageID = tourImageRef.push().getKey();
                            Moment newMoment = new Moment(imageName,imageDownloadUrl);
                            tourImageRef.child(newImageID).setValue(newMoment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(TourDetailsActivity.this, "Successfully saved", Toast.LENGTH_SHORT).show();
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

    private void getTourDetailsFromDB() {
        DatabaseReference tourRef = databaseReference.child("User(TourMateApp)").child(uid).child("Tour information").child(tourID);
        tourRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentTour = dataSnapshot.getValue(Tour.class);
                    tourNameTV.setText(currentTour.getTourName());
                    tourLocationTV.setText(currentTour.getTourLocation());
                    tourBudgetTV.setText(String.valueOf(currentTour.getTourBudget()));
                    tourReturnDateTV.setText(currentTour.getTourReturnDate());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TourDetailsActivity.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        routeClick = findViewById(R.id.viewRoutesClick);
        expenseClick = findViewById(R.id.viewExpensesClick);
        circleClick = findViewById(R.id.viewCirclesClick);
        tourMomentsClick = findViewById(R.id.viewTourMomentsClick);
        addMomentsClick = findViewById(R.id.addTourMomentsClick);
        tourNameTV = findViewById(R.id.detailsTourNameTV);
        tourLocationTV = findViewById(R.id.detailsTourLocationTV);
        tourBudgetTV = findViewById(R.id.detailsTourBudgetTV);
        tourTotalExpenseTV = findViewById(R.id.detailsTourTotalExpenseTV);
        tourReturnDateTV = findViewById(R.id.detailsTourReturnDateTV);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

    }

    public void goBack(View view) {
        startActivity(new Intent(TourDetailsActivity.this,TourHistoryActivity.class));
        finish();
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);

        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
