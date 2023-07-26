package cn.dxr.xiaoming.quake;

import cn.dxr.xiaoming.DxrQuake;
import cn.dxr.xiaoming.quake.Utils.ImageDownloadUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class CencDataAutoSender {

    private static String id = null;

    public static double calcMaxInt(double magnitude, double depth) {
        double a = 1.65 * magnitude;
        double b = depth < 10 ? 1.21 * Math.log10(10) : 1.21 * Math.log10(depth);
        return a / b;
    }

    public static Runnable sender() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                File file = new File("/lpdp/data/botdata/cenc.json");
                try {
                    String httpGet = FileUtils.readFileToString(file);
                    JSONObject jsonObject = JSON.parseObject(httpGet);
                    JSONArray jsonArray = jsonObject.getJSONArray("list");
                    JSONObject json = jsonArray.getJSONObject(0);
                    DecimalFormat decimalFormat = new DecimalFormat("0.0");
                    if (!(json.getString("eventId")).equals(id)) {
                        String type = json.getString("infoTypeName");
                        String time = json.getString("shockTime");
                        String region = json.getString("placeName");
                        String lat = json.getString("latitude");
                        String lng = json.getString("longitude");
                        String mag = json.getString("magnitude");
                        double mag1 = json.getDouble("magnitude");
                        String level = null;
                        if (mag1 < 3.0) {
                            level = "(微震)";
                        }
                        if (mag1 >= 3.0 && mag1 < 4.5) {
                            level = "(有感地震)";
                        }
                        if (mag1 >= 4.5 && mag1 < 6.0) {
                            level = "(中强震)";
                        }
                        if (mag1 >= 6.0 && mag1 < 7.0) {
                            level = "(强震)";
                        }
                        if (mag1 >= 7.0 && mag1 < 8.0) {
                            level = "(大地震)";
                        }
                        if (mag1 >= 8.0) {
                            level = "(巨大地震)";
                        }
                        String depth = json.getString("depth");
                        double maxInt = calcMaxInt(json.getDouble("magnitude"), json.getDouble("depth"));
                        DxrQuake.getInstance().getLogger().info("中国地震台网信息更新");
                        String finalLevel = level;
                        DxrQuake.getInstance().getXiaoMingBot().getContactManager().getGroupContacts().forEach(groupContact -> groupContact.sendMessage("中国地震台网" + type + "\n 发震时刻: " + time + "\n 震中: " + region + "(北纬" + lat + "度,东经" + lng + "度)" + "\n 震级: M" + mag + "  " + finalLevel + "\n 深度: " + depth + "公里" + "\n 预想最大烈度: " + decimalFormat.format(maxInt) + "度"));
                        try {
                            ImageDownloadUtil.sendCNImage();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        id = json.getString("eventId");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Timer().schedule(timerTask, 0L, 2000L);
        return null;
    }
}