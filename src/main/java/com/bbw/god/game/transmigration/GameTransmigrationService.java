package com.bbw.god.game.transmigration;

import com.bbw.App;
import com.bbw.cache.GameCacheService;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.transmigration.cfg.CfgTransmigration;
import com.bbw.god.game.transmigration.cfg.TransmigrationTool;
import com.bbw.god.game.transmigration.entity.GameTransmigration;
import com.bbw.god.game.transmigration.entity.TransmigrationCard;
import com.bbw.god.game.transmigration.entity.TransmigrationDefender;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.RDCardStrengthen;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 轮回世界
 *
 * @author: suhq
 * @date: 2021/9/10 11:02 上午
 */
@Slf4j
@Service
public class GameTransmigrationService {
    @Autowired
    private GameCacheService gameCacheService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private App app;

    /**
     * 创建新的一轮轮回。
     * 奖励发放后创建
     */
    public void createNewTransmigrations() {
        long start = System.currentTimeMillis();
        CfgTransmigration cfg = TransmigrationTool.getCfg();
        GameTransmigration lastTransmigration = null;
        List<GameTransmigration> gameDatas = gameCacheService.getGameDatas(GameTransmigration.class);
        if (ListUtil.isNotEmpty(gameDatas)) {
            lastTransmigration = gameDatas.get(gameDatas.size() - 1);
        }
        Date begin = cfg.gainFirstBeginDate();
        Date end = cfg.gainFirstEndDate();
        List<Integer> mainCityDefenderTypes = new ArrayList<>();
        if (null == lastTransmigration) {
            mainCityDefenderTypes = cfg.getFirstMainCityDefenderTypes();
        } else {
            //开始
            begin = DateUtil.addDays(lastTransmigration.getEnd(), cfg.getGapDays());
            begin = DateUtil.toDate(begin, cfg.getBeginHms());
            //结束
            end = DateUtil.addDays(begin, cfg.getDuration()-1);
            end = DateUtil.toDate(end, cfg.getEndHms());
            // 主城属性
            for (int i = 0; i < 5; i++) {
                mainCityDefenderTypes.add(PowerRandom.getRandomBySeed(5) * 10);
            }
        }
        //各城守卫
        Map<String, TransmigrationDefender> defenders = makeDefenders(mainCityDefenderTypes);
        //生成实例
        List<Integer> sgIds = cfg.getSgIds();
        if (app.runAsDev()) {
            sgIds = Arrays.asList(1);
        }
        List<GameTransmigration> newTransmigraions = new ArrayList<>();
        for (Integer sgId : sgIds) {
            GameTransmigration gameTransmigration = GameTransmigration.getInstance(sgId, begin, end, mainCityDefenderTypes);
            gameTransmigration.setDefenders(defenders);
            newTransmigraions.add(gameTransmigration);
        }
        gameCacheService.addGameDatas(newTransmigraions);
        log.error("轮回对象构建时间：" + (System.currentTimeMillis() - start));
    }

    /**
     * 获取当前的轮回世界
     *
     * @return
     */
    public GameTransmigration getCurTransmigration(int sgId) {
        if (sgId == 17){
            sgId = 16;
        }
        Date now = DateUtil.now();
        List<GameTransmigration> transmigrations = gameCacheService.getGameDatas(GameTransmigration.class);
        int finalSgId = sgId;
        GameTransmigration transmigration = transmigrations.stream()
                .filter(tmp -> tmp.getSgId() == finalSgId && tmp.getBegin().before(now) && tmp.getEnd().after(now))
                .findFirst().orElse(null);
        return transmigration;
    }

    /**
     * 获取当前或者前一轮轮回世界
     *
     * @return
     */
    public GameTransmigration getCurOrPreviousTransmigration(int sgId) {
        if (sgId == 17){
            sgId = 16;
        }
        Date now = DateUtil.now();
        List<GameTransmigration> transmigrations = gameCacheService.getGameDatas(GameTransmigration.class);
        int finalSgId = sgId;
        transmigrations = transmigrations.stream().filter(tmp -> tmp.getSgId() == finalSgId).collect(Collectors.toList());
        //获取当前
        GameTransmigration transmigration = transmigrations.stream()
                .filter(tmp -> tmp.getBegin().before(now) && tmp.getEnd().after(now))
                .findFirst().orElse(null);
        //如果当前没有，获取前一轮
        if (null == transmigration) {
            transmigrations = transmigrations.stream()
                    .filter(tmp -> tmp.getEnd().before(now))
                    .collect(Collectors.toList());
            transmigration = transmigrations.get(transmigrations.size() - 1);
        }
        return transmigration;
    }

    /**
     * 获取某个时间段的轮回
     *
     * @param date
     * @return
     */
    public List<GameTransmigration> getTransmigrations(Date date) {
        List<GameTransmigration> transmigrations = gameCacheService.getGameDatas(GameTransmigration.class);
        List<GameTransmigration> results = transmigrations.stream()
                .filter(tmp -> tmp.getBegin().before(date) && tmp.getEnd().after(date))
                .collect(Collectors.toList());
        return results;
    }


    /**
     * 获取下一轮的数据
     *
     * @param sgId
     * @return
     */
    public GameTransmigration getNextTransmigration(int sgId) {
        if (sgId == 17){
            sgId = 16;
        }
        Date now = DateUtil.now();
        List<GameTransmigration> transmigrations = gameCacheService.getGameDatas(GameTransmigration.class);
        int finalSgId = sgId;
        transmigrations = transmigrations.stream().filter(tmp -> tmp.getSgId() == finalSgId).collect(Collectors.toList());
        //获取下一轮
        GameTransmigration transmigration = transmigrations.stream()
                .filter(tmp -> tmp.getBegin().after(now))
                .findFirst().orElse(null);
        return transmigration;
    }


    /**
     * 是否处于挑战期间
     *
     * @param transmigration
     */
    public void checkTransmigration(GameTransmigration transmigration) {
        if (null == transmigration) {
            throw ExceptionForClientTip.fromi18nKey("transmigration.is.not.open");
        }
    }

    /**
     * 构建守卫
     * @param mainCityDefenderTypes
     * @return
     */
    public Map<String, TransmigrationDefender> makeDefenders(List<Integer> mainCityDefenderTypes){
        CfgTransmigration cfg = TransmigrationTool.getCfg();
        Map<String, TransmigrationDefender> defenders = new HashMap<>();
        for (Integer originalType : cfg.getAreaDevision().keySet()) {
            int mainCityType = mainCityDefenderTypes.get(originalType / 10 - 1);
            for (String cityName : cfg.getAreaDevision().get(originalType)) {
                Integer cityId = CityTool.getChengCByName(cityName).getId();
                CfgCityEntity city = CityTool.getCityById(cityId);
                TransmigrationDefender defender = TransmigrationTool.createDefender(mainCityType, city);
                defenders.put(cityId.toString(), defender);
            }
        }
        return defenders;
    }
    /**
     * 轮回战斗查看卡牌详情
     * @param cardId
     * @param cityIdAndUid 城池id 加 uid
     * @return
     */
    public RDCardStrengthen getCardInfo(Integer cardId, String  cityIdAndUid ) {
        String[] split = cityIdAndUid.split(",");
        int cityId = Integer.parseInt(split[0]);
        long uid = Long.parseLong(split[1]);
        int sid = gameUserService.getActiveGid(uid);
        RDCardStrengthen rd = new RDCardStrengthen();
        GameTransmigration curTransmigration = getCurTransmigration(sid);
        TransmigrationDefender cityDefender = curTransmigration.gainCityDefender(cityId);
        TransmigrationCard transmigrationCard = cityDefender.gainCards().stream().filter(tmp -> tmp.getId().equals(cardId)).findFirst().orElse(null);
        if (null == transmigrationCard) {
            return  rd;
        }
        rd.setCardId(cardId);
        rd.setSkill0(transmigrationCard.getSkills().get(0));
        rd.setSkill5(transmigrationCard.getSkills().get(1));
        rd.setSkill10(transmigrationCard.getSkills().get(2));
        rd.setAttackSymbol(0);
        rd.setDefenceSymbol(0);
        boolean isUseSkillScroll = false;
        List<Integer> originalSkills = CardTool.getCardById(transmigrationCard.getId()).getSkills();
        for (int i = 0; i < transmigrationCard.getSkills().size(); i++) {
            if (transmigrationCard.getSkills().get(i).intValue() != originalSkills.get(i)){
                isUseSkillScroll = true;
                break;
            }
        }
        rd.setIsUseSkillScroll(isUseSkillScroll?1:0);
        return rd;
    }
}
