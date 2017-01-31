package tasa.appy;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class AppItemObject implements Serializable {

    private boolean selected;
    private boolean installed;
    private Drawable appIcon;
    private String appTitle;
    private String appPackage;

    public AppItemObject fromApplicationInfo(PackageManager pm, ApplicationInfo app){
        AppItemObject d = new AppItemObject();
        d.selected = false;
        d.installed = false;
        d.appIcon = app.loadIcon(pm);
        d.appTitle = app.loadLabel(pm).toString();
        d.appPackage = app.packageName;
        return d;
    }

    public String toSavedString(){
        return appTitle +"~"+appPackage+"~"+Utility.convertDrawableToBase64(appIcon);
    }

    public AppItemObject fromSavedString(Context context, String savedData){
        AppItemObject d = new AppItemObject();
        List<String> data = Arrays.asList(savedData.split("~"));
        Log.e("__size","is " + data.size());
        d.selected = false;
        d.installed = false;
        try{
            d.appTitle = data.get(0);
            d.appPackage = data.get(1);
            d.appIcon = Utility.convertBase64ToDrawable(context, data.get(2));
        }catch(Exception e){
            e.printStackTrace();
        }
        return d;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isInstalled() {
        return installed;
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public String getAppTitle() {
        return appTitle;
    }

    public String getAppPackage() {
        return appPackage;
    }

}
