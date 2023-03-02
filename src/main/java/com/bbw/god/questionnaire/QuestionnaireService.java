package com.bbw.god.questionnaire;

import com.bbw.common.DateUtil;
import com.bbw.common.Rst;
import com.bbw.db.redis.RedisHashUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * @author suchaobin
 * @description 问卷调查service
 * @date 2020/12/4 09:26
 **/
@Service
public class QuestionnaireService {
    /**
     * 到redis中的存储结构是field是玩家id，对应的val true是显示图标，false是不显示图标
     */
    @Autowired
    private RedisHashUtil<Long, Boolean> redisHashUtil;
    @Autowired
    private GameUserService gameUserService;
    /**
     * 主界面问卷调查图标显示截至时间
     */
    private final static Date SHOW_END_TIME = DateUtil.fromDateTimeString("2020-12-14 23:59:59");
    /**
     * redis中的key的过期时间
     */
    private final static Integer TIME_OUT = 7 * 24 * 60 * 60;

    /**
     * 参与问卷调查
     *
     * @param uid 玩家id
     * @return 操作是否成功
     */
    public Rst join(long uid) {
        if (DateUtil.now().after(SHOW_END_TIME)) {
            return Rst.businessFAIL("问卷调查已结束!");
        }
        String key = getKey(uid);
        redisHashUtil.putField(key, uid, true, TIME_OUT);
        return Rst.businessOK();
    }

    /**
     * 隐藏主界面图标
     *
     * @param uid 玩家id
     * @return 操作是否成功
     */
    public Rst hideIcon(long uid) {
        String key = getKey(uid);
        redisHashUtil.putField(key, uid, false);
        return Rst.businessOK();
    }

    /**
     * 获取存储到redis中的key
     *
     * @param uid 玩家id
     * @return 存储到redis中的key
     */
    private String getKey(long uid) {
        int sid = gameUserService.getOriServer(uid).getMergeSid();
        CfgServerEntity server = ServerTool.getServer(sid);
        Integer groupId = server.getGroupId();
        return "game" + SPLIT + "questionnaire" + SPLIT + groupId;
    }

    /**
     * 主界面是否显示图标
     *
     * @param uid 玩家id
     * @return 主界面是否显示图标
     */
    public boolean isShowIcon(long uid) {
        // 当前时间已经晚于截至时间了
        if (DateUtil.now().after(SHOW_END_TIME)) {
            return false;
        }
        // 等级没到10级的不显示
        Integer level = gameUserService.getGameUser(uid).getLevel();
        if (level < 10) {
            return false;
        }
        String key = getKey(uid);
        Boolean hasField = redisHashUtil.hasField(key, uid);
        // 没参与的显示图标
        if (!hasField) {
            return true;
        }
        return redisHashUtil.getField(key, uid);
    }

    /**
     * 是否参与了问卷调查
     *
     * @param uid 玩家id
     * @return 是否参与了问卷调查
     */
    public boolean isJoinQuestionnaire(long uid) {
        String key = getKey(uid);
        return redisHashUtil.hasField(key, uid);
    }
}
