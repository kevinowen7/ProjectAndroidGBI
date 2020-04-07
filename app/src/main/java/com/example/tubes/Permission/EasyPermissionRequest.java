package com.example.tubes.Permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import com.example.tubes.R;

import pub.devrel.easypermissions.EasyPermissions;

public class EasyPermissionRequest {
    private Context context;
    private int RC_CAMERA_PERM = 123;
    private int RC_LOCATION_PERM = 124;
    private int RC_SMS_PERM = 125;
    private int RC_CONTACTS_PERM = 126;
    private int RC_WRITE_PERMISSION = 127;
    private int RC_READ_PERMISSION = 128;

    public EasyPermissionRequest (Context context){
        this.context = context;
    }

    public Boolean hasCameraPermission() {
        return EasyPermissions.hasPermissions(context, Manifest.permission.CAMERA);
    }

    public void reqCameraPermission(){
        EasyPermissions.requestPermissions((Activity) context, context.getResources().getString(R.string.rationale_camera),RC_CAMERA_PERM,Manifest.permission.CAMERA);
    }

    public Boolean hasWriteMediaPermissions() {
        return EasyPermissions.hasPermissions(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void reqWriteMediaPermission(){
        EasyPermissions.requestPermissions((Activity) context, context.getResources().getString(R.string.rationale_file),RC_CAMERA_PERM,Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public Boolean hasReadMediaPermissions() {
        return EasyPermissions.hasPermissions(context, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public void reqReadMediaPermission(){
        EasyPermissions.requestPermissions((Activity) context, context.getResources().getString(R.string.rationale_file),RC_READ_PERMISSION,Manifest.permission.READ_EXTERNAL_STORAGE);
    }


}
