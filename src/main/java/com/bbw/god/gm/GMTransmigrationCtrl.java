package com.bbw.god.gm;

import com.bbw.cache.GameCacheService;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.config.city.ChengC;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.game.transmigration.*;
import com.bbw.god.game.transmigration.entity.GameTransmigration;
import com.bbw.god.game.transmigration.entity.TransmigrationDefender;
import com.bbw.god.game.transmigration.entity.UserTransmigration;
import com.bbw.god.game.transmigration.entity.UserTransmigrationRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 轮回管理接口
 *
 * @author: suhq
 * @date: 2021/9/26 6:05 下午
 */
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMTransmigrationCtrl extends AbstractController {
    @Autowired
    private GameTransmigrationService transmigrationService;
    @Autowired
    private GameDataService gameDataService;
    @Autowired
    private GameCacheService gameCacheService;
    @Autowired
    private GameTransmigrationService gameTransmigrationService;
    @Autowired
    private TransmigrationRankTotalService transmigrationRankTotalService;
    @Autowired
    private TransmigrationEnterService transmigrationEnterService;
    @Autowired
    private TransmigrationCityRecordService transmigrationCityRecordService;
    @Autowired
    private UserTransmigrationService userTransmigrationService;

    /**
     * 创建新的轮回
     *
     * @return
     */
    @GetMapping("transmigration!createNew")
    public Rst createNew() {
        transmigrationService.createNewTransmigrations();
        return Rst.businessOK();
    }

    /**
     * 显示轮回数据
     *
     * @param sgId
     * @return
     */
    @GetMapping("transmigration!show")
    public Object show(int sgId,String beginDate) {
        Date begin = DateUtil.fromDateTimeString(beginDate);
        begin = DateUtil.addSeconds(begin, 1);
        List<GameTransmigration> transmigrations = gameTransmigrationService.getTransmigrations(begin);
        GameTransmigration transmigration = transmigrations.stream().filter(tmp->tmp.getSgId() == sgId).findFirst().get();
        if (null != transmigration) {
            return transmigration;
        }
        return Rst.businessOK("没有对应事件的轮回数据");
    }

    /**
     * 更新轮回时间守卫
     *
     * @param beginDate
     * @return
     */
    @GetMapping("transmigration!updateDefenders")
    public Rst update(String beginDate) {
        Date begin = DateUtil.fromDateTimeString(beginDate);
        begin = DateUtil.addSeconds(begin, 1);
        List<GameTransmigration> transmigrations = gameTransmigrationService.getTransmigrations(begin);
        if (ListUtil.isEmpty(transmigrations)){
            return Rst.businessFAIL("该时间没有对应的轮回");
        }
        Map<String, TransmigrationDefender> defenders = gameTransmigrationService.makeDefenders(transmigrations.get(0).getMainCityDefenderTypes());
        for (GameTransmigration transmigration : transmigrations) {
            transmigration.setDefenders(defenders);
            gameDataService.updateGameData(transmigration);
        }
        gameCacheService.cacheData(GameTransmigration.class);
        Rst rst = Rst.businessOK();
        return rst;
    }

    /**
     * 更新城池的守卫
     *
     * @param beginDate
     * @param cityName
     * @return
     */
    @GetMapping("transmigration!updateCityDefender")
    public Rst update(String beginDate, String cityName) {
        Date begin = DateUtil.fromDateTimeString(beginDate);
        begin = DateUtil.addSeconds(begin, 1);
        List<GameTransmigration> transmigrations = gameTransmigrationService.getTransmigrations(begin);
        if (ListUtil.isEmpty(transmigrations)) {
            return Rst.businessFAIL("该时间没有对应的轮回");
        }
        ChengC chengC = CityTool.getChengCByName(cityName);
        if (null == chengC) {
            return Rst.businessFAIL("无效的城池名称");
        }
        String chengCField = chengC.getId().toString();
        Map<String, TransmigrationDefender> defenders = gameTransmigrationService.makeDefenders(transmigrations.get(0).getMainCityDefenderTypes());
        for (GameTransmigration transmigration : transmigrations) {
            transmigration.getDefenders().put(chengCField, defenders.get(chengCField));
            gameDataService.updateGameData(transmigration);
        }
        gameCacheService.cacheData(GameTransmigration.class);
        Rst rst = Rst.businessOK();
        return rst;
    }

    /**
     * 修改轮回收件符文
     *
     * @param cityName
     * @param runeIds
     * @return
     */
    @GetMapping("transmigration!changeRunes")
    public Rst changeRunes(int sgId, String cityName, String runeIds) {
        ChengC chengC = CityTool.getChengCByName(cityName);
        GameTransmigration curTransmigration = transmigrationService.getCurTransmigration(sgId);
        if (null != curTransmigration) {
            List<Integer> runeIdInts = ListUtil.parseStrToInts(runeIds);
            curTransmigration.getDefenders().get(chengC.getId()).setRunes(runeIdInts);
            gameDataService.updateGameData(curTransmigration);
            gameCacheService.cacheData(GameTransmigration.class);
            Rst rst = Rst.businessOK();
            return rst;
        }
        return Rst.businessOK("当前没有轮回世界");
    }

    /**
     * 更新榜单
     *
     * @param sgId
     * @return
     */
    @GetMapping("transmigration!updateRankers")
    public Rst sendAwards(int sgId) {
        GameTransmigration curTransmigration = gameTransmigrationService.getCurTransmigration(sgId);
        if (null == curTransmigration) {
            Rst.businessFAIL("当前轮回已结束或未开始");
        }
        transmigrationRankTotalService.updateRankers(curTransmigration);
        return Rst.businessOK();
    }

    /**
     * 轮回补发奖励
     *
     * @param sgId
     * @param beginDate 轮回开始时间
     * @return
     */
    @GetMapping("transmigration!sendAwards")
    public Rst sendAwards(int sgId, String beginDate) {
        Date begin = DateUtil.fromDateTimeString(beginDate);
        begin = DateUtil.addSeconds(begin, 1);
        List<GameTransmigration> transmigrations = gameTransmigrationService.getTransmigrations(begin);
        if (ListUtil.isEmpty(transmigrations)) {
            return Rst.businessFAIL("没有该时间段的轮回世界");
        }
        GameTransmigration transmigration = transmigrations.stream().filter(tmp -> tmp.getSgId() == sgId).findFirst().orElse(null);
        if (null == transmigration) {
            return Rst.businessFAIL("没有该时间段的轮回世界");
        }
        //结算
        transmigrationRankTotalService.sendRankerAwards(transmigration);
        return Rst.businessOK();
    }

    /**
     * 修正UserTransmigration的cityScores
     *
     * @param sgId
     * @return
     */
    @GetMapping("transmigration!fixScore")
    public Rst fixScore(int sgId) {
        GameTransmigration curTransmigration = gameTransmigrationService.getCurTransmigration(sgId);
        Set<Long> uids = transmigrationEnterService.getUids(curTransmigration);
        for (Long uid : uids) {
            List<UserTransmigrationRecord> myBestRecords = transmigrationCityRecordService.getMyRecords(curTransmigration, uid);
            if (ListUtil.isEmpty(myBestRecords)) {
                continue;
            }
            UserTransmigration userTransmigration = userTransmigrationService.getTransmigration(uid);
            for (UserTransmigrationRecord myBestRecord : myBestRecords) {
                userTransmigration.updateScore(myBestRecord.getCityId(), myBestRecord.gainScore());
            }
            gameUserService.updateItem(userTransmigration);
        }
        return Rst.businessOK();
    }

    /**
     * 修正UserTransmigrationRecord的score
     *
     * @param sgId
     * @return
     */
    @GetMapping("transmigration!fixRecordScores")
    public Rst fixRecordScores(int sgId) {
        GameTransmigration curTransmigration = gameTransmigrationService.getCurTransmigration(sgId);
        Set<Long> uids = transmigrationEnterService.getUids(curTransmigration);
        for (Long uid : uids) {
            List<UserTransmigrationRecord> myBestRecords = transmigrationCityRecordService.getMyRecords(curTransmigration, uid);
            if (ListUtil.isEmpty(myBestRecords)) {
                continue;
            }
            List<UserTransmigrationRecord> recordsToUpdate = new ArrayList<>();
            for (UserTransmigrationRecord myBestRecord : myBestRecords) {
                if (myBestRecord.getScoreCompositions().get(2) > 15) {
                    myBestRecord.getScoreCompositions().remove(2);
                    myBestRecord.getScoreCompositions().add(2, 15);
                    recordsToUpdate.add(myBestRecord);
                }
            }
            if (ListUtil.isEmpty(recordsToUpdate)) {
                continue;
            }
            gameUserService.updateItems(recordsToUpdate);

            UserTransmigration userTransmigration = userTransmigrationService.getTransmigration(uid);
            for (UserTransmigrationRecord myBestRecord : myBestRecords) {
                userTransmigration.updateScore(myBestRecord.getCityId(), myBestRecord.gainScore());
            }
            gameUserService.updateItem(userTransmigration);
        }
        return Rst.businessOK();
    }
}
