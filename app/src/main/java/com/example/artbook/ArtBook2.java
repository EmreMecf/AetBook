package com.example.artbook;

import android.Manifest;
import android.bluetooth.BluetoothHidDeviceAppSdpSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.artbook.databinding.ActivityArtBook2Binding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.security.spec.ECField;

public class ArtBook2 extends AppCompatActivity {
    private ActivityArtBook2Binding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Bitmap selectedImage;
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArtBook2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        registerLauncher();
        Intent intent =getIntent();
        String info=intent.getStringExtra("info");
        sqLiteDatabase=this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);



        if (info.equals("new")){
            //new art
            binding.nameText.setText(" ");
            binding.artistName.setText(" ");
            binding.yearText.setText(" ");
            binding.button.setVisibility(View.VISIBLE);
            binding.imageView.setImageResource(R.drawable.az);

        }else{
            int artId=intent.getIntExtra("artId",1);
            binding.button.setVisibility(View.INVISIBLE);

            try {

                Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM arts WHERE id=?",new String[]{String.valueOf(artId)});
                int artNameIx = cursor.getColumnIndex("artname");
                int painterNameIx=cursor.getColumnIndex("paintername");
                int yearIx =cursor.getColumnIndex("year");
                int imageIx =cursor.getColumnIndex("image");

                while (cursor.moveToNext()){
                    binding.nameText.setText(cursor.getString(artNameIx));
                    binding.artistName.setText(cursor.getString(painterNameIx));
                    binding.yearText.setText(cursor.getString(yearIx));
                     byte[] bytes= cursor.getBlob(imageIx);
                     Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                     binding.imageView.setImageBitmap(bitmap);


                }
                cursor.close();





            }catch(Exception e){
                e.printStackTrace();
            }

        }


    }
    public void save(View view){

        String name =binding.nameText.getText().toString();
        String artistName=binding.artistName.getText().toString();
        String year=binding.yearText.getText().toString();

        Bitmap smallerImage=makeSmallerImage(selectedImage,300);

        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        smallerImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArray=outputStream.toByteArray();

        try {

            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS arts(id INTEGER PRIMARY KEY , artname VARCHAR ,paintername VARCHAR,year VARCHAR,image BLOB )");


            String sqlString="INSERT INTO arts(artname,paintername,year,image) VALUES(?,?,?,?)";
            SQLiteStatement sqLiteStatement=sqLiteDatabase.compileStatement(sqlString);
            sqLiteStatement.bindString(1,name);
            sqLiteStatement.bindString(2,artistName);
            sqLiteStatement.bindString(3,year);
            sqLiteStatement.bindBlob(4,byteArray);
            sqLiteStatement.execute();

        }catch (Exception e){
            e.printStackTrace();

        }
        Intent intent=new Intent(ArtBook2.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


    }

    public Bitmap makeSmallerImage(Bitmap image,int maxiumumSize){
        int width =image.getWidth();
        int heigth=image.getHeight();

        float bitmapRagio=(float) width/(float) heigth;
        if (bitmapRagio>1){
            //land scape image
            width=maxiumumSize;
            heigth=(int)(width/bitmapRagio);

        }else{
            //portrait image
            heigth=maxiumumSize;
            width=(int)(heigth*bitmapRagio);
        }


        return image.createScaledBitmap(image,width,heigth,true);
    }

    public void select(View view){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permisson needed for Gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                    }
                }).show();
            }else{
                //Request Permisson
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }



        }else{
              //Gallery
            Intent intenttogallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intenttogallery);

        }

    }

    private void registerLauncher(){

        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()==RESULT_OK){
                    Intent intentfromresult=result.getData();
                    if (intentfromresult!=null){
                        Uri uriData= intentfromresult.getData();
                        //binding.imageView.setImageURI(uriData);
                        try {
                            if (Build.VERSION.SDK_INT >=28){
                                ImageDecoder.Source source=ImageDecoder.createSource(getContentResolver(),uriData);
                                selectedImage =ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedImage);

                            }else{
                                selectedImage=MediaStore.Images.Media.getBitmap(getContentResolver(),uriData);
                                binding.imageView.setImageBitmap(selectedImage);
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

            }
        });

        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    //permission grated
                    Intent intenttogallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intenttogallery);
                }else{
                    //permission denied
                    Toast.makeText(ArtBook2.this, "Permission needed!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}