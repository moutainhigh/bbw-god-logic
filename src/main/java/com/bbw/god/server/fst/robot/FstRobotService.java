package com.bbw.god.server.fst.robot;

import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgFst;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-06-30
 */
@Service
public class FstRobotService {
    @Autowired
    private RedisHashUtil<Integer,FstGameRobot> redisHashUtil;

    private String getRobotHashKey(){
        return "game:fstRobots";
    }

    /**
     * 初始化全服机器人，因为机器人只是一个过渡 无需在每个区服生成 只要全服一份即可
     * 机器人ID 为 -编号
     * 区服获取机器人可通过编号获取机器人
     */
    public void initRobot(){
        CfgFst cfgFst= Cfg.I.getUniqueConfig(CfgFst.class);
        List<CfgFst.RobotRule> robotRules=cfgFst.getRobotsRule();
        String nickname="";
        int number=1;
        Long index=1L;
        Map<Integer,FstGameRobot> maps=new HashMap<>();
        for(CfgFst.RobotRule rule:robotRules) {
            //相同名字的 则编号往上加
            if (!nickname.equals(rule.getName())) {
                nickname=rule.getName();
                number=1;
            }
            List<CfgFst.CardRule> cardRules = cfgFst.getCardRules(rule.getCardType());
            for (int i = 1; i <= rule.getNum(); i++) {
                FstGameRobot robot=FstGameRobot.getInstance(index.intValue(),rule.getLv(),nickname+number);
                robot.setCards(getCards(cardRules));
                robot.setHead(robot.getCards().get(robot.getCards().size()-1).getCardId());
                maps.put(index.intValue(),robot);
                number++;
                index++;
            }
        }
        redisHashUtil.putAllField(getRobotHashKey(),maps);
    }

    /**
     * -9800  区服 000000+编号
     * 目前应该是 -9800  区服 000001~  -9800 区服 000300
     */
    public FstGameRobot getRobotInfo(Long robotId){
        Optional<FstGameRobot> op = getRobotInfoOp(robotId);
        if (op.isPresent()){
            return op.get();
        }
        ////没有找到就取第一个机器人的信息
        return redisHashUtil.getField(getRobotHashKey(),1);
    }
    /**
     * -9800  区服 000000+编号
     * 目前应该是 -9800  区服 000001~  -9800 区服 000300
     */
    public Optional<FstGameRobot> getRobotInfoOp(Long robotId){
        Long id=-robotId%1000;
        FstGameRobot gameData = redisHashUtil.getField(getRobotHashKey(),id.intValue());
        return Optional.ofNullable(gameData);
    }


    
    private List<FstGameRobot.CardInfo> getCards(List<CfgFst.CardRule> rules){
        List<FstGameRobot.CardInfo> list=new ArrayList<>();
        List<Integer> cardIds=new ArrayList<>();
        for (CfgFst.CardRule rule : rules) {
            List<CfgCardEntity> cards = CardTool.getRandomCard(rule.getStar(), 1, cardIds);
            CfgCardEntity cardEntity=cards.get(0);
            cardIds.add(cardEntity.getId());
            list.add(FstGameRobot.CardInfo.getInstance(cardEntity.getId(), rule.getHv(), rule.getLv()));
        }
        return list;
    }

    /**
     * 获取全服榜单中的机器人ID
     * @param number
     * @return
     */
    public static Long getGameRobotId(int number){
        return Long.valueOf(-90000-number);
    }
    
    /**
     * 是否是全服机器人ID
     * @param robotId
     * @return
     */
    public static boolean  isGameRobotId(long robotId){
        long abs = Math.abs(robotId);
        return abs>90000 && abs<99000;
    }
    /**
     * -9800  5位数区服 000000+编号
     * 目前的封神台设定 假设区服是96则 取值为 -980000096000001 ~ -980000096000300
     * 获取区服机器人ID
     * @param number
     * @return
     */
    public static Long getServerRobotId(int number,int sid){
        String format = String.format("9800%05d000000", sid);
        return -Long.valueOf(format)-number;
    }
    public static Integer getServerId(Long robotId){
        Long abs = Math.abs(robotId)/1000000%100000;
        return abs.intValue();
    }
    /**
     * 获取卡组初始化信息
     * @param robotId
     * @return
     */
    public CPlayerInitParam getRobotInitParam(long robotId){
        FstGameRobot info = getRobotInfo(robotId);
        CPlayerInitParam param= CPlayerInitParam.initParam(info.getLevel(),info.getNickname(),info.getHead(), TreasureEnum.HEAD_ICON_Normal.getValue());
        List<CCardParam> cardParams=new ArrayList<>();
        int multiple=isGameRobotId(robotId)?2:1;
        for (FstGameRobot.CardInfo card : info.getCards()) {
            cardParams.add(CCardParam.init(card.getCardId(),card.getLv()*multiple,card.getHv()));
        }
        param.setCards(cardParams);
        return param;
    }

}
