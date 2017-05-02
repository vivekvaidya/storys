package vivekvaidya.com.storys;

import android.graphics.Color;
import android.support.v4.app.Fragment;

/**
 * Created by vivekvaidya on 4/25/17.
 */

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchActivity extends Fragment {

    private DatabaseReference mDatabaseRef;
    private ArrayList<String> poop = new ArrayList<>();
    private EditText editText;
    private Button mButton;

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
        final View view = inflater.inflate(R.layout.search_activity, container, false);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("states");
        editText = (EditText) view.findViewById(R.id.meme);

        mButton = (Button) view.findViewById(R.id.search);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(getContext().getApplicationContext().INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                mDatabaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        showData(dataSnapshot, view);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        return view;
    }

    public void showData(DataSnapshot dataSnapshot, View view) {
        for(DataSnapshot s : dataSnapshot.getChildren()) {
            poop.add(s.getKey());
        }

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.kek);

        HorizontalScrollView sv = new HorizontalScrollView(view.getContext().getApplicationContext());
        sv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        LinearLayout ll = new LinearLayout(view.getContext().getApplicationContext());
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ll.setOrientation(LinearLayout.HORIZONTAL);

        sv.addView(ll);

        if (poop.contains(editText.getText().toString())) {

            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                if(ds.getKey().compareTo(editText.getText().toString()) == 0) {

                    for (DataSnapshot ds1 : ds.getChildren()) {
                        String url = (String) ds1.getValue();
                        ImageView image = new ImageView(view.getContext().getApplicationContext());
                        image.setLayoutParams(new android.view.ViewGroup.LayoutParams(700, 700));
                        image.setMaxHeight(300);
                        image.setMaxWidth(300);
                        image.setPadding(10, 10, 10, 0);
                        Picasso.with(view.getContext().getApplicationContext()).load(url).into(image);
                        ll.addView(image);
                    }
                }
            }
            layout.addView(sv);

        } else {
                Toast.makeText(getActivity().getApplicationContext(), "No pictures were taken in this state.", Toast.LENGTH_LONG).show();
            }
        }
}
