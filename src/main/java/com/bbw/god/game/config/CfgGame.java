package com.bbw.god.game.config;

import com.bbw.common.ListUtil;
import com.bbw.common.Nicknames;
import lombok.Data;

import java.util.List;

/**
 * 游戏全局配置
 *
 * @author suhq
 * @date 2019年3月11日 下午9:51:33
 */
@Data
public class CfgGame implements CfgInterface {
    private String key;
    private String uacBaseUrl;// 用户中心地址
    private String versionConfigUrl;// 版本配置地址
    private Integer version;// 版本号
    // 测试白名单
    private List<String> whiteAccounts;
    // 管理接口白名单
    private List<String> gmWhiteIps;
    // 系统昵称集
    private List<String> nicknames;
    // 富甲第一个账号，用于开服初始化一个角色
    private String firstAccount;
    // 摇一次骰子需要的体力
    private Integer diceOneShake;
    // 体力最大值
    private Integer maxDice;
    // 体力增长上限基数
    private Integer baseDiceIncLimit;
    //可进多少天内的区服
    private Integer ableEnterServerInDays;
    /**
     * 最大游戏昵称长度
     */
    private Integer maxNicknameLength = 5;

    /**
     * 获取昵称集
     *
     * @return
     */
    public List<String> gainNicknames() {
        if (ListUtil.isEmpty(this.nicknames)) {
            this.nicknames = Nicknames.getNicknames();
        }
        return this.nicknames;
    }

    /**
     * 是否是白名单账号
     *
     * @param account 玩家对象
     * @return
     */
    public boolean isWhiteAccount(String account) {
        return this.whiteAccounts.contains(account);
    }

    @Override
    public String getId() {
        return this.key;
    }

    @Override
    public int getSortId() {
        return 1;
    }
}
