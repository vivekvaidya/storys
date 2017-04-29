package vivekvaidya.com.storys;

/**
 * Created by vivekvaidya on 4/25/17.
 */

import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class HomeActivity extends Fragment {

    private StorageReference mStorageRef;
    private FirebaseDatabase mDatabase;
    private ImageView mImageView;
    private static final int OPEN_CAMERA = 1;
    private static final int COMPRESS_SIZE = 100;
    private Geocoder geocoder;
    private LocationManager locationManager;
    private LocationListener locationListener;
    List<Address> addressList;

    public static HomeActivity newInstance() {
        HomeActivity fragment = new HomeActivity();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDatabase.getReference();
        myRef.child("state").child("URI").setValue("unique link to image");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_activity, container, false);
    }
}
