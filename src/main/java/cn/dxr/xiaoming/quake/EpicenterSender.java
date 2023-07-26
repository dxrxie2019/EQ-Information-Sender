package cn.dxr.xiaoming.quake;

import cn.dxr.xiaoming.DxrQuake;
import cn.dxr.xiaoming.quake.Utils.HttpUtil;
import cn.dxr.xiaoming.quake.Utils.ImageDownloadUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class EpicenterSender {
    public static Runnable sender() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                String url = HttpUtil.sendGet("https://api.pinduoduo.com/api/server/_stm");
                JSONObject jsonObject = JSON.parseObject(url);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                Long time = Long.parseLong(jsonObject.getString("server_time"));
                String date = simpleDateFormat.format(time);
                if (date.equals("21:00:00")) {
                    DxrQuake.getInstance().getXiaoMingBot().getContactManager().getGroupContacts().forEach(groupContact -> groupContact.sendMessage("日本过去24小时地震分布图(来源:日本气象协会 tenki.jp)"));
                    try {
                        ImageDownloadUtil.sendEpicenterImage();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Timer().schedule(timerTask,0L,1000L);
        return null;
    }
}
