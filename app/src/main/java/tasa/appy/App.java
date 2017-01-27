package tasa.appy;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class App {

    static public ProgressDialog processing;

    // show processing dialog
    static public void showProcessing(Context context, String msg){
        if(processing == null){
            processing = new ProgressDialog(context, R.style.AppTheme);
        }
        processing.setMessage(msg);
        processing.setCancelable(false);
        processing.show();
    }

    // hide processing dialog
    static public void hideProcessing(){
        if(processing != null && processing.isShowing()){
            processing.dismiss();
        }
        processing = null;
    }

    // show alert dialog
    static public void showAlert(Context context, String msg){
        hideProcessing();
        AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.AppTheme);
        alert.setMessage(msg);
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }
}
