package vivekvaidya.com.storys;

import android.support.v4.app.Fragment;

/**
 * Created by vivekvaidya on 4/25/17.
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchActivity extends Fragment {

    // Setting up the required variables
    private DatabaseReference mDatabaseRef;
    private ArrayList<String> mKeys = new ArrayList<>();
    private EditText mEditText;

    /**
     * Sets up a new SearchActiviy and returns it for use as a fragment.
     * @return SearchActivity
     */
    public static SearchActivity newInstance() {
        SearchActivity fragment = new SearchActivity();
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
        final View view = inflater.inflate(R.layout.search_activity, container, false);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("states");
        mEditText = (EditText) view.findViewById(R.id.meme); //TODO pls fix
        Button mButton = (Button) view.findViewById(R.id.search);

        // Takes in the input from EditText onClick
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Automatically hides the keyboard on buttonClick - EXTRA POINTS????
                InputMethodManager mgr = (InputMethodManager) getActivity().
                        getSystemService(getContext().getApplicationContext().INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);

                // Calls showData() when data is changed/updated on the realtime database
                mDatabaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        showData(dataSnapshot, view);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Trouble reading from database.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        return view;
    }

    /**
     * Helper method that programmatically sets up masterLayouts and lays out elements pulled
     * from the realtime database.
     *
     * @param dataSnapshot
     * @param view
     */
    public void showData(DataSnapshot dataSnapshot, View view) {

        // Populates Arraylist mKeys with the keys from dataSnapshot
        for (DataSnapshot entireDB : dataSnapshot.getChildren()) {
            mKeys.add(entireDB.getKey());
        }

        // Master linear layout that contains child elements
        LinearLayout masterLayout = (LinearLayout) view.findViewById(R.id.kek); //TODO fix pls

        // HorizontalScrollView for images corresponding to each state.  
        HorizontalScrollView stateImages = new HorizontalScrollView
                (view.getContext().getApplicationContext());
        stateImages.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        // Linear layout that hosts multiple images.  
        LinearLayout multipleImages = new LinearLayout(view.getContext().getApplicationContext());
        multipleImages.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        multipleImages.setOrientation(LinearLayout.HORIZONTAL);

        // Add multipleImages to the ScrollView.
        stateImages.addView(multipleImages);

        if (mKeys.contains(mEditText.getText().toString())) {

            // Two loops to go through the entire dataSnapshot and then individual paths.
            for (DataSnapshot entireDB : dataSnapshot.getChildren()) {

                // Check if the search term matches the current key
                if (entireDB.getKey().compareTo(mEditText.getText().toString()) == 0) {
                    for (DataSnapshot individualPaths : entireDB.getChildren()) {
                        String imgUri = (String) individualPaths.getValue();

                        // Dynamically generate imageViews to load the pulled images into.
                        ImageView img = new ImageView(view.getContext().getApplicationContext());
                        img.setLayoutParams(new android.view.ViewGroup.LayoutParams(700, 700));
                        img.setMaxHeight(300);
                        img.setMaxWidth(300);
                        img.setPadding(10, 10, 10, 0);
                        Picasso.with(getContext().getApplicationContext()).load(imgUri).into(img);
                        multipleImages.addView(img);
                    }
                }
            }
            masterLayout.addView(stateImages);
        } else {
            Toast.makeText(getActivity().getApplicationContext(),
                    "No pictures were taken in this state.", Toast.LENGTH_LONG).show();
        }
    }
}
