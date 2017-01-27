package tasa.appy;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class Utility {

    public static String convertDrawableToBase64(Drawable drawable){
        String s = "";
        try{
            BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
            Bitmap bitmap = bitmapDrawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bitmapByte = stream.toByteArray();
            bitmapByte = Base64.encode(bitmapByte,Base64.DEFAULT);
            s = bitmapByte.toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        return s;
    }

    public static Drawable convertBase64ToDrawable(Context context, String base64String){
        try{
            byte[] decodedBytes = Base64.decode(base64String, 0);
            Bitmap b = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            Drawable d = new BitmapDrawable(context.getResources(), b);
            return d;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
