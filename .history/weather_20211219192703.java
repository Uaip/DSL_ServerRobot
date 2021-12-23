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
        try {
            URL url = new URL("http://www.weather.com.cn/data/cityinfo/" + city
                    + ".html");
            URLConnection connectionData = url.openConnection();
            connectionData.setReadTimeout(1000);
            BufferedReader br = new BufferedReader(new InputStreamReader(connectionData.getInputStream(), "UTF-8"));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            System.out.println(sb.toString());
        } catch (Exception e) {
            System.out.println("查询出错");
        }
    }
}
