package com.example.tubes.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tubes.Callback.EditImgProfileCallBack;
import com.example.tubes.Callback.EditUserCallBack;
import com.example.tubes.Callback.LoginUserCallback;
import com.example.tubes.Model.AlertCustom;
import com.example.tubes.Permission.EasyPermissionRequest;
import com.example.tubes.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class EditUserActivity extends AppCompatActivity {
    private ImageView mLoading;
    private ProgressBar mProgress;
    private SharedPreferences sp;

    private String mNama , mEmail , mHp , mAlamat , mPekerjaan ,mUsername, mImageUrl;
    private Button mConfirm;
    private Button mCancel;
    private EditText mNameAccount,mEmailAccount,mHpAccount,mAlamatAccount,mPekerjaanAccount;
    private ImageView mProfileImg;

    private Context mContext;
    private static int RESULT_SELECT_IMAGE = 1;
    private static int RESULT_TAKE_PHOTO = 3;
    private static final int REQUEST_CROP_ICON = 2;
    private String currentPhotoPath;
    private Uri imageSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        initUI();
        validateLogin();
    }

    private void initUI() {
        mProgress = findViewById(R.id.progressBar);
        mLoading = findViewById(R.id.loadingBackround);
        mConfirm = findViewById(R.id.confirm);
        mCancel = findViewById(R.id.cancel);

        mNameAccount = findViewById(R.id.nameAccount);
        mEmailAccount = findViewById(R.id.emailAccount);
        mHpAccount = findViewById(R.id.contactAccount);
        mAlamatAccount = findViewById(R.id.alamatAccount);
        mPekerjaanAccount = findViewById(R.id.pekerjaanAccount);
        mProfileImg = findViewById(R.id.profileImg);

        mContext = this;
        buttonCancelListener();

        buttonSubmitListener();

        buttonUploadImg();
        //request premission camera
        EasyPermissionRequest reqPermission = new EasyPermissionRequest(mContext);
        if (!reqPermission.hasCameraPermission()) {
            reqPermission.reqCameraPermission();
        }
        //request permission media
        if (!reqPermission.hasReadMediaPermissions()) {
            reqPermission.reqReadMediaPermission();
        }
        //request permission media
        if (!reqPermission.hasWriteMediaPermissions()) {
            reqPermission.reqWriteMediaPermission();
        }
    }

    private void buttonUploadImg() {
        mProfileImg.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(View v) {
                //request premission camera
                EasyPermissionRequest reqPermission = new EasyPermissionRequest(mContext);

                //request permission camera
                if (reqPermission.hasCameraPermission()) {
                    //request permission media
                    if (reqPermission.hasReadMediaPermissions()) {
                        //request permission media
                        if (reqPermission.hasWriteMediaPermissions()) {
                            selectImage(EditUserActivity.this);
                        } else {
                            reqPermission.reqWriteMediaPermission();
                        }
                    } else {
                        reqPermission.reqReadMediaPermission();
                    }
                } else {
                    reqPermission.reqCameraPermission();
                }
            }
        });
    }

    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Remove Picture","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(EditUserActivity.this,
                                    "com.example.android.fileprovider",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            Log.d("okee", "onClick: ");
                            startActivityForResult(takePictureIntent, RESULT_TAKE_PHOTO);
                        }
                    }

                } else if (options[item].equals("Choose from Gallery")) {
                    //open album to select image
                    Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    getIntent.setType("image/*");

                    Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickIntent.setType("image/*");

                    Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
                    startActivityForResult(chooserIntent, RESULT_SELECT_IMAGE);

                } else if (options[item].equals("Remove Picture")) {
                    showLoading();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                        //call http API untuk register
                        ArrayList<HashMap<String, String>> reqEditImgProfile = RequestEditImgProfile("none", mUsername);

                        Log.d("HasilAkhir", reqEditImgProfile.toString());

                        if (Objects.equals(reqEditImgProfile.get(0).get("success"), "1")) {
                            hideLoading();
                            EditUserActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProfileImg.setImageResource(R.drawable.default_user);
                                    Toast.makeText(EditUserActivity.this, "Image Profile telah di perbaharui", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (Objects.equals(reqEditImgProfile.get(0).get("success"), "-1")) {
                            //Internal error
                            final ArrayList<HashMap<String, String>> finalReqLogin = reqEditImgProfile;
                            EditUserActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertCustom.showDialog(finalReqLogin.get(0).get("message"), "Error", EditUserActivity.this);
                                    hideLoading();
                                }
                            });
                        } else if (Objects.equals(reqEditImgProfile.get(0).get("success"), "0")) {
                            final ArrayList<HashMap<String, String>> finalReqLogin = reqEditImgProfile;
                            EditUserActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertCustom.showDialog(finalReqLogin.get(0).get("message"), "Error", EditUserActivity.this);
                                    hideLoading();
                                }
                            });
                        }
                        }
                    }).start();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

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
            this.sendBroadcast(mediaScanIntent);

            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(imageSelected)
                    .setAspectRatio(3, 3)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        //image from gallery
        if (requestCode == RESULT_SELECT_IMAGE && resultCode == RESULT_OK && data != null){

            imageSelected = data.getData();

            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(imageSelected)
                    .setAspectRatio(3, 3)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);


        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri imgCrop = result.getUri();
                mProfileImg.setImageURI(imgCrop);

                ContentResolver cR = this.getContentResolver();
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
                    imgBit = MediaStore.Images.Media.getBitmap(getContentResolver(), imgCrop);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                //compress the image to jpg format
                imgBit.compress(Bitmap.CompressFormat.JPEG, perc, byteArrayOutputStream);

                final String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                showLoading();

                final String finalType = type;
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //call http API untuk register
                        ArrayList<HashMap<String, String>> reqEditImgProfile = RequestEditImgProfile(encodeImage, mUsername + "." + finalType);

                        Log.d("HasilAkhir", reqEditImgProfile.toString());

                        if (Objects.equals(reqEditImgProfile.get(0).get("success"), "1")) {
                            hideLoading();
                            EditUserActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(EditUserActivity.this, "Image Profile telah di perbaharui", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (Objects.equals(reqEditImgProfile.get(0).get("success"), "-1")) {
                            //Internal error
                            final ArrayList<HashMap<String, String>> finalReqLogin = reqEditImgProfile;
                            EditUserActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertCustom.showDialog(finalReqLogin.get(0).get("message"), "Error", EditUserActivity.this);
                                    hideLoading();
                                }
                            });
                        } else if (Objects.equals(reqEditImgProfile.get(0).get("success"), "0")) {
                            final ArrayList<HashMap<String, String>> finalReqLogin = reqEditImgProfile;
                            EditUserActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertCustom.showDialog(finalReqLogin.get(0).get("message"), "Error", EditUserActivity.this);
                                    hideLoading();
                                }
                            });
                        }
                    }
                }).start();
            }
        }
    }

    private static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void buttonSubmitListener() {
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // parse Object dari Form ke Object String
                final String mNameData = mNameAccount.getText().toString();
                final String mMailData = mEmailAccount.getText().toString();
                final String mHpData = mHpAccount.getText().toString();
                final String mLocData = mAlamatAccount.getText().toString();
                final String mJobData = mPekerjaanAccount.getText().toString();

                //validasi form
                if( mNameData.length() == 0 ) {
                    mNameAccount.setError("Masukan Nama!");
                } else if( mMailData.length() == 0 ) {
                    mNameAccount.setError(null);
                    mEmailAccount.setError("Masukan Email!");
                } else if( !isValidEmail(mMailData)) {
                    mEmailAccount.setError("Email Tidak Valid!");
                } else if( mHpData.length() == 0 ) {
                    mEmailAccount.setError(null);
                    mHpAccount.setError("Masukan No Hp!");
                }else if( mLocData.length() == 0 ) {
                    mHpAccount.setText(null);
                    mAlamatAccount.setError("Masukan Alamat!");
                } else if( mJobData.length() == 0 ) {
                    mAlamatAccount.setError(null);
                    mPekerjaanAccount.setError("Masukan Pekerjaan!");
                } else {
                    mPekerjaanAccount.setError(null);
                    showLoading();
                    sp = getSharedPreferences("login",MODE_PRIVATE);
                    final String shUser = sp.getString("username", "");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //call http API untuk register
                            ArrayList<HashMap<String, String>> reqRegis = RequestEditUser(mNameData, mMailData, mHpData, mLocData, mJobData,shUser);

                            Log.d("HasilAkhir", reqRegis.toString());

                            if (Objects.equals(reqRegis.get(0).get("success"), "1")) {
                                // pindah halaman ke main activity (login berhasil)
                                startActivity(new Intent(EditUserActivity.this, MainActivity.class));
                            } else if (Objects.equals(reqRegis.get(0).get("success"), "-1")) {
                                //Internal error
                                final ArrayList<HashMap<String, String>> finalReqLogin = reqRegis;
                                EditUserActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertCustom.showDialog(finalReqLogin.get(0).get("message"),"Error",EditUserActivity.this);
                                        hideLoading();
                                    }
                                });
                            } else if (Objects.equals(reqRegis.get(0).get("success"), "0")) {
                                final ArrayList<HashMap<String, String>> finalReqLogin = reqRegis;
                                EditUserActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertCustom.showDialog(finalReqLogin.get(0).get("message"),"Error",EditUserActivity.this);
                                        hideLoading();
                                    }
                                });
                            }
                        }
                    }).start();

                }
            }
        });
    }

    private ArrayList<HashMap<String, String>> RequestEditUser(String name, String mail, String hp, String loc, String job,String user) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        EditUserCallBack update_book = new EditUserCallBack(EditUserActivity.this);
        try {
            arrayList = update_book.execute(
                    name
                    , mail
                    , hp
                    , loc
                    , job
                    , user
            ).get();
        }catch (Exception e){
            Log.d("Error Message",e.getMessage());
        }

        return arrayList;
    }

    private ArrayList<HashMap<String, String>> RequestEditImgProfile(String img, String name) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        EditImgProfileCallBack update_book = new EditImgProfileCallBack(EditUserActivity.this);
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

    private void buttonCancelListener() {
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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

    private void validateLogin() {
        showLoading();

        sp = getSharedPreferences("login",MODE_PRIVATE);
        final String shUser = sp.getString("username", "");
        final String shPass = sp.getString("password", "");


        new Thread(new Runnable() {
            @Override
            public void run() {
                //call http API untuk register
                ArrayList<HashMap<String, String>> reqLogin = RequestLoginUser(shUser,shPass);

                Log.d("Logchecker",reqLogin.toString());

                if (Objects.equals(reqLogin.get(0).get("success"), "1") && reqLogin.size()>1){
                    //get data
                    mNama = reqLogin.get(1).get("nama");
                    mEmail = reqLogin.get(1).get("email");
                    mHp = reqLogin.get(1).get("no_hp");
                    mAlamat = reqLogin.get(1).get("alamat");
                    mPekerjaan = reqLogin.get(1).get("pekerjaan");
                    mUsername = reqLogin.get(1).get("username");
                    mImageUrl = reqLogin.get(1).get("image_url");

                    EditUserActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mNameAccount.setText(mNama);
                            mEmailAccount.setText(mEmail);
                            mHpAccount.setText(mHp);
                            mAlamatAccount.setText(mAlamat);
                            mPekerjaanAccount.setText(mPekerjaan);
                            //Change Picture Profile if exist
                            if (!mImageUrl.equals("null")){
                                Picasso.get().load(getString(R.string.WEB_SERVER)+mImageUrl).memoryPolicy(MemoryPolicy.NO_CACHE).into(mProfileImg);
                            }
                            //hide loading
                            mProgress.setVisibility(View.INVISIBLE);
                            mLoading.setVisibility(View.INVISIBLE);
                        }
                    });
                } else if (Objects.equals(reqLogin.get(0).get("success"), "-1")) {
                    //koneksi error
                    final ArrayList<HashMap<String, String>> finalReqLogin = reqLogin;
                    EditUserActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertCustom.showDialog(finalReqLogin.get(0).get("message"),"Error", EditUserActivity.this);
                            hideLoading();
                        }
                    });
                } else if (Objects.equals(reqLogin.get(0).get("success"), "0")) {
                    //form kosong
                    final ArrayList<HashMap<String, String>> finalReqLogin = reqLogin;
                    EditUserActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertCustom.showDialog(finalReqLogin.get(0).get("message"),"Error", EditUserActivity.this);
                            hideLoading();
                        }
                    });
                } else {
                    final ArrayList<HashMap<String, String>> finalReqLogin = reqLogin;
                    EditUserActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertCustom.showDialog(finalReqLogin.get(0).get("message"),"Error", EditUserActivity.this);
                            hideLoading();
                        }
                    });
                }
            }
        }).start();

    }

    private ArrayList<HashMap<String, String>> RequestLoginUser(String username, String pass) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        LoginUserCallback login_req = new LoginUserCallback(EditUserActivity.this);
        //Log.d("CEKIDBOOK",id_book);
        try {
            arrayList = login_req.execute(
                    username
                    , pass
            ).get();
        }catch (Exception e){
            Log.d("Error Message",e.getMessage());
        }

        return arrayList;
    }
}
