package com.ae.apps.randomcontact.permissions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ae.apps.randomcontact.utils.AppConstants;
import com.ae.apps.randomcontact.utils.Utils;

/**
 * An abstract Fragment that can check for Runtime Permissions
 */
public abstract class PermissionCheckingFragment extends Fragment {

    protected LayoutInflater mInflater;
    protected ViewGroup mContainer;
    protected Context mContext;
    private ViewGroup mMainContainer;
    private String[] mPermissionNames;
    private int mRequestCode;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        mContainer = container;
        mMainContainer = Utils.createParentLayout(mContext);

        mPermissionNames = getRequiredPermissions();
        mRequestCode = getRequestCode();

        checkPermissions(savedInstanceState);

        return mMainContainer;
    }

    /**
     * The request code
     *
     * @return request code
     */
    protected abstract int getRequestCode();

    /**
     * Permissions that are required
     *
     * @return an array of required permissions
     */
    protected abstract String[] getRequiredPermissions();

    /**
     * Create the view to be shown when the permissions are not granted
     *
     * @return a view that will be added to the view group
     */
    protected abstract View setupViewWithoutPermission();

    /**
     * Create the view when permissions are granted
     *
     * @param savedInstanceState bundle
     * @return a view that will be added to the view group
     */
    protected abstract View setupViewWithPermission(Bundle savedInstanceState);

    /**
     * Invoked when the requested permissions are not granted
     *
     * @param requestCode requestCode
     * @param permissions list of permissions
     * @param grantResults grant results
     */
    protected abstract void onPermissionNotGranted(int requestCode, String[] permissions, int[] grantResults);

    private void onPermissionGranted(Bundle savedInstanceState){
        View randomContactView = setupViewWithPermission(savedInstanceState);
        mMainContainer.removeAllViews();
        mMainContainer.addView(randomContactView);
    }

    private void checkPermissions(Bundle savedInstanceState) {
        if(checkAllPermissions()){
            onPermissionGranted(savedInstanceState);
        } else {
            // else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), READ_CONTACTS))
            // show a no access view as read contacts permission is required
            View noAccessView = setupViewWithoutPermission();
            if(null != noAccessView) {
                mMainContainer.addView(noAccessView);
            }
            requestForPermissions();
        }
    }

    private boolean checkAllPermissions(){
        for(String permissionName : mPermissionNames){
            if(PackageManager.PERMISSION_GRANTED != PermissionChecker.checkSelfPermission(mContext, permissionName)){
                return false;
            }
        }
        return true;
    }

    protected void requestForPermissions() {
        requestPermissions(mPermissionNames, mRequestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case AppConstants.PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionGranted(null);
                } else {
                    onPermissionNotGranted(requestCode, permissions, grantResults);
                }
                break;
            }
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
}
