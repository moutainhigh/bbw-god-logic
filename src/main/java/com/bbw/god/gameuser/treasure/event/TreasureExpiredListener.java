package com.bbw.god.gameuser.treasure.event;

import com.bbw.common.LM;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 活动监听器
 *
 * @author suhq
 * @date 2019年3月4日 下午4:15:07
 */
@Component
@Slf4j
public class TreasureExpiredListener {
    @Autowired
    private MailService mailService;

    private final static List<Integer> VOUCHER_TREASURE = Arrays.asList(11210, 11220);

    private final static List<Integer> ZI_TIE = Arrays.asList(11203, 11204, 11205, 11206, 11207, 50098, 50099, 50100, 50101, 50102);

    private final static List<Integer> WAN_SHENG = Arrays.asList(50120, 50121, 50122, 50123, 50124);
    /** 感恩节活动 */
    private final static List<Integer> THANKS_GIVING = Arrays.asList(50135, 50136, 50137, 50138, 50139, 50140,
            50141, 50142, 50143, 50144, 50145, 50146, 50147, 50148, 50149, 50134, 50150);
    /** 双旦节活动 */
    private final static List<Integer> NEW_YEAR_AND_CHRISTMAS = Arrays.asList(50162, 50163, 50165, 50166, 50167, 50168, 50169);
    /** 新年活动道具 */
    private final static List<Integer> SPRING_FESTIVAL = Arrays.asList(50175, 50176, 50177, 50178);
    /** 周年庆活动道具 */
    private final static List<Integer> ANNIVERSARY = Arrays.asList(50217, 50218, 50219);
    /** 清明活动道具 */
    private final static List<Integer> QINGMING = Arrays.asList(50222, 50223, 50224, 50225);
    /** 集字有礼 */
    private final static List<Integer> JZYL = Arrays.asList(11211, 11212, 11213, 11214, 11215);
    /** 端午节活动 */
    private final static List<Integer> DRAGON_BOAT_FESTIVAL = Arrays.asList(50302, 50303, 50304);
    /** 龙舟活动 */
    private final static List<Integer> DRAGON_BOAT = Arrays.asList(11710);
    /** 清凉一夏 */
    private final static List<Integer> COOL_SUMMER = Arrays.asList(50308, 50309, 50310, 50311);
    /** 七夕情人节 */
    private final static List<Integer> VALENTINE_S_DAY = Arrays.asList(50315, 50316, 50317, 50318, 50319, 50320, 50208);
    /** 中秋 */
    private final static List<Integer> MID_AUTUMN_FESTIVAL = Arrays.asList(50321, 50322, 50323);
    /** 国庆节 */
    private final static List<Integer> NATIONAL_DAY = Arrays.asList(50402, 50403, 50404, 50405, 50407);
    /** 世界杯 */
    private final static List<Integer> WORLD_CUP = Arrays.asList(50430, 50431);
    /** 2022 感恩节 */
    private final static List<Integer> THANKS_GIVING_2022 = Arrays.asList(50433, 50434, 50435, 50436, 50437, 50438, 50439, 50440,
            50441, 50442, 50443, 50445, 50446, 50447, 50448, 50449);
    /** 2022 冬幕日 */
    private final static List<Integer> FEAST_OF_WINTER_VEIL = Arrays.asList(50450, 50451, 50452, 50453, 50454, 50455);
    /** 2022圣诞节 */
    private final static List<Integer> CHRISTMAS = Arrays.asList(11216, 50456, 50457, 50458, 50459, 50460, 50461, 50462, 50463, 50464, 50465, 50466
            , 50467, 50468, 50469, 50470, 50471, 50472, 50473);
    /** 2022元旦节 */
    private final static List<Integer> NEW_YEAR_S_DAY = Arrays.asList(50474, 50475, 50476, 50220, 50170);
    /** 萌虎辞旧 */
    private final static List<Integer> CUTE_TIGER_RESIGNS_THE_OLD = Arrays.asList(50171, 10235, 10220, 50174);
    /** 瑞兔迎新 */
    private final static List<Integer> THE_AUSPICIOUS_RABBIT_WELCOMES_THE_NEW_YEAR = Arrays.asList(50478, 50479, 50480, 50481, 50161);
    /** 元宵节 */
    private final static List<Integer> THE_LANTERN_FESTIVAL = Arrays.asList(50203, 50204, 50205, 50206, 50209, 50494);
    /** 23情人节 */
    private final static List<Integer> VALENTINESDAY = Arrays.asList(11440, 11450, 11460, 11470, 11480, 50207);
    private final static List<Integer> DRAGON = Arrays.asList(50497, 50498);


    /** 固定兑换铜钱 */
    private final static Integer FIXED_EXCHANGE_TQ = 1000;

    @EventListener
    public void treasureExpired(TreasureExpiredEvent event) {
        EPTreasureExpired ep = event.getEP();
        Integer treasureId = ep.getTreasureId();
        long expiredNum = ep.getExpiredNum();
        //每个道具固定兑换1000铜钱
        long copperNum = expiredNum * FIXED_EXCHANGE_TQ;
        //返还铜钱
        Award award = new Award(AwardEnum.TQ, (int) copperNum);
        List<Award> awards = Arrays.asList(award);
        //过期法宝名称
        String treasureName = TreasureTool.getTreasureById(treasureId).getName();

        if (WAN_SHENG.contains(treasureId)) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "万圣节");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "万圣节", treasureName + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
            return;
        }
        if (THANKS_GIVING.contains(treasureId)) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "感恩节");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "感恩节", treasureName + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
            return;
        }
        if (NEW_YEAR_AND_CHRISTMAS.contains(treasureId)) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "双旦活动");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "双旦活动", treasureName + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
            return;
        }
        if (SPRING_FESTIVAL.contains(treasureId)) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "春节");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "春节", treasureName + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
            return;
        }
        //集字有礼活动法宝过期
        if (JZYL.contains(treasureId)) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "集字有礼");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "劳动光荣", TreasureTool.getTreasureById(treasureId).getName() + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
            return;
        }
        //端午活动法宝过期
        if (DRAGON_BOAT_FESTIVAL.contains(treasureId)) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "端午食物");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "萌虎集市", TreasureTool.getTreasureById(treasureId).getName() + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
            return;
        }  //龙舟活动法宝过期
        if (DRAGON_BOAT.contains(treasureId)) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "龙舟鼓槌");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "端午赛舟", TreasureTool.getTreasureById(treasureId).getName() + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
            return;
        }
        if (COOL_SUMMER.contains(treasureId)) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "盛夏光年");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "盛夏光年", TreasureTool.getTreasureById(treasureId).getName() + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
            return;

        }
        if (VALENTINE_S_DAY.contains(treasureId)) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "七夕");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "七夕", TreasureTool.getTreasureById(treasureId).getName() + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
            return;
        }
        //中秋
        if (MID_AUTUMN_FESTIVAL.contains(treasureId)) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "中秋");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "中秋", TreasureTool.getTreasureById(treasureId).getName() + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
            return;

        }
        //国庆节
        if (NATIONAL_DAY.contains(treasureId)) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "国庆节");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "国庆节", TreasureTool.getTreasureById(treasureId).getName() + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
            return;
        }
        //23情人节
        if (VALENTINESDAY.contains(treasureId)) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "情人节");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "情人节", TreasureTool.getTreasureById(treasureId).getName() + "*" + expiredNum);
            if (TreasureEnum.SKY_LANTERN_LOTTERY.getValue() == treasureId) {
                content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "情人节", "超级奖券*" + expiredNum);
            }
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
            return;
        }
        // 世界杯
        if (WORLD_CUP.contains(treasureId)) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "世界杯");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "世界杯", TreasureTool.getTreasureById(treasureId).getName() + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
            return;
        }
        // 2022 感恩节
        if (THANKS_GIVING_2022.contains(treasureId)) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "感恩节");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "感恩节", TreasureTool.getTreasureById(treasureId).getName() + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
            return;
        }
        // 2022 冬幕日
        if (FEAST_OF_WINTER_VEIL.contains(treasureId)) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "冬幕日");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "冬幕日", TreasureTool.getTreasureById(treasureId).getName() + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
            return;
        }
        // 2022 圣诞节
        if (CHRISTMAS.contains(treasureId)) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "圣诞节");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "圣诞节", TreasureTool.getTreasureById(treasureId).getName() + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
            return;
        }
        // 2022 元旦节
        if (NEW_YEAR_S_DAY.contains(treasureId)) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "元旦");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "元旦", TreasureTool.getTreasureById(treasureId).getName() + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
            return;
        }
        //萌虎辞旧
        if (CUTE_TIGER_RESIGNS_THE_OLD.contains(treasureId)) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "萌虎辞旧");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "萌虎辞旧", TreasureTool.getTreasureById(treasureId).getName() + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
            return;
        }
        //瑞兔迎新
        if (THE_AUSPICIOUS_RABBIT_WELCOMES_THE_NEW_YEAR.contains(treasureId)) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "瑞兔迎新");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "瑞兔迎新", TreasureTool.getTreasureById(treasureId).getName() + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
            return;
        }
        //元宵节
        if (THE_LANTERN_FESTIVAL.contains(treasureId)) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "元宵节");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "元宵节", TreasureTool.getTreasureById(treasureId).getName() + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
            return;
        }
        //龙抬头
        if (DRAGON.contains(treasureId)) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "龙抬头");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "龙抬头", TreasureTool.getTreasureById(treasureId).getName() + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
            return;
        }
    }

    /**
     * 清明活动法宝过期邮件
     *
     * @param event
     */
    @EventListener
    public void qingMingTreasureExpired(TreasureExpiredEvent event) {
        EPTreasureExpired ep = event.getEP();
        Integer treasureId = ep.getTreasureId();
        if (!QINGMING.contains(treasureId)) {
            return;
        }
        long expiredNum = ep.getExpiredNum();
        //每个道具固定兑换1000铜钱
        long copperNum = expiredNum * FIXED_EXCHANGE_TQ;
        String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "清明活动");
        String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "清明", TreasureTool.getTreasureById(treasureId).getName() + "*" + expiredNum);
        Award award = new Award(AwardEnum.TQ, (int) copperNum);
        mailService.sendAwardMail(title, content, ep.getGuId(), Arrays.asList(award));
    }

    /**
     * 周年庆
     *
     * @param event
     */
    @EventListener
    public void anniversaryExpired(TreasureExpiredEvent event) {
        EPTreasureExpired ep = event.getEP();
        Integer treasureId = ep.getTreasureId();
        if (!ANNIVERSARY.contains(treasureId)) {
            return;
        }
        long expiredNum = ep.getExpiredNum();
        //每个道具固定兑换1000铜钱
        long copperNum = expiredNum * FIXED_EXCHANGE_TQ;
        String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "周年庆活动");
        String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "周年庆", TreasureTool.getTreasureById(treasureId).getName() + "*" + expiredNum);
        Award award = new Award(AwardEnum.TQ, (int) copperNum);
        mailService.sendAwardMail(title, content, ep.getGuId(), Arrays.asList(award));
    }
    
    @EventListener
    public void voucherExpired(TreasureExpiredEvent event) {
        EPTreasureExpired ep = event.getEP();
        Integer treasureId = ep.getTreasureId();
        if (!VOUCHER_TREASURE.contains(treasureId)) {
            return;
        }
        long expiredNum = ep.getExpiredNum();
        int gold = (int) (treasureId == TreasureEnum.GOD_VOUCHER.getValue() ? 388 * expiredNum : 188 * expiredNum);
        Award award = new Award(AwardEnum.YB, gold);
        String treasureName = TreasureTool.getTreasureById(treasureId).getName();
        String title = LM.I.getMsgByUid(ep.getGuId(), "mail.treasure.outdate.title", treasureName);
        String content = LM.I.getMsgByUid(ep.getGuId(), "mail.treasure.outdate.content.with.gold", treasureName);
        mailService.sendAwardMail(title, content, ep.getGuId(), Arrays.asList(award));
    }

    /**
     * 周年庆道具过期
     *
     * @param event
     */
    @EventListener
    public void zhouNianTreasureExpired(TreasureExpiredEvent event) {
        EPTreasureExpired ep = event.getEP();
        Integer treasureId = ep.getTreasureId();
        long expiredNum = ep.getExpiredNum();
        //每个道具固定兑换1000铜钱
        long copperNum = expiredNum * FIXED_EXCHANGE_TQ;
        Award award = new Award(AwardEnum.TQ, (int) copperNum);
        List<Award> awards = Arrays.asList(award);
        String treasureName = TreasureTool.getTreasureById(treasureId).getName();
        if (ZI_TIE.contains(treasureId)) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.title", "集字有礼");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "集字有礼", treasureName + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
        } else if (TreasureEnum.COLORFUL_FLOWERS.getValue() == treasureId) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.treasure.outdate.title", "七彩花");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "封神赛马", treasureName + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
        } else if (TreasureEnum.CHANG_ZI.getValue() == treasureId) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.treasure.outdate.title", "挖宝铲子");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "野外挖宝", treasureName + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
        } else if (TreasureEnum.GOLDEN_KEY.getValue() == treasureId) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.treasure.outdate.title", "金钥匙");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "雨露均沾", treasureName + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
        } else if (TreasureEnum.BO_CAKE_TICKET.getValue() == treasureId) {
            String title = LM.I.getMsgByUid(ep.getGuId(), "mail.treasure.outdate.title", "博饼券");
            String content = LM.I.getMsgByUid(ep.getGuId(), "mail.activity.treasure.outdate.content", "佳节博饼", treasureName + "*" + expiredNum);
            mailService.sendAwardMail(title, content, ep.getGuId(), awards);
        }
    }
}
