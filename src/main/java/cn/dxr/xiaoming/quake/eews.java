package cn.dxr.xiaoming.quake;

import cn.dxr.xiaoming.DxrQuake;
import cn.dxr.xiaoming.quake.Utils.ImageDownloadUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class eews {
    private static String EventID = null;
    public static double calcMaxInt(double magnitude, double depth) {
        double a = 1.65 * magnitude;
        double b = depth < 10 ? 1.21 * Math.log10(10) : 1.21 * Math.log10(depth);
        return a / b;
    }
    public static Runnable sender() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                File file = new File("/lpdp/data/exptech/data.json");
                String data;
                try {
                    data = FileUtils.readFileToString(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                JSONObject jsonObject = JSON.parseObject(data);
                String num = jsonObject.getString("number");
                String time1 = jsonObject.getString("time");
                String region = jsonObject.getString("location");
                String mag = jsonObject.getString("scale");
                String depth = jsonObject.getString("depth");
                String lat = jsonObject.getString("lat");
                String lng = jsonObject.getString("lon");
                DecimalFormat decimalFormat = new DecimalFormat("0.0");
                double maxInt = calcMaxInt(jsonObject.getDouble("scale"), jsonObject.getDouble("depth"));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Long date = Long.parseLong(time1);
                String time = simpleDateFormat.format(date);
                if (jsonObject.getString("type").equals("eew-cwb")) {
                    String isCancel;
                    if (jsonObject.getBoolean("cancel")) {
                        isCancel = "取消";
                    } else {
                        isCancel = "正常";
                    }
                    if (!Objects.equals(jsonObject.getString("id") + num,EventID)) {
                        DxrQuake.getInstance().getXiaoMingBot().getContactManager().getGroupContacts().forEach(groupContact -> groupContact.sendMessage("台湾地震预警(第" + num + "报)" + "\n 发报状态: " + isCancel +  "\n 发震时间: " + time + "\n 震央: " + region + "(北纬" + lat + "度,东经" + lng + "度)" + "\n 规模: M" + mag + "\n 深度: " + depth + "Km" + "\n 中央氣象局(CWB)"));
                        EventID = jsonObject.getString("id") + num;
                    }
                }
                if (jsonObject.getString("type").equals("eew-scdzj")) {
                    if (!Objects.equals(jsonObject.getString("id") + num,EventID)) {
                        DxrQuake.getInstance().getXiaoMingBot().getContactManager().getGroupContacts().forEach(groupContact -> groupContact.sendMessage("四川省地震局地震预警" + "(第" + num + "报)" + "\n 发震时刻: " + time + "\n 震中: " + region + "(北纬" + lat + "度,东经" + lng + "度)" + "\n 震级: M" + mag + "\n 深度: " + depth + "Km" + "\n 预想最大烈度: " + decimalFormat.format(maxInt) + "度"));
                        EventID = jsonObject.getString("id") + num;
                    }
                }
                if (jsonObject.getString("type").equals("eew-fjdzj")) {
                    if (!Objects.equals(jsonObject.getString("id") + num,EventID)) {
                        DxrQuake.getInstance().getXiaoMingBot().getContactManager().getGroupContacts().forEach(groupContact -> groupContact.sendMessage("福建省地震局地震预警" + "(第" + num + "报)" + "\n 发震时刻: " + time + "\n 震中: " + region + "(北纬" + lat + "度,东经" + lng + "度)" + "\n 震级: M" + mag + "\n 深度: " + depth + "Km" + "\n 预想最大烈度: " + decimalFormat.format(maxInt) + "度"));
                        EventID = jsonObject.getString("id") + num;
                    }
                }
                if (jsonObject.getString("type").equals("eew-kma")) {
                    if (!Objects.equals(jsonObject.getString("id") + num,EventID)) {
                        DxrQuake.getInstance().getXiaoMingBot().getContactManager().getGroupContacts().forEach(groupContact -> groupContact.sendMessage("韩国气象厅地震预警" + "(第" + num + "报)" + "\n 发震时刻: " + time + "\n 震中: " + region + "(北纬" + lat + "度,东经" + lng + "度)" + "\n 震级: M" + mag + "\n 深度: " + depth + "Km"));
                        EventID = jsonObject.getString("id") + num;
                    }
                }
                if (jsonObject.getString("type").equals("report")) {
                    if (!Objects.equals(jsonObject.getString("id") + depth + lat + lng,EventID)) {
                        String max = jsonObject.getString("max");
                        DxrQuake.getInstance().getXiaoMingBot().getContactManager().getGroupContacts().forEach(groupContact -> groupContact.sendMessage("台湾地震报告" + "\n 发震时刻: " + time + "\n 震央: " + region + "(北纬" + lat + "度,东经" + lng + "度)" + "\n 震级: M" + mag + "\n 深度: " + depth + "Km" + "\n 最大震度: " + max + "\n 中央氣象局(CWB)"));
                        try {
                            Thread.sleep(30000L);
                            ImageDownloadUtil.sendTWImage();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        EventID = jsonObject.getString("id") + depth + lat + lng;
                    }
                }
            }
        };
        new Timer().schedule(timerTask,0L,2000L);
        return null;
    }
}
