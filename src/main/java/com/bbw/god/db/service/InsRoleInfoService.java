package com.bbw.god.db.service;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.db.PageUtils;
import com.bbw.god.db.entity.InsRoleInfoEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 角色信息表
 *
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2019-04-02 10:25:53
 */
public interface InsRoleInfoService extends IService<InsRoleInfoEntity> {
    //
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 更新最后一次登录时间
     *
     * @param uid
     * @param lastDate
     * @return
     */
    int updateLastLoginDate(long uid, int lastDate);

    /**
     * 更新昵称
     *
     * @param uid
     * @param nickname
     * @return
     */
    int updateNickname(long uid, String nickname);

    /**
     * 更新等级
     *
     * @param uid
     * @param nickname
     * @return
     */
    int updateLevel(long uid, int level);

    /**
     * 区服ID
     *
     * @param uid
     * @param newSid
     * @return
     */
    int updateSid(int oldSid, int newSid);

    /**
     * 根据用户名获取当前登录区服的角色ID
     *
     * @param loginServerId
     * @param username
     * @return
     */
    Optional<InsRoleInfoEntity> getUidAtLoginServer(int loginServerId, String username);

    /**
     * 根据区服ID获取所有角色信息
     *
     * @param sid
     * @return
     */
    List<InsRoleInfoEntity> getByServer(int sid);

    /**
     * 获取区服所有用户
     *
     * @param sid
     * @return
     */
    List<Long> getAllUidsByServer(int sid);

    /**
     * 获取最后登录时间在指定区间的用户
     *
     * @param sid：区服ID
     * @param begin_date:      开始日期,包含
     * @param end_date：结束日期，包含
     * @return
     */
    public List<Long> getUidsLoginBetween(int sid, int begin_date, int end_date);

    List<Long> getUidsLoginBetween(int begin_date, int end_date);

    /**
     * 获取等级<=4，并在指定时间段内有登录段用户
     *
     * @param begin_date
     * @param end_date
     * @return
     */
    public List<Long> getUidsLevelLess4LoginBetween(int begin_date, int end_date);

    /**
     * 获取等级 >= 25，并在指定时间段内有登录段用户
     *
     * @param begin_date
     * @param end_date
     * @return
     */
    public List<Long> getUIdsLevelLarge25LoginBetween(int begin_date, int end_date);


    /**
     * 获取指定日期之后的登录用户
     *
     * @param sid
     * @param dateInt: 日期,包含
     * @return
     */
    public List<Long> getUidsLoginAfter(int sid, int dateInt);

    /**
     * 获取指定日期之后的登录用户
     *
     * @param sid
     * @param cid      渠道ID
     * @param dateInt: 日期,包含
     * @return
     */
    public List<Long> getUidsLoginAfter(int sid, int cid, int dateInt);

    /**
     * 更新累充金额
     *
     * @param pay
     */
    public void incPay(Long uid, int pay);

    /**
     * @Description 获取滚服数据
     * @Author suchaobin
     * @Date 2019/9/19 15:07
     * @Param username
     * @Return
     * @Exception
     */
    List<InsRoleInfoEntity> getRollingData(String username);

    /**
     * 根据账号查询数据
     *
     * @param username 账号
     * @return: java.util.List<com.bbw.god.db.service.InsRoleInfoService>
     * @author suchaobin
     * @date 2019/11/14 17:04
     **/
    List<InsRoleInfoEntity> getByUsername(String username);

    List<Long> getRechargeUids(int sid);
}