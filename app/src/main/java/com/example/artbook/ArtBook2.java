package com.example.artbook;

import android.Manifest;
import android.bluetooth.BluetoothHidDeviceAppSdpSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
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
            

        }catch (Exception e){
            e.printStackTrace();

        }


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