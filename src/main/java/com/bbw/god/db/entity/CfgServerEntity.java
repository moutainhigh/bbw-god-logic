package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.bbw.common.DateUtil;
import com.bbw.god.game.config.CfgEntityInterface;
import com.bbw.god.server.ServerStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 区服列表
 *
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-11-27 21:57:48
 */
@Data
@TableName("cfg_server")
public class CfgServerEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    @TableId
    private Integer id; // 原始区服ID
    private Integer mergeSid; // 合服后区服ID
    private Integer groupId; // 服务器群组ID
    private String shortName; // 名称
    private String name; // 名称
    // private String ip; // 服务器ip
    // private Integer port; // 服务器端口
    // private String extension; // 应用扩展名
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime; // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date beginTime; // 开服时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date mtBeginTime; // 开始维护时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date mtEndTime; // 结束维护时间
    // private Date closeTime; // 关闭时间
    // private Integer status; // 未开启0，运行中20
    // private Boolean ifRecomended; // 是否推荐标志
    // private Integer onlineNum; // 在线人数
    private String connString; //
    private String redisDs; //Redis数据源
    private String memo; //
    // private Integer statement = 20; // 10新服、20流畅、30饱和

    public int getMergeSid() {
        return mergeSid.intValue();
    }

    public ServerStatus getServerStatus() {
        Date now = DateUtil.now();

        if (now.before(beginTime)) {
            return ServerStatus.PREDICTING;
        }
        if (mtBeginTime != null && mtEndTime != null && now.after(mtBeginTime) && now.before(mtEndTime)) {
            return ServerStatus.MAINTAINING;
        }
        return ServerStatus.RUNNING;
    }

    /**
     * 服务器是否为开发测试
     *
     * @return
     */
    public boolean isDevTest() {
        return null != memo && "开发测试".equals(memo.trim());
    }

    @Override
    public int getSortId() {
        return this.getId();
    }
}
