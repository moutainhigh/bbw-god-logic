package com.bbw.god.gameuser.unique;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 诛仙阵相关
 *
 * @author suhq 2018年9月30日 下午1:50:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Deprecated
public class UserZxz extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_GUARDIANS = "0,0;0,0;0,0;0,0;0,0;0,0";
    private Integer points = 0;
    private Integer level = 0;
    private Integer status = 0;
    private String guardians = null;// -1战败 0未挑战 1战神
    private Date begindDate = null;

    public static UserZxz instance(long guId, int level, String guardians) {
        UserZxz userZxz = new UserZxz();
        userZxz.setId(ID.INSTANCE.nextId());
        userZxz.setGameUserId(guId);
        userZxz.setGuardians(guardians);
        userZxz.setBegindDate(DateUtil.now());
        return userZxz;
    }

    public static UserZxz instance(long guId) {
        UserZxz userZxz = new UserZxz();
        userZxz.setId(ID.INSTANCE.nextId());
        userZxz.setGameUserId(guId);
        return userZxz;
    }

//	public void addPoint(int point) {
//		this.points += point;
//	}
//
//	public void deductPoint(int point) {
//		this.points -= point;
//	}

    /**
     * 阵位是否有对手
     *
     * @return
     */
    public boolean ifGuardiansValid() {
        return guardians != null && !guardians.equals(DEFAULT_GUARDIANS);
    }

    /**
     * 更新阵位的状态
     *
     * @param opponentId
     * @param status
     */
    public void updateGuardians(long opponentId, int status) {
        String[] guardianArray = guardians.split(";");
        String newZxzGuardians = Stream.of(guardianArray).map(guardian -> {
            String newGuardian = guardian;
            String[] guardianInfo = guardian.split(",");
            if (Long.valueOf(guardianInfo[0]) == opponentId) {
                newGuardian = guardianInfo[0] + "," + status;
            }
            return newGuardian;
        }).collect(Collectors.joining(";"));
        guardians = newZxzGuardians;
    }

    /**
     * 获得阵位上的玩家ID
     *
     * @return
     */
    public List<Long> gainZxzGuIds() {
        String[] guardianArray = guardians.split(";");
        return Stream.of(guardianArray).map(guardian -> {
            String[] guardianInfo = guardian.split(",");
            return Long.valueOf(guardianInfo[0]);
        }).collect(Collectors.toList());
    }

    /**
     * 诛仙阵是否通关
     *
     * @return
     */
    public boolean ifPassZxz() {
        return !guardians.contains(",0");
    }

    /**
     * 诛仙阵是否失败
     *
     * @return
     */
    public boolean ifFailure() {
        return guardians.contains(",-1");
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.ZXZ;
    }
}
