package com.luck.picture.lib.permissions;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import com.luck.picture.lib.basic.PictureCommonFragment;
import com.luck.picture.lib.utils.ActivityCompatHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2021/11/18 10:07 上午
 * @describe：PermissionChecker
 */
public class PermissionChecker {

    private static final int REQUEST_CODE = 10086;

    private static PermissionChecker mInstance;

    private PermissionChecker() {

    }

    public static PermissionChecker getInstance() {
        if (mInstance == null) {
            synchronized (PermissionChecker.class) {
                if (mInstance == null) {
                    mInstance = new PermissionChecker();
                }
            }
        }
        return mInstance;
    }


    public void requestPermissions(Fragment fragment, String[] permissionArray, PermissionResultCallback callback) {
        List<String[]> groupList = new ArrayList<>();
        groupList.add(permissionArray);
        requestPermissions(fragment, groupList, REQUEST_CODE, callback);
    }

    public void requestPermissions(Fragment fragment, List<String[]> permissionGroupList, PermissionResultCallback callback) {
        requestPermissions(fragment, permissionGroupList, REQUEST_CODE, callback);
    }

    private void requestPermissions(Fragment fragment, List<String[]> permissionGroupList, final int requestCode, PermissionResultCallback permissionResultCallback) {
        if (ActivityCompatHelper.isDestroy(fragment.getActivity())) {
            return;
        }
        if (fragment instanceof PictureCommonFragment) {
            if (Build.VERSION.SDK_INT < 23) {
                if (permissionResultCallback != null) {
                    permissionResultCallback.onGranted();
                }
                return;
            }
            Activity activity = fragment.getActivity();
            List<String> permissionList = new ArrayList<>();
            for (String[] permissionArray : permissionGroupList) {
                for (String permission : permissionArray) {
                    if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                        permissionList.add(permission);
                    }
                }
            }
            if (permissionList.size() > 0) {
                ((PictureCommonFragment) fragment).setPermissionsResultAction(permissionResultCallback);
                String[] requestArray = new String[permissionList.size()];
                permissionList.toArray(requestArray);
                fragment.requestPermissions(requestArray, requestCode);
                ActivityCompat.requestPermissions(activity, requestArray, requestCode);
            } else {
                if (permissionResultCallback != null) {
                    permissionResultCallback.onGranted();
                }
            }
        }
    }

    public void onRequestPermissionsResult(int[] grantResults, PermissionResultCallback action) {
        if (PermissionUtil.isAllGranted(grantResults)) {
            action.onGranted();
        } else {
            action.onDenied();
        }
    }
}