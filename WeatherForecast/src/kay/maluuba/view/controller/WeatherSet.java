package kay.maluuba.view.controller;

import java.io.Serializable;
import java.util.ArrayList;

public class WeatherSet implements Serializable
{
	private static final long serialVersionUID = -4485037818705561060L;
	private Weatherrelayinfor myForecastInformation = null;
	private Currentweather myCurrentCondition = null;
	private ArrayList<Weatherrelay> myForecastConditions =
			new ArrayList<Weatherrelay>(4);

	public Currentweather getWeatherCurrentCondition()
	{
		return myCurrentCondition;
	}

	public void setWeatherCurrentCondition(
			Currentweather myCurrentWeather)
	{
		this.myCurrentCondition = myCurrentWeather;
	}

	public ArrayList<Weatherrelay> getWeatherForecastConditions()
	{
		return this.myForecastConditions;
	}

	public Weatherrelay getLastWeatherForecastCondition()
	{
		return this.myForecastConditions
				.get(this.myForecastConditions.size() - 1);
	}

	public void setWeatherForecastInformation(Weatherrelayinfor myForecastInformation)
	{
		this.myForecastInformation = myForecastInformation;
	}

	public Weatherrelayinfor getWeatherForecastInformation()
	{
		return myForecastInformation;
	}
}