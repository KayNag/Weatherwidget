package kay.maluuba.view;

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


import kay.maluuba.view.Widget.UpdateService;
import kay.maluuba.view.controller.Currentweather;
import kay.maluuba.view.controller.Weatherrelay;
import kay.maluuba.view.controller.Icon;
import kay.maluuba.view.controller.Preference;
import kay.maluuba.view.controller.WeatherSet;
import kay.maluuba.view.controller.Utils;
import kay.maluuba.helper.JsonParser;
import kay.maluuba.model.Weatherclass;


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
import android.widget.Toast;


public class Detailview extends Activity
{
        public final static String DEBUG_TAG = "WEATHER_FORECAST";
        private EditText txtCidade;
        private ImageButton btnOk;
        private Event event = new Event();
        private Preference weatherPref;
        private LocationManager location;
        private Geocoder geo;
        private WeatherSet ws;
        private JsonParser decoder;
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
                weatherPref = new Preference(getSharedPreferences("weatherPref", MODE_PRIVATE));
                decoder = new JsonParser(Detailview.this);
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
                        Log.e(Detailview.DEBUG_TAG, e.getMessage(), e);
                        Utils.showMessage(Detailview.this, getString(R.string.locationErrorMsg));
                        resetWeatherInfoViews();
                }
        }

    
        private void updateWeatherInfoView(int aResourceID, Weatherrelay aWFIS)
        {
                ((Weatherclass) findViewById(aResourceID)).setImageDrawable(getResources().getDrawable(Icon.getImageDrawable(aWFIS.getIconURL())));
                ((Weatherclass) findViewById(aResourceID)).setTempString(aWFIS.getDayofWeek() + "\n" + aWFIS.getCondition() + "\n" + aWFIS.getTempMin() + "°C/" + aWFIS.getTempMax() + "°C" + "\n" + getString(R.string.rain) + ": " + aWFIS.getPrecipitation());
        }

   
        private void updateWeatherInfoView(Currentweather aWCIS)
        {
                ((ImageView) findViewById(R.id.imgWeather)).setImageDrawable(getResources().getDrawable(Icon.getImageDrawable(aWCIS.getIconURL())));
                ((TextView) findViewById(R.id.weather_today_temp)).setText(aWCIS.getTempCelcius() + "°C");
                ((TextView) findViewById(R.id.weather_today_city)).setText(txtCidade.getText().toString());
                ((TextView) findViewById(R.id.weather_today_condition)).setText(aWCIS.getCondition() + "\n" + getString(R.string.wind) + " " + aWCIS.getWindCondition() + "\n" + getString(R.string.humidity) + ": " + aWCIS.getHumidity());
                ((TextView) findViewById(R.id.lblProximosDias)).setText(getString(R.string.nextDays));
        }

    
        private void resetWeatherInfoViews()
        {
                ((ImageView)findViewById(R.id.imgWeather)).setImageDrawable(getResources().getDrawable(R.drawable.undefined));
                ((TextView) findViewById(R.id.weather_today_city)).setText(getString(R.string.city));
                ((TextView) findViewById(R.id.weather_today_temp)).setText("0 °C");
                ((TextView) findViewById(R.id.weather_today_condition)).setText(getString(R.string.condition));
                ((Weatherclass) findViewById(R.id.weather_1)).reset();
                ((Weatherclass) findViewById(R.id.weather_2)).reset();
                ((Weatherclass) findViewById(R.id.weather_3)).reset();
        }

  
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
                                Detailview.this.startService(new Intent(Detailview.this, UpdateService.class));
                        }
                }
                catch (Exception e)
                {
                        Log.e(Detailview.DEBUG_TAG, e.getMessage(), e);
                }
        }
        
      
        private void update()
        {       
                if(txtCidade.getText().toString().equals(""))
                        Utils.showMessage(Detailview.this, getString(R.string.enterCity));
                else
                        new Progress().execute();
                Toast.makeText(getApplicationContext(), "Weather updated :)", 
                		   Toast.LENGTH_LONG).show();
        }
        
      
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
                        Utils.showMessage(Detailview.this, getString(R.string.forecastNotFound));
        }
        
       
        private void saveCache(WeatherSet weatherset) throws IOException
        {
                File cache = new File(getCacheDir(), "cache.dat");
                ObjectOutputStream out;

                out = new ObjectOutputStream(new FileOutputStream(cache));
                out.writeObject(weatherset);
                out.close();
                weatherPref.setLastUpdate(new Date(System.currentTimeMillis()));
        }
        
       
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
        
       
        private class Event implements OnClickListener, OnKeyListener
        {
                @Override
                public void onClick(View arg0)
                {
                        if (arg0.equals(btnOk))
                        {
                                txtCidade.setText(Utils.captalizeWords(txtCidade.getText().toString()));
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
                                        txtCidade.setText(Utils.captalizeWords(txtCidade.getText().toString()));
                                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(arg0.getWindowToken(), 0);
                                        update();
                                }
                        }
                        return retorno;
                }
        }
        
      
        private class Progress extends AsyncTask<Void, String, Void>
        {
                ProgressDialog dialog = new ProgressDialog(Detailview.this);
                
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