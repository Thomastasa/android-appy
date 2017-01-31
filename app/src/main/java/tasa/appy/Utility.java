package tasa.appy;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

import static java.lang.Integer.parseInt;

public class Utility {

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    public static String convertDrawableToBase64(Drawable drawable){
        try{
            BitmapDrawable bitmapDrawable = ((BitmapDrawable)drawable);
            Bitmap bitmap = Bitmap.createBitmap(bitmapDrawable.getBitmap());
            bitmap.setHasAlpha(true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
            byte[] byteFormat = stream.toByteArray();
            return Base64.encodeToString(byteFormat,Base64.NO_WRAP);
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public static Drawable convertBase64ToDrawable(Context context, String base64String){
        try{
            byte[] decodedBytes = Base64.decode(base64String, Base64.NO_WRAP);
            Bitmap b = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            b.setHasAlpha(true);
            Drawable d = new BitmapDrawable(context.getResources(), b);
            return d;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String convertTo12TimeString(String h, String m, String s){
        int hr = parseInt(h);
        String mode = " AM";
        if(hr > 12){
            hr = (hr - 12);
            mode = " PM";
        }
        return hr + ":" + m + ":" + s + mode;
    }
}
