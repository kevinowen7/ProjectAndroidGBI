package com.example.tubes.Model;

import android.content.Context;
import android.content.DialogInterface;

public class AlertCustom {

    public static void showDialog(String errorMessage, Context context){
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(
                context);

        // set title dialog
        alertDialogBuilder.setTitle("Error Login");

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage(errorMessage)
                .setCancelable(false)
                .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // jika tombol diklik, maka akan menutup activity ini
                        dialog.cancel();
                    }
                });

        // membuat alert dialog dari builder
        androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        // menampilkan alert dialog
        alertDialog.show();
    }
}
