package br.com.android.weatherforecast.weather;

import java.util.Calendar;
import br.com.android.weatherforecast.R;

public class WeatherIcons
{
	public static int getImageDrawable(String fileName) 
	{
		boolean day = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 6 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 18;
    	int retorno = R.drawable.undefined;
    	
		if(fileName.contains("partlycloudy") || fileName.contains("cloudy"))
			retorno = day ? R.drawable.cloud:R.drawable.cloud_night;
		if(fileName.contains("mostlycloudy") || fileName.contains("overcast"))
			retorno = day ? R.drawable.mostly_cloudy:R.drawable.cloud_night;
		if(fileName.contains("rainsnow"))
			retorno = day ? R.drawable.rain_snow:R.drawable.snow_night;
		if(fileName.contains("snow"))
			retorno = day ? R.drawable.snow:R.drawable.snow_night;
		if(fileName.contains("storm") || fileName.contains("thunderstorm"))
			retorno = day ? R.drawable.thunderstorm:R.drawable.thunderstorm_night;
		if(fileName.contains("sunny"))
			retorno = day ? R.drawable.sunny:R.drawable.sunny_night;
		if(fileName.contains("mostlysunny") || fileName.contains("clear"))
			retorno = day ? R.drawable.mostly_sunny:R.drawable.sunny_night;
		if(fileName.contains("rain") || fileName.contains("scatteredshowers") || fileName.contains("drizzle"))
			retorno = day ? R.drawable.rain:R.drawable.rain_night;
		if(fileName.contains("haz") || fileName.contains("fog"))
			retorno = day ? R.drawable.fog:R.drawable.fog_night;
		if(fileName.contains("mist"))
			retorno = day ? R.drawable.rain:R.drawable.rain_night;
		return retorno;
	}
}
