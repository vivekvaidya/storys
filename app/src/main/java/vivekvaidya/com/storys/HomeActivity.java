package vivekvaidya.com.storys;

/**
 * Created by vivekvaidya on 4/25/17.
 */

import android.graphics.Color;
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
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

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

    public void showData(DataSnapshot dataSnapshot, View view) {
        DataSnapshot states = dataSnapshot.child("states");
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.container);

        ScrollView sv = new ScrollView(getContext().getApplicationContext());
        sv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        LinearLayout ll = new LinearLayout(getContext().getApplicationContext());
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ll.setOrientation(LinearLayout.VERTICAL);


        sv.addView(ll);

        for(DataSnapshot ds : states.getChildren()) {

            LinearLayout mm = new LinearLayout(getContext().getApplicationContext());
            mm.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            mm.setOrientation(LinearLayout.HORIZONTAL);

            String state = ds.getKey();
            TextView text = new TextView(getContext().getApplicationContext());
            text.setText(state + " > ");
            text.setPadding(20, 40, 0, 0);
            text.setTextAppearance(getContext().getApplicationContext(), android.R.style.TextAppearance_Large);
            text.setTextColor(Color.rgb(0,0,0));
            ll.addView(text);

            for (DataSnapshot ds1 : ds.getChildren()) {
                String url =  (String) ds1.getValue();
                ImageView image = new ImageView(getContext().getApplicationContext());
                image.setLayoutParams(new android.view.ViewGroup.LayoutParams(700,700));
                image.setMaxHeight(300);
                image.setMaxWidth(300);
                image.setPadding(10,10,10,0);
                Picasso.with(getContext().getApplicationContext()).load(url).into(image);
                mm.addView(image);
            }

            HorizontalScrollView sv_1 = new HorizontalScrollView(getContext().getApplicationContext());
            sv_1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            sv_1.addView(mm);
            ll.addView(sv_1);
        }
        layout.addView(sv);
    }
}
