package com.learn.johanfabiel.sunshine.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.learn.johanfabiel.sunshine.R;


public class DetailActivity extends ActionBarActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);
    if (savedInstanceState == null) {
      Bundle arguments = new Bundle();
      arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());
      getSupportFragmentManager().beginTransaction()
          .add(R.id.weather_detail_container, DetailFragment.newInstance(arguments))
          .commit();
    }
  }
}
