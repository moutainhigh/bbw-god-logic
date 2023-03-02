package com.bbw.god.gm;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.bbw.common.Rst;
import com.bbw.db.Query;
import com.bbw.god.db.entity.AttackCityStrategyEntity;
import com.bbw.god.db.entity.InsCityOwnDetailEntity;
import com.bbw.god.db.service.AttackCityStrategyService;
import com.bbw.god.db.service.InsCityOwnDetailService;
import com.bbw.god.game.combat.attackstrategy.StrategyEnum;
import com.bbw.god.game.combat.attackstrategy.service.StrategyRedisService;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author：lwb
 * @date: 2020/12/1 9:12
 * @version: 1.0
 */
@RequestMapping("gm/strategy")
@RestController
public class GMStrategyCtrl {
    @Autowired
    private AttackCityStrategyService attackCityStrategyService;
    @Autowired
    private StrategyRedisService strategyRedisService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private InsCityOwnDetailService ownDetailService;
    @RequestMapping("/repair")
    public Rst repair(int cityLv,int gid){
        System.err.println("开始修复-------");
        long times=System.currentTimeMillis();
        List<Integer> ids = CityTool.getAllCityIdByLevel(cityLv);
        for (Integer cityId:ids){
           doRepair(cityId,cityLv,gid);
        }
        System.err.println("总耗时："+(System.currentTimeMillis()-times));
        return Rst.businessOK();
    }
    @RequestMapping("/repairUserInfo")
    public Rst repairOld(){
        System.err.println("开始修复-------");
        EntityWrapper<AttackCityStrategyEntity> ew=new EntityWrapper<>();
        ew.isNull("seq");
        List<AttackCityStrategyEntity> list = attackCityStrategyService.selectList(ew);
        for (AttackCityStrategyEntity entity : list) {
            GameUser userData = gameUserService.getGameUser(entity.getUid());
            entity.setNickname(userData.getRoleInfo().getNickname());
            entity.setIcon(userData.getRoleInfo().getHeadIcon());
            entity.setHead(userData.getRoleInfo().getHead());
            EntityWrapper<InsCityOwnDetailEntity> ewOD=new EntityWrapper<>();
            ewOD.eq("city_name",entity.getCity()).eq("uid",entity.getUid());
            InsCityOwnDetailEntity insCityOwnDetailEntity = ownDetailService.selectOne(ewOD);
            if (insCityOwnDetailEntity!=null){
                entity.setSeq(insCityOwnDetailEntity.getCityLvNum());
            }else {
                entity.setSeq(1);
            }
        }
        attackCityStrategyService.updateBatchById(list,list.size());
        return Rst.businessOK();
    }

    /**
     * 刘少军 2020/11/26 14:13:42
     * 攻城攻略：
     * 四、五星城增加攻略按钮。点击后显示攻打此城池胜利的玩家及战斗视频列表。
     * 选择4场战斗：
     * 1. 回合数最少的
     * 2. 卡组最少的
     * 3. 使用道具回合数最少的
     * 4. 卡牌有修改技能且回合数最少的
     * 5. 召唤师等级最低
     * @param cityId
     */
    private void doRepair(int cityId,int lv,int gid){
        int count=15;
        if (lv==5){
            count=5;
        }
        Map<String, Object> params =new HashMap<>();
        params.put("limit","5");
        for (int seq=1;seq<=count;seq++){
            //  * 1. 回合数最少的
            EntityWrapper<AttackCityStrategyEntity> ew1=new EntityWrapper<>();
            ew1.eq("city_id",cityId).eq("seq",seq).in("gid","0,"+gid).orderBy("round",true);
            Page<AttackCityStrategyEntity> page=attackCityStrategyService.selectPage(new Query<AttackCityStrategyEntity>(params).getPage(),ew1);
            strategyRedisService.addAll(page.getRecords(),cityId,seq, StrategyEnum.ROUND_MIN,gid);
            //    * 2. 卡组最少的
            EntityWrapper<AttackCityStrategyEntity> ew2=new EntityWrapper<>();
            ew2.eq("city_id",cityId).eq("seq",seq).in("gid","0,"+gid).orderBy("cards",true);
            Page<AttackCityStrategyEntity> page2=attackCityStrategyService.selectPage(new Query<AttackCityStrategyEntity>(params).getPage(),ew2);
            strategyRedisService.addAll(page2.getRecords(),cityId,seq, StrategyEnum.CARDS_MIN,gid);
            //    3. 使用道具回合数最少的
            EntityWrapper<AttackCityStrategyEntity> ew3=new EntityWrapper<>();
            ew3.eq("city_id",cityId).eq("seq",seq).gt("use_weapons",0).in("gid","0,"+gid).orderBy("round",true);
            Page<AttackCityStrategyEntity> page3=attackCityStrategyService.selectPage(new Query<AttackCityStrategyEntity>(params).getPage(),ew3);
            strategyRedisService.addAll(page3.getRecords(),cityId,seq, StrategyEnum.USE_WEAPON_ROUND_MIN,gid);
            //  4. 卡牌有修改技能且回合数最少的
            EntityWrapper<AttackCityStrategyEntity> ew4=new EntityWrapper<>();
            ew4.eq("city_id",cityId).eq("seq",seq).gt("special_cards",0).in("gid","0,"+gid).orderBy("round",true);
            Page<AttackCityStrategyEntity> page4=attackCityStrategyService.selectPage(new Query<AttackCityStrategyEntity>(params).getPage(),ew4);
            strategyRedisService.addAll(page4.getRecords(),cityId,seq, StrategyEnum.SPECIAL_CARD_ROUND_MIN,gid);
            //  召唤师等级最低
            EntityWrapper<AttackCityStrategyEntity> ew5=new EntityWrapper<>();
            ew5.eq("city_id",cityId).eq("seq",seq).in("gid","0,"+gid).orderBy("lv",true);
            Page<AttackCityStrategyEntity> page5=attackCityStrategyService.selectPage(new Query<AttackCityStrategyEntity>(params).getPage(),ew5);
            strategyRedisService.addAll(page5.getRecords(),cityId,seq, StrategyEnum.USER_LV_MIN,gid);
        }
    }

}
