package com.bbw.god.gameuser.statistic.resource.nightmarecity;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatistic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 梦魇城池统计
 * @date 2020/9/15 10:27
 **/
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NightmareCityStatistic extends ResourceStatistic {
    private Integer oneStarCity = 0;
    private Integer twoStarCity = 0;
    private Integer threeStarCity = 0;
    private Integer fourStarCity = 0;
    private Integer fiveStarCity = 0;

    private Integer goldCountryCity = 0;
    private Integer woodCountryCity = 0;
    private Integer waterCountryCity = 0;
    private Integer fireCountryCity = 0;
    private Integer earthCountryCity = 0;

    public NightmareCityStatistic(Integer today, Integer total, Integer date, int type, Integer oneStarCity,
                                  Integer twoStarCity, Integer threeStarCity, Integer fourStarCity, Integer fiveStarCity,
                                  Integer goldCountryCity, Integer woodCountryCity, Integer waterCountryCity,
                                  Integer fireCountryCity, Integer earthCountryCity) {
        super(today, total, date, AwardEnum.NIGHTMARE_CITY, type);
        this.oneStarCity = oneStarCity;
        this.twoStarCity = twoStarCity;
        this.threeStarCity = threeStarCity;
        this.fourStarCity = fourStarCity;
        this.fiveStarCity = fiveStarCity;
        this.goldCountryCity = goldCountryCity;
        this.woodCountryCity = woodCountryCity;
        this.waterCountryCity = waterCountryCity;
        this.fireCountryCity = fireCountryCity;
        this.earthCountryCity = earthCountryCity;
    }

    public void addCity(int cityLevel, int cityCountry) {
        switch (cityLevel) {
            case 1:
                this.oneStarCity += 1;
                break;
            case 2:
                this.twoStarCity += 1;
                break;
            case 3:
                this.threeStarCity += 1;
                break;
            case 4:
                this.fourStarCity += 1;
                break;
            case 5:
                this.fiveStarCity += 1;
                break;
            default:
                break;
        }
        TypeEnum typeEnum = TypeEnum.fromValue(cityCountry);
        switch (typeEnum) {
            case Gold:
                this.goldCountryCity += 1;
                break;
            case Wood:
                this.woodCountryCity += 1;
                break;
            case Water:
                this.waterCountryCity += 1;
                break;
            case Fire:
                this.fireCountryCity += 1;
                break;
            case Earth:
                this.earthCountryCity += 1;
                break;
            default:
                break;
        }
        this.setToday(this.getToday() + 1);
        this.setTotal(this.getTotal() + 1);
    }
}
