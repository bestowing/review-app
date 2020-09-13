package com.bestowing.restaurant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoModuleActivity extends AppCompatActivity {
    private final int REQUEST_TAKE_PHOTO = 1;
    private final int GET_GALLERY_IMAGE = 200;

    private String currentPhotoPath;
    public Uri upload_photo = null;
    public ImageView user_profile;
    public Boolean photoUploaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_module);
    }

    public void findImageView(ImageView imageView) {this.user_profile = imageView; }

    public void findImageView(int viewId) {
        this.user_profile = findViewById(viewId);
    }

    public void chooseForPhoto() {
        new AlertDialog.Builder(this)
                .setTitle("프로필 사진 변경")
                .setPositiveButton("카메라", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dispatchTakePictureIntent();
                    }
                }).setNegativeButton("갤러리", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectPhotoInGallery();
            }
        }).show();
    }

    // 카메라로 촬영한 이미지를 처리함
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.restaurant_project.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // 갤러리에서 사진 파일을 선택함
    private void selectPhotoInGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, GET_GALLERY_IMAGE);
    }

    public void setImage(Uri uri) {
        user_profile.setImageURI(uri);
        photoUploaded = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK) {         // 갤러리 이미지 요청 성공
            try {
                upload_photo = (data.getData());
                setImage(upload_photo);
            } catch (Exception e) { e.printStackTrace(); };                       // 오류 발생시
        } else if(requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) { // 카메라 이미지 요청 성공
            try {
                upload_photo = Uri.fromFile(new File(currentPhotoPath));
                setImage(upload_photo);
            } catch (Exception e) { e.printStackTrace(); };
        } else {
            // 잘못된 요청 혹은 잘못된 응답
        }
    }


}
