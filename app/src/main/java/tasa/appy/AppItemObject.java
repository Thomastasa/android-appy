package tasa.appy;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

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
        return appTitle +","+appPackage+","+Utility.convertDrawableToBase64(appIcon);
    }

    public AppItemObject fromSavedString(Context context, String savedData){
        AppItemObject d = new AppItemObject();
        String[] data = savedData.split(",");
        d.selected = false;
        d.installed = false;
        d.appTitle = data[0];
        d.appPackage = data[1];
        Drawable icon = Utility.convertBase64ToDrawable(context, data[2]);
        if(icon != null){
            d.appIcon = icon;
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
