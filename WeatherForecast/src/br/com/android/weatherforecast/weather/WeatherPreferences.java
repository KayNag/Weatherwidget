package br.com.android.weatherforecast.weather;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class WeatherPreferences
{
	private SharedPreferences preferences;
	
	public WeatherPreferences(SharedPreferences sharedPreferences)
	{
		preferences = sharedPreferences;
	}
	
	public void setCity(String city)
	{
		Editor edit = preferences.edit();
		
		edit.putString("city", city);
		edit.commit();
	}
	
	public String getCity()
	{
		return preferences.getString("city", "");
	}
	
	public void setLastUpdate(Date date)
	{
		SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Editor edit = preferences.edit();
		
		edit.putString("lastUpdate", dateformat.format(date));
		edit.commit();
	}
	
	public Date getLastUpdate() throws ParseException
	{
		SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		return dateformat.parse(preferences.getString("lastUpdate", "01/01/2000 00:00"));
	}
}
