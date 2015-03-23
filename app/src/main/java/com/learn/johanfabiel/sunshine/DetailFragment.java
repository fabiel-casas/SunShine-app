package com.learn.johanfabiel.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailFragment extends Fragment {

  public static final String KEY_NAME_WEATHER = "weather";

  private String LOG_TAG = DetailFragment.class.getSimpleName();
  private String nameWeather;
  private TextView nameDetail;

  public DetailFragment() {
    // Required empty public constructor
    setHasOptionsMenu(true);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      nameWeather = getArguments().getString(KEY_NAME_WEATHER);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_detail_fragement, container, false);
    nameDetail = (TextView) rootView.findViewById(R.id.nameDetail);
    nameDetail.setText(nameWeather);
    return rootView;
  }

  public static DetailFragment newInstance(Bundle bundle) {
    DetailFragment detailFragment = new DetailFragment();
    detailFragment.setArguments(bundle);
    return detailFragment;
  }

  private Intent createShareForecastIntent() {
    Intent shareIntent = new Intent();
    shareIntent.setAction(Intent.ACTION_SEND);
    shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_TEXT, nameWeather +" "+ getString(R.string.shared_copy));
    return  shareIntent;
  }


  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    // Inflate the menu; this adds items to the action bar if it is present.
    inflater.inflate(R.menu.detailfragment, menu);

    // Find the MenuItem that we know has the ShareActionProvider
    MenuItem item = menu.findItem(R.id.menu_item_share);

    // Get its ShareActionProvider
    ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

    // Connect the dots: give the ShareActionProvider its Share Intent
    if (mShareActionProvider != null) {
      mShareActionProvider.setShareIntent(createShareForecastIntent());
    } else {
      Log.d(LOG_TAG, "Share Action Provider is null?");
    }
  }

}
