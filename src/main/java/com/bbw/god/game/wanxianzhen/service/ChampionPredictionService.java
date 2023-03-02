package com.bbw.god.game.wanxianzhen.service;

import com.bbw.common.JSONUtil;
import com.bbw.common.LM;
import com.bbw.common.StrUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.wanxianzhen.RDWanXian;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.mc.mail.MailAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 冠军预测
 * @author lwb
 * @date 2020/5/19 15:39
 */
@Service
@Slf4j
public class ChampionPredictionService {

    @Autowired
    private WanXianSeasonService wanXianSeasonService;
    @Autowired
    private WanXianScoreRankService wanXianScoreRankService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private MailService mailService;
    @Autowired
    private WanXianLogic wanXianLogic;

    /**
     * 获取冠军预测列表
     * @param uid
     * @param type
     * @param rd
     * @return
     */
    public RDWanXian getChampionPredictionPage(long uid,int type,RDWanXian rd){
        int gid=gameUserService.getActiveGid(uid);
        int cpType=4;
        if (rd.getWxType()==68){
            cpType=8;
        }
        List<Integer> bets=getBetList(uid,type,cpType);
        List<RDWanXian.RDUser> users=getCPUserList(gid,type,cpType);
        for (RDWanXian.RDUser user:users){
            wanXianLogic.getUserInfo(user);
            user.setBet(bets.get(user.getOrder()-1));
        }
        rd.setBetList(users);
        return rd;
    }

    /**
     * 获取玩家的下注情况，每个被下注的选手有一个编号，8强为1~8 4强为1~4
     * 对应的玩家报名参数保存方式为 1为压了1号  10为压了2号  100为压了3号  1001 为  压了1号、4号
     *
     * @param uid
     * @param type
     * @param cpType
     * @return
     */
    public List<Integer> getBetList(long uid,int type,int cpType){
        int gid=gameUserService.getActiveGid(uid);
        int val=wanXianScoreRankService.getValByKey(wanXianScoreRankService.getChampionPredictionKey(gid,type,cpType),uid);
        List<Integer> list= Arrays.asList(0,0,0,0,0,0,0,0);
        if (val<=0){
            return list;
        }
        for (int i=0;i<8;i++){
           int bet=val%10;
           list.set(i,bet);
           val=val/10;
           if (val==0){
               break;
           }
        }
        return list;
    }

    /**
     * 预测冠军
     * @param uid  玩家ID
     * @param raceType  几强
     * @param type 赛事类型
     * @param users 下注编号
     * @return
     */
    public RDCommon championPrediction(long uid,int raceType,int type, String users){
        RDCommon rd=new RDCommon();
        List<Integer> userOrders=StrUtil.toList(users,",");
        int need=userOrders.size();
        if (need<=0){
            //没有预测对象
            return rd;
        }
        TreasureChecker.checkIsEnough(TreasureEnum.XZY.getValue(),need,uid);
        TreasureEventPublisher.pubTDeductEvent(uid,TreasureEnum.XZY.getValue(),need, WayEnum.WANXIAN_CHAMPOIN_PREDICTION,rd);
        int cpType=4;
        if (raceType==68){
            cpType=8;
        }
        int gid=gameUserService.getActiveGid(uid);
        int val=wanXianScoreRankService.getValByKey(wanXianScoreRankService.getChampionPredictionKey(gid,type,cpType),uid);
        for (Integer order:userOrders){
            val+=Math.pow(10,(order-1));
        }
        wanXianScoreRankService.updateVal(wanXianScoreRankService.getChampionPredictionKey(gid,type,cpType),uid,val);
        return rd;
    }

    /**
     * 获取选手信息
     * @param gid
     * @param type
     * @param cpType
     * @return
     */
    public List<RDWanXian.RDUser> getCPUserList(int gid,int type,int cpType){
        String item="groupStage_cp_"+cpType;
        String jsonArray=wanXianSeasonService.getVal(gid,type,item);
        List<RDWanXian.RDUser> users=JSONUtil.fromJsonArray(jsonArray, RDWanXian.RDUser.class);
        if (users==null){
            return new ArrayList<>();
        }
        return users;
    }

    /**
     * 获取预测界面中的 选手 列表，里面包含 玩家下注状态
     * @param uid
     * @param type
     * @param cpType
     * @return
     */
    public List<RDWanXian.RDUser> getChampionPredictionList(long uid,int type,int cpType){
        List<Integer> bets=getBetList(uid,type,cpType);
        int gid=gameUserService.getActiveGid(uid);
        List<RDWanXian.RDUser> users=getCPUserList(gid,type,cpType);
        List<RDWanXian.RDUser> rdUsers=new ArrayList<>();
        for (RDWanXian.RDUser user:users){
            if (bets.get(user.getOrder()-1)==1){
                wanXianLogic.getUserInfo(user);
                rdUsers.add(user);
            }
            if(user.getStatus()==null){
                user.setStatus(0);
            }
        }
        return rdUsers;
    }

    /**
     * 淘汰选手
     * @param gid
     * @param type
     * @param eliminateUids 被淘汰的选手id
     */
    public void eliminate(int gid, int type,List<Long> eliminateUids){
        int[] types={4,8};
        for (int cpType:types){
            List<RDWanXian.RDUser> cps=getCPUserList(gid,type,cpType);
            if (cps==null || cps.isEmpty()){
                continue;
            }
            for (RDWanXian.RDUser user:cps){
                if (eliminateUids.contains(user.getUid())){
                    //玩家已淘汰  设置状态为已淘汰
                    user.setStatus(-1);
                }
            }
            //更新选手押注的 淘汰信息
            wanXianSeasonService.addVal(gid,type,"groupStage_cp_"+cpType,JSONUtil.toJson(cps));
        }
    }

    /**
     * 设置获胜者
     * @param gid
     * @param type 类型
     * @param uid 获胜者ID
     */
    public void setWinner(int gid,int type,long uid){
        int[] types={8,4};
        for (int cpType:types){
            List<RDWanXian.RDUser> cps=getCPUserList(gid,type,cpType);
            RDWanXian.RDUser winner=null;
            for (RDWanXian.RDUser user:cps){
                if (uid!=user.getUid()){
                    //将所有不是获胜者ID的玩家设置为淘汰状态
                    user.setStatus(-1);
                }else {
                    //并将获胜者 设置为 胜利状态
                    user.setStatus(1);
                    winner=user;
                }
            }
            //将上述变动更新到redis
            wanXianSeasonService.addVal(gid,type,"groupStage_cp_"+cpType,JSONUtil.toJson(cps));
            if (winner!=null){
                //发放押注奖励
                sendAward(gid,type,cpType,winner);
            }
        }
    }

    /**
     * 发放中奖人的奖励
     * @param gid
     * @param type
     * @param cpType
     * @param winner
     */
    private void sendAward(int gid,int type,int cpType,RDWanXian.RDUser winner){
        int count=wanXianScoreRankService.getCount(wanXianScoreRankService.getChampionPredictionKey(gid,type,cpType));
        int begin=1;
        Set<ZSetOperations.TypedTuple<Long>> keysVals=wanXianScoreRankService.getKeysValsByRank(wanXianScoreRankService.getChampionPredictionKey(gid,type,cpType),begin,count);
        Iterator<ZSetOperations.TypedTuple<Long>> iterator=keysVals.iterator();
        Set<Long> uids=new HashSet<>();
        int order=winner.getOrder();//获胜者编号1~8
        //获取获胜编号对应的数字，1号为1,2号为10,3号为100,4号为1000,5号为10000,6号为100000,7号为1000000,8号为10000000
        int seed=(int)Math.pow(10,order-1);
        while (iterator.hasNext()){
            ZSetOperations.TypedTuple<Long> item=iterator.next();
            //原始下注信息 如玩家只下注1号则 为1，仅下注3号为100，下注了2号和4号 则为1010
            int val=item.getScore().intValue();
            //是否压中  1为压中；
            //例：玩家下注的有1号5号，6号 则值为110001，中奖的为5号=》seed值为10000，110001/10000=11=》取模11%10=》1，即压中
            //例：玩家下注的为3号 则值为1000，中奖的为5号=》seed值为10000，1000/10000=0=》取模0%10=》0，即不中
            int bet=val/seed%10;
            if (bet==1){
                //对的
                uids.add(item.getValue());
            }
        }
        String title= LM.I.getMsg("mail.wanXian.conventional.predict.title");
        if (WanXianLogic.TYPE_SPECIAL_RACE==type){
            title= LM.I.getMsg("mail.wanXian.special.predict.title");
        }
        String content=LM.I.getFormatMsg("mail.wanXian.predict.content",cpType);
        List<Award> awards=new ArrayList<>();
        awards.add(new Award(TreasureEnum.XZY.getValue(),AwardEnum.FB,winner.getMultiple()));
        mailService.sendAwardMail(title,content,uids,awards);
        log.info("平台："+gid+";"+cpType+"强"+title+"中奖的鲜之源："+winner.getMultiple());
        log.info("人数："+uids.size());
        log.info("名单为："+JSONUtil.toJson(uids));

    }
}
