package br.com.android.weatherforecast.views;

import br.com.android.weatherforecast.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Componente para Exibir o Icone + os dados do Clima
 */
public class WeatherLine extends LinearLayout
{
	private ImageView myWeatherImageView;
	private TextView myTempTextView;

	public WeatherLine(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		myWeatherImageView = new ImageView(context, attrs);
		myTempTextView = new TextView(context, attrs);
		myTempTextView.setTextSize(12);
		myTempTextView.setTextColor(Color.WHITE);
		addView(myWeatherImageView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		addView(myTempTextView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	public void reset()
	{
		myWeatherImageView.setImageDrawable(getResources().getDrawable(R.drawable.undefined));
		myTempTextView.setText("");
	}

	public void setImageDrawable(Drawable drawable)
	{
		myWeatherImageView.setImageDrawable(drawable);
	}

	public void setTempString(String aTempString)
	{
		myTempTextView.setText(aTempString);
	}
}