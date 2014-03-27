package kay.maluuba.view.controller;

import java.io.Serializable;

public class Weatherrelay implements Serializable
{
     
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

                if (dayofWeek.startsWith("Mon"))
                        retorno = "Monday";
                if (dayofWeek.startsWith("tue"))
                        retorno = "Tuesday";
                if (dayofWeek.startsWith("wed"))
                        retorno = "Wednesday";
                if (dayofWeek.startsWith("thu"))
                        retorno = "Thursday";
                if (dayofWeek.startsWith("Fri"))
                        retorno = "Friday";
                if (dayofWeek.startsWith("sat"))
                        retorno = "Saturday";
                if (dayofWeek.startsWith("sun"))
                        retorno = "Sunday";
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