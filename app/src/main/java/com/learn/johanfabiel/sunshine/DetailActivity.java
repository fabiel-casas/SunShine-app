package com.learn.johanfabiel.sunshine;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


public class DetailActivity extends ActionBarActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);
    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .add(R.id.container, DetailFragment.newInstance(getIntent().getExtras()))
          .commit();
    }
  }
}
