package com.learn.johanfabiel.sunshine.app;


import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.learn.johanfabiel.sunshine.FetchWeatherTask;
import com.learn.johanfabiel.sunshine.ForecastAdapter;
import com.learn.johanfabiel.sunshine.R;
import com.learn.johanfabiel.sunshine.Utility;
import com.learn.johanfabiel.sunshine.data.WeatherContract;

/**
 * Created by JohanFabiel on 01/03/2015.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

  private final String LOG_TAG = ForecastFragment.class.getSimpleName();
  private ListView mListView;
  private ForecastAdapter mForecastAdapter;

  private static final int FORECAST_LOADER = 0;

  private static final String[] FORECAST_COLUMNS = {
      // In this case the id needs to be fully qualified with a table name, since
      // the content provider joins the location & weather tables in the background
      // (both have an _id column)
      // On the one hand, that's annoying.  On the other, you can search the weather table
      // using the location set by the user, which is only in the Location table.
      // So the convenience is worth it.
      WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
      WeatherContract.WeatherEntry.COLUMN_DATE,
      WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
      WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
      WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
      WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
      WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
      WeatherContract.LocationEntry.COLUMN_COORD_LAT,
      WeatherContract.LocationEntry.COLUMN_COORD_LONG
  };

  // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
  // must change.
  public static final int COL_WEATHER_ID = 0;
  public static final int COL_WEATHER_DATE = 1;
  public static final int COL_WEATHER_DESC = 2;
  public static final int COL_WEATHER_MAX_TEMP = 3;
  public static final int COL_WEATHER_MIN_TEMP = 4;
  public static final int COL_LOCATION_SETTING = 5;
  public static final int COL_WEATHER_CONDITION_ID = 6;
  public static final int COL_COORD_LAT = 7;
  public static final int COL_COORD_LONG = 8;

  public static final String SELECTED_KEY = "current_position";
  private int mPosition = 0;

  public void setUseTodayLayout(boolean useTodayLayout) {
    if(mForecastAdapter != null){
      mForecastAdapter.setUseTodayLayout(useTodayLayout);
    }
  }

  /**
   * A callback interface that all activities containing this fragment must
   * implement. This mechanism allows activities to be notified of item
   * selections.
   */
  public interface Callback {
    /**
     * DetailFragmentCallback for when an item has been selected.
     */
    public void onItemSelected(Uri dateUri);
  }

  public ForecastFragment() {
  }

  @Override

  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    String locationSetting = Utility.getPreferredLocation(getActivity());
    String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
    Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());
    Cursor cur = getActivity().getContentResolver().query(weatherForLocationUri,
                null, null, null, sortOrder);
    // The CursorAdapter will take data from our cursor and populate the ListView
    // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
    // up with an empty list the first time we run.
    mForecastAdapter = new ForecastAdapter(getActivity(), cur, 0);

    if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
      mPosition = savedInstanceState.getInt(SELECTED_KEY);
    }

    View rootView = inflater.inflate(R.layout.fragment_main, container, false);

    mListView = (ListView) rootView.findViewById(R.id.listView_forecast);
    mListView.setAdapter(mForecastAdapter);
    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView adapterView, View view, int position, long l) {
        // CursorAdapter returns a cursor at the correct position for getItem(), or null
        // if it cannot seek to that position.
        Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        mPosition = position;
        if (cursor != null) {
          String locationSetting = Utility.getPreferredLocation(getActivity());
          ((Callback) getActivity()).onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
              locationSetting, cursor.getLong(COL_WEATHER_DATE)));
        }
      }
    });
    return rootView;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getLoaderManager().initLoader(FORECAST_LOADER, null, this);
  }

  // since we read the location when we create the loader, all we need to do is restart things
  public void onLocationChanged( ) {
    updateWeather();
    getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.forecastfragment, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    switch (itemId){
      case R.id.action_refresh:
        updateWeather();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void updateWeather() {
    FetchWeatherTask fetchWeatherTask = new FetchWeatherTask(getActivity());
    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
    String location = Utility.getPreferredLocation(getActivity());
    String units = pref.getString(getString(R.string.pref_units_key),
        getString(R.string.pref_units_default));
    fetchWeatherTask.execute(location, units);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    String locationSetting = Utility.getPreferredLocation(getActivity());
    //Sort order: Ascending, by date.
    String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
    Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
        locationSetting,
        System.currentTimeMillis()
    );
    return new CursorLoader(getActivity(), weatherForLocationUri, FORECAST_COLUMNS, null, null, sortOrder);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    mForecastAdapter.swapCursor(cursor);
    if(mPosition != ListView.INVALID_POSITION) {
      mListView.setSelection(mPosition);
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    mForecastAdapter.swapCursor(null);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    if(mPosition != ListView.INVALID_POSITION) {
      outState.putInt(SELECTED_KEY, mPosition);
    }
    super.onSaveInstanceState(outState);
  }


}
