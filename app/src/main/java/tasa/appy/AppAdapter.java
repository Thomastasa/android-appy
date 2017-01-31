package tasa.appy;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class AppAdapter extends ArrayAdapter<AppItemObject> {

    private ArrayList<AppItemObject> appsList = null;
    private Context context;
    private AppAdapterCallback cb;

    public AppAdapter(Context context, int textViewResourceId, ArrayList<AppItemObject> appsList, AppAdapterCallback appCb) {
        super(context, textViewResourceId, appsList);
        this.context = context;
        this.appsList = appsList;
        this.cb = appCb;
    }

    @Override
    public int getCount() {
        return ((null != appsList) ? appsList.size() : 0);
    }

    @Override
    public AppItemObject getItem(int position) {
        return ((null != appsList) ? appsList.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (null == view) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.item_app, null);
        }

        final AppItemObject item = appsList.get(position);
        if (item != null) {
            final RelativeLayout itemWrap = (RelativeLayout) view.findViewById(R.id.wrap_item);
            TextView appName = (TextView) view.findViewById(R.id.app_name);
            TextView packageName = (TextView) view.findViewById(R.id.app_package);
            ImageView appIcon = (ImageView) view.findViewById(R.id.app_icon);
            appIcon.setClickable(true);
            appIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id="+item.getAppPackage()));
                    context.startActivity(goToMarket);
                }
            });

            if(item.isInstalled()){
                itemWrap.setBackgroundResource(R.color.bgInstalled);
            }else{

                if(item.isSelected()){
                    itemWrap.setBackgroundResource(R.color.bgSelected);
                }else{
                    itemWrap.setBackgroundResource(R.color.bgUnselected);
                }
                itemWrap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(item.isSelected()){
                            item.setSelected(false);
                            itemWrap.setBackgroundResource(R.color.bgUnselected);
                        }else{
                            item.setSelected(true);
                            itemWrap.setBackgroundResource(R.color.bgSelected);
                        }
                        cb.update();
                    }
                });
            }

            appName.setText(item.getAppTitle());
            packageName.setText(item.getAppPackage());
            appIcon.setImageDrawable(item.getAppIcon());
        }
        return view;
    }
}
