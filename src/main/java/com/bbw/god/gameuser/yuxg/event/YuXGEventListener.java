package com.bbw.god.gameuser.yuxg.event;

import com.bbw.god.city.chengc.in.FaTanService;
import com.bbw.god.city.chengc.in.event.BuildingLevelUpEvent;
import com.bbw.god.city.chengc.in.event.EPBuildingLevelUp;
import com.bbw.god.event.EventParam;
import com.bbw.god.game.config.city.BuildingEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.yuxg.*;
import com.bbw.god.gameuser.yuxg.cfg.CfgFuTuSlotNum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 玉虚宫事件监听
 *
 * @author fzj
 * @date 2021/11/2 11:46
 */
@Component
@Slf4j
@Async
public class YuXGEventListener {
    @Autowired
    UserYuXGService userYuXGService;
    @Autowired
    GameUserService gameUserService;
    @Autowired
    YuXGLogic yuXGLogic;
    @Autowired
    FaTanService faTanService;
    @Autowired
    YuXGService yuXGService;

    /**
     * 法坛升级行为事件监听
     */
    @Order(1)
    @EventListener
    public void buildingLevelUp(BuildingLevelUpEvent event) {
        try {
            EventParam<EPBuildingLevelUp> ep = (EventParam<EPBuildingLevelUp>) event.getSource();
            List<Integer> levelUpBuildings = ep.getValue().getLevelUpBuildings();
            if (!levelUpBuildings.contains(BuildingEnum.FT.getValue())) {
                return;
            }
            long uid = ep.getGuId();
            int totalFaTanLv = faTanService.getTotalLevel(uid) + 1;
            greaterFuCeData(uid, totalFaTanLv);
            unlockFuTan(uid, totalFaTanLv);
            unlockFuTuPos(uid, totalFaTanLv);
            addFuCeNum(uid, totalFaTanLv);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 开启玉虚宫时初始化生成符册
     *
     * @param uid
     * @param allFaTanLv
     */
    private void greaterFuCeData(long uid, int allFaTanLv) {
        if (allFaTanLv != 10) {
            return;
        }
        int fuTuSlotNum = YuXGTool.getFuTuSlotNum(allFaTanLv);
        List<UserFuCe> userFuCes = userYuXGService.getUserFuCes(uid);
        if (!userFuCes.isEmpty()){
            return;
        }
        //默认开启两个符册
        for (int index = 1; index <= 2; index++) {
            UserFuCe userFuCe = UserFuCe.getInstance(uid, "符册" + index, 0, index, fuTuSlotNum);
            userFuCes.add(userFuCe);
        }
        gameUserService.addItems(userFuCes);
    }

    /**
     * 解锁符坛
     *
     * @param allFaTanLv
     */
    private void unlockFuTan(long uid, int allFaTanLv) {
        if (allFaTanLv != 20 && allFaTanLv != 30) {
            return;
        }
        UserYuXG userYuXg = yuXGService.getOrCreateYuXGData(uid);
        int fuTanNum = userYuXg.getFuTan().size();
        if (fuTanNum >= 5) {
            return;
        }
        userYuXg.getFuTan().add(fuTanNum + 1);
        gameUserService.updateItem(userYuXg);
    }

    /**
     * 解锁符图卡槽
     *
     * @param allFaTanLv
     */
    private void unlockFuTuPos(long uid, int allFaTanLv) {
        List<CfgFuTuSlotNum> cfgFuTuSlotNums = new ArrayList<>(YuXGTool.getYuXGInfo().getFuTuSlotNumAndFaTanAllLv());
        CfgFuTuSlotNum cfgFuTuSlotNum = cfgFuTuSlotNums.stream().filter(f -> f.getFaTanAllLv() == allFaTanLv).findFirst().orElse(null);
        if (null == cfgFuTuSlotNum) {
            return;
        }
        List<UserFuCe> userFuCes = userYuXGService.getUserFuCes(uid);
        for (UserFuCe userFuCe : userFuCes) {
            int fuTuPosNum = cfgFuTuSlotNum.getFuTuSlotNum() - 1;
            userFuCe.getFuTus().add(UserFuCe.FuTu.getInstance(fuTuPosNum));
        }
        gameUserService.updateItems(userFuCes);
    }

    /**
     * 增加符册数量
     *
     * @param allFaTanLv
     */
    private void addFuCeNum(long uid, int allFaTanLv) {
        if (allFaTanLv != 50 && allFaTanLv != 150 && allFaTanLv != 250) {
            return;
        }
        List<UserFuCe> userFuCes = userYuXGService.getUserFuCes(uid);
        long fuCeNum = userFuCes.stream().filter(f -> f.getOpenMethod() == 1).count();
        if (fuCeNum >= 3){
            return;
        }
        //开启新的符册
        int hasFuCeNum = userFuCes.size() + 1;
        //获取符图槽数量
        Integer fuTuSlotNum = YuXGTool.getFuTuSlotNum(allFaTanLv);
        UserFuCe userFuCe = UserFuCe.getInstance(uid, "符册" + hasFuCeNum, 1, hasFuCeNum, fuTuSlotNum);
        gameUserService.addItem(uid, userFuCe);
    }
}
