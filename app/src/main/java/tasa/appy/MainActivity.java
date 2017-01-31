package tasa.appy;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView labelSelected;
    private ListView appList;
    private Button btnRestore;
    private Button btnLoad;
    private Button btnSave;

    private PackageManager packageManager;
    private AppAdapter appAdapter;

    private List<ApplicationInfo> rawApps;
    private ArrayList<AppItemObject> installedApps;
    private ArrayList<AppItemObject> importedApps;

    Context mContext;
    final int PERMISSION_WRITE_CODE = 2;
    boolean permissionGranted = false;

    private String[] fileList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;


        packageManager = getPackageManager();


        labelSelected = (TextView) findViewById(R.id.label_info);
        appList = (ListView) findViewById(R.id.app_list);
        btnRestore = (Button) findViewById(R.id.btn_restore);
        btnLoad = (Button) findViewById(R.id.btn_load);
        btnSave = (Button) findViewById(R.id.btn_save);

        getInstalledApps();





        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSave();
            }
        });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInstalledApps();
            }
        });

        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImport();
            }
        });

    }

    private void getInstalledApps(){
        installedApps = new ArrayList<>();
        rawApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for(int i=0; i<rawApps.size();i++){
            ApplicationInfo item = rawApps.get(i);
            if((item.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) == 0){
                AppItemObject app = new AppItemObject().fromApplicationInfo(packageManager,item);
                installedApps.add(app);
            }
        }
        // sort alphabetically
        Collections.sort(installedApps, new Comparator<AppItemObject>() {
            @Override
            public int compare(AppItemObject o1, AppItemObject o2) {
                return (o1.getAppTitle().compareToIgnoreCase(o2.getAppTitle()));
            }
        });

        appAdapter = new AppAdapter(this, R.layout.item_app, installedApps, new AppAdapterCallback() {
            @Override
            public void update() {
                int count = 0;
                for(int i=0; i<installedApps.size(); i++){
                    if(installedApps.get(i).isSelected()){
                        count++;
                    }
                }
                String selected = count + "/" + installedApps.size() + " Selected";
                labelSelected.setText(selected);
            }
        });
        appList.setAdapter(appAdapter);

        String selectedInitial = "0/" + installedApps.size() + " Selected";
        labelSelected.setText(selectedInitial);
    }

    private void getImportedApps(){
        // sort alphabetically
        Collections.sort(importedApps, new Comparator<AppItemObject>() {
            @Override
            public int compare(AppItemObject o1, AppItemObject o2) {
                return (o1.getAppTitle().compareToIgnoreCase(o2.getAppTitle()));
            }
        });
        for(AppItemObject installed: installedApps){
            for(AppItemObject imported: importedApps){
                if(installed.getAppPackage().equalsIgnoreCase(imported.getAppPackage())){
                    imported.setInstalled(true);
                }
            }
        }

        appAdapter = new AppAdapter(this, R.layout.item_app, importedApps, new AppAdapterCallback() {
            @Override
            public void update() {
                int count = 0;
                for(int i=0; i<importedApps.size(); i++){
                    if(importedApps.get(i).isSelected()){
                        count++;
                    }
                }
                String selected = count + "/" + importedApps.size() + " Selected";
                labelSelected.setText(selected);
            }
        });
        appList.setAdapter(appAdapter);

        String selectedInitial = "0/" + importedApps.size() + " Selected";
        labelSelected.setText(selectedInitial);

    }


    private void checkSave(){
        if(Utility.isExternalStorageWritable()){

            Log.e("__perm", ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) + " : " + PackageManager.PERMISSION_GRANTED);

            if(permissionGranted || checkWritePermission()){
               saveAppList();
            }else{
                Log.e("__save","no");
            }
        }else{
            App.showAlert(mContext, "Error: Can Not Write To Storage");
        }
    }

    private void saveAppList(){

        Calendar calendar = Calendar.getInstance();
        String today = calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1) + "_" + calendar.get(Calendar.DAY_OF_MONTH) + "_" + calendar.get(Calendar.HOUR_OF_DAY) + "_" + calendar.get(Calendar.MINUTE) + "_" + calendar.get(Calendar.SECOND);
        String filename = "appy_backup_" + today + ".xml";

        boolean success = true;
        File folder = new File(Environment.getExternalStorageDirectory(),  "appy_backup");
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            FileOutputStream outputStream;
            String file = "";
            int selected = 0;
            for (int i = 0; i < installedApps.size(); i++) {
                if (installedApps.get(i).isSelected()) {
                    selected++;
                    file += installedApps.get(i).toSavedString() + "\n";
                }
            }
            if (selected > 0) {
                App.showProcessing(mContext, "Saving " + selected + " Apps");
                try {


//
//                    String root = Environment.getExternalStorageDirectory().toString();
//                    File myDir = new File(root + "/appy_backup");
//                    myDir.mkdirs();


                    FileOutputStream fos = new FileOutputStream(new File(folder, filename));
                    fos.write(file.getBytes());
                    fos.close();




//                    File fPath = new File(folder, filename);
//
//                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fPath));
//                    bufferedWriter.write(file);
//                    bufferedWriter.close();


//                    outputStream = openFileOutput(filename, MODE_PRIVATE);
//                    outputStream.write(file.getBytes());
//                    outputStream.close();
                    App.showAlert(mContext, selected + " Apps Saved");

//                    outputStream = openFileOutput(newFile, Context.MODE_PRIVATE);
//                    outputStream.write(file.getBytes());
//                    outputStream.close();
//                    App.showAlert(mContext, selected+" Apps Saved");
                } catch (Exception e) {
                    e.printStackTrace();
                    App.showAlert(mContext, "Error Saving Apps");
                }
            } else {
                App.showAlert(mContext, "No Apps Selected");
            }

        }

    }

    private void chooseImport(){
        String backupPath = "appy_backup";
        File folder = new File(Environment.getExternalStorageDirectory(), backupPath);
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                File sel = new File(dir, filename);
                return filename.contains("appy_backup_");
            }
        };
        fileList = folder.list(filter);
        if(fileList.length > 0){
            String[] options = new String[fileList.length];
            for(int i=0;i<fileList.length; i++){
                options[i] = prettyFileName(fileList[i]);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Select Backup");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String f = fileList[which];
                    if(f != null && f.length() > 0){
                        checkImport(f);
                    }else{
                        App.showAlert(mContext, "Invalid File Selected");
                    }
                }
            });
            builder.show();
        }else{
            App.showAlert(mContext, "No Backups Found");
        }
    }

    private void checkImport(String fileString){
        boolean error = false;
        String err = "Error Importing Backup";
        ArrayList<AppItemObject> importList = new ArrayList<>();
        if(fileString.length() > 0){
            File folder = new File(Environment.getExternalStorageDirectory(),  "appy_backup");
            File file = new File(folder,fileString);
            StringBuilder builder = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    if(line.length() > 0) {
                        importList.add(new AppItemObject().fromSavedString(mContext,line));
                    }
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
                error = true;
            }
        }
        if(error){
            App.showAlert(mContext, err);
        }else{
            importedApps = importList;
            getImportedApps();
        }
    }

    private String prettyFileName(String raw){
        String ugly = raw.replace("appy_backup_","").replace(".xml","");
        String[] parts = ugly.split("_");
        if(parts.length == 6){
            return parts[1] + "/" + parts[2] + "/" + parts[0] + " @ " + Utility.convertTo12TimeString(parts[3],parts[4],parts[5]);
        }
        return raw;
    }

    // check permission: write external
    private boolean checkWritePermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_CODE);
                return false;
            }
        }
        return true;
    }

    // permission request results callbacks: write external
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean found = false;
        switch(requestCode){
            case PERMISSION_WRITE_CODE:
                // check permission request results
                found = true;
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                    checkSave();
                }else{
                    permissionGranted = false;
                    App.showAlert(mContext, "Error: Storage Write Permission NOT Granted");
                }
                break;
        }
        if(!found){
            App.showAlert(mContext, "Error granting permissions. Please try again");
        }
    }
}
