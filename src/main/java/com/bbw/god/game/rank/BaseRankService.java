package com.bbw.god.game.rank;

import com.bbw.common.DateUtil;
import com.bbw.db.redis.RedisZSetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 排行榜基础类 目前排行类的都是分平台或者区服的，暂时没法将key写在一个方法中统一获取
 * @author lwb
 * @date 2020/4/10 16:21
 */
public abstract class BaseRankService<T> {
    @Autowired
    protected RedisZSetUtil<T> redisZSetUtil;
    /**
     * 是否是按值  正叙排列从小到大排序 ，否则的话从大到小排序
     * @return
     */
    public abstract boolean orderByVal();

    /**
     * 改变值
     * @param item
     * @param val
     * @return
     */
    public boolean incVal(String key,T item,int val){
        int nowVal=getValByKey(key,item);
        return redisZSetUtil.add(key,item,packVal(nowVal+val));
    }


    /**
     * 设置值
     * @param item
     * @param val
     * @return
     */
    public boolean setVal(String key,T item,int val){
        return redisZSetUtil.add(key,item,packVal(val));
    }

    /**
     * 设置完整的值
     * @param key
     * @param item
     * @param val
     * @return
     */
    public boolean setDoubleVal(String key,T item,double val){
        return redisZSetUtil.add(key,item,val);
    }
    /**
     * 根据key获取到排名
     * </br>排名的大小按值从大到小排序，越大的越靠前
     * @param item
     * @return
     */
    public int getRankByKey(String key,T item){
        Long rank=0l;
        if (orderByVal()){
            rank=redisZSetUtil.rank(key,item);
        }else {
            rank=redisZSetUtil.reverseRank(key,item);
        }
        if (rank!=null){
            return rank.intValue()+1;
        }
        return 0;
    }

    public void addKeyVals(String key,T[] keys,double[] socre){
        redisZSetUtil.add(key,socre,keys);
    }
    /**
     * 通过key获取值
     * @param item
     * @return
     */
    public int getValByKey(String key,T item){
        Double score=redisZSetUtil.score(key,item);
        return score.intValue();
    }
    /**
     * 通过排名获取值
     * @param rank
     * @return
     */
    public int getValByRank(String key,int rank){
        if (rank<1){
            return -1;
        }
        List<T> keys=getKeysByRank(key,rank,rank);
        if (keys==null || keys.isEmpty()){
            return -1;
        }
        Double score=redisZSetUtil.score(key,keys.get(0));
        if (score==null){
            return -1;
        }
        return score.intValue();
    }

    /**
     * 获取所有成员的key
     * @param key
     * @return
     */
    public List<T> getAllItemKeys(String key){
        int num=getCount(key);
        if (num<1){
            return new ArrayList<>();
        }
        return getKeysByRank(key,1,num);
    }
    /**
     * 根据指定名次获取key[begin,end],当取值超出实际排行人数时，只返回有效排名
     * @param begin
     * @param end
     * @return
     */
    public List<T> getKeysByRank(String key,int begin,int end){
        return getSetKeysByRank(key,begin,end).stream().collect(Collectors.toList());
    }

    /**
     * 根据指定名次获取key[begin,end],当取值超出实际排行人数时，只返回有效排名
     * @param begin
     * @param end
     * @return
     */
    public Set<T> getSetKeysByRank(String key,int begin,int end){
        begin=begin-1<0?0:begin-1;
        end=end-1<0?0:end-1;
        Set<T> keys=null;
        if (orderByVal()){
            keys=redisZSetUtil.range(key,begin,end);
        }else {
            keys=redisZSetUtil.reverseRange(key,begin,end);
        }
        if (keys==null || keys.isEmpty()){
            return new HashSet<>();
        }
        return keys;
    }

    /**
     * 根据指定名次获取key[begin,end],当取值超出实际排行人数时，只返回有效排名
     * 含key和score
     * @param key
     * @param begin
     * @param end
     * @return
     */
    public Set<ZSetOperations.TypedTuple<T>> getKeysValsByRank(String key,int begin,int end){
        begin=begin-1<0?0:begin-1;
        end=end-1<0?0:end-1;
        Set<ZSetOperations.TypedTuple<T>> keys=null;
        if (orderByVal()){
            keys=redisZSetUtil.rangeWithScores(key,begin,end);
        }else {
            keys=redisZSetUtil.reverseRangeWithScores(key,begin,end);
        }
        if (keys==null || keys.isEmpty()){
            return new HashSet<>();
        }
        return keys;
    }

    /**
     * 获取所有与指定实际值相同的项
     * @return
     */
    public List<T> getAllKeysByVal(String key,int val){
        Set<T> keys=null;
        if (orderByVal()){
            keys=redisZSetUtil.rangeByScore(key,val,val);
        }else {
            keys=redisZSetUtil.reverseRangeByScore(key,val,val);
        }
        if (keys==null||keys.isEmpty()){
            return new ArrayList<>();
        }
        return keys.stream().collect(Collectors.toList());
    }
    /**
     * 参与榜单中的数量
     * @return
     */
    public int getCount(String key){
        Long count=redisZSetUtil.size(key);
        return count.intValue();
    }
    /**
     * 包装值
     * @param val
     * @return
     */
    protected double packVal(int val){
        if (val<getConfigMinVal()){
            val=getConfigMinVal();
        }
        Double score=Double.valueOf(val+"."+rankSeed());
        return score.doubleValue();
    }

    /**
     * 最小值
     * @return
     */
    protected int getConfigMinVal(){
        return 0;
    }

    // 排行区分种子，用于区分相同数值的先后顺序
    // 2位天数+5位数秒钟
    // 默认为 （31-几号）+今日还剩多少秒（一天总共86400秒，所以为5位）；
    // 如4月10号00:00:01分存储了一个数字10，则实际存储的值为10.2186399;
    // 如4月10号00:00:02分存储了一个数字10，则实际存储的值为10.2186398;
    protected String rankSeed(){
        Long interval= DateUtil.getSecondsBetween(DateUtil.getDateBegin(new Date()),new Date());
        String seedTail=interval.toString();
        while (seedTail.length()<5){
            seedTail="0"+seedTail;
        }
        Integer day=31- DateUtil.getTodayInt()%10;
        String seedPre=day<10?"0"+day:day.toString();
        return seedPre+seedTail;
    }

    /**
     * 获取实际值中最大的伪值
     * @param val
     * @return
     */
    protected double randMaxSeed(int val){
        return Double.valueOf(val+"."+999999);
    }
    /**
     * 获取实际值中最小的伪值
     * @param val
     * @return
     */
    protected double randMinSeed(int val){
        return Double.valueOf(val+"."+000001);
    }


    public void delRank(String key){
        redisZSetUtil.remove(key);
    }
}
