package com.bbw.god.fight;

import com.bbw.common.StrUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FightResultValidator {
    @Autowired
    private UserCardService userCardService;

    /**
     * 解密血量串
     *
     * @param code
     * @return
     */
    public static String decodeResultCode(String code) {
        char[] c = code.toCharArray();
        int a[] = {4, 9, 6, 2, 8, 7, 3};
        StringBuffer sbu = new StringBuffer();
        for (int i = 0, j = 0; j < c.length; j++, i = (i + 1) % 7) {
            c[j] -= a[i];
            if (c[j] < 32) c[j] += 90;

            sbu.append(c[j]);
        }
        return sbu.toString();
    }

    /**
     * 战斗结果校验
     *
     * @param gu
     * @param resultPart
     * @param resultCode
     * @param isFightAgain  处打野怪外，其他都传false
     * @param oppFightsInfo
     */
    @Deprecated
    public void validMapFightResult(GameUser gu, String[] resultPart, String resultCode, Boolean isFightAgain, RDFightsInfo oppFightsInfo) {
//		String[] opp = resultPart[1].split(",");
//		int oppLevelAsRequest = Integer.valueOf(opp[0]); // 对手等级
//		int oppLostBloodAsRequest = Integer.valueOf(opp[1]); // 对手损失血量
//
//		int oppLevel = oppFightsInfo.getLevel();
//		int oppBlood = FightResultUtil.getBloodByLevel(oppLevel);
//
//		if (oppLevel != oppLevelAsRequest) {
//			throw new ExceptionForClientTip("fight.invalid.level");
//		}
//
//		if (oppLostBloodAsRequest > oppBlood) {
//			throw new ExceptionForClientTip("fight.invalid.blood");
//		}
//
//		if (Integer.valueOf(resultPart[4]) > userCardService.getZCMaxNum(gu)) {
//			throw new ExceptionForClientTip("fight.invalid.zc");
//		}
//		List<RDFightsInfo.RDFightCard> oppFightCards = oppFightsInfo.getCards();
//		if (!isFightAgain && oppLostBloodAsRequest < oppBlood && oppFightCards.size() != resultPart[2].split(",").length) {
//			throw new ExceptionForClientTip("fight.invalid");
//		}
        // validCard(resultPart, oppFightCards);
        // validBlood(gu, oppLostBloodAsRequest, resultCode);
    }

    /**
     * 战斗结果校验
     *
     * @param gu
     * @param resultPart
     * @param resultCode
     * @param oppFightsInfo
     */
    @Deprecated
    public void validFightResult(GameUser gu, String[] resultPart, RDFightsInfo oppFightsInfo) {
//		String[] opp = resultPart[1].split(",");
//		int oppLevelAsRequest = Integer.valueOf(opp[0]); // 对手等级
//		int oppLostBloodAsRequest = Integer.valueOf(opp[1]); // 对手损失血量
//
//		int oppLevel = oppFightsInfo.getLevel();
//		int oppBlood = FightResultUtil.getBloodByLevel(oppLevel);
//
//		if (oppLevel != oppLevelAsRequest) {
//			throw new ExceptionForClientTip("fight.invalid.level");
//		}
//
//		if (oppLostBloodAsRequest > oppBlood) {
//			throw new ExceptionForClientTip("fight.invalid.blood");
//		}
//
//		if (Integer.valueOf(resultPart[4]) > userCardService.getZCMaxNum(gu)) {
//			throw new ExceptionForClientTip("fight.invalid.zc");
//		}
        // List<RDFightCard> oppFightCards = oppFightsInfo.getCards();
        // if (oppLostBloodAsRequest < oppBlood && oppFightCards.size() !=
        // resultPart[2].split(",").length) {
        // throw new ExceptionForClientTip("fight.invalid");
        // }
        // validCard(resultPart, oppFightCards);
        // validBlood(gu, oppLostBloodAsRequest, resultCode);
    }

    /**
     * 打掉的对手卡牌的数据是否有效
     *
     * @param resultPart
     * @param oppFightCards
     */
    private static void validCard(String[] resultPart, List<UserCard> oppFightCards) {
        if (StrUtil.isNotBlank(resultPart[2])) {
            String[] beatedCard = resultPart[2].split(",");
            for (int i = 0; i < beatedCard.length; i++) {
                String[] beatedCardInfos = beatedCard[i].split("!");
                int cardId = Integer.valueOf(beatedCardInfos[0]);
                int cardLevel = Integer.valueOf(beatedCardInfos[2]);
                int cardHierarchy = Integer.valueOf(beatedCardInfos[3]);
                if (!isBeatedCardValid(cardId, cardLevel, cardHierarchy, oppFightCards)) {
                    log.warn("请勿修改对手卡牌数据");
                    throw new ExceptionForClientTip("fight.invalid");
                }
            }
        }
    }

    /**
     * 客户端传血量数据
     *
     * @param gu
     * @param oppLostBlood
     * @param resultCode
     */
    private void validBlood(GameUser gu, int oppLostBlood, String resultCode) {
        int beatBlood = 0;
        if (StrUtil.isNotBlank(resultCode)) {
            List<UserCard> selfCards = userCardService.getFightingCards(gu.getId());
            String decodedResult = decodeResultCode(resultCode);
            log.info("decodedResultCode:" + decodedResult);
            String[] bloodArray = decodedResult.split("!");
            for (int i = 0; i < bloodArray.length; i++) {
                String[] bloodInfo = bloodArray[i].split(",");
                log.info(bloodInfo[0] + "," + bloodInfo[1] + "," + bloodInfo[2]);
                int cardId = Integer.valueOf(bloodInfo[0]);
                int skill = Integer.valueOf(bloodInfo[1]);
                int blood = Integer.valueOf(bloodInfo[2]);
                if (blood > getAttackForCheck(cardId, skill > 0, selfCards)) {
                    log.warn("对手卡牌攻击召唤师的血量被修改");
                    throw new ExceptionForClientTip("fight.invalid");
                }
                beatBlood += blood;
            }
        }

        log.info("validBlood：oppLostBlood=" + (-oppLostBlood) + ";beatBlood=" + beatBlood);
        if (-oppLostBlood != beatBlood) {
            log.warn("客户端血量被修改");
            throw new ExceptionForClientTip("fight.invalid.blood");
        }
    }

    /**
     * 判断打掉的卡牌是否有效
     *
     * @param cardId
     * @param cardLevel
     * @param cardHierarchy
     * @param opponentCards 对手卡牌
     * @return
     */
    private static boolean isBeatedCardValid(int cardId, int cardLevel, int cardHierarchy, List<UserCard> opponentCards) {
        UserCard fightCard = opponentCards.stream().filter(card -> card.getBaseId() == cardId).findFirst()
                .orElse(null);
        if (fightCard != null) {
            return fightCard.getLevel() == cardLevel && fightCard.getHierarchy() == cardHierarchy;
        }
        return false;
    }

    /**
     * 获得攻击
     *
     * @param cardId
     * @param isSkill
     * @param selfCards
     * @return
     */
    private static int getAttackForCheck(int cardId, boolean isSkill, List<UserCard> selfCards) {
        if (cardId == 0) {
            return 15000;
        }

        UserCard userCard = selfCards.stream().filter(uc -> uc.getBaseId() == cardId).findFirst().orElse(null);
        if (userCard == null) {
            return 15000;
        }
        int attack = userCard.gainAttack();

        if (isSkill) {
            return attack * 2;
        } else {
            return attack + 10;
        }
    }

}
