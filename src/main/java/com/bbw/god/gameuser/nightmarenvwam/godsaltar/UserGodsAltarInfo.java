package com.bbw.god.gameuser.nightmarenvwam.godsaltar;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 玩家封神祭坛信息
 *
 * @author fzj
 * @date 2022/5/10 14:43
 */
@Data
public class UserGodsAltarInfo extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 兑换进度 */
    private List<ExchangeProgress> progressList;

    @Data
    public static class ExchangeProgress {
        /** 卡牌id */
        private int cardId;
        /** 当前进度 */
        private List<Integer> progress;
        /** 兑换次数 */
        private int hasExchangeTimes = 0;
    }

    public static UserGodsAltarInfo getInstance(long uid) {
        UserGodsAltarInfo userGodsAltarInfo = new UserGodsAltarInfo();
        userGodsAltarInfo.setId(ID.INSTANCE.nextId());
        userGodsAltarInfo.setGameUserId(uid);
        userGodsAltarInfo.setProgressList(new ArrayList<>());
        return userGodsAltarInfo;
    }

    public static ExchangeProgress getInstance(int cardId) {
        ExchangeProgress progress = new ExchangeProgress();
        progress.setCardId(cardId);
        progress.setProgress(new ArrayList<>());
        return progress;
    }

    /**
     * 加限制次数
     *
     * @param progress
     */
    public void addLimitTimes(ExchangeProgress progress) {
        int limitTimes = progress.getHasExchangeTimes();
        progress.setHasExchangeTimes(limitTimes + 1);
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_GODS_ALTAR;
    }
}
