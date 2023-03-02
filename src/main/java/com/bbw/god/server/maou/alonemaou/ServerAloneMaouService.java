package com.bbw.god.server.maou.alonemaou;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.SetUtil;
import com.bbw.god.game.config.CfgAloneMaou;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouAttackSummary;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouAttackSummaryService;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouLevelInfo;
import com.bbw.god.server.maou.alonemaou.attackinfo.AloneMaouLevelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 独战魔王服务类
 *
 * @author suhq
 * @date 2019年2月12日 下午5:19:50
 */
@Slf4j
@Service
public class ServerAloneMaouService extends ServerDataService {
    @Autowired
    private AloneMaouLevelService maouLevelService;// 魔王被攻击的信息
    @Autowired
    private AloneMaouAttackSummaryService attackSummaryService;// 玩家攻击魔王的信息
    @Autowired
    private ServerDataService serverDataService;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 获得当前服务器生效的魔王
     *
     * @param sid
     * @return
     */
    public Optional<ServerAloneMaou> getCurAloneMaou(int sid) {
        Date now = DateUtil.now();
        return getAloneMaou(sid, now);
    }

    /**
     * 获得某一天的独战魔王
     *
     * @param sid
     * @param date
     * @return
     */
    public Optional<ServerAloneMaou> getAloneMaou(int sid, Date date) {
        String loopKey = ServerAloneMaou.getLoopKey(date);
        List<ServerAloneMaou> maous = this.serverDataService.getServerDatas(sid, ServerAloneMaou.class, loopKey);
        Optional<ServerAloneMaou> maou = maous.stream().filter(tmp -> tmp.ifMe(date)).findFirst();
        return maou;
    }

    /**
     * 初始化魔王层级信息
     *
     * @param uid
     * @param maou
     */
    public void initMaouLevelInfo(long uid, ServerAloneMaou maou) {
        //初始化数据
        CfgAloneMaou config = AloneMaouTool.getConfig();
        config.getMaous().forEach(tmp -> {
            AloneMaouLevelInfo maouAttackInfo = AloneMaouLevelInfo.getInstance(uid, maou.getId(), tmp);
            this.maouLevelService.saveMaouLevelInfo(maou, maouAttackInfo);
        });

    }

    /**
     * 获得当前要攻打的独战魔王
     *
     * @param uid
     * @param maou
     * @return
     */
    public AloneMaouLevelInfo getCurLevelMaou(long uid, ServerAloneMaou maou) {
        AloneMaouAttackSummary myAttack = this.attackSummaryService.getMyAttackInfo(uid, maou);
        Map<String, AloneMaouLevelInfo> attackInfoMap = this.maouLevelService.getMaouLevelInfo(uid, maou);
        if (attackInfoMap == null || SetUtil.isEmpty(attackInfoMap.keySet())) {
            return null;
        }
        return attackInfoMap.get(myAttack.getAttackingMaouLevel().toString());
    }

    /**
     * 获得打到的魔王等级
     *
     * @param uid
     * @return
     */
    public int getAttackedLevel(long uid, int sid, Date date) {
        Optional<ServerAloneMaou> optional = this.getAloneMaou(sid, date);
        if (!optional.isPresent()) {
            return 1;
        }
        ServerAloneMaou maou = optional.get();
        AloneMaouAttackSummary myAttack = this.attackSummaryService.getMyAttackInfo(uid, maou);
        Integer maouLevel = 1;
        if (myAttack != null) {
            maouLevel = myAttack.getAttackingMaouLevel();
        }
        return maouLevel;
    }

    /**
     * 将编组中的卡牌id替换
     *
     * @param uid       玩家id
     * @param oldCardId 旧卡id
     * @param newCardId 新卡id
     */
    public void replaceCard(long uid, int oldCardId, int newCardId) {
        UserAloneMaouData userAloneMaouData = gameUserService.getSingleItem(uid, UserAloneMaouData.class);
        if (null == userAloneMaouData) {
            return;
        }
        List<Integer> attackCards = userAloneMaouData.getAttackCards();
        if (attackCards.contains(oldCardId)) {
            attackCards.remove((Integer) oldCardId);
            attackCards.add(newCardId);
        }
        gameUserService.updateItem(userAloneMaouData);
    }

    /**
     * 将编组中的多张卡牌id替换
     *
     * @param uid        玩家id
     * @param oldCardIds 旧卡id集合
     * @param newCardIds 新卡id集合
     */
    public void replaceCards(long uid, List<Integer> oldCardIds, List<Integer> newCardIds) {
        UserAloneMaouData userAloneMaouData = gameUserService.getSingleItem(uid, UserAloneMaouData.class);
        if (null == userAloneMaouData) {
            return;
        }
        List<Integer> attackCards = userAloneMaouData.getAttackCards();
        if (ListUtil.isEmpty(attackCards)) {
            return;
        }
        for (Integer oldCardId : oldCardIds) {
            if (attackCards.contains(oldCardId)) {
                attackCards.remove((Integer) oldCardId);
                attackCards.add(newCardIds.get(oldCardIds.indexOf(oldCardId)));
            }
        }

        gameUserService.updateItem(userAloneMaouData);
    }

    public void timeOutTmpData(ServerAloneMaou maou) {
        maouLevelService.expireData(maou);
        attackSummaryService.expireData(maou);
    }
}
