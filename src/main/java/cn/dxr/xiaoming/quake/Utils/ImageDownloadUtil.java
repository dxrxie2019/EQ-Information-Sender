package cn.dxr.xiaoming.quake.Utils;

import cn.dxr.xiaoming.DxrQuake;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.message.data.SingleMessage;
import net.mamoe.mirai.utils.ExternalResource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class ImageDownloadUtil {
    public static String getHtmlResourceByUrl(String url,String encoding) {
        StringBuilder buffer = new StringBuilder();
        URL urlObj;
        URLConnection uc;
        InputStreamReader in = null;
        BufferedReader reader;
        try {
            urlObj = new URL(url);
            uc = urlObj.openConnection();
            in = new InputStreamReader(uc.getInputStream(),encoding);
            reader = new BufferedReader(in);
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return buffer.toString();
    }

    public static void sendImage() throws Exception {
        String url = "https://typhoon.yahoo.co.jp/weather/jp/earthquake/?t=1";
        String encoding = "UTF-8";
        String htmlResource = getHtmlResourceByUrl(url, encoding);
        Document document = Jsoup.parse(htmlResource);
        Elements elements = document.getElementsByTag("img");
        DxrQuake.getInstance().getLogger().info("开始发送震度分布图!");
        for(Element element : elements){
            String imgSrc = element.attr("src");
            String alt = element.attr("alt");
            if ((imgSrc.startsWith("http://") || imgSrc.startsWith("https://"))) {
                if (alt.equals("各地域の震度")) {
                    URL url1 = new URL(imgSrc);
                    InputStream inputStream = url1.openStream();
                    ExternalResource externalResource = ExternalResource.create(inputStream);
                    DxrQuake.getInstance().getXiaoMingBot().getContactManager().getGroupContacts().forEach(groupContact -> groupContact.sendMessage(new SingleMessage[] { groupContact.uploadImage(externalResource) }));
                    externalResource.close();
                    inputStream.close();
                }
            }
        }
        DxrQuake.getInstance().getLogger().info("发送完毕!");
    }

    public static void sendEpicenterImage() throws Exception {
        String url = "https://earthquake.tenki.jp/static-images/earthquake-map/recent/24hours/japan-detail-large.jpg";
        DxrQuake.getInstance().getLogger().info("开始发送震中分布图!");
        URL url1 = new URL(url);
        InputStream inputStream = url1.openStream();
        ExternalResource externalResource = ExternalResource.create(inputStream);
        DxrQuake.getInstance().getXiaoMingBot().getContactManager().getGroupContacts().forEach(groupContact -> groupContact.sendMessage(new SingleMessage[] { groupContact.uploadImage(externalResource) }));
        externalResource.close();
        inputStream.close();
        DxrQuake.getInstance().getLogger().info("发送完毕!");
    }

    public static void sendTWImage() throws Exception {
        String url = "https://exptech.com.tw/api/v1/trem/image";
        DxrQuake.getInstance().getLogger().info("开始发送台湾震度分布图!");
        URL url1 = new URL(url);
        InputStream inputStream = url1.openStream();
        ExternalResource externalResource = ExternalResource.create(inputStream);
        DxrQuake.getInstance().getXiaoMingBot().getContactManager().getGroupContacts().forEach(groupContact -> groupContact.sendMessage(new SingleMessage[] { groupContact.uploadImage(externalResource) }));
        externalResource.close();
        inputStream.close();
        DxrQuake.getInstance().getLogger().info("发送完毕!");
    }

    public static void sendCNImage() throws Exception {
        String httpGet = HttpUtil.sendGet("http://218.5.2.111:9088/earthquakeWarn/bulletin/list.json?pageSize=1");
        JSONObject jsonObject = JSON.parseObject(httpGet);
        JSONArray jsonArray = jsonObject.getJSONArray("list");
        JSONObject json = jsonArray.getJSONObject(0);
        String url = "https://restapi.amap.com/v3/staticmap?location=" + json.getString("longitude") + "," + json.getString("latitude") + "&zoom=8&size=920*620&markers=mid,,A:" + json.getString("longitude") + "," + json.getString("latitude") + "&key=此处为高德api key";
        DxrQuake.getInstance().getLogger().info("开始发送台网图!");
        URL url1 = new URL(url);
        InputStream inputStream = url1.openStream();
        ExternalResource externalResource = ExternalResource.create(inputStream);
        DxrQuake.getInstance().getXiaoMingBot().getContactManager().getGroupContacts().forEach(groupContact -> groupContact.sendMessage(new SingleMessage[] { groupContact.uploadImage(externalResource) }));
        externalResource.close();
        inputStream.close();
        DxrQuake.getInstance().getLogger().info("发送完毕!");
    }
}
