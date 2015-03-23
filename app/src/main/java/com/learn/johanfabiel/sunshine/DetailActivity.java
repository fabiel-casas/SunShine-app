package com.learn.johanfabiel.sunshine;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


public class DetailActivity extends ActionBarActivity {

  private String nameWeather;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);
    nameWeather = getIntent().getExtras().getString(DetailFragment.KEY_NAME_WEATHER);
    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .add(R.id.container, DetailFragment.newInstance(getIntent().getExtras()))
          .commit();
    }
  }
}
