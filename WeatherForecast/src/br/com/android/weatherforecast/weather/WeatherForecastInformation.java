package br.com.android.weatherforecast.weather;

import java.io.Serializable;

public class WeatherForecastInformation implements Serializable
{
	private static final long serialVersionUID = -836325109897133077L;
	private String city;
	private long time;

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getCity()
	{
		return city;
	}
	
	public void setTime(long time)
	{
		this.time = time;
	}
	
	public long getTime()
	{
		return time;
	}

}
