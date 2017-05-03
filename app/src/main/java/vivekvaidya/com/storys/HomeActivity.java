package vivekvaidya.com.storys;

/**
 * Created by vivekvaidya on 4/25/17.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class HomeActivity extends Fragment {

    /**
     * Sets up a new HomeActivity and returns it for use as a fragment.
     * @return HomeActivity
     */
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

        // view is final because it's accessed from an inner class
        final View view = inflater.inflate(R.layout.home_activity, container, false);
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("states");

        // Calls showData() when data is changed/updated on the realtime database
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot, view);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Trouble reading from the database.", Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    /**
     * Helper method that programmatically sets up layouts and lays out elements pulled from the
     * realtime database.
     *
     * @param dataSnapshot - snapshot of the database
     * @param view - current view
     */
    public void showData(DataSnapshot dataSnapshot, View view) {

        // Master linear layout that contains child elements
        LinearLayout masterLayout = (LinearLayout) view.findViewById(R.id.container);

        // ScrollView to scroll through the different states.
        ScrollView verticalScroll = new ScrollView(view.getContext().getApplicationContext());
        verticalScroll.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        // Linear layout to encapsulate elements for the ScrollView
        // (since it can only host one direct child)
        LinearLayout encapsulateLayout = new LinearLayout(view.getContext().getApplicationContext());
        encapsulateLayout.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        encapsulateLayout.setOrientation(LinearLayout.VERTICAL);

        // Add this encapsulated layout to the verticalScrollView.
        verticalScroll.addView(encapsulateLayout);

        for(DataSnapshot entireDB : dataSnapshot.getChildren()) {

            // Another linear layout to hold the horizontal scrolling components.
            LinearLayout horizontalLayout = new LinearLayout(view.getContext().getApplicationContext());
            horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

            // Gets the current state and places it in a textview.
            String currentState = entireDB.getKey();
            TextView txt = new TextView(getContext().getApplicationContext());
            txt.setText(currentState + " > ");
            txt.setPadding(20, 40, 0, 0);
            txt.setTextColor(Color.rgb(0,0,0));
            encapsulateLayout.addView(txt);

            // Gets individual URIs, loads images and places in a horizontal layout.
            for (DataSnapshot individualPaths : entireDB.getChildren()) {
                String url = (String) individualPaths.getValue();

                // Dynamically generates imageViews to load the pulled images into.
                ImageView img = new ImageView(view.getContext().getApplicationContext());
                img.setLayoutParams(new android.view.ViewGroup.LayoutParams(700,700));
                img.setMaxHeight(300);
                img.setMaxWidth(300);
                img.setPadding(10,10,5,0);
                Picasso.with(view.getContext().getApplicationContext()).load(url).into(img);
                horizontalLayout.addView(img);
            }

            // Horizontal scrollView that works with the horizontal Layout to make images scroll.
            HorizontalScrollView horizontalScroll = new HorizontalScrollView
                    (view.getContext().getApplicationContext());
            horizontalScroll.setLayoutParams(new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            // Puts all of the elements together for a great experience.
            horizontalScroll.addView(horizontalLayout);
            encapsulateLayout.addView(horizontalScroll);
        }
        masterLayout.addView(encapsulateLayout);
    }
}
