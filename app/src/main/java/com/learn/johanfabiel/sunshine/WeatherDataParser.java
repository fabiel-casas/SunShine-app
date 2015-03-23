package com.learn.johanfabiel.sunshine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JohanFabiel on 01/03/2015.
 */
public class WeatherDataParser {

  private static final String LIST_DAYS = "list";
  private static final String DAYS_TEMP = "temp";
  private static final String TEMP_MAX = "max";
  /**
   * Given a string of the form returned by the api call:
   * http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7
   * retrieve the maximum temperature for the day indicated by dayIndex
   * (Note: 0-indexed, so 0 would refer to the first day).
   */
  public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
      throws JSONException {
    JSONObject forecastJson = new JSONObject(weatherJsonStr);
    JSONArray listDayWeader = forecastJson.getJSONArray(LIST_DAYS);
    if(listDayWeader.length() > dayIndex){
      JSONObject tempDay = listDayWeader.getJSONObject(dayIndex).getJSONObject(DAYS_TEMP);
      return tempDay.getDouble(TEMP_MAX);
    }
    return -1;
  }

}
