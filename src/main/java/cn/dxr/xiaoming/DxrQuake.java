package cn.dxr.xiaoming;

import cn.chuanwise.xiaoming.plugin.JavaPlugin;
import cn.dxr.xiaoming.quake.*;

public class DxrQuake extends JavaPlugin {
    private static final DxrQuake INSTANCE = new DxrQuake();

    public static DxrQuake getInstance() {
        return INSTANCE;
    }

    public void onEnable() {
        getXiaoMingBot().getScheduler().run(JmaDataAutoSender.sender());
        getXiaoMingBot().getScheduler().run(CencDataAutoSender.sender());
        getXiaoMingBot().getScheduler().run(EpicenterSender.sender());
        getXiaoMingBot().getScheduler().run(eews.sender());
        getXiaoMingBot().getScheduler().run(ICLEEW.EEW());
        getXiaoMingBot().getScheduler().run(JmaEEW.EEW());
    }
}

