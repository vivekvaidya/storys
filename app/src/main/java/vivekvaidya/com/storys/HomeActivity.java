package vivekvaidya.com.storys;

/**
 * Created by vivekvaidya on 4/25/17.
 */

import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeActivity extends Fragment {

    private DatabaseReference mDatabaseRef;
    private ImageButton mImageButton;
    private ListView mListView;

    public static HomeActivity newInstance() {
        HomeActivity fragment = new HomeActivity();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.home_activity, container, false);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot, view);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

    private void showData(DataSnapshot dataSnapshot, View view) {
        DataSnapshot states = dataSnapshot.child("states");
        LinearLayout layout = (LinearLayout)view.findViewById(R.id.container);
        for(DataSnapshot ds : states.getChildren()) {
            for (DataSnapshot ds1 : ds.getChildren()) {
                String url =  (String) ds1.getValue();
                ImageView image = new ImageView(getContext().getApplicationContext());
                image.setLayoutParams(new android.view.ViewGroup.LayoutParams(80,60));
                image.setMaxHeight(40);
                image.setMaxWidth(40);
                Picasso.with(getContext().getApplicationContext()).load(url).into(image);
                layout.addView(image);
            }
        }
    }
}
