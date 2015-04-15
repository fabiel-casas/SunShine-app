package com.learn.johanfabiel.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;


public class MainActivity extends ActionBarActivity {

  private String LOG_TAG = MainActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .add(R.id.container, new ForecastFragment())
          .commit();
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

    switch (item.getItemId()){
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
      Log.e(LOG_TAG, "Couldn't call " + location);
    }
  }
}
