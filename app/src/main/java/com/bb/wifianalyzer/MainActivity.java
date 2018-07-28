package com.bb.wifianalyzer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // declaring all the global variables
    // ----------------------------------
    // get the activity context
    private Context context;
    // mainActivity linear layout
    private LinearLayout linearLayout;
    // recyclerView to hold the list
    private RecyclerView recyclerView;
    // wifi manager to get other wifi nets
    private WifiManager wifi;
    // list of wifiNets
    private List<WifiNet> wifiNets;
    // recyclerView adapter
    private RecyclerView.Adapter myWifiRvAdapter;
    // constant for location permission request
    private final int LOCATION_PERMISSION = 1;
    // string array of location permission
    private String[] location_permission_list = {
            // Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    // refresh layout by swiping
    private SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // set dataMembers and their references
        setPointer();
    }

    private void setPointer() {
        context = this;
        // get linearLayout reference
        linearLayout = findViewById(R.id.linearLayout);
        // get swipeLayout reference
        swipeLayout = findViewById(R.id.refreshLayout);
        // change swipeLayout circle colors
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        // create swipeLayout OCL (onClickListener) for the swipe down action
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // run handleWifiListing and stop refreshing
                handleWifiListing();
                swipeLayout.setRefreshing(false);
            }
        });
        // get recyclerView reference
        recyclerView = findViewById(R.id.recyclerView);
        // set fixed size for the item
        recyclerView.setHasFixedSize(true);
        // create a local var for linear layout manager
        RecyclerView.LayoutManager myLayoutManager = new LinearLayoutManager(this);
        // set our layout manger to our recyclerView
        recyclerView.setLayoutManager(myLayoutManager);
        // add dividers to the recyclerView with the item decoration option
        recyclerView.addItemDecoration(new SeparatorDecoration(getApplicationContext(), Color.BLACK, 1));
        // get the wifi manager
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // create an empty wifiNets list fo pass to the recyclerView Adapter
        wifiNets = new ArrayList<>();
        // create the recyclerView Adapter
        myWifiRvAdapter = new MyWifiRvAdapter(context, wifiNets);
        // attach the adapter to the recyclerView
        recyclerView.setAdapter(myWifiRvAdapter);
        // lunch the main logic of the wifi listing
        handleWifiListing();

    }

    // wifi-listing main logic
    private void handleWifiListing() {
        // check if the phone wifi is enabled
        if (wifi.isWifiEnabled()) {
            // get the wifi list
            getWifiList();
        } else {
            // if the wifi turned off ask the user to turn it on
            String message = "Please turn on your wifi";
            int myDuration = 5000;
            final Snackbar snackbar = Snackbar.make(linearLayout, message, Snackbar.LENGTH_LONG); //create the snack bar
            snackbar.setAction("Turn on wifi", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wifi.setWifiEnabled(true);
                    Toast.makeText(context, "Wifi turned on", Toast.LENGTH_SHORT).show();
                    // dismiss the snackBar
                    snackbar.dismiss();
                    // get the wifi list
                    getWifiList();
                }
            })
                    // override Snackbar.LENGTH_LONG with my duration var
                    .setDuration(myDuration)
                    .setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void getWifiList() {
        // check for location permissions
        if (!hasLocationPermissions(location_permission_list)) {
            // request permission
            requestLocationPermission();
        } else {
            // get the scan result to list
            List<ScanResult> wifiScanResults = wifi.getScanResults();
            // clears the wifiNets list
            wifiNets.clear();
            String currentWifiBssid = getCurrentMacAddress();
            // loop through all the wifi that scanned
            for (ScanResult item : wifiScanResults) {
                // add net item to wifiNets list
                wifiNets.add(new WifiNet(
                                item.SSID,
                                item.capabilities,
                                WifiManager.calculateSignalLevel(item.level, 5),
                                item.BSSID
                        )
                );
                // if the user connected to this net set to true the connected attribute
                // works only if the phone connected to network
                wifiNets.get(wifiNets.size() - 1).setConnected(currentWifiBssid.equals(item.BSSID));
                // because we refreshing all the list we refreshing the entire adapter
                myWifiRvAdapter.notifyDataSetChanged();
            }
        }
    }

    // check the location permission status
    public boolean hasLocationPermissions(String... permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    // get current macAddress (if available)
    private String getCurrentMacAddress() {
        // create wifiInfo to get current wifi connection data
        WifiInfo wifiInfo = wifi.getConnectionInfo();
        String currentWifiBssid = "";
        // check if the phone connected or connecting to wifi and get the connection mac address
        if (wifiInfo != null) {
            NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
            if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                currentWifiBssid = wifiInfo.getBSSID();
            }
        }
        return currentWifiBssid;
    }

    // check if we requested the permission in the past
    public boolean isThePermissionRequested(String... permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                    return true;
                }
            }
        }
        return false;
    }

    // handle the location permission request
    // to make it generic change the AlertDialog
    private void requestLocationPermission() {
        // Checks whether the user was previously asked about this permission
        if (isThePermissionRequested(location_permission_list)) {
            // Pops-up an alert dialog that explains why the app needs this permission
            new AlertDialog.Builder(context)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to get the wifi ssid list")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Asks for Location permission (the response-handling is performed in the callback function on the MainActivity)
                            ActivityCompat.requestPermissions((Activity) context,
                                    location_permission_list, LOCATION_PERMISSION);
                        }
                    })
                    // Dismiss the dialog
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            // Asks for the location permission (the response-handling is performed in the callback function on the MainActivity)
            ActivityCompat.requestPermissions((Activity) context,
                    location_permission_list, LOCATION_PERMISSION);
        }
    }

    // request Permissions callback
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Checks if there is any grantResult in this request
        if (grantResults.length < 1) {
            return;
        }
        // Switch between the request codes
        switch (requestCode) {
            case LOCATION_PERMISSION:
                /**  Checks if the first grantResults (we sent only one grant request for one permission)
                 *  is equal to the permission-granted value and pops up toast according to the permission state
                 */
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                    getWifiList();
                } else {
                    Toast.makeText(context, "Permission DENIED", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

}
