package com.bbw.god.gameuser.nightmarenvwam.godsaltar;

import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 封神祭坛返回客户端
 *
 * @author fzj
 * @date 2022/5/10 14:04
 */
@Data
public class RDGodsAltar extends RDSuccess {
    /** 神格牌进度信息 */
    private List<RDGodHeadInfo> godHeadInfos;

    @Data
    public static class RDGodHeadInfo {
        /** 卡牌id */
        private Integer cardId;
        /** 进度 */
        private List<Integer> progress;
        /** 已兑换次数 */
        private Integer hasExchangeTime;
    }

    public static RDGodHeadInfo getInstance(int cardId) {
        RDGodHeadInfo rd = new RDGodHeadInfo();
        rd.setCardId(cardId);
        rd.setProgress(new ArrayList<>());
        rd.setHasExchangeTime(0);
        return rd;
    }

    public static RDGodHeadInfo getInstance(UserGodsAltarInfo.ExchangeProgress progress) {
        RDGodHeadInfo rd = new RDGodHeadInfo();
        rd.setCardId(progress.getCardId());
        rd.setProgress(progress.getProgress());
        rd.setHasExchangeTime(progress.getHasExchangeTimes());
        return rd;
    }
}
