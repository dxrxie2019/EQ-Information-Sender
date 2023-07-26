package cn.dxr.xiaoming.quake;

import cn.dxr.xiaoming.DxrQuake;
import cn.dxr.xiaoming.quake.Utils.GzipUtil;
import cn.dxr.xiaoming.quake.Utils.HttpUtil;
import cn.dxr.xiaoming.quake.Utils.ImageDownloadUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class JmaDataAutoSender {
    public static String EventID = null;

    public static Runnable sender() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    String httpGet = HttpUtil.sendGet("http://127.0.0.1:8080/data/dmdata/data.json");
                    JSONObject jsonObject = JSON.parseObject(httpGet);
                    String data = jsonObject.getString("body");
                    String jsonObj = URLDecoder.decode(GzipUtil.unGZip(data), StandardCharsets.UTF_8);
                    JSONObject json = JSON.parseObject(jsonObj);
                    JSONObject json1 = json.getJSONObject("body");
                    String editorialOffice = json.getString("editorialOffice");
                    if (!Objects.equals(json.getString("_originalId"),EventID)) {
                        if (json.getString("title").equals("震源・震度情報")) {
                            JSONObject jsonObject11 = json1.getJSONObject("earthquake");
                            JSONObject jsonObject2 = jsonObject11.getJSONObject("hypocenter");
                            JSONObject jsonObject3 = jsonObject2.getJSONObject("depth");
                            JSONObject jsonObject4 = jsonObject11.getJSONObject("magnitude");
                            JSONObject jsonObject5 = json1.getJSONObject("intensity");
                            JSONObject jsonObject6 = json1.getJSONObject("comments");
                            JSONObject jsonObject7 = jsonObject6.getJSONObject("forecast");
                            String region = jsonObject2.getString("name");
                            String mag = jsonObject4.getString("value");
                            String depth = jsonObject3.getString("value");
                            String shindo = jsonObject5.getString("maxInt");
                            String info = jsonObject7.getString("text");
                            DxrQuake.getInstance().getXiaoMingBot().getContactManager().getGroupContacts().forEach(groupContact -> groupContact.sendMessage("日本気象庁地震情報\n " + json.getString("headline") + "\n 震源地: " + region + "\n 震级: Mj" + mag + "\n 深度: " + depth + "Km\n 最大震度: " + shindo + "\n 津波(海啸)情报: " + info + "\n 编辑单位: " + editorialOffice + "\n 详细信息: https://typhoon.yahoo.co.jp/weather/jp/earthquake/?t=1"));
                            try {
                                Thread.sleep(35000L);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            try {
                                ImageDownloadUtil.sendImage();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (json.getString("title").equals("遠地地震に関する情報")) {
                            JSONObject jsonObject6 = json1.getJSONObject("comments");
                            JSONObject jsonObject7 = jsonObject6.getJSONObject("forecast");
                            JSONObject jsonObject11 = json1.getJSONObject("earthquake");
                            JSONObject jsonObject2 = jsonObject11.getJSONObject("hypocenter");
                            JSONObject jsonObject4 = jsonObject11.getJSONObject("magnitude");
                            String mag = jsonObject4.getString("value");
                            String region = jsonObject2.getString("name");
                            String depth = "---";
                            String shindo = "---";
                            String info = jsonObject7.getString("text");
                            DxrQuake.getInstance().getXiaoMingBot().getContactManager().getGroupContacts().forEach(groupContact -> groupContact.sendMessage("日本気象庁遠地地震情報\n " + json.getString("headline") + "\n 震源地: " + region + "\n 震级: Mj" + mag + "\n 深度: " + depth + "Km\n 最大震度: " + shindo + "\n 津波(海啸)情报: " + info + "\n " + "\n 编辑单位: " + editorialOffice + "\n 详细信息: https://typhoon.yahoo.co.jp/weather/jp/earthquake/?t=1"));
                            try {
                                Thread.sleep(35000L);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            try {
                                ImageDownloadUtil.sendImage();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (json.getString("infoKind").equals("震度速報")) {
                            JSONObject jsonObject5 = json1.getJSONObject("intensity");
                            String shindo = jsonObject5.getString("maxInt");
                            DxrQuake.getInstance().getXiaoMingBot().getContactManager().getGroupContacts().forEach(groupContact -> groupContact.sendMessage("日本気象庁震度速報\n " + json.getString("headline") + "\n 最大震度: " + shindo + "\n 震源 调查中" + "\n 编辑单位: " + editorialOffice));
                        }
                        if (json.getString("infoKind").equals("長周期地震動に関する観測情報")) {
                            JSONObject jsonObject5 = json1.getJSONObject("intensity");
                            JSONObject jsonObject6 = json1.getJSONObject("comments");
                            String shindo = jsonObject5.getString("maxLgInt");
                            String free = jsonObject6.getString("free");
                            DxrQuake.getInstance().getXiaoMingBot().getContactManager().getGroupContacts().forEach(groupContact -> groupContact.sendMessage("長周期地震動に関する観測情報\n " + json.getString("headline") + "\n 最大長周期地震動階級 " + shindo + "\n " + free + "\n 编辑单位: " + editorialOffice));
                        }
                        if (json.getString("title").equals("地震回数に関する情報")) {
                            JSONArray jsonArray = json1.getJSONArray("earthquakeCounts");
                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                            JSONObject times = jsonObject1.getJSONObject("targetTime");
                            JSONObject values = jsonObject1.getJSONObject("values");
                            DxrQuake.getInstance().getXiaoMingBot().getContactManager().getGroupContacts().forEach(groupContact -> groupContact.sendMessage("気象庁地震回数情報\n " + json.getString("headline") + "\n 开始记录时间: " + times.getString("start") + "\n 结束记录时间: " + times.getString("end") + "\n 期间地震总数(含无感): " + values.getString("all") + "\n 期间有感地震数: " + values.getString("felt") + "\n " + json1.getString("nextAdvisory")));
                        }
                        EventID = json.getString("_originalId");
                    }
                } catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        };
        new Timer().schedule(timerTask, 0L, 3000L);
        return null;
    }
}


