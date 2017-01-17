package com.example.ramesh.myapplication;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Button select;
    SQLiteDatabase sqldb;
    EditText t2, t3, t4, t5, t6, t7;
    Cursor c;
    Cursor c2;
    //MyDB mydb = new MyDB(this, "dbname5", 2);
    //MyDB2 mydb2 = new MyDB2(this, "dbname6", 2);
    private StorageReference mStorage;
    private final int PICK_IMAGE = 1;
    private ProgressDialog detectionProgressDialog;
    int flag;
    String url;
    private ProgressDialog progressDialog;
    TextView user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button1 = (Button) findViewById(R.id.browse);
        detectionProgressDialog = new ProgressDialog(this);
        progressDialog = new ProgressDialog(this);
        Log.d("aaya", "aaya");
        mStorage = FirebaseStorage.getInstance().getReference();
        select = (Button) findViewById(R.id.upload);
        t2 = (EditText) findViewById(R.id.name);
        t3 = (EditText) findViewById(R.id.description);
        t4 = (EditText) findViewById(R.id.location);
        t6 = (EditText) findViewById(R.id.relation);

        user = (TextView) findViewById(R.id.textView6);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 1;
                Intent gallIntent = new Intent(Intent.ACTION_GET_CONTENT);
                gallIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(gallIntent, "Select Picture"), PICK_IMAGE);
            }
        });


        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 2;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE);

            }
        });

    }

    public void next(View v) {
        Intent in = new Intent(this, Chatbot.class);
        startActivity(in);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && flag == 1) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imageView = (ImageView) findViewById(R.id.imageView1);
                imageView.setImageBitmap(bitmap);
                detectAndFrame(bitmap);
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "Error in loading", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && flag == 2) {
            progressDialog.setMessage("Uploading...");
            progressDialog.show();
            Uri uri = data.getData();
            final ImageView imageView = (ImageView) findViewById(R.id.imageView1);
            //Log.d("it", "came in");
            StorageReference childref = mStorage.child("Photos").child(uri.getLastPathSegment());
            //Log.d("it", "came in");
            try{
            childref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                   url = taskSnapshot.getDownloadUrl().toString();
                //    Log.d("it", "came in");
                    Uri download=taskSnapshot.getDownloadUrl();
                     Picasso.with(MainActivity.this).load(download).fit().centerCrop().into(imageView);
                    Toast.makeText(MainActivity.this, "Upload Done..", Toast.LENGTH_LONG).show();
                    // set();
                    // set2();
                }
            });

        }catch(Exception e)
            {
                Log.d("hello","Hello");
            }
        }
    }


    private FaceServiceClient faceServiceClient =
            new FaceServiceRestClient("1cc1bc4c1f1a430e99159b5782da3dd3");

    private void detectAndFrame(final Bitmap imageBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(outputStream.toByteArray());
        AsyncTask<InputStream, String, Face[]> detectTask =
                new AsyncTask<InputStream, String, Face[]>() {
                    @Override
                    protected Face[] doInBackground(InputStream... params) {
                        try {
                            publishProgress("Detecting...");
                            FaceServiceClient.FaceAttributeType[] blah=new FaceServiceClient.FaceAttributeType[]{FaceServiceClient.FaceAttributeType.Gender,FaceServiceClient.FaceAttributeType.Age,FaceServiceClient.FaceAttributeType.Smile,FaceServiceClient.FaceAttributeType.Glasses};

                            Face[] result = faceServiceClient.detect(
                                    params[0],
                                    true,         // returnFaceId
                                    false,        // returnFaceLandmarks
                                    blah           // returnFaceAttributes: a string like "age, gender"
                            );

                            if (result == null) {
                                publishProgress("Detection Finished. Nothing detected");
                                return null;
                            }
                            publishProgress(
                                    String.format("Detection Finished. %d face(s) detected",
                                            result.length));
                            return result;
                        } catch (Exception e) {
                            publishProgress("Detection failed");
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                        //TODO: show progress dialog
                        detectionProgressDialog.show();
                    }

                    @Override
                    protected void onProgressUpdate(String... progress) {
                        //TODO: update progress
                        detectionProgressDialog.setMessage(progress[0]);
                    }

                    @Override
                    protected void onPostExecute(Face[] result) {
                        //TODO: update face frames
                        detectionProgressDialog.dismiss();
                        if (result == null) return;
                        ImageView imageView = (ImageView) findViewById(R.id.imageView1);
                        imageView.setImageBitmap(drawFaceRectanglesOnBitmap(imageBitmap, result));
                        imageBitmap.recycle();
                    }
                };
        detectTask.execute(inputStream);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
        user.setText("");
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();
        Log.i("LOG OUT", "log out on back press");
    }

    private static Bitmap drawFaceRectanglesOnBitmap(Bitmap originalBitmap, Face[] faces) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        int stokeWidth = 2;
        paint.setStrokeWidth(stokeWidth);
        if (faces != null) {
            for (Face face : faces) {
                FaceRectangle faceRectangle = face.faceRectangle;
                canvas.drawRect(
                        faceRectangle.left,
                        faceRectangle.top,
                        faceRectangle.left + faceRectangle.width,
                        faceRectangle.top + faceRectangle.height,
                        paint);
            }
        }
        return bitmap;
    }
}
   /* public void set() {
        int id = new Random().nextInt();
        String persons = t2.getText().toString();
        String description = t3.getText().toString();
        String location = t4.getText().toString();
        //String emotion = t5.getText().toString();
        ContentValues values = new ContentValues();
        values.put("url", url);
        values.put("persons", persons);
        values.put("description", description);
        values.put("location", location);
        values.put("emotion", "null");
        values.put("_id", id);
        mydb.insert(values);
    }

    public void set2() {
        int id = new Random().nextInt();
        String person = t6.getText().toString();
        String relation = t7.getText().toString();
        ContentValues values = new ContentValues();
        values.put("person", person);
        values.put("relation", relation);
        values.put("_id", id);
        mydb2.insert(values);
    }
}
    class MyDB extends SQLiteOpenHelper
    {
        private final static String tname="tab1";
        private static String DBName1;
        private int version=1;
        SQLiteDatabase sqldb;

        public MyDB(Context c,String dbname,int v)
        {
            super(c,dbname,null,v);
            DBName1=dbname;
            version=v;

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table tab1(_id INTEGER PRIMARY KEY,url TEXT,persons TEXT,description TEXT,location TEXT,emotion TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP table "+tname);
            onCreate(sqldb);

        }

        public void insert(ContentValues values)
        {
            sqldb=getWritableDatabase();
            sqldb.insert(tname,null,values);
        }

        public Cursor get()
        {
            sqldb=getWritableDatabase();
            return sqldb.query(tname,null,null,null,null,null,null,null);
        }
    }

    class MyDB2 extends SQLiteOpenHelper
    {
        private final static String tname="tab2";
        private static String DBName;
        private int version=1;
        SQLiteDatabase sqldb2;

        public MyDB2(Context c, String dbname, int v)
        {
            super(c,dbname,null,v);
            DBName=dbname;
            version=v;

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table tab2(_id INTEGER PRIMARY KEY,person TEXT,relation TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP table "+tname);
            onCreate(sqldb2);

        }

        public void insert(ContentValues values)
        {
            sqldb2=getWritableDatabase();
            sqldb2.insert(tname,null,values);
        }

        public Cursor get()
        {
            sqldb2=getWritableDatabase();
            return sqldb2.query(tname,null,null,null,null,null,null,null);
        }
}*/


