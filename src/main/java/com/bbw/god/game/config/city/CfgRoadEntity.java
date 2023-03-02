package com.bbw.god.game.config.city;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * way 前后左右 有路用1 ，没路0 1111 表示都有路
 *
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-11-27 21:57:48
 */
@Data
public class CfgRoadEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id; //
    private Integer y; //
    private Integer x; //
    private String way; //
    private Integer country; //

    @Override
    public int getSortId() {
        return this.getId();
    }

    /**
     * 获得城池
     *
     * @return
     */
    public CfgCityEntity getCity() {
        List<CfgCityEntity> cities = CityTool.getCities();
        for (int i = 0; i < cities.size(); i++) {
            CfgCityEntity city = cities.get(i);
            if (city.getAddress1().equals(this.id) || (city.getAddress2() != null && city.getAddress2().equals(this.id))) {
                return city;
            }
        }
        return null;

    }

    /**
     * 得到上一格
     *
     * @return
     */
    public Integer getUpId() {
        if (this.way.substring(0, 1).equals("1")) {
            return this.y * 100 + this.x + 1;
        }
        return null;
    }

    /**
     * 得到下一格
     *
     * @return
     */
    public Integer getDownId() {
        if (this.way.substring(3, 4).equals("1")) {
            return this.y * 100 + this.x - 1;
        }
        return null;
    }

    /**
     * 得到左一格
     *
     * @return
     */
    public Integer getLeftId() {
        if (this.way.substring(1, 2).equals("1")) {
            return (this.y + 1) * 100 + this.x;
        }

        return null;
    }

    /**
     * 得到右一格
     *
     * @return
     */
    public Integer getRightId() {
        if (this.way.substring(2, 3).equals("1")) {
            return (this.y - 1) * 100 + this.x;
        }

        return null;
    }

    /**
     * 掉落到一格时 随机产生一个方向 比如玩家刚注册时， 或者使用（山河社稷图：到全地图任意地点） *
     *
     * @return
     */
    public Integer getDirectionByRandom() {
        while (true) {
            int random = PowerRandom.getRandomBySeed(4);
            if (this.way.substring(random - 1, random).equals("1")) {
                return random;
            }
        }
    }

    /**
     * 走路的时候获得下一个方向
     *
     * @return
     * @lastDirection 上一格相对本格的方向 1，2，3，4 表示上左右下
     */
    public Integer getNextDirection(int lastDirection, int excludeDir) {
        int tmp = 1000;
        int random = 0;
        while (tmp > 0) {
            random = PowerRandom.getRandomBySeed(4);
            if (this.way.substring(random - 1, random).equals("1")) {
                if (!isCross() && (random + lastDirection != 5)) return random;
                if (isCross() && (random + lastDirection != 5) && random != excludeDir) return random;
            }
            tmp--;
        }
        random = lastDirection;
        if (!ifHasRoadByDirection(random)) {
            random = getDirectionByRandom();
        }
        // System.out.println("the direction for avoiding repeat forver " + random);
        return random;
    }

    public Integer getNextDirection(int guLevel, int lastDirection, int excludeDir) {
        int nextDirection = getNextDirection(lastDirection, excludeDir);
        if (guLevel < 6) {// 新手优先离开出生区域
            if (this.id == 1939 || this.id == 1933) {
                nextDirection = 4;
            }
        }
        return nextDirection;
    }

    /**
     * 根据方向 得到下一格
     *
     * @return
     * @lastDirection 上一格相对本格的方向 1，2，3，4 表示上左右下
     */
    public Integer getCellByNextDirection(int nextDirection) {
        if (nextDirection == 1) return getUpId();
        else if (nextDirection == 2) return getLeftId();
        else if (nextDirection == 3) return getRightId();
        else if (nextDirection == 4) return getDownId();
        else return null;
    }

    /**
     * 是否交叉路
     *
     * @return
     */
    public boolean isCross() {
        if (this.way.replace("0", "").equals("11")) {
            return false;
        }
        return true;
    }

    /**
     * 此方向是否有路
     *
     * @return
     */
    public boolean ifHasRoadByDirection(int direction) {

        if (this.way.substring(direction - 1, direction).equals("1")) {
            return true;
        }
        return false;

    }
}
