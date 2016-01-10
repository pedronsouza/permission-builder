package com.pedronsouza.permissionbuilder;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.request_permission).setOnClickListener(clickListener);
    }


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PermissionManager.create()
                    .addPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    .addPermission(Manifest.permission.READ_CONTACTS)
                    .withCancelCallback(cancelCallback)
                    .withPermissionCallback(successCallback)
                    .requestPermissions(MainActivity.this, false);
        }
    };

    PermissionManager.Callback successCallback = new PermissionManager.Callback() {

        @Override
        public void onPermissionResult(String permission, boolean granted) {
            if (granted) {
                Toast.makeText(MainActivity.this, String.format("Permission %s granted!", permission), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, String.format("Permission %s denied!", permission), Toast.LENGTH_SHORT).show();
            }
        }
    };

    PermissionManager.CancelCallback cancelCallback = new PermissionManager.CancelCallback() {
        @Override
        public void onCancel() {
            Toast.makeText(MainActivity.this, "User cancel operation", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.bindPermissionResult(this, permissions, grantResults);
    }
}
