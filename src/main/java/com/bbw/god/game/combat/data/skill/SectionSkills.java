package com.bbw.god.game.combat.data.skill;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import lombok.Getter;

/**
 * 各阶段技能配置。section字段不可重复
 *
 * @author: suhq
 * @date: 2021/10/11 3:35 下午
 */
@Getter
public enum SectionSkills {
    //主流程阶段
    /** 回合布阵结束 */
    DEPLOY_END_SKILLS_0("deployEndSkills0", "0-1-0", new int[]{1101}),
    /** 回合布阵结束 */
    DEPLOY_END_SKILLS_1("deployEndSkills1", "0-1-1", new int[]{1105, 1104, 1103, 1102, 1106, 6005}),
    /** 组合技 */
    GROUP_SKILLS("groupSkills", "0-2", new int[]{2009, 2002, 2003, 2004, 2010, 2011, 2016, 2007, 2018, 2008, 2001, 2014, 2005, 2006, 2020, 2015, 2012, 2017, 2013, 2019}),
    /** 先制技能(联攻) */
    PRIORITY1_SKILLS("priority1Skills", "0-3", new int[]{2201}),
    /** 上场技 */
    DEPLOY_SKILLS("deploySkills", "0-4", new int[]{1001, 1002, 1003, 1004, 1005, 1007, 1008, 1009, 1010, 1011, 1012, 1013, 1014, 1015, 1016, 1090, 1091, 1017, 1018, 1019, 1020, 1021, 1023}),
    /** 给别人的盾牌类技能 */
    SHIELD_SKILLS("shieldSkills", "0-5-0", new int[]{3140}),
    /** 先制法术效果 */
    PRIORITY_SPELL_ATTACK_SKILLS("prioritySpellAttackSkills", "0-5-1", new int[]{3501, 3502}),
    /** 法术攻击 */
    SPELL_ATTACK_SKILLS("spellAttackSkills", "0-5-2", new int[]{3101, 3102, 3103, 3104, 3105, 3106, 3107, 3108, 3109, 3110, 3111, 3112, 3113, 3114, 3115, 3116,
            3117, 3118, 3119, 3120, 3121, 3122, 3123, 3124, 3125, 3126, 3127, 3128, 3130, 3131, 3132, 3133,
            3134, 3135, 3136, 3137, 3138, 3139, 3140, 3141, 3142, 3144, 3145, 3146, 3147, 3148, 3149, 3150, 3151, 3152, 3153,
            3154, 3155, 3156, 3157, 3158, 3159, 3160, 3161, 3162, 3163, 3164, 3165, 3166, 3167, 3168, 3170, 3171, 3172, 3175, 3176}),
    /** 物理攻击之前，法术之后 */
    BEFORE_NORMAL_SKILLS("beforeNormalSkills", "0-6", new int[]{3901, 3902}),
    /** 属性克制 */
    ATTRIBUTE_RESTRAINT_SKILLS("attributeRestraintSkills", "0-7", new int[]{4106, 4107, 4108, 4109, 4110}),
    /** 物理攻击buff_0 */
    NORMAL_BUFF_SKILLS_0("normalBuffSkills0", "0-8-0", new int[]{10201, 10202}),
    /** 物理攻击buff_1 */
    NORMAL_BUFF_SKILLS_1("normalBuffSkills1", "0-8-1", new int[]{4104, 4106, 4107, 4108, 4109, 4110, 4111, 4112, 4113, 4114, 4115, 4116, 4118, 4119, 4120, 10101, 10102}),
    /** 物理攻击先手 */
    NORMAL_PRIORITY1_SKILLS("normalPrioritySkills", "0-9", new int[]{4301, 4302, 4303}),
    /** 物理攻击技能 */
    NORMAL_ATTACK_SKILLS("normalAttackSkills", "0-10", new int[]{4401}),
    /** 被物理击中 */
    NORMAL_HITTED_SKILLS("normalHittedSkills", "0-11", new int[]{4599}),
    /** 物理溅射 */
    PHYSICAL_PARTICLE_SKILLS("physicalParticlSkills", "0-12", new int[]{CombatSkillEnum.LX.getValue()}),
    /** 物理攻击后 */
    NORMAL_END_ATTACK_SKILLS("normalEndAttackSkills", "0-13", new int[]{4201, 4202, 4203}),
    /** 第二个物理攻击阶段 ： 连击（再走一次 属性克制 ~ 物理攻击后） */
    NORMAL_ATTACK2_SKILLS("normalAttack2Skills", "0-14", new int[]{4451}),

    // 法术防御,组合技 ~ 法术攻击 均可触发
    /** 共享完全免疫防御(不受效果) */
    SHARE_ABSOLUTE_DEFENCE_SKILLS("ShareAbsoluteDefense", "1-1", new int[]{3014, 3143, 3018}),
    /** 法术防御[3001,3099]+法宝添加防御技能[8001,8099] (无效) */
    SPELL_DEFENCE_SKILLS("spellDefenceSkills", "1-1", new int[]{3001, 3002, 3003, 3004, 3005, 3006, 3007, 3008, 3009, 3012, 3013, 3014, 3015, 3016, 3018, 3143, 8001, 8002}),
    /** 共享的防御技能(减伤) */
    SHARE_DEFENCE_SKILLS("shareDefenseSkills", "1-2", new int[]{3011}),
    /** 法术效果削弱 (减伤) */
    SPELL_EFFECT_WEAKEN_SKILLS("spellEffectWeakenSkills", "1-2", new int[]{3010, 3011, 11102}),
    /** 法术反击 */
    SPECLL_FIGHT_BACK_SKILLS("spellFightBackSkills", "1-3-1", new int[]{3201, 3202}),
    /** 法术反制（受到法术效果后触发，该阶段技能可同时触发） */
    SPECLL_COUNTER_SKILLS("specllCounterSkills", "1-4", new int[]{3301, 3302}),


    //物理防御
    /** 物理buff防御 (属性克制 ~ 物理攻击buff 可触发) */
    NORMAL_BUFF_DEFENCE_SKILLS("normalBuffDefenseSkills", "2-1", new int[]{4001, 4116}),
    /** 物理防御前 */
    BEFORE_NORMAL_DEFENCE_SKILLS("beforeNormalDefenseSkills", "2-2-0", new int[]{}),
    /** 物理防御 (物理攻击先手 ~ 物理溅射 可触发) */
    NORMAL_DEFENCE_SKILLS("normalDefenseSkills", "2-2-1", new int[]{4505, 4501, 4503, 11201}),
    /** 物理防御后 */
    NORMAL_DEFENCE_AFTER_SKILLS("normalDefenseAfterSkills", "2-2-2", new int[]{4502, 4504, 11101, 11202}),
    /** 物理反制（受到物理攻击后触发，该阶段技能可同时触发） */
    PHYSICAL_COUNTER_SKILLS("physicalCounterSkills", "2-3", new int[]{4601, 4602}),
    /** 物理反制后 */
    AFTER_PHYSICAL_COUNTER_SKILLS("afterPhysicalCounterSkills", "2-4", new int[]{4603}),

    //死亡技
    /** 死亡时的增益技能 如 长生 */
    DYING_BENEFIT_SKILLS("dyingBenefitSkills", "3-1", new int[]{1205}),
    /** 死亡时的攻击技能 */
    DYING_ATTACK_SKILLS("dyingAttakSkills", "3-2", new int[]{1202, 1203, 1204, 1206, 1207, 1208}),
    /** 死亡后(进入坟场)的增益技能 如 复活 */
    INTO_DISCARD_SKILLS("intoDiscardSkills", "3-3", new int[]{1201}),

    //召唤师防御与反击
    /** 召唤师防御（收到组合技、法术攻击、物理攻击均可触发） */
    ZHS_SKILLS("zhsSkills", "4-1", new int[]{5001}),
    /** 召唤师防御（收到法术攻击均可触发） */
    ZHS_ATTACK_AFTER_SKILLS("zhsAttakAtferSkills", "4-2", new int[]{5002}),

    /** 死亡技能 */
    DIE_SKILLS("dieSkills", "-", new int[]{1201, 1202, 1203, 1204, 1205, 1206, 1207, 1208}),
    /** 只执行一次的技能 */
    PERFORM_ONE_SKILLS("performOneTimeSkills", "-", new int[]{CombatSkillEnum.CS.getValue()}),
    /** 所有溅射技能 */
    ALL_PARTICLE_SKILLS("allParticleSkills", "-", new int[]{CombatSkillEnum.LX.getValue(), CombatSkillEnum.LEI_DIAN.getValue()}),
    /** 获取中毒效果的技能、法宝：瘟君、入痘、百毒豆 */
    ZHONG_DU_EFFECT_SKILLS("zhongDuEffectSkills", "-", new int[]{CombatSkillEnum.WJ.getValue(), CombatSkillEnum.RD.getValue(), 490}),
    /** 所有剑的法宝 */
    ALL_JIAN_SKILLS("allJianSkills", "-", new int[]{250, 270, 290, 340, 390, 410}),
    /** 护卫不能生效的BUFF */
    HU_WEI_CANT_DEFENCE_SKILLS("huWeiCantDefenseSkills", "-", new int[]{131480, 131430}),
    /** 封禁类技能:禁术 封咒 蚀月 迷魂阵 */
    FENG_JIN_SKILLS("fengJinSkills", "-", new int[]{CombatSkillEnum.JINS.getValue(), CombatSkillEnum.FZ.getValue(), CombatSkillEnum.SY.getValue(), CombatSkillEnum.MHZ_G.getValue()}),
    ;
    /** section不可重复 */
    private String section;
    /** 阶段次序，仅供阅读使用，不可用于业务逻辑判断 */
    private String order;
    private int[] skills;

    SectionSkills(String section, String order, int[] skills) {
        this.section = section;
        this.order = order;
        this.skills = skills;
    }
}