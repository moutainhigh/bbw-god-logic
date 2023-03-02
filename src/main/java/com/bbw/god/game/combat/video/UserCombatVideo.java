package com.bbw.god.game.combat.video;

import com.bbw.common.ID;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年12月17日 上午10:05:19
 * 类说明
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class UserCombatVideo extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<CombatData> videos;

    public UserCombatVideo(long uid) {
        this.id = ID.INSTANCE.nextId();
        this.gameUserId = uid;
        this.videos = new ArrayList<UserCombatVideo.CombatData>();
    }

    @Data
    public static class CombatData implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long dataId;
        private Integer fightType;
        private Integer winner;//胜利方1或2 对应P1,P2
        private VideoPlayer p1;
        private VideoPlayer P2;
        private String url;
        private String datetime;

        public static CombatData instance(CombatVideo video, Combat combat) {
            CombatData data = new CombatData();
            data.setDataId(combat.getId());
            data.setP1(VideoPlayer.instance(combat.getFirstPlayer()));
            data.setP2(VideoPlayer.instance(combat.getSecondPlayer()));
            data.setDatetime(video.getDatetime());
            data.setFightType(combat.getFightType().getValue());
            if (combat.getFirstPlayer().getId().getValue()==combat.getWinnerId()){
                data.setWinner(1);
            }else {
                data.setWinner(2);
            }
            return data;
        }

    }

    @Data
    public static class VideoPlayer {
        private Integer lv;
        private Integer head;
        private Integer icon;
        private String nickname;

        public static VideoPlayer instance(Player player) {
            VideoPlayer p = new VideoPlayer();
            p.setHead(player.getImgId());
            p.setIcon(player.getIconId());
            p.setLv(player.getLv());
            p.setNickname(player.getName());
            return p;
        }
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.CombatVideo;
    }
}
