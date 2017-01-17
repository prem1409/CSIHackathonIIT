package com.example.ramesh.myapplication;

import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Show extends AppCompatActivity {
ImageView imageView;
    private StorageReference mStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        imageView=(ImageView)findViewById(R.id.imageshow);
        mStorage = FirebaseStorage.getInstance().getReference();
        mStorage.child("Photos").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Task<Uri> download1=mStorage.child("Photos").getDownloadUrl();
                //Picasso.with(Show.this).load(download1).fit().
            }
        });

    }
}
