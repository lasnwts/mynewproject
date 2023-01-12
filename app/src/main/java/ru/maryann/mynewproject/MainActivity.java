package ru.maryann.mynewproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private EditText city;
    private TextView weather;
    private String NameOfCity;
    private String result;
    private String resultWeather;
    private JSONArray arrayJson;
    private EditText dataNow;
    private String timeStamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=findViewById(R.id.button);
        city=findViewById(R.id.datePersonName);
        weather=findViewById(R.id.weather);
        dataNow=findViewById(R.id.editTextDate);
        timeStamp =  getResources().getString(R.string.label_date) + new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
        dataNow.setText(timeStamp);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NameOfCity=city.getText().toString().trim();
                if(NameOfCity.equals("")){
                    Toast.makeText(MainActivity.this,R.string.massageerror,Toast.LENGTH_LONG).show();
                }else {
                    // Если ввели, то формируем ссылку для получения погоды
                    //String city = user_field.getText().toString();
                    String key = "bea10c7adfa9c90007423922a4367cb7";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + NameOfCity + "&appid=" + key + "&units=metric&lang=ru";

                    // Запускаем класс для получения погоды
                    new GetURLData().execute(url);
                }
            }
        });
    }


    @SuppressLint("StaticFieldLeak")
    private class GetURLData extends AsyncTask<String, String, String> {

        // Будет выполнено до отправки данных по URL
        protected void onPreExecute() {
            super.onPreExecute();
            weather.setText("Ожидайте...");
        }

        // Будет выполняться во время подключения по URL
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                // Создаем URL подключение, а также HTTP подключение
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // Создаем объекты для считывания данных из файла
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                // Генерируемая строка
                StringBuilder buffer = new StringBuilder();
                String line = "";

                // Считываем файл и записываем все в строку
                while((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                // Возвращаем строку
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Закрываем соединения
                if(connection != null)
                    connection.disconnect();

                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        // Выполняется после завершения получения данных
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Конвертируем JSON формат и выводим данные в текстовом поле
            if (result ==null){
                weather.setText(getResources().getString(R.string.label_fail_get_weather));
            } else
            try {

                JSONObject jsonObject = new JSONObject(result);
                arrayJson = jsonObject.getJSONArray("weather");
                resultWeather= "Температура: " + jsonObject.getJSONObject("main").getDouble("temp");
                resultWeather= resultWeather+"\n"+"Ощущается как: " + jsonObject.getJSONObject("main").getDouble("feels_like");
                resultWeather= resultWeather+"\n"+ "Скорость ветра: " + jsonObject.getJSONObject("wind").getString("speed");
                resultWeather= resultWeather+"\n"+ "Облака: " + jsonObject.getJSONObject("clouds").getString("all");
                resultWeather= resultWeather+"\n"+ "Видимость: " + jsonObject.getString("visibility");
                resultWeather= resultWeather+"\n"+ "Описание: " + arrayJson.getJSONObject(0).getString("description");
                weather.setText(resultWeather);
            } catch (JSONException e) {
                weather.setText(getResources().getString(R.string.label_fail_get_weather));
                e.printStackTrace();
            }
        }

    }


}