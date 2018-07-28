package com.bb.wifianalyzer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MyWifiRvAdapter extends RecyclerView.Adapter<MyWifiRvAdapter.WifiHolder> {
    // Context
    private Context context;

    // wifi list
    private List<WifiNet> listWifi;

    //c'tor
    public MyWifiRvAdapter(Context context, List<WifiNet> listWifi) {
        this.context = context;
        this.listWifi = listWifi;
    }

    // set item count to listWifi size
    @Override
    public int getItemCount() {
        return listWifi.size();
    }

    // inflate the custom row element to view, place it in the custom wifi viewHolder and return it
    @NonNull
    @Override
    public WifiHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate our card view row layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wifi, parent,false);
        //set the inflated view to our holder
        WifiHolder wifiHolder = new WifiHolder(view);
        //return the data object holder
        return wifiHolder;
    }

    // set each view element with the specific attribute of the current item(ssid) from the list
    @Override
    public void onBindViewHolder(@NonNull WifiHolder holder, final int position) {
        // set signal image according to the signal level
        switch(listWifi.get(position).getSignalStrength()) {
        case 0:
            holder.imgSignal.setImageDrawable(context.getDrawable(R.drawable.ic_signal_wifi_0_bar_black_24dp));
            break;
        case 1:
            holder.imgSignal.setImageDrawable(context.getDrawable(R.drawable.ic_signal_wifi_1_bar_black_24dp));
            break;
        case 2:
            holder.imgSignal.setImageDrawable(context.getDrawable(R.drawable.ic_signal_wifi_2_bar_black_24dp));
            break;
        case 3:
            holder.imgSignal.setImageDrawable(context.getDrawable(R.drawable.ic_signal_wifi_3_bar_black_24dp));
            break;
        case 4:
            holder.imgSignal.setImageDrawable(context.getDrawable(R.drawable.ic_signal_wifi_4_bar_black_24dp));
            break;
        default:
            break;
        }
        // set ssid and security to their textView
        holder.txtSsidName.setText(listWifi.get(position).getSsidName());
        holder.txtSecurityType.setText(listWifi.get(position).getSecurityType());

        // set isConnected image visibility for the current connected ssid
        if (listWifi.get(position).isConnected()){
            holder.connected.setVisibility(View.VISIBLE);
        }else{
            holder.connected.setVisibility(View.INVISIBLE);
        }
        // set onclicklistener on holder, to show the mac address in toast
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, listWifi.get(position).getMacAddress(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    // custom view holder to hold row elements
    public class WifiHolder extends RecyclerView.ViewHolder{
        ImageView imgSignal;
        TextView txtSsidName;
        TextView txtSecurityType;
        ImageView connected;

        public WifiHolder(View itemView) {
            super(itemView);
            this.imgSignal = itemView.findViewById(R.id.imgSignal);
            this.txtSsidName = itemView.findViewById(R.id.lblSsid);
            this.txtSecurityType = itemView.findViewById(R.id.lblSecType);
            this.connected = itemView.findViewById(R.id.imgIsConnected);
        }
    }
}
