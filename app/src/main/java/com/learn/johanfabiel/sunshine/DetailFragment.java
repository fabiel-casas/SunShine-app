package com.learn.johanfabiel.sunshine;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
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

import com.learn.johanfabiel.sunshine.data.WeatherContract;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

  public static final String KEY_NAME_WEATHER = "weather";

  private TextView nameDetail;
  private String LOG_TAG = DetailFragment.class.getSimpleName();
  private String mForecastStr;
  private ShareActionProvider mShareActionProvider;

  private static final int DETAIL_LOADER = 0;

  private static final String[] FORECAST_COLUMNS = {
      WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
      WeatherContract.WeatherEntry.COLUMN_DATE,
      WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
      WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
      WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
  };

  public DetailFragment() {
    // Required empty public constructor
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_detail_fragement, container, false);
    nameDetail = (TextView) rootView.findViewById(R.id.nameDetail);
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
    shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr +" "+ getString(R.string.shared_copy));
    return  shareIntent;
  }


  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    // Inflate the menu; this adds items to the action bar if it is present.
    inflater.inflate(R.menu.detailfragment, menu);

    // Find the MenuItem that we know has the ShareActionProvider
    MenuItem item = menu.findItem(R.id.menu_item_share);

    // Get its ShareActionProvider
    mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

    // Connect the dots: give the ShareActionProvider its Share Intent
    if (mShareActionProvider != null) {
      mShareActionProvider.setShareIntent(createShareForecastIntent());
    } else {
      Log.d(LOG_TAG, "Share Action Provider is null?");
    }
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    Log.v(LOG_TAG, "In onCreateLoader");
    Intent intent = getActivity().getIntent();
    if (intent == null) {
      return null;
    }

    return new CursorLoader(
        getActivity(),
        intent.getData(),
        FORECAST_COLUMNS,
        null,
        null,
        null
    );
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    Log.v(LOG_TAG, "In onLoadFinished");
    if(!data.moveToFirst()){
      return;
    }
    String dateString = Utility.formatDate(data.getLong(ForecastFragment.COL_WEATHER_DATE));
    String weatherDescription = data.getString(ForecastFragment.COL_WEATHER_DESC);
    boolean isMetric = Utility.isMetric(getActivity());
    String high = Utility.formatTemperature(
        data.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP), isMetric
    );
    String low = Utility.formatTemperature(
        data.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP), isMetric
    );
    mForecastStr = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

    nameDetail.setText(mForecastStr);

    if (mShareActionProvider != null) {
      mShareActionProvider.setShareIntent(createShareForecastIntent());
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {

  }
}
