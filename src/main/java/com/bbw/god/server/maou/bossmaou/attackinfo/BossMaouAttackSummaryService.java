package com.bbw.god.server.maou.bossmaou.attackinfo;

import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.server.maou.AbstractAttackDataService;
import com.bbw.god.server.maou.BaseServerMaou;
import com.bbw.god.server.maou.bossmaou.ServerBossMaou;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suhq
 * @description: 魔王攻打明细记录
 * @date 2019-12-23 14:48
 **/
@Service
public class BossMaouAttackSummaryService extends AbstractAttackDataService {
    @Autowired
    private RedisHashUtil<String, BossMaouAttackSummary> rankerRedisHash;// 魔王攻打排名

    BossMaouAttackSummaryService() {
        this.dataKey = "attackSummary";
    }

    /**
     * 设置我的攻打信息
     *
     * @param uid
     * @param maou
     * @param ranker
     */
    public void setMyAttackInfo(Long uid, ServerBossMaou maou, BossMaouAttackSummary ranker) {
        this.rankerRedisHash.putField(getRedisKey(maou), uid.toString(), ranker);
    }

    /**
     * 获得玩家打魔王的信息
     *
     * @param user
     * @param maou
     * @return
     */
    public BossMaouAttackSummary getMyAttackInfo(GameUser user, ServerBossMaou maou) {
        BossMaouAttackSummary attackInfo = this.rankerRedisHash.getField(getRedisKey(maou), user.getId().toString());
        if (attackInfo == null) {
            attackInfo = new BossMaouAttackSummary();
            attackInfo.setMaouId(maou.getId());
            attackInfo.setGuId(user.getId());
            attackInfo.setLevel(user.getLevel());
            attackInfo.setNickname(user.getRoleInfo().getNickname());
            attackInfo.setHead(user.getRoleInfo().getHead());
            attackInfo.setLastAttackTime(maou.getAttackTime().getTime() - 10000);
            setMyAttackInfo(user.getId(), maou, attackInfo);
        } else if (attackInfo.getLevel() < user.getLevel()) {
            attackInfo.setLevel(user.getLevel());
            setMyAttackInfo(user.getId(), maou, attackInfo);
        }
        return attackInfo;
    }

    /**
     * 获取所有玩家的攻击信息
     *
     * @param maou
     * @return
     */
    @NonNull
    public List<BossMaouAttackSummary> getAttackInfoSorted(ServerBossMaou maou) {
        List<BossMaouAttackSummary> rankers = this.rankerRedisHash.getFieldValueList(getRedisKey(maou));
        if (null == rankers) {
            return new ArrayList<BossMaouAttackSummary>();
        }
        rankers = rankers.stream().filter(tmp -> tmp.getBeatedBlood() > 0).collect(Collectors.toList());
        // 排行
        sortRanker(rankers);
        return rankers;
    }

    /**
     * 打魔王排序
     *
     * @param rankers
     */
    private void sortRanker(List<BossMaouAttackSummary> rankers) {
        rankers.sort((o1, o2) -> o2.getBeatedBlood() - o1.getBeatedBlood());
    }

    @Override
    public void expireData(BaseServerMaou maou) {
        String redisKey = getRedisKey(maou);
//        this.rankerRedisHash.expire(redisKey, TIME_OUT_DAYS, TimeUnit.DAYS);
        this.rankerRedisHash.delete(redisKey);
    }
}
