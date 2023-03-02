package com.bbw.god.gm;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.common.Rst;
import com.bbw.god.city.UserCityService;
import com.bbw.god.db.dao.InsRoleInfoDao;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.statistic.StatisticServiceFactory;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.yaozu.YaoZuStatistic;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 法坛图纸相关操作
 *
 * @author fzj
 * @date 2021/11/30 12:45
 */
@RestController
@RequestMapping("/gm/ft")
public class GMFaTanPicCtrl {
    @Autowired
    private InsRoleInfoDao insRoleInfoDao;
    @Autowired
    UserTreasureService userTreasureService;
    @Autowired
    StatisticServiceFactory statisticServiceFactory;
    @Autowired
    UserCityService userCityService;

    /**
     * 获得在某个时间获得法坛数量与击败妖族数量不一致的玩家
     *
     * @param beginTime
     * @param level
     * @return
     */
    @RequestMapping("user!getNeedRepairUids")
    public List<Long> getNeedRepairUids(int beginTime, int level) {
        List<Long> uids = insRoleInfoDao.getUidByLevelAndLastLoginDate(level, beginTime);
        BehaviorStatisticService yaoZuService = statisticServiceFactory.getByBehaviorType(BehaviorType.YAO_ZU_WIN);
        List<Long> uidLists = new ArrayList<>();
        for (long uid : uids) {
            List<String> citiesName = userCityService.getUserCities(uid).stream().filter(c -> c.getFt() != null)
                    .map(UserCfgObj::getName).collect(Collectors.toList());
            int unlockFaTanNum = citiesName.size();
            YaoZuStatistic yaoZuStatistic = yaoZuService.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
            if (unlockFaTanNum == 0 || yaoZuStatistic.getTotal() == 0 || yaoZuStatistic.getTotal() == 90) {
                continue;
            }
            List<UserTreasure> userFaTanPics = userTreasureService.getAllUserTreasures(uid).stream()
                    .filter(t -> t.getBaseId() >= 50013 && t.getBaseId() <= 50097).collect(Collectors.toList());
            int hasFaTanPicNum = (int) userFaTanPics.stream().filter(f -> !citiesName.contains(f.getName()
                    .substring(0, f.getName().length() - 4))).count() + unlockFaTanNum;
            if (hasFaTanPicNum == yaoZuStatistic.getTotal()) {
                continue;
            }
            uidLists.add(uid);
        }
        return uidLists;
    }

    /**
     * 随机发放一张未获得的法坛图纸
     *
     * @param uid
     * @return
     */
    @RequestMapping("user!sendFaTanPic")
    public Rst sendFaTanPic(long uid, int num) {
        //根据法坛图纸的type获取全部法坛建筑图纸id
        List<CfgTreasureEntity> drawingIdList = new ArrayList<>(TreasureTool.getTreasuresByType(52));
        List<Integer> citiesId = userCityService.getUserCities(uid).stream().filter(c -> c.getFt() == null)
                .map(UserCfgObj::getBaseId).collect(Collectors.toList());
        List<String> citiesName = CityTool.getCities().stream().filter(c -> citiesId.contains(c.getId()))
                .map(CfgCityEntity::getName).collect(Collectors.toList());
        List<Integer> noOpenedFaTan = drawingIdList.stream().filter(f -> citiesName.contains(f.getName()
                .substring(0, f.getName().length() - 4))).map(CfgTreasureEntity::getId).collect(Collectors.toList());
        RDCommon rd = new RDCommon();
        for (int i = 1; i <= num; i++){
            //获取玩家拥有的法宝id
            List<Integer> userTreasuresIdList = userTreasureService.getAllUserTreasures(uid)
                    .stream().map(UserTreasure::getBaseId).collect(Collectors.toList());
            List<Integer> userNotDrawingTreasures = new ArrayList<>();
            for (int treasureId : drawingIdList.stream().map(CfgTreasureEntity::getId).collect(Collectors.toList())) {
                if (!userTreasuresIdList.contains(treasureId)) {
                    userNotDrawingTreasures.add(treasureId);
                }
            }
            userNotDrawingTreasures.retainAll(noOpenedFaTan);
            if (userNotDrawingTreasures.isEmpty()){
                return Rst.businessFAIL("该玩家已获得全部图纸！");
            }
            Integer randomTreasure = PowerRandom.getRandomFromList(userNotDrawingTreasures);
            TreasureEventPublisher.pubTAddEvent(uid, randomTreasure, 1, WayEnum.YAOZU_FIGHT, rd);
        }
        return Rst.businessOK();
    }

}
