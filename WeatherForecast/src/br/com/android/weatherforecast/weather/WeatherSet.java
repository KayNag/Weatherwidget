package br.com.android.weatherforecast.weather;

import java.io.Serializable;
import java.util.ArrayList;

public class WeatherSet implements Serializable
{
	private static final long serialVersionUID = -4485037818705561060L;
	private WeatherForecastInformation myForecastInformation = null;
	private WeatherCurrentCondition myCurrentCondition = null;
	private ArrayList<WeatherForecastCondition> myForecastConditions =
			new ArrayList<WeatherForecastCondition>(4);

	public WeatherCurrentCondition getWeatherCurrentCondition()
	{
		return myCurrentCondition;
	}

	public void setWeatherCurrentCondition(
			WeatherCurrentCondition myCurrentWeather)
	{
		this.myCurrentCondition = myCurrentWeather;
	}

	public ArrayList<WeatherForecastCondition> getWeatherForecastConditions()
	{
		return this.myForecastConditions;
	}

	public WeatherForecastCondition getLastWeatherForecastCondition()
	{
		return this.myForecastConditions
				.get(this.myForecastConditions.size() - 1);
	}

	public void setWeatherForecastInformation(WeatherForecastInformation myForecastInformation)
	{
		this.myForecastInformation = myForecastInformation;
	}

	public WeatherForecastInformation getWeatherForecastInformation()
	{
		return myForecastInformation;
	}
}