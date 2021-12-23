import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

public class weather {
    public static void main(String[] args) {
        weatherSee("北京");
    }

    public static void weatherSee(String city) {
        String weatherUrl = "https://restapi.amap.com/v3/weather/weatherInfo?city=330100&key=KEY";
        ResponseEntity<String> weatherEntity = restTemplate.getForEntity(weatherUrl, String.class);
        String weatherBody = weatherEntity.getBody();
        JSONObject weatherObject = JSONObject.parseObject(weatherBody);
        System.out.println(weatherObject);

    }
}
