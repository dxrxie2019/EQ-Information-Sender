package cn.dxr.xiaoming.quake;

import cn.dxr.xiaoming.quake.Utils.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class test {
    public static String EventID = null;

    public static void main(String[] args) {
        String httpGet = HttpUtil.sendGet("https://sample.dmdata.jp/eew/20171213b/json/vxse44_rjtd_20171213112232.json");
        try {
            JSONObject jsonObject = JSON.parseObject(httpGet);
            JSONObject json = jsonObject.getJSONObject("body");
            if (!jsonObject.getString("_originalId").equals(EventID)) {
                if (!json.getBoolean("isCanceled")) {
                    JSONObject json1 = json.getJSONObject("earthquake");
                    JSONObject hypocenter = json1.getJSONObject("hypocenter");
                    JSONObject depth1 = hypocenter.getJSONObject("depth");
                    JSONObject magnitude = json1.getJSONObject("magnitude");
                    JSONObject intensity = json.getJSONObject("intensity");
                    JSONObject forecastMaxInt = intensity.getJSONObject("forecastMaxInt");
                    String type = "";
                    String title;
                    String time0 = json1.getString("originTime");
                    String time1 = time0.replace("+09:00", "");
                    String time = time1.replace("T", " ");
                    String report_time0 = jsonObject.getString("reportDateTime");
                    String report_time1 = report_time0.replace("+09:00", "");
                    String report_time = report_time1.replace("T", " ");
                    String num = jsonObject.getString("serialNo");
                    String region = hypocenter.getString("name");
                    String mag = magnitude.getString("value");
                    String depth = depth1.getString("value");
                    String shindo = forecastMaxInt.getString("to");
                    if (json.getBoolean("isLastInfo")) {
                        type = "最终";
                    }
                    if (json.getBoolean("isWarning")) {
                        title = "緊急地震速報（警報）";
                    } else {
                        title = "緊急地震速報（予報）";
                    }
                    EventID = jsonObject.getString("_originalId");
                    String finalType = type;
                    String finalTitle = title;
                    System.out.println(finalTitle + "(気象庁)" + "\n (" + finalType + "第" + num + "报)" + "\n 发震时间: " + time + "(东京时间)" + "\n 震源地: " + region + "\n 震级: Mj" + mag + "\n 深度: " + depth + "Km" + "\n 预想最大震度: " + shindo + "\n 发报时间: " + report_time + "(东京时间)");
                } else {
                    String finaltype = null;
                    String num = jsonObject.getString("serialNo");
                    String report_time0 = jsonObject.getString("reportDateTime");
                    String report_time1 = report_time0.replace("+09:00", "");
                    String report_time = report_time1.replace("T", " ");
                    String text = json.getString("text");
                    if (json.getBoolean("isLastInfo")) {
                        finaltype = "最终";
                    }
                    System.out.println("緊急地震速報（予報）" + "(気象庁)" + "\n (" + finaltype + "第" + num + "报)" + "\n 发震时间: " + "---" + "(东京时间)" + "\n 震源地: " + "---" + "\n 震级: Mj" + "---" + "\n 深度: " + "---" + "Km" + "\n 预想最大震度: " + "---" + "\n 发报时间: " + report_time + "(东京时间)" + "\n " + text);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

