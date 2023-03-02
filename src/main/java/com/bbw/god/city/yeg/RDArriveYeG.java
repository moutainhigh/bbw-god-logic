package com.bbw.god.city.yeg;

import com.bbw.god.city.RDCityInfo;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 到达野怪
 *
 * @author suhq
 * @date 2019年3月18日 下午3:52:23
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDArriveYeG extends RDCityInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer level = null;// 对方召唤师等级
	private String nickname = null;
	private Integer headIcon = null;
	private Integer head = null;
    private List<RDFightsInfo.RDFightCard> cards = null;// 对手卡牌
    private Integer randomGoal = null;// 打野怪随机目标
    private Integer siteId = null;// 野地ID
    private Boolean hasWin = false;
	private Integer yeGuaiType = YeGuaiEnum.YG_NORMAL.getType();
	private Integer country = null;// 所属地属性
	private Integer ygAttribute =null;//野怪属性
    private Integer buff=null;
    // private RDFightsInfo fightsInfo = null;// 打野怪对手

    public static RDArriveYeG getInstance(RDFightsInfo rdFightsInfo, CfgCityEntity city, int additionGoal, int ygAttr) {
        RDArriveYeG rdArriveYG = new RDArriveYeG();
        // rdArriveYG.setFightsInfo(rdFightsInfo);
        rdArriveYG.setRandomGoal(additionGoal);
        rdArriveYG.setArriveCityId(city.getId());
        rdArriveYG.setCountry(city.getCountry());
        rdArriveYG.setYgAttribute(ygAttr);
        rdArriveYG.updateInfo(rdFightsInfo);
        return rdArriveYG;
    }

    public static RDArriveYeG getInstance(RDFightsInfo rdFightsInfo, CfgCityEntity city, int additionGoal) {
        return getInstance(rdFightsInfo, city, additionGoal, city.getType() - 100);
    }

    public void updateInfo(RDFightsInfo rdFightsInfo) {
        this.setLevel(rdFightsInfo.getLevel());
        this.setCards(rdFightsInfo.getCards());
        this.setHeadIcon(rdFightsInfo.getHeadIcon());
        this.setNickname(rdFightsInfo.getNickname());
        this.setHead(rdFightsInfo.getHead());
        this.setBuff(rdFightsInfo.getCityBuff());
    }

}
