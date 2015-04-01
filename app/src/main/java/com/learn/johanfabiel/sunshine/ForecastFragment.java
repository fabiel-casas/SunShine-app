package com.learn.johanfabiel.sunshine;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by JohanFabiel on 01/03/2015.
 */
public class ForecastFragment extends Fragment {

  private final String LOG_TAG = ForecastFragment.class.getSimpleName();
  private ListView listForeCast;
  private ArrayAdapter<String> arrayAdapter;

  public ForecastFragment() {
  }

  @Override

  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_main, container, false);

    ArrayList<String> foreCastEntry = new ArrayList<>();

    listForeCast = (ListView) rootView.findViewById(R.id.listView_forecast);
    arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, foreCastEntry);
    listForeCast.setAdapter(arrayAdapter);
    listForeCast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String namePosition = (String) parent.getItemAtPosition(position);
        Intent detailActivity = new Intent(getActivity(), DetailActivity.class);
        detailActivity.putExtra(DetailFragment.KEY_NAME_WEATHER, namePosition);
        getActivity().startActivity(detailActivity);
      }
    });
    arrayAdapter.setNotifyOnChange(true);
    return rootView;
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
  public void onStart() {
    super.onStart();
    updateWeather();
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
    FetchWeatherTask fetchWeatherTask = new FetchWeatherTask(getActivity(), arrayAdapter);
    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
    String location = pref.getString(getString(R.string.pref_location_key),
        getString(R.string.pref_location_default));
    String units = pref.getString(getString(R.string.pref_units_key),
        getString(R.string.pref_units_default));
    fetchWeatherTask.execute(location, units);
  }

  private void loadListAdapterWeather(String[] strings) {
    arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, new ArrayList<String>(Arrays.asList(strings)));
    listForeCast.setAdapter(arrayAdapter);
  }

}
