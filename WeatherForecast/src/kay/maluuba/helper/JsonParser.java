package kay.maluuba.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import kay.maluuba.view.controller.Currentweather;
import kay.maluuba.view.controller.Weatherrelay;
import kay.maluuba.view.controller.Weatherrelayinfor;
import kay.maluuba.view.controller.WeatherSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class JsonParser {

	private WeatherSet weatherSet = new WeatherSet();

	private Context context;

	public JsonParser(Context context) {
		this.context = context;
	}

	public WeatherSet getWeatherSet(String userid)
			throws MalformedURLException, IOException, JSONException {
		String line;
		String queryString;
		StringBuilder response = new StringBuilder();
		URLConnection connection;
		BufferedReader reader;

		if (userid.equals(""))
			return weatherSet;
		queryString = "http://ec2-54-198-118-231.compute-1.amazonaws.com:8080/wunderground/getWeather?input=%7B%22gps%22:%7B%22latitude%22:39.923614,%22longitude%22:116.396086,%22radius%22:0.0%7D%7D&oauthToken=ANDROIDINTERVIEW&requestInfo=%7B%22userId%22:%22"
				+ userid + "%22%7D";
		connection = new URL(queryString.replace(" ", "%20")).openConnection();
		connection.setConnectTimeout(1000 * 5);
		reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream(), Charset.forName("UTF-8")));
		while ((line = reader.readLine()) != null)
			response.append(line);
		decodeJSONForecast(response.toString());
		reader.close();
		connection = null;
		reader = null;
		response = new StringBuilder();
		queryString = "http://ec2-54-198-118-231.compute-1.amazonaws.com:8080/wunderground/getWeather?input=%7B%22gps%22:%7B%22latitude%22:39.923614,%22longitude%22:116.396086,%22radius%22:0.0%7D%7D&oauthToken=ANDROIDINTERVIEW&requestInfo=%7B%22userId%22:%22"
				+ userid + "%22%7D";
		connection = new URL(queryString.replace(" ", "%20")).openConnection();
		connection.setConnectTimeout(1000 * 5);
		reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream(), Charset.forName("UTF-8")));
		while ((line = reader.readLine()) != null)
			response.append(line);
		decodeJSONConditions(response.toString());
		weatherSet.setWeatherForecastInformation(new Weatherrelayinfor());
		weatherSet.getWeatherForecastInformation().setCity(userid);
		weatherSet.getWeatherForecastInformation().setTime(
				System.currentTimeMillis());
		return weatherSet;
	}

	private void decodeJSONConditions(String response) throws JSONException {
		JSONObject json = new JSONObject(response.toString());
		Currentweather current = new Currentweather();
		JSONObject condition = (JSONObject) json.get("platformResponse");
		JSONObject simpleforecast = (JSONObject) condition.get("weather");
		JSONObject present = (JSONObject) simpleforecast
				.get("currentCondition");
		JSONObject weather = (JSONObject) present.get("weatherData");
		JSONObject wind = (JSONObject) weather.get("windData");
		JSONObject tempdata = (JSONObject) weather.get("temperatureData");

		current.setTempCelcius(tempdata.getInt("tempFahrenheit"));
		current.setCondition(present.getString("currCondition"));
		current.setWindCondition(wind.getString("direction") + " a "
				+ wind.getString("speedMPH") + " MPH");
		current.setIconURL(present.getString("currCondition"));
		current.setHumidity(tempdata.getString("relativeHumidity"));
		weatherSet.setWeatherCurrentCondition(current);
	}

	private void decodeJSONForecast(String response) throws JSONException {
		JSONObject json = new JSONObject(response.toString());
		JSONObject forecast = (JSONObject) json.get("platformResponse");
		JSONObject simpleforecast = (JSONObject) forecast.get("weather");

		JSONArray forecastDay = (JSONArray) simpleforecast.get("forecast");
		JSONObject day;

		for (int i = 0; i < forecastDay.length(); i++) {

			Weatherrelay dayForecast = new Weatherrelay();
			day = (JSONObject) forecastDay.get(i);

			dayForecast.setDayofWeek(day.getString("day"));
			dayForecast.setCondition(day.getString("conditions"));
			dayForecast.setIconURL(day.getString("timeOfDayIconURLs"));
			dayForecast.setTempMax(day.getInt("tempHighF"));
			dayForecast.setTempMin(day.getInt("tempLowF"));
			dayForecast.setPrecipitation(day.getString("avgHumidity") + "%");
			weatherSet.getWeatherForecastConditions().add(dayForecast);
		}
	}
}
