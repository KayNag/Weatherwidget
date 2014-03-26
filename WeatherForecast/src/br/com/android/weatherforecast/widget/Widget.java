package br.com.android.weatherforecast.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import br.com.android.weatherforecast.R;
import br.com.android.weatherforecast.WeatherForecast;
import br.com.android.weatherforecast.weather.WeatherIcons;
import br.com.android.weatherforecast.weather.WeatherPreferences;
import br.com.android.weatherforecast.weather.WeatherSet;
import br.com.android.weatherforecast.weather.WundergroundDecoder;

/**
 * Classe para Gerenciamento do Widget de Previsão do Tempo
 * @author Felipe Cobello
 */
public class Widget extends AppWidgetProvider
{
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		context.startService(new Intent(context, UpdateService.class));
	}

	/**
	 * Classe para o Gerencimento do Serviço Atualização
	 * @author Felipe Cobello
	 */
	public static class UpdateService extends Service
	{
		private WeatherPreferences weatherPref;
		private WeatherSet weatherSet;
		
		@Override
		public void onStart(Intent intent, int startId)
		{
			weatherPref = new WeatherPreferences(getSharedPreferences("weatherPref", MODE_PRIVATE));
			new Progress().execute();
		}

		/**
		 * Retorna o {@link RemoteViews} com a Atualização do Clima
		 * @param context Context
		 * @return RemoteViews
		 */
		public RemoteViews buildUpdate(Context context)
		{
			RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget);

			updateViews = updateWeatherInfo(context, weatherPref.getCity());
			return updateViews;
		}

		/**
		 * Retorna o {@link RemoteViews} com a Atualização do Clima On-Line
		 * @param context Context
		 * @param city Cidade
		 * @return RemoteViews
		 */
		private RemoteViews updateWeatherInfo(Context context, String city)
		{
			RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget);
			Intent defineIntent = new Intent(context, WeatherForecast.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, defineIntent, 0);

			try
			{
				updateViews.setOnClickPendingIntent(R.id.widget, pendingIntent);
				if (weatherSet != null)
				{
					updateViews.setTextViewText(R.id.definition, weatherSet.getWeatherCurrentCondition().getTempCelcius() + "°C");
					updateViews.setTextViewText(R.id.city, weatherPref.getCity());
					updateViews.setTextViewText(R.id.condition, weatherSet.getWeatherCurrentCondition().getCondition());
					updateViews.setImageViewResource(R.id.background, WeatherIcons.getImageDrawable(weatherSet.getWeatherCurrentCondition().getIconURL()));
				}
			}
			catch (Exception e)
			{
				Log.e(WeatherForecast.DEBUG_TAG, e.getMessage(), e);
			}
			return updateViews;
		}

		@Override
		public IBinder onBind(Intent intent)
		{
			return null;
		}

		private class Progress extends AsyncTask<Void, String, Void>
		{
			@Override
			protected void onPostExecute(Void result) {
				RemoteViews updateViews;
				ComponentName thisWidget = new ComponentName(UpdateService.this, Widget.class);
				AppWidgetManager manager = AppWidgetManager.getInstance(UpdateService.this);
				
				updateViews = buildUpdate(UpdateService.this);
				manager.updateAppWidget(thisWidget, updateViews);
			}
			
			@Override
			protected Void doInBackground(Void... params) 
			{
				WundergroundDecoder decoder = new WundergroundDecoder(UpdateService.this);
				
				try {
					weatherSet = decoder.getWeatherSet(weatherPref.getCity());
				} catch (Exception e) {
					Log.e(WeatherForecast.DEBUG_TAG, e.getMessage(), e);
				}
				return null;
			}
		}
	}
}
