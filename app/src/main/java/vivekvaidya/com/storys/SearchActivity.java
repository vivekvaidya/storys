package vivekvaidya.com.storys;

import android.support.v4.app.Fragment;

/**
 * Created by vivekvaidya on 4/25/17.
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SearchActivity extends Fragment {
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
        return inflater.inflate(R.layout.search_activity, container, false);
    }
}
