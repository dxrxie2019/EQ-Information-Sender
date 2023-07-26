package cn.dxr.xiaoming.quake;

import cn.dxr.xiaoming.DxrQuake;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ICLEEW {

    private static String EventID = null;

    public static String sendGet(String url, String param) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            String urlNameString = url + param;
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1) okhttp/4.0.0");
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            DxrQuake.getInstance().getLogger().error("发送请求出现异常！" + e);
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }
    public static Runnable EEW() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    String httpGet = sendGet("https://mobile-new.chinaeew.cn", "/v1/earlywarnings?updates=3&start_at=");
                    JSONObject jsonObject = JSON.parseObject(httpGet);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    JSONObject json = jsonArray.getJSONObject(0);
                    DecimalFormat decimalFormat = new DecimalFormat("0.0");
                    if (!Objects.equals(json.getString("updates") + json.getString("eventId"), EventID)) {
                        String num = json.getString("updates");
                        String startAt = json.getString("startAt");
                        String region = json.getString("epicenter");
                        String mag = decimalFormat.format(json.getDouble("magnitude"));
                        String depth = json.getString("depth");
                        String lat = json.getString("latitude");
                        String lng = json.getString("longitude");
                        String stations = json.getString("sations");
                        SimpleDateFormat format =  new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
                        Long time = Long.parseLong(startAt);
                        String date = format.format(time);
                        DxrQuake.getInstance().getLogger().info("ICL地震预警更新");
                        DxrQuake.getInstance().getXiaoMingBot().getContactManager().getGroupContacts().forEach(groupContact -> groupContact.sendMessage("ICL地震预警" + "(第" + num + "报)" + "\n 发震时刻: " + date + "\n 震中: " + region + "(北纬" + lat + "度,东经" + lng + "度)" + "\n 震级: M" + mag + "\n 震源深度: " + depth + "Km" + "\n 触发测站: " + stations));
                        EventID = json.getString("updates") + json.getString("eventId");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(120000L);
                        DxrQuake.getInstance().getXiaoMingBot().getScheduler().run(EEW());
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        };
        new Timer().schedule(timerTask, 0L, 5000L);
        return null;
    }
}