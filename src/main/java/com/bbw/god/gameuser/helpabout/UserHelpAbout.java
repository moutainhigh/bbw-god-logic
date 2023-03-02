package com.bbw.god.gameuser.helpabout;

import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户帮助阅读奖励  存储实体
 *
 * @author lwb
 * @version 1.0
 * @date 2019年4月10日
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UserHelpAbout extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer version = null;
    private List<Info> infos;

    @Data
    public static class Info implements Serializable {
        private static final long serialVersionUID = 1L;
        private int helpId;//帮助id
        private String title;//帮助标题
        private int type;//帮助分类
        private int show = 1;//是否显示
        private int status = 0;//奖励领取状态  0为可领取 1为已领取
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.HELP_ABOUT;
    }

    /**
     * 获取已经领取奖励的帮助id
     *
     * @return
     */
    public List<Integer> getAwardedHelpIds() {
        if (infos == null) {
            return new ArrayList<Integer>();
        }
        return infos.stream().filter(p -> p.getStatus() == UserHelpAboutService.TYPE_GONE).map(Info::getHelpId)
                .collect(Collectors.toList());
    }

    /**
     * 是否领取的该帮助奖励
     *
     * @param helpeId
     * @return
     */
    public boolean awarded(int helpId) {
        if (infos == null) {
            return false;
        }
        Optional<Info> optional = infos.stream().filter(p -> p.getHelpId() == helpId).findFirst();
        if (!optional.isPresent()) {
            return false;
        }
        return optional.get().getStatus() == UserHelpAboutService.TYPE_GONE;
    }

    public void gainAward(Info info) {
        if (infos == null) {
            infos = new ArrayList<Info>();
        }
        info.setStatus(UserHelpAboutService.TYPE_GONE);
        infos.add(info);
    }
}
