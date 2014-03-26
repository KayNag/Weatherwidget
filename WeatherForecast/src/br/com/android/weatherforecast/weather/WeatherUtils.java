package br.com.android.weatherforecast.weather;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Utilitarios
 * @author Felipe Cobello
 *
 */
public class WeatherUtils {

	public static int fahrenheitToCelsius(int tFahrenheit) 
	{
		return (int) ((5.0f / 9.0f) * (tFahrenheit - 32));
	}

	public static int celsiusToFahrenheit(int tCelsius) 
	{
		return (int) ((9.0f / 5.0f) * tCelsius + 32);
	}
	
	public static boolean checkInternet(Context ctx) 
	{
	    NetworkInfo info = (NetworkInfo) ((ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
	    
	    if (info == null || !info.isConnected()) 
	        return false;
	    if (info.isRoaming())
	        return false;
	    return true;
	}
	
	public static void showMessage(Context context, String msg)
	{	
		Toast.makeText(context, (CharSequence) msg, Toast.LENGTH_LONG).show();
	}
	
	public static String captalizeWords(String words)
	{
		char[] caracteres = words.toLowerCase().toCharArray();
		
		for (int i = 0; i < caracteres.length; i++)
		{
			if(i == 0 || caracteres[i-1] == ' ')
			{
				caracteres[i] = Character.toUpperCase(caracteres[i]);
			}
		}
		return new String(caracteres);
	}
	
	public static String trataAcento(String texto)
	{
		//Acento Agudo
		texto = texto.replace("á", "&aacute;");
		texto = texto.replace("é", "&eacute;");
		texto = texto.replace("í", "&iacute;");
		texto = texto.replace("ó", "&oacute;");
		texto = texto.replace("ú", "&uacute;");
		texto = texto.replace("Á", "&Aacute;");
		texto = texto.replace("É", "&Eacute;");
		texto = texto.replace("Í", "&Iacute;");
		texto = texto.replace("Ó", "&Oacute;");
		texto = texto.replace("Ú", "&Uacute;");
		//Acento Circunflexo
		texto = texto.replace("â", "&acirc;");
		texto = texto.replace("ê", "&ecirc;");
		texto = texto.replace("î", "&icirc;");
		texto = texto.replace("ô", "&ocirc;");
		texto = texto.replace("û", "&ucirc;");
		texto = texto.replace("Â", "&Acirc;");
		texto = texto.replace("Ê", "&Ecirc;");
		texto = texto.replace("Î", "&Icirc;");
		texto = texto.replace("Ô", "&Ocirc;");
		texto = texto.replace("Û", "&Ucirc;");
		//Acento Til
		texto = texto.replace("ã", "&atilde;");
		texto = texto.replace("õ", "&otilde;");
		texto = texto.replace("Ã", "&Atilde;");
		texto = texto.replace("Õ", "&Otilde;");
		return texto;
	}
}
