package br.com.android.weatherforecast.weather;

import java.io.Serializable;

public class WeatherForecastCondition implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 355766120744171602L;
	private String dayofWeek = null;
	private Integer tempMin = null;
	private Integer tempMax = null;
	private String iconURL = null;
	private String condition = null;
	private String precipitation = null;

	public String getDayofWeek()
	{
		String retorno = dayofWeek;

		if (dayofWeek.startsWith("seg"))
			retorno = "Segunda - Feira";
		if (dayofWeek.startsWith("ter"))
			retorno = "Terça - Feira";
		if (dayofWeek.startsWith("qua"))
			retorno = "Quarta - Feira";
		if (dayofWeek.startsWith("qui"))
			retorno = "Quinta - Feira";
		if (dayofWeek.startsWith("sex"))
			retorno = "Sexta - Feira";
		if (dayofWeek.endsWith("b"))
			retorno = "Sábado";
		if (dayofWeek.startsWith("dom"))
			retorno = "Domingo";
		return retorno;
	}

	public void setDayofWeek(String dayofWeek)
	{
		this.dayofWeek = dayofWeek;
	}

	public Integer getTempMin()
	{
		return tempMin;
	}

	public void setTempMin(Integer tempMin)
	{
		this.tempMin = tempMin;
	}

	public Integer getTempMax()
	{
		return tempMax;
	}

	public void setTempMax(Integer tempMax)
	{
		this.tempMax = tempMax;
	}

	public String getIconURL()
	{
		return iconURL;
	}

	public void setIconURL(String iconURL)
	{
		this.iconURL = iconURL;
	}

	public String getCondition()
	{
		return condition;
	}

	public void setCondition(String condition)
	{
		this.condition = condition;
	}

	public void setPrecipitation(String precipitation) {
		this.precipitation = precipitation;
	}

	public String getPrecipitation() {
		return precipitation;
	}
}
