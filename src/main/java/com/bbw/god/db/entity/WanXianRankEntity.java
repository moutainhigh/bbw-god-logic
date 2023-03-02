package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.god.game.wanxianzhen.WanXianTool;
import lombok.Data;

/**
 * @author lwb
 * @date 2020/5/26 15:31
 */
@Data
@TableName("wanxian_rank")
public class WanXianRankEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long uid;
    private Integer qualifyingScore;
    private Integer qualifyingRank;
    private Integer groupScore=0;
    private String groupName="未晋级";
    private Integer groupRank=0;
    private Integer rank;
    private Integer season= WanXianTool.getThisSeason();
    private Integer gid;
    private Integer wxType;
}
