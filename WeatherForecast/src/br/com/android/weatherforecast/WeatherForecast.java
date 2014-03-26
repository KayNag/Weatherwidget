package br.com.android.weatherforecast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.android.weatherforecast.views.WeatherLine;
import br.com.android.weatherforecast.weather.WeatherCurrentCondition;
import br.com.android.weatherforecast.weather.WeatherForecastCondition;
import br.com.android.weatherforecast.weather.WeatherIcons;
import br.com.android.weatherforecast.weather.WeatherPreferences;
import br.com.android.weatherforecast.weather.WeatherSet;
import br.com.android.weatherforecast.weather.WeatherUtils;
import br.com.android.weatherforecast.weather.WundergroundDecoder;
import br.com.android.weatherforecast.widget.Widget.UpdateService;

/**
 * Classe para Gerenciamento da Aplicação de Previsão do Tempo
 * @author Felipe Cobello
 *
 */
public class WeatherForecast extends Activity
{
	public final static String DEBUG_TAG = "WEATHER_FORECAST";
	private EditText txtCidade;
	private ImageButton btnOk;
	private Event event = new Event();
	private WeatherPreferences weatherPref;
	private LocationManager location;
	private Geocoder geo;
	private WeatherSet ws;
	private WundergroundDecoder decoder;
	private boolean firstStart = true;

	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.main);
		txtCidade = ((EditText) findViewById(R.id.edit_input));
		btnOk = ((ImageButton) findViewById(R.id.cmd_submit));
		txtCidade.setOnKeyListener(event);
		txtCidade.setOnClickListener(event);
		btnOk.setOnClickListener(event);
	}

	public void onStart()
	{
		super.onStart();
		weatherPref = new WeatherPreferences(getSharedPreferences("weatherPref", MODE_PRIVATE));
		decoder = new WundergroundDecoder(WeatherForecast.this);
		if(firstStart && !weatherPref.getCity().equals(""))
		{
			txtCidade.setText(weatherPref.getCity());
			firstStart = false;
			update();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.itmSair:
				super.finish();
				return true;
			case R.id.itmAtualizar:
				update();
				return true;
			case R.id.itmLocation:
				searchLocation();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * Busca localização atraves do GPS ou Triangulação WiFi
	 */
	private void searchLocation()
	{
		List<Address> enderecos;
		Location myLocation;
		try
		{
			geo = new Geocoder(this, Locale.getDefault());
			location = (LocationManager)getSystemService(LOCATION_SERVICE);
			myLocation = location.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if(myLocation == null)
				myLocation = location.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			enderecos = geo.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);
			if(enderecos.size() > 0)
				txtCidade.setText(enderecos.get(0).getLocality());
			else
				txtCidade.setText(weatherPref.getCity());
			update();
		}
		catch (Exception e) 
		{
			Log.e(WeatherForecast.DEBUG_TAG, e.getMessage(), e);
			WeatherUtils.showMessage(WeatherForecast.this, getString(R.string.locationErrorMsg));
			resetWeatherInfoViews();
		}
	}

	/**
	 * Atualiza Previsão dos Proximos Dias
	 * @param aResourceID Identificador do Componentem que vai ser atualizado
	 * @param aWFIS Informaçoes do Tempo
	 * @throws MalformedURLException
	 */
	private void updateWeatherInfoView(int aResourceID, WeatherForecastCondition aWFIS)
	{
		((WeatherLine) findViewById(aResourceID)).setImageDrawable(getResources().getDrawable(WeatherIcons.getImageDrawable(aWFIS.getIconURL())));
		((WeatherLine) findViewById(aResourceID)).setTempString(aWFIS.getDayofWeek() + "\n" + aWFIS.getCondition() + "\n" + aWFIS.getTempMin() + "°C/" + aWFIS.getTempMax() + "°C" + "\n" + getString(R.string.rain) + ": " + aWFIS.getPrecipitation());
	}

	/**
	 * Atualiza Previsão Atual
	 * @param aWCIS Informaçoes do Tempo
	 */
	private void updateWeatherInfoView(WeatherCurrentCondition aWCIS)
	{
		((ImageView) findViewById(R.id.imgWeather)).setImageDrawable(getResources().getDrawable(WeatherIcons.getImageDrawable(aWCIS.getIconURL())));
		((TextView) findViewById(R.id.weather_today_temp)).setText(aWCIS.getTempCelcius() + "°C");
		((TextView) findViewById(R.id.weather_today_city)).setText(txtCidade.getText().toString());
		((TextView) findViewById(R.id.weather_today_condition)).setText(aWCIS.getCondition() + "\n" + getString(R.string.wind) + " " + aWCIS.getWindCondition() + "\n" + getString(R.string.humidity) + ": " + aWCIS.getHumidity());
		((TextView) findViewById(R.id.lblProximosDias)).setText(getString(R.string.nextDays));
	}

	/**
	 * Reinicia Informacoes
	 */
	private void resetWeatherInfoViews()
	{
		((ImageView)findViewById(R.id.imgWeather)).setImageDrawable(getResources().getDrawable(R.drawable.undefined));
		((TextView) findViewById(R.id.weather_today_city)).setText(getString(R.string.city));
		((TextView) findViewById(R.id.weather_today_temp)).setText("0 °C");
		((TextView) findViewById(R.id.weather_today_condition)).setText(getString(R.string.condition));
		((WeatherLine) findViewById(R.id.weather_1)).reset();
		((WeatherLine) findViewById(R.id.weather_2)).reset();
		((WeatherLine) findViewById(R.id.weather_3)).reset();
	}

	/**
	 * Realiza a busca da previsão do tempo, quando tempo da ultima busca for inferior a 1,5hrs utiliza cache, senão
	 * faz uma nova consulta ao serviço
	 */
	private void searchWeatherInfo()
	{
		try
		{
			
			if(weatherPref.getCity().equalsIgnoreCase(txtCidade.getText().toString()))
			{
				if((System.currentTimeMillis() - weatherPref.getLastUpdate().getTime()) > 1800000)
				{
					ws = decoder.getWeatherSet(txtCidade.getText().toString());
					saveCache(ws);
				}
				else{
					ws = restoreCache();
				}
			}
			else
			{
				weatherPref.setCity(txtCidade.getText().toString());
				ws = decoder.getWeatherSet(txtCidade.getText().toString());
				saveCache(ws);
				WeatherForecast.this.startService(new Intent(WeatherForecast.this, UpdateService.class));
			}
		}
		catch (Exception e)
		{
			Log.e(WeatherForecast.DEBUG_TAG, e.getMessage(), e);
		}
	}
	
	/**
	 * Inicializa a execução da Thread de processamento
	 */
	private void update()
	{	
		if(txtCidade.getText().toString().equals(""))
			WeatherUtils.showMessage(WeatherForecast.this, getString(R.string.enterCity));
		else
			new Progress().execute();
	}
	
	/**
	 * Atualiza Objetos da Tela
	 * @throws ParseException
	 */
	private void updateView() throws ParseException
	{
		if (ws != null)
		{
			updateWeatherInfoView(ws.getWeatherCurrentCondition());
			updateWeatherInfoView(R.id.weather_1, ws.getWeatherForecastConditions().get(1));
			updateWeatherInfoView(R.id.weather_2, ws.getWeatherForecastConditions().get(2));
			updateWeatherInfoView(R.id.weather_3, ws.getWeatherForecastConditions().get(3));
			((TextView)findViewById(R.id.lblAtualizacao)).setText(getString(R.string.lastUpdate) + ": " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(weatherPref.getLastUpdate()));
		}
		else
			WeatherUtils.showMessage(WeatherForecast.this, getString(R.string.forecastNotFound));
	}
	
	/**
	 * Salva classe serializada no Cache
	 * @param weatherset
	 * @throws IOException
	 */
	private void saveCache(WeatherSet weatherset) throws IOException
	{
		File cache = new File(getCacheDir(), "cache.dat");
		ObjectOutputStream out;

		out = new ObjectOutputStream(new FileOutputStream(cache));
		out.writeObject(weatherset);
		out.close();
		weatherPref.setLastUpdate(new Date(System.currentTimeMillis()));
	}
	
	/**
	 * Recupera classe serializada do cache
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws JSONException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private WeatherSet restoreCache() throws FileNotFoundException, IOException, ClassNotFoundException, JSONException, InterruptedException, ExecutionException
	{
		File cache = new File(getCacheDir(), "cache.dat");
		WeatherSet ws = null;
		ObjectInputStream out;
		
		if(cache.exists())
		{
			out = new ObjectInputStream(new FileInputStream(new File(getCacheDir(), "cache.dat")));
			ws = (WeatherSet) out.readObject();
			out.close();
		}
		if(ws == null)
			ws = decoder.getWeatherSet(txtCidade.getText().toString());
		return ws;
	}
	
	/**
	 * Eventos da Tela
	 * @author Felipe Cobello
	 *
	 */
	private class Event implements OnClickListener, OnKeyListener
	{
		@Override
		public void onClick(View arg0)
		{
			if (arg0.equals(btnOk))
			{
				txtCidade.setText(WeatherUtils.captalizeWords(txtCidade.getText().toString()));
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(arg0.getWindowToken(), 0);
				update();
			}
		}

		@Override
		public boolean onKey(View arg0, int arg1, KeyEvent arg2)
		{
			boolean retorno = false;
			
			if(arg0.equals(txtCidade))
			{
				if (arg2.getAction() == KeyEvent.ACTION_DOWN && arg2.getKeyCode() == KeyEvent.KEYCODE_ENTER)
				{
					retorno = true;
					txtCidade.setText(WeatherUtils.captalizeWords(txtCidade.getText().toString()));
					((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(arg0.getWindowToken(), 0);
					update();
				}
			}
			return retorno;
		}
	}
	
	/**
	 * Thread para controlar a chamada ao serviço e atualização da tela
	 * @author Felipe Cobello
	 *
	 */
	private class Progress extends AsyncTask<Void, String, Void>
	{
		ProgressDialog dialog = new ProgressDialog(WeatherForecast.this);
		
		@Override
		protected void onPreExecute() {
			dialog.setMessage(getString(R.string.processMsg));
			dialog.setCancelable(false);
			dialog.show();
		}
		
		@Override
		protected void onPostExecute(Void result) {
			try {
				updateView();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dialog.dismiss();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			publishProgress(getString(R.string.processMsg));
			searchWeatherInfo();
			return null;
		}
		
		@Override
		protected void onProgressUpdate(String... values) {
			dialog.setMessage(getString(R.string.processMsg));
		}
	}
}