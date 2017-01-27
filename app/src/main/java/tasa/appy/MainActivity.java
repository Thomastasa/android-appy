package tasa.appy;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView labelSelected;
    private ListView appList;
    private Button btnRestore;
    private Button btnSave;

    private PackageManager packageManager;
    private AppAdapter appAdapter;

    private List<ApplicationInfo> rawApps;
    private ArrayList<AppItemObject> installedApps;

    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;


        packageManager = getPackageManager();


        labelSelected = (TextView) findViewById(R.id.label_info);
        appList = (ListView) findViewById(R.id.app_list);
        btnRestore = (Button) findViewById(R.id.btn_restore);
        btnSave = (Button) findViewById(R.id.btn_save);

        installedApps = new ArrayList<>();
        rawApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for(int i=0; i<rawApps.size();i++){
            ApplicationInfo item = rawApps.get(i);
            if((item.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0){
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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAppList();
            }
        });


    }

    private void saveAppList(){

        Calendar calendar = Calendar.getInstance();
        String today = calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH)+1) + "_" + calendar.get(Calendar.DAY_OF_MONTH) + "_" + calendar.get(Calendar.HOUR_OF_DAY) + "_" + calendar.get(Calendar.MINUTE) + "_" + calendar.get(Calendar.SECOND);
        String filename = "appy_backup_"+today;
        Log.e("_today",filename);
        FileOutputStream outputStream;
        String file = "";
        int selected = 0;
        for(int i=0; i<installedApps.size(); i++){
            if(installedApps.get(i).isSelected()){
                selected++;
                file += installedApps.get(i).toSavedString() + "\n";
            }
        }
        if(selected > 0){
            App.showProcessing(mContext, "Saving "+selected+" Apps");
            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(file.getBytes());
                outputStream.close();
                App.showAlert(mContext, selected+" Apps Saved");
            } catch (Exception e) {
                e.printStackTrace();
                App.showAlert(mContext, "Error Saving Apps");
            }
        }else{
            App.showAlert(mContext, "No Apps Selected");
        }
    }
}
