package vivekvaidya.com.storys;

/**
 * Created by vivekvaidya on 4/25/17.
 */

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v4.app.ActivityCompat;
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

    // Sets up Firebase references
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    // UI elements
    private ImageView mImageView;
    private String mPictureState;
    private Button mShareImage;

    // Tags to avoid magic numbers
    private static final int OPEN_CAMERA = 100;
    private static final int COMPRESS_SIZE = 100;
    private static final int MAX_R = 1;
    private static final int REFRESH_TIME = 5000;

    // Geolocation attributes
    private Geocoder mGeocoder;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private List<Address> mAddressList;

    /**
     * Creates a new instance of a CameraActivity and returns it.
     * @return a CameraActivity
     */
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

        // Gets the real Firebase references
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("states");

        // Connects UI elements to their counterparts in code
        Button mCaptureImage = (Button) view.findViewById(R.id.button2);
        mImageView = (ImageView) view.findViewById(R.id.imageView2);
        mShareImage = (Button) view.findViewById(R.id.button);

        // Clicking on the button starts a Camera Activity
        mCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {

                    // OPEN_CAMERA is the thing that generates 100
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
            final Uri imgUri = getImageUri(context, img);
            Picasso.with(context).load(imgUri).fit().centerCrop()
                    .into(mImageView);

            // Clicking on the button shares the image to Firebase Storage & Realtime Database
            mShareImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view1) {

                    // URI.getLastPathSegment() uniquely identifies each image
                    StorageReference filePath = mStorageRef.child(imgUri.getLastPathSegment());
                    filePath.putFile(imgUri).addOnSuccessListener
                            (new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @SuppressWarnings("VisibleForTests")

                        // onSuccess is called when the image is uploaded successfully
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            mLocationManager = (LocationManager) getActivity()
                                    .getSystemService(LOCATION_SERVICE);
                            mGeocoder = new Geocoder(context, Locale.getDefault());

                            // Sets up a location listener that implements methods to work with
                            // location data and update when the data changes.
                            mLocationListener = new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    try {

                                        // Populates mAddressList with the address returned
                                        // by the geocoder.
                                        mAddressList = mGeocoder.getFromLocation
                                                (location.getLatitude(),
                                                        location.getLongitude(), MAX_R);

                                        // Gets the "AdminArea" aka State
                                        mPictureState = mAddressList.get(0).getAdminArea();
                                        Uri pictureURL = taskSnapshot.getDownloadUrl();

                                        // Writes the state and imgUrl to Firebase Database
                                        mDatabaseRef.child(mPictureState).child
                                                (pictureURL.getLastPathSegment()).setValue
                                                (pictureURL.toString()).addOnSuccessListener
                                                (new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, "Your photo was shared!",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }

                                // The following methods are unnecessary but cannot be done
                                // away with because LocationListener requires it.
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

                            // Checks for permissions to access location.
                            if (ActivityCompat.checkSelfPermission(context, 
                                    android.Manifest.permission.ACCESS_FINE_LOCATION) != 
                                    PackageManager.PERMISSION_GRANTED && 
                                    ActivityCompat.checkSelfPermission(context, 
                                            android.Manifest.permission.ACCESS_COARSE_LOCATION) 
                                            != PackageManager.PERMISSION_GRANTED) {
                           
                                return;
                            }
                            // Updates the location every REFRESH_TIME (5000) milliseconds.
                            mLocationManager.requestLocationUpdates("gps",
                                    REFRESH_TIME, 0, mLocationListener);
                        }
                    });
                }
            });
        }
    }

    /**
     * Helper method that returns the Uri of a Bitmap passed in as a parameter
     * Sourced from the one and only - Stackoverflow
     * @param inContext - application context
     * @param inImage - image returned from the camera activity under the data Intent
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

