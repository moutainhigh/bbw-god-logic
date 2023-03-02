package com.bbw.god.gm;

import com.bbw.god.server.RoleVO;
import lombok.Data;

/**
 * 创建角色请求参数
 *
 * @author suhq
 * @date 2018年11月5日 下午4:1905
 */
@Data
public class GMRoleVO extends RoleVO {
    private Integer level = 1;
    private Long exp = 0L;
    private Long copper = 0L;
    private Integer gold = 0;
    private String eles;//金,木,水,火,土
    private String cardInfo;//101_10000_10_5_0;102_1000000_10_5_2;卡牌ID_经验_等级_阶数_灵石数
    private String cityInfo;//城池ID_阶数_府衙_矿场_钱庄_特产铺_聚贤庄_炼宝炉_道场_炼丹房
    private String treasureInfo;//10_10;20_3;法宝ID_法宝数量
}
