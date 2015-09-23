package com.learn.johanfabiel.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.learn.johanfabiel.sunshine.R;
import com.learn.johanfabiel.sunshine.SettingsActivity;
import com.learn.johanfabiel.sunshine.Utility;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback {

  private String LOG_TAG = MainActivity.class.getSimpleName();
  private static final String DETAILFRAGMENT_TAG = "DFTAG";

  private String mLocation;
  private boolean mTwoPane = true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mLocation = Utility.getPreferredLocation(this);

    setContentView(R.layout.activity_main);
    if (findViewById(R.id.weather_detail_container) != null) {
      // The detail container view will be present only in the large-screen layouts
      // (res/layout-sw600dp). If this view is present, then the activity should be
      // in two-pane mode.
      mTwoPane = true;
      // In two-pane mode, show the detail view in this activity by
      // adding or replacing the detail fragment using a
      // fragment transaction.
      if (savedInstanceState == null) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
            .commit();
      }
    } else {
      mTwoPane = false;
      //getSupportActionBar().setElevation(0f);
    }
    ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
    if (null != ff) {
      ff.onLocationChanged();
      ff.setUseTodayLayout(!mTwoPane);
    }
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.

    switch (item.getItemId()) {
      case R.id.action_settings:
        //noinspection SimplifiableIfStatement
        Intent intentSettings = new Intent(this, SettingsActivity.class);
        this.startActivity(intentSettings);
        return true;
      case R.id.map_location:
        openPreferredLocationInMap();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void openPreferredLocationInMap() {
    String location = Utility.getPreferredLocation(this);
    Uri geoLocation = Uri.parse("geo?0,0?").buildUpon()
        .appendQueryParameter("q", location)
        .build();
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(geoLocation);
    if (intent.resolveActivity(getPackageManager()) != null) {
      startActivity(intent);
    } else {
      Log.d(LOG_TAG, "Couldn't call " + location + ", no receiving apps installed!");
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    String location = Utility.getPreferredLocation(this);
    // update the location in our second pane using the fragment manager
    if (location != null && !location.equals(mLocation)) {
      DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
      if (null != df) {
        df.onLocationChanged(location);
      }
      mLocation = location;
    }
  }

  @Override
  public void onItemSelected(Uri dateUri) {
    if(mTwoPane) {
      Bundle bundle = new Bundle();
      bundle.putParcelable(DetailFragment.DETAIL_URI, dateUri);
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.weather_detail_container, DetailFragment.newInstance(bundle), DETAILFRAGMENT_TAG)
          .commit();
    } else {
      Intent intent = new Intent(this, DetailActivity.class)
          .setData(dateUri);
      startActivity(intent);
    }
  }
}
