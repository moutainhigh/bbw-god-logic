package com.bbw.god.city.chengc.in.building;

import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.game.config.city.BuildingEnum;

public enum BuildingFactory {
    I;

    /**
     * 获得建筑实例
     *
     * @param type
     * @return
     */
    public Building create(UserCity userCity, BuildingEnum type) {
        if (type == BuildingEnum.DC) {
            return new DaoC(userCity);
        }

        if (type == BuildingEnum.FY) {
            return new FuY(userCity);
        }

        if (type == BuildingEnum.JXZ) {
            return new JuXZ(userCity);
        }

        if (type == BuildingEnum.KC) {
            return new KuangC(userCity);
        }

        if (type == BuildingEnum.LBL) {
            return new LianBL(userCity);
        }

        if (type == BuildingEnum.LDF) {
            return new LianDF(userCity);
        }

        if (type == BuildingEnum.QZ) {
            return new QianZ(userCity);
        }

        if (type == BuildingEnum.TCP) {
            return new TeCP(userCity);
        }

        if (type == BuildingEnum.FT) {
            return new FaTan(userCity);
        }
        return null;
    }

}
