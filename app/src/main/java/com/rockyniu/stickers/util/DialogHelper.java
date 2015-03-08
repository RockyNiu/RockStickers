package com.rockyniu.stickers.util;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class DialogHelper {

    /**
     * Shows an alert dialog with the given message. click is need to dismiss
     * the message.
     *
     * @param activity activity
     * @param message  message to show or {@code null} for none
     * @param title    title of dialog
     */
    public static void showNeedClickDialog(Activity activity, String title, String message) {
        DialogInterface.OnClickListener onClickListenerPositive = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };
        showNeedClickDialog(activity, message, title, "OK", onClickListenerPositive, null, null
        );
    }

    private static void showNeedClickDialog(Activity activity, String title, String message,
                                           String buttonPositive, DialogInterface.OnClickListener onClickListenerPositive, String buttonNegative, DialogInterface.OnClickListener onClickListenerNegative) {
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, buttonPositive, onClickListenerPositive);
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, buttonNegative, onClickListenerNegative);
        alertDialog.show();
    }

}
