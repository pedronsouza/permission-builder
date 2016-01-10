package com.pedronsouza.permissionbuilder;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by pedrosouza on 1/10/16.
 */
public class PermissionManager {
    private static PermissionManager mInstance;
    private HashMap<String, Integer> mPermissionsMap;

    private Callback permissionCallback;
    private CancelCallback cancelCallback;

    private int index = 0;
    private boolean continueAfterFails;

    private PermissionManager() {
        mPermissionsMap = new HashMap<>();

    }

    public static void bindPermissionResult(Context context, String[] permissions, int[] grantResults) {
        if (permissions.length == 0) {
            if (mInstance.cancelCallback  != null)
                mInstance.cancelCallback.onCancel();
        } else {
            boolean allAuthorized = true;
            for (int i : grantResults)
                allAuthorized = (i == PackageManager.PERMISSION_GRANTED && allAuthorized);

            if (allAuthorized && mInstance.permissionCallback != null) {
                mInstance.permissionCallback.onPermissionResult(permissions[0], true);
                mInstance.next(context);
            } else {
                assert mInstance.permissionCallback != null;
                mInstance.permissionCallback.onPermissionResult(permissions[0], allAuthorized);

                if (mInstance.continueAfterFails)
                    mInstance.next(context);
            }
        }
    }

    private void next(Context context) {
        index++;

        if (index < mPermissionsMap.size())
            requestPermissions(context, continueAfterFails);
    }

    public static PermissionManager create() {
        mInstance = new PermissionManager();
        return mInstance;
    }

    public PermissionManager addPermission(String permission) {
        Boolean unique = false;
        Integer key = 0;

        while (!unique) {
            key = generateKey();
            unique = (!mPermissionsMap.containsKey(key));
        }

        mPermissionsMap.put(permission, key);
        return mInstance;
    }

    public PermissionManager withPermissionCallback(Callback callback) {
        permissionCallback = callback;
        return mInstance;
    }

    public PermissionManager withCancelCallback(CancelCallback callback) {
        cancelCallback = callback;
        return mInstance;
    }

    public void requestPermissions(Context context, boolean continueAfterFails) {
        this.continueAfterFails = continueAfterFails;
        String permission = (String) mPermissionsMap.keySet().toArray()[index];
        int requestCode = (int) mPermissionsMap.values().toArray()[index];

        try {
            canUse(context, permission);

            if (permissionCallback != null)
                permissionCallback.onPermissionResult(permission, true);

        } catch (IllegalAccessException e) {
            ActivityCompat.requestPermissions((AppCompatActivity) context,
                    new String[] { permission },
                    requestCode);
        }
    }

    private int generateKey() {
        int max = 100;
        int min = 1;

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    private void canUse(Context context, String permission) throws IllegalAccessException{
        if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
            throw new IllegalAccessException(String.format("Can't use this resource because user not authorized the following permission %s", permission));

    }

    public interface Callback {
        void onPermissionResult(String permission, boolean granted);
    }

    public interface CancelCallback {
        void onCancel();
    }
}
