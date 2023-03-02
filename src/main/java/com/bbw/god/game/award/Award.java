package com.bbw.god.game.award;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 奖励的数据结构
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-13 17:03
 */
@Data
public class Award implements Serializable {
    private static final long serialVersionUID = -7020464926324448451L;
    private Integer awardId = 0;// 指定的ID
    private int item;// 见AwardEnum
    private int num;// 数量
    private Integer star;// 卡牌星级,或者法宝星级
    private Integer isSpecial;// 是否包含1,2类卡，默认是0类卡
    private Integer isFhb;// 是否加入富豪榜
    private Integer week = 0;// 第几周的奖励
    private Integer isNotOwn = 0;// 是否未拥有
    private String strategy = "";// 策略
    private Date gainDate = null; //(可选)时间
    private Integer probability = null;//(可选)概率

    // 注意!!!空构造函数必须，否则json数据解析会报错
    public Award() {

    }

    public Award(AwardEnum item, int star, int isNotOwn, String strategy, int num) {
        this.item = item.getValue();
        this.star = star;
        this.isNotOwn = isNotOwn;
        this.strategy = strategy;
        this.num = num;
    }

    public Award(int awardId, AwardEnum item, int num) {
        this.awardId = awardId;
        this.item = item.getValue();
        this.num = num;
    }

    public Award(AwardEnum item, int num) {
        this.item = item.getValue();
        this.num = num;
    }

    public Award(AwardEnum item, int num, Integer star) {
        this.item = item.getValue();
        this.num = num;
        this.star = star;
    }

    /**
     *
     * @param awardLists  key为awardId value为数量
     * @param item
     * @return
     */
    public static List<Award> getAwards(Map<Integer, Integer> awardLists , int item) {
        List<Award> awardList = new ArrayList<>();
        for (Map.Entry awards : awardLists.entrySet()) {
            Award award = new Award();
            award.setAwardId((Integer) awards.getKey());
            award.setNum((Integer) awards.getValue());
            award.setItem(item);
            awardList.add(award);
        }
        return awardList;
    }

    public static Award instance(int awardId, AwardEnum item, int num){
        return new Award(awardId,item,num);
    }

    public int gainAwardId() {
        return this.awardId == null ? 0 : this.awardId;
    }

    public int gainStar() {
        return this.star == null ? 0 : this.star;
    }

    public int gainIsSpecial() {
        return this.isSpecial == null ? 0 : this.isSpecial;
    }

    public int gainIsFhb() {
        return this.isFhb == null ? 1 : this.isFhb;
    }

    public int gainWeek() {
        return this.week == null ? 0 : this.week;
    }

    public int gainIsNotOwn() {
        return this.isNotOwn == null ? 0 : this.isNotOwn;
    }

    /***
     * 是否相同
     * @return
     */
    public boolean ifEqual(Award award){
        if (award.getAwardId()!=0){
            if (award.getAwardId().equals(getAwardId()) && award.getItem()==getItem()){
                return true;
            }
            return false;
        }
        if (award.getItem()!=getItem()){
            return false;
        }
        if (!checkVal(award.getStar(),getStar())){
            return false;
        }
        if (!checkVal(award.getIsSpecial(),getIsSpecial())){
            return false;
        }

        if (!checkVal(award.getIsFhb(),getIsFhb())){
            return false;
        }
        if (!checkVal(award.getIsNotOwn(),getIsNotOwn())){
            return false;
        }
        return true;
    }

    private boolean checkVal(Integer val1,Integer val2){
        if (val1==null && val2==null){
            return true;
        }
        if (val1!=null && val2!=null){
            return val1.equals(val2);
        }
        return false;
    }
}
