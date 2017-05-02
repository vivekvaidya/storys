package vivekvaidya.com.storys;

/**
 * Created by vivekvaidya on 4/25/17.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

public class CameraActivity extends Fragment {

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private ImageView mImageView;
    private Button mCaptureImage;
    private String pictureState;
    private Button mShareImage;
    private static final int OPEN_CAMERA = 1;
    private static final int COMPRESS_SIZE = 100;
    private Geocoder geocoder;
    private LocationManager locationManager;
    private LocationListener locationListener;
    List<Address> addressList;

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
        View view = inflater.inflate(R.layout.camera_activity, container, false);

        // Gets reference to storage location on Firebase
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("states");
        mCaptureImage = (Button) view.findViewById(R.id.button2);
        mImageView = (ImageView) view.findViewById(R.id.imageView2);
        mShareImage = (Button) view.findViewById(R.id.button);

        // Clicking on the button starts a Camera Activity
        mCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {

                    // Open_Camera is the thing that generates 100
                    startActivityForResult(intent, OPEN_CAMERA);
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Checks to make sure that the Camera was opened and an Image was returned
        if (requestCode == OPEN_CAMERA && resultCode == RESULT_OK && null != data) {
            final Context context = getContext().getApplicationContext();
            Bitmap img = (Bitmap) data.getExtras().get("data");

            // Calls a helper method that returns Uri of the Bitmap
            final Uri uri = getImageUri(context, img);
            Picasso.with(context).load(uri).fit().centerCrop()
                    .into(mImageView);

            mShareImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    StorageReference filePath = mStorageRef.child(uri.getLastPathSegment());
                    filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @SuppressWarnings("VisibleForTests")
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                            geocoder = new Geocoder(context, Locale.getDefault());
                            locationListener = new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    try {
                                        addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                        pictureState = addressList.get(0).getAdminArea();
                                        Uri pictureURL = taskSnapshot.getDownloadUrl();
                                        mDatabaseRef.child(pictureState).child(pictureURL.getLastPathSegment()).setValue(pictureURL.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getActivity().getApplicationContext(), "Your photo was shared!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }

                                @Override
                                public void onStatusChanged(String provider, int status, Bundle extras) {

                                }

                                @Override
                                public void onProviderEnabled(String provider) {

                                }

                                @Override
                                public void onProviderDisabled(String provider) {

                                }
                            };
                            locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
                        }
                    });
                }
            });
        }
    }

    /**
     * Helper method that returns the Uri of a Bitmap passed in as a parameter
     * Sourced from the one and only - Stackoverflow
     *
     * @param inContext - application context
     * @param inImage   - image returned from the camera activity under the data Intent
     * @return the Uri of the Bitmap.
     */
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, COMPRESS_SIZE, bytes);
        String path = MediaStore.Images.Media.
                insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}

