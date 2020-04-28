package com.example.tubes.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tubes.Callback.EditImgProfileCallBack;
import com.example.tubes.Model.AlertCustom;
import com.example.tubes.R;
import com.example.tubes.activity.EditUserActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    private ImageView mLoading;
    private ProgressBar mProgress;

    private LinearLayout mPhoto , mGallery , mRemove , mCancel;
    private Context mContext;
    private Activity mActivity;

    private static int RESULT_SELECT_IMAGE = 1;
    private static int RESULT_TAKE_PHOTO = 3;
    private static final int REQUEST_CROP_ICON = 2;
    private String currentPhotoPath;
    private Uri imageSelected;
    private String mUsername;

    private ImageView mProfileImg;

    public BottomSheetFragment(Context mContext, Activity mActivity, String mUsername) {
        this.mContext = mContext;
        this.mUsername = mUsername;
        this.mActivity = mActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initUI();

    }

    private void initUI() {
        mPhoto = Objects.requireNonNull(getView()).findViewById(R.id.take_photo_sheet);
        mGallery = Objects.requireNonNull(getView()).findViewById(R.id.choose_sheet);
        mRemove = Objects.requireNonNull(getView()).findViewById(R.id.remove_sheet);
        mCancel = Objects.requireNonNull(getView()).findViewById(R.id.cancel_sheet);
        mProfileImg = mActivity.findViewById(R.id.profileImg);
        mProgress = mActivity.findViewById(R.id.progressBar);
        mLoading = mActivity.findViewById(R.id.loadingBackround);
        mPhotoOnClick();
        mGalleryOnClick();
        mRemoveOnClick();
        mCancelOnClick();
    }

    private void mCancelOnClick() {
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void mRemoveOnClick() {
        mRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                showLoading();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //call http API untuk register
                        ArrayList<HashMap<String, String>> reqEditImgProfile = RequestEditImgProfile("none", mUsername);

                        Log.d("HasilAkhir", reqEditImgProfile.toString());

                        if (Objects.equals(reqEditImgProfile.get(0).get("success"), "1")) {
                            hideLoading();
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProfileImg.setImageResource(R.drawable.default_user);
                                    Toast.makeText(mActivity, "Image Profile telah di perbaharui", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (Objects.equals(reqEditImgProfile.get(0).get("success"), "-1")) {
                            //Internal error
                            final ArrayList<HashMap<String, String>> finalReqLogin = reqEditImgProfile;
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertCustom.showDialog(finalReqLogin.get(0).get("message"), "Error", mContext);
                                    hideLoading();
                                }
                            });
                        } else if (Objects.equals(reqEditImgProfile.get(0).get("success"), "0")) {
                            final ArrayList<HashMap<String, String>> finalReqLogin = reqEditImgProfile;
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertCustom.showDialog(finalReqLogin.get(0).get("message"), "Error", mContext);
                                    hideLoading();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }

    private void mGalleryOnClick() {
        mGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open album to select image
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
                startActivityForResult(chooserIntent, RESULT_SELECT_IMAGE);
            }
        });
    }

    private void mPhotoOnClick() {
        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(mContext,
                                "com.example.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, RESULT_TAKE_PHOTO);
                    }
                }
            }
        });
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                mUsername,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == RESULT_TAKE_PHOTO){
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(currentPhotoPath);
            imageSelected = Uri.fromFile(f);
            mediaScanIntent.setData(imageSelected);
            mContext.sendBroadcast(mediaScanIntent);

            // start cropping activity for pre-acquired image saved on the device
            Intent intent = CropImage.activity(imageSelected)
                    .setAspectRatio(3, 3)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .getIntent(mActivity);
            startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
        }

        //image from gallery
        if (requestCode == RESULT_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null){

            imageSelected = data.getData();

            // start cropping activity for pre-acquired image saved on the device
            Intent intent = CropImage.activity(imageSelected)
                    .setAspectRatio(3, 3)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .getIntent(mActivity);
            startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);


        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri imgCrop = result.getUri();
                mProfileImg.setImageURI(imgCrop);

                ContentResolver cR = mContext.getContentResolver();
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                assert imageSelected != null;
                String type = mime.getExtensionFromMimeType(cR.getType(imageSelected));
                int perc = 75;
                if (type==null){
                    type="jpg";
                    perc=60;
                }


                //get image in bitmap format
                Bitmap imgBit = null;
                try {
                    imgBit = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), imgCrop);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                //compress the image to jpg format
                imgBit.compress(Bitmap.CompressFormat.JPEG, perc, byteArrayOutputStream);

                final String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                showLoading();
                dismiss();

                final String finalType = type;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                        String currentDateandTime = sdf.format(new Date());
                        //call http API untuk register
                        ArrayList<HashMap<String, String>> reqEditImgProfile = RequestEditImgProfile(encodeImage, currentDateandTime+"_"+mUsername + "." + finalType);

                        Log.d("HasilAkhir", reqEditImgProfile.toString());

                        if (Objects.equals(reqEditImgProfile.get(0).get("success"), "1")) {
                            hideLoading();
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mActivity, "Image Profile telah di perbaharui", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (Objects.equals(reqEditImgProfile.get(0).get("success"), "-1")) {
                            //Internal error
                            final ArrayList<HashMap<String, String>> finalReqLogin = reqEditImgProfile;
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertCustom.showDialog(finalReqLogin.get(0).get("message"), "Error", mContext);
                                    hideLoading();
                                }
                            });
                        } else if (Objects.equals(reqEditImgProfile.get(0).get("success"), "0")) {
                            final ArrayList<HashMap<String, String>> finalReqLogin = reqEditImgProfile;
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertCustom.showDialog(finalReqLogin.get(0).get("message"), "Error", mContext);
                                    hideLoading();
                                }
                            });
                        }
                    }
                }).start();
            }
        }
    }

    private void showLoading(){
        //show loading
        mProgress.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        //hide loading
        mProgress.setVisibility(View.INVISIBLE);
        mLoading.setVisibility(View.INVISIBLE);
    }


    private ArrayList<HashMap<String, String>> RequestEditImgProfile(String img, String name) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        EditImgProfileCallBack update_book = new EditImgProfileCallBack(mContext);
        try {
            arrayList = update_book.execute(
                    img
                    , name
            ).get();
        } catch (Exception e){
            Log.d("Error Message",e.getMessage());
        }

        return arrayList;
    }

}