package com.bbw.god.gameuser.businessgang.Enum;

import com.bbw.exception.CoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商帮npc枚举
 *
 * @author fzj
 * @date 2022/1/20 9:50
 */
@Getter
@AllArgsConstructor
public enum BusinessGangNpcEnum {
    ZHAO_GM("赵公明", 1001),
    LE_ZL("乐长老", 1002),
    HUA_WY("花无雨", 1003),
    //    XIAO_JZ("小杰子",1004),
//    BUBU("布布",1005),
    XIAO_S("萧升", 2001),
    CHI_ZL("池长老", 2002),
    //    YI_SC("逸少聪",2003),
//    XIAO_QZ("小强子",2004),
//    XIN_XIN("欣欣",2005),
    CHEN_JG("陈九公", 3001),
//    LIU_ZL("刘长老",3002),
//    XIA_RT("夏日天",3003),
//    XIAO_GZ("小桂子",3004),
//    SI_SI("思思",3005),
    ;

    private final String name;
    private final int id;

    public static BusinessGangNpcEnum fromValue(int type) {
        for (BusinessGangNpcEnum item : values()) {
            if (item.getId() == type) {
                return item;
            }
        }
        throw CoderException.high("无效的npc-" + type);
    }
}
