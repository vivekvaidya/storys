package vivekvaidya.com.storys;

/**
 * Created by vivekvaidya on 4/25/17.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CameraActivity extends Fragment {
    public static CameraActivity newInstance() {
        CameraActivity fragment = new CameraActivity();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.camera_activity, container, false);
    }
}

