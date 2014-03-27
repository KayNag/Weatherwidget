package kay.maluuba.view;


import kay.maluuba.helper.JsonParser;
import kay.maluuba.view.controller.Icon;
import kay.maluuba.view.controller.Preference;
import kay.maluuba.view.controller.WeatherSet;
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


public class Widget extends AppWidgetProvider
{
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		context.startService(new Intent(context, UpdateService.class));
	}


	public static class UpdateService extends Service
	{
		private Preference weatherPref;
		private WeatherSet weatherSet;
		
		@Override
		public void onStart(Intent intent, int startId)
		{
			weatherPref = new Preference(getSharedPreferences("weatherPref", MODE_PRIVATE));
			new Progress().execute();
		}


		public RemoteViews buildUpdate(Context context)
		{
			RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget);

			updateViews = updateWeatherInfo(context, weatherPref.getCity());
			return updateViews;
		}


		private RemoteViews updateWeatherInfo(Context context, String city)
		{
			RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget);
			Intent defineIntent = new Intent(context, Detailview.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, defineIntent, 0);

			try
			{
				updateViews.setOnClickPendingIntent(R.id.widget, pendingIntent);
				if (weatherSet != null)
				{
					updateViews.setTextViewText(R.id.definition, weatherSet.getWeatherCurrentCondition().getTempCelcius() + "°C");
					updateViews.setTextViewText(R.id.city, weatherPref.getCity());
					updateViews.setTextViewText(R.id.condition, weatherSet.getWeatherCurrentCondition().getCondition());
					updateViews.setImageViewResource(R.id.background, Icon.getImageDrawable(weatherSet.getWeatherCurrentCondition().getIconURL()));
				}
			}
			catch (Exception e)
			{
				Log.e(Detailview.DEBUG_TAG, e.getMessage(), e);
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
				JsonParser decoder = new JsonParser(UpdateService.this);
				
				try {
					weatherSet = decoder.getWeatherSet(weatherPref.getCity());
				} catch (Exception e) {
					Log.e(Detailview.DEBUG_TAG, e.getMessage(), e);
				}
				return null;
			}
		}
	}
}
