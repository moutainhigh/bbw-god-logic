package com.bbw.god.server.maou;

import com.bbw.common.Rst;
import com.bbw.common.StrUtil;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import com.bbw.god.server.maou.alonemaou.ServerAloneMaouProcessor;
import com.bbw.god.server.maou.alonemaou.rd.RDAloneMaouAwards;
import com.bbw.god.server.maou.attack.MaouAttackType;
import com.bbw.god.server.maou.bossmaou.ServerBossMaouProcessor;
import com.bbw.god.server.maou.bossmaou.rd.RDMaouRankerAwards;
import com.bbw.god.server.maou.bossmaou.rd.RDMaouRankers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 魔王相关接口
 *
 * @author suhq
 * @date 2018年11月1日 上午9:54:29
 */
@Slf4j
@RestController
public class ServerMaouCtrl extends AbstractController {
    @Autowired
    private MaouProcessorFactory maouProcessorFactory;
    @Autowired
    private ServerBossMaouProcessor bossMaouProcessor;
    @Autowired
    private ServerAloneMaouProcessor aloneMaouProcessor;

    /**
     * 获取魔王信息
     *
     * @return
     */
    @GetMapping(CR.Maou.GET_MAOU)
    public RDSuccess getMaou(int maouType) {
        IServerMaouProcessor maouProcessor = this.maouProcessorFactory.getMaouProcessor(getServerId(), maouType);
        return maouProcessor.getMaou(getUserId(), getServerId());
    }

    /**
     * 定时刷新魔王信息
     *
     * @param maouKind
     * @return
     */
    @GetMapping(CR.Maou.REFRESH_MAOU)
    public RDSuccess refreshMaou(int maouKind) {
        IServerMaouProcessor maouProcessor = this.maouProcessorFactory.getMaouProcessorAsMaouKind(maouKind);
        return maouProcessor.getMaou(getUserId(), getServerId());
    }

    /**
     * 进入攻击界面信息
     *
     * @return
     */
    @GetMapping(CR.Maou.GET_ATTACKING_INFO)
    public RDSuccess getAttackingInfo(int maouKind) {
        IServerMaouProcessor maouProcessor = this.maouProcessorFactory.getMaouProcessorAsMaouKind(maouKind);
        return maouProcessor.getAttackingInfo(getUserId(), getServerId());
    }

    /**
     * 设置攻打魔王的卡牌
     *
     * @return
     */
    @GetMapping(CR.Maou.SET_CARDS)
    public RDSuccess setMaouCards(int maouKind, String maouCards) {
        IServerMaouProcessor maouProcessor = this.maouProcessorFactory.getMaouProcessorAsMaouKind(maouKind);
        return maouProcessor.setMaouCards(getUserId(), maouCards);
    }

    /**
     * 获取魔王排行
     *
     * @return
     */
    @GetMapping(CR.Maou.GET_RANKERS)
    public RDMaouRankers getRankers() {
        RDMaouRankers rd = this.bossMaouProcessor.getRankers(getUserId(), getServerId());
        return rd;
    }

    @GetMapping(CR.Maou.GET_RANKER_AWARDS)
    public RDMaouRankerAwards getRankerAwards() {
        RDMaouRankerAwards rd = this.bossMaouProcessor.getRankerAwards();
        return rd;
    }

    @GetMapping(CR.Maou.RESET_MAOU_LEVEL)
    public RDCommon resetMaouLevel() {
        RDCommon rd = this.aloneMaouProcessor.resetMaouLevel(getUserId(), getServerId());
        return rd;
    }

    /**
     * 攻打魔王
     *
     * @param maouKind 魔王种类
     * @param useGold  使用元宝情况
     * @return
     */
    @GetMapping(CR.Maou.ATTACK)
    public RDCommon attack(int maouKind, @RequestParam(defaultValue = "0") String useGold) {
        IServerMaouProcessor maouProcessor = this.maouProcessorFactory.getMaouProcessorAsMaouKind(maouKind);
        int maouFightType = MaouAttackType.COMMON_ATTACK.getValue();
        if (StrUtil.isNotEmpty(useGold)) {
            maouFightType = Integer.valueOf(useGold);
        }
        return maouProcessor.attack(getUserId(), getServerId(), maouFightType);
    }

    /**
     * 确认魔王
     *
     * @param maouType 魔王类型
     * @return
     */
    @GetMapping(CR.Maou.CONFIRM_MAOU)
    public Rst confirmMaou(int maouType) {
        return bossMaouProcessor.confirmMaouType(getUserId(), maouType);
    }

    /**
     * 获取独占魔王奖励
     *
     * @return
     */
    @GetMapping(CR.Maou.GET_ALONE_MAOU_AWARD)
    public RDAloneMaouAwards getAloneMaouAwards() {
        return aloneMaouProcessor.getAwards(getUserId(), getServerId());
    }
}
