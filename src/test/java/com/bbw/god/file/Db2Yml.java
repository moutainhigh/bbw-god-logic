package com.bbw.god.file;

import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.exchangegood.CfgExchangeGoodEntity;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.FavorableBagEnum;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class Db2Yml {
    private static String GAME_CONFIG_PATH = "/Users/suhq/Desktop/eclipse-workspace/bbw-god-game-main/src/main/resources/config/game/";

    @Test
    public void toYml() throws IOException {
        Path path = Paths.get("/Users/suhq/Desktop/富甲封神传/开发设计/美国ip地址段.txt");
        List<String> ipRanges = Files.readAllLines(path);
        ipRanges.forEach(tmp -> System.out.println("- " + tmp));
        // loadYeD("city/野地.yml");
        // loadYeG("city/野怪.yml");
        // loadCardSkills("card/卡牌技能.yml");
        // loadTasks("task/任务.yml");
        // loadMalls("mall/所有商城物品.yml");
        // List<CfgRoadEntity> cfgRoads = Cfg.I.get(CfgRoadEntity.class);
        // System.out.println(cfgRoads.toString());
        // for (int i = 0; i < cfgRoads.size(); i++) {
        // CfgRoadEntity cfgRoad = Cfg.I.get(cfgRoads.get(i).getId(), CfgRoadEntity.class);
        // System.out.println(cfgRoad.toString());
        // }

        // System.out.println("to yml succssfully");
    }

    private void loadMalls(String fileName) throws IOException {
        dbToYml(fileName, (path) -> {
            try {
                StringBuilder sBuilder = new StringBuilder();
                // sBuilder.append("!!" + classPath + "\n");
                sBuilder.append("#商城物品\n\n");
                sBuilder.append("key: 唯一\n");
                sBuilder.append("malls: \n");
                System.out.println("添加文本：" + sBuilder.toString());
                Files.write(path, sBuilder.toString().getBytes(), StandardOpenOption.APPEND);

                List<CfgMallEntity> entities = Cfg.I.get(CfgMallEntity.class);
                entities.sort(Comparator.comparing(CfgMallEntity::getType));
                for (CfgMallEntity tmp : entities) {
                    sBuilder = new StringBuilder();
                    sBuilder.append("- {");
                    sBuilder.append("id: " + tmp.getId());
                    sBuilder.append(",goodId: " + tmp.getGoodsId());
                    String name = "";
                    if (tmp.getType() > 30) {
                        name = FavorableBagEnum.fromValue(tmp.getGoodsId()).getName();
                    } else {
                        CfgTreasureEntity treasureEntity = TreasureTool.getTreasureById(tmp.getGoodsId());
                        name = treasureEntity.getName();
                    }
                    sBuilder.append(",name: " + name);
                    sBuilder.append(",type: " + tmp.getType());
                    sBuilder.append(",serial: " + tmp.getSerial());
                    sBuilder.append(",unit: " + tmp.getUnit());
                    sBuilder.append(",price: " + tmp.getPrice());
                    sBuilder.append(",limit: " + tmp.getLimit());
                    sBuilder.append(",peroid: " + tmp.getPeroid());
                    sBuilder.append(",status: " + tmp.getStatus());

                    sBuilder.append("}\n");

                    String strToAppend = sBuilder.toString();
                    System.out.println("添加文本：" + strToAppend);
                    Files.write(path, strToAppend.getBytes(), StandardOpenOption.APPEND);
                }
            } catch (Exception e) {
                System.out.println("fail as writing " + fileName);
            }

        });
    }

    private void loadExchangeGoods(String fileName) throws IOException {
        dbToYml(fileName, (path) -> {
            try {
                StringBuilder sBuilder = new StringBuilder();
                // sBuilder.append("!!" + classPath + "\n");
                sBuilder.append("#兑换品\n\n");
                sBuilder.append("key: 唯一\n");
                sBuilder.append("exchangeGoods: \n");
                System.out.println("添加文本：" + sBuilder.toString());
                Files.write(path, sBuilder.toString().getBytes(), StandardOpenOption.APPEND);

                List<CfgExchangeGoodEntity> entities = Cfg.I.get(CfgExchangeGoodEntity.class);
                entities.sort(Comparator.comparing(CfgExchangeGoodEntity::getWay));
                for (CfgExchangeGoodEntity tmp : entities) {
                    sBuilder = new StringBuilder();
                    sBuilder.append("- {");
                    sBuilder.append("id: " + tmp.getId());
                    sBuilder.append(",name: " + tmp.getName());
                    sBuilder.append(",serial: " + tmp.getSerial());
                    sBuilder.append(",type: " + tmp.getType());
                    if (tmp.getGoodId() != null) {
                        sBuilder.append(",goodId: " + tmp.getGoodId());
                    }

                    sBuilder.append(",num: " + tmp.getNum());
                    sBuilder.append(",unit: " + tmp.getUnit());

                    sBuilder.append(",price: " + tmp.getPrice());
                    sBuilder.append(",way: " + tmp.getWay());
                    sBuilder.append(",isValid: " + tmp.getIsValid());

                    sBuilder.append("}\n");

                    String strToAppend = sBuilder.toString();
                    System.out.println("添加文本：" + strToAppend);
                    Files.write(path, strToAppend.getBytes(), StandardOpenOption.APPEND);
                }
            } catch (Exception e) {
                System.out.println("fail as writing " + fileName);
            }

        });
    }

    private void loadSpecials(String fileName) throws IOException {
        dbToYml(fileName, (path) -> {
            try {
                StringBuilder sBuilder = new StringBuilder();
                // sBuilder.append("!!" + classPath + "\n");
                sBuilder.append("#特产\n\n");
                sBuilder.append("key: 唯一\n");
                sBuilder.append("specials: \n");
                System.out.println("添加文本：" + sBuilder.toString());
                Files.write(path, sBuilder.toString().getBytes(), StandardOpenOption.APPEND);

                List<CfgSpecialEntity> entities = Cfg.I.get(CfgSpecialEntity.class);
                for (CfgSpecialEntity tmp : entities) {
                    sBuilder = new StringBuilder();
                    sBuilder.append("- {");
                    sBuilder.append("id: " + tmp.getId());
                    sBuilder.append(",name: " + tmp.getName());
                    sBuilder.append(",type: " + tmp.getType());
                    sBuilder.append(",price: " + tmp.getPrice());
                    sBuilder.append(",country: " + tmp.getCountry());
                    sBuilder.append(",sellingCities: \"" + tmp.getSellingCities() + "\"");

                    sBuilder.append("}\n");

                    String strToAppend = sBuilder.toString();
                    System.out.println("添加文本：" + strToAppend);
                    Files.write(path, strToAppend.getBytes(), StandardOpenOption.APPEND);
                }
            } catch (Exception e) {
                System.out.println("fail as writing " + fileName);
            }

        });
    }

    private void loadTreasures(String fileName) throws IOException {
        dbToYml(fileName, (path) -> {
            try {
                StringBuilder sBuilder = new StringBuilder();
                // sBuilder.append("!!" + classPath + "\n");
                sBuilder.append("#法宝\n\n");
                sBuilder.append("key: 唯一\n");
                sBuilder.append("treasures: \n");
                System.out.println("添加文本：" + sBuilder.toString());
                Files.write(path, sBuilder.toString().getBytes(), StandardOpenOption.APPEND);

                List<CfgTreasureEntity> entities = Cfg.I.get(CfgTreasureEntity.class);
                for (CfgTreasureEntity tmp : entities) {
                    sBuilder = new StringBuilder();
                    sBuilder.append("- {");
                    sBuilder.append("id: " + tmp.getId());
                    sBuilder.append(",name: " + tmp.getName());
                    sBuilder.append(",type: " + tmp.getType());
                    sBuilder.append(",star: " + tmp.getStar());
                    sBuilder.append(",memo: \"" + tmp.getMemo() + "\"");
                    sBuilder.append("}\n");

                    String strToAppend = sBuilder.toString();
                    System.out.println("添加文本：" + strToAppend);
                    Files.write(path, strToAppend.getBytes(), StandardOpenOption.APPEND);
                }
            } catch (Exception e) {
                System.out.println("fail as writing " + fileName);
            }

        });
    }

    // private void loadRoads(String fileName) throws IOException {
    // dbToYml(fileName, (path) -> {
    // try {
    // StringBuilder sBuilder = new StringBuilder();
    // // sBuilder.append("!!" + classPath + "\n");
    // sBuilder.append("#地图格子\n\n");
    // sBuilder.append("key: 唯一\n");
    // sBuilder.append("roads: \n");
    // System.out.println("添加文本：" + sBuilder.toString());
    // Files.write(path, sBuilder.toString().getBytes(), StandardOpenOption.APPEND);
    //
    // List<CfgRoadEntity> entities = Cfg.I.get(CfgRoadEntity.class);
    // for (CfgRoadEntity tmp : entities) {
    // sBuilder = new StringBuilder();
    // sBuilder.append("- {");
    // sBuilder.append("id: " + tmp.getId());
    // sBuilder.append(",y: " + tmp.getY());
    // sBuilder.append(",x: " + tmp.getX());
    // sBuilder.append(",way: " + tmp.getWay());
    // sBuilder.append(",country: " + tmp.getCountry());
    // sBuilder.append("}\n");
    //
    // String strToAppend = sBuilder.toString();
    // System.out.println("添加文本：" + strToAppend);
    // Files.write(path, strToAppend.getBytes(), StandardOpenOption.APPEND);
    // }
    // } catch (Exception e) {
    // System.out.println("fail as writing " + fileName);
    // }
    //
    // });
    // }

    // private void loadCities(String fileName) throws IOException {
    // dbToYml(fileName, (path) -> {
    // try {
    // StringBuilder sBuilder = new StringBuilder();
    // // sBuilder.append("!!" + classPath + "\n");
    // sBuilder.append("#地图城市\n\n");
    // sBuilder.append("key: 唯一\n");
    // sBuilder.append("cities: \n");
    // System.out.println("添加文本：" + sBuilder.toString());
    // Files.write(path, sBuilder.toString().getBytes(), StandardOpenOption.APPEND);
    //
    // List<CfgCityEntity> entities = Cfg.I.get(CfgCityEntity.class);
    // for (CfgCityEntity tmp : entities) {
    // sBuilder = new StringBuilder();
    // sBuilder.append("- {");
    // sBuilder.append("id: " + tmp.getId());
    // sBuilder.append(",name: " + tmp.getName());
    // sBuilder.append(",address1: " + tmp.getAddress1());
    // sBuilder.append(",address2: " + (tmp.getAddress2() == null ? 0 : tmp.getAddress2()));
    // sBuilder.append(",type: " + tmp.getType());
    // sBuilder.append(",property: " + (tmp.getProperty() == null ? 0 : tmp.getProperty()));
    // sBuilder.append("}\n");
    //
    // String strToAppend = sBuilder.toString();
    // System.out.println("添加文本：" + strToAppend);
    // Files.write(path, strToAppend.getBytes(), StandardOpenOption.APPEND);
    // }
    // } catch (Exception e) {
    // System.out.println("fail as writing " + fileName);
    // }
    //
    // });
    // }

    // private void loadCardSkills(String fileName) throws IOException {
    // dbToYml(fileName, (path) -> {
    // try {
    // StringBuilder sBuilder = new StringBuilder();
    // // sBuilder.append("!!" + classPath + "\n");
    // sBuilder.append("#卡牌技能\n\n");
    // sBuilder.append("key: 唯一\n");
    // sBuilder.append("cardSkills: \n");
    // System.out.println("添加文本：" + sBuilder.toString());
    // Files.write(path, sBuilder.toString().getBytes(), StandardOpenOption.APPEND);
    //
    // List<CfgCardSkillEntity> entities = Cfg.I.get(CfgCardSkillEntity.class);
    // for (CfgCardSkillEntity tmp : entities) {
    // sBuilder = new StringBuilder();
    // sBuilder.append("- {");
    // sBuilder.append("id: " + tmp.getId());
    // sBuilder.append(",name: " + tmp.getName());
    // // sBuilder.append(",namePinyin: " + tmp.getNamePinyin());
    // // sBuilder.append(",serial: " + tmp.getSerial());
    // sBuilder.append(",value: " + tmp.getValue());
    // sBuilder.append(",memo: \"" + tmp.getMemo() + "\"");
    // sBuilder.append("}\n");
    //
    // String strToAppend = sBuilder.toString();
    // System.out.println("添加文本：" + strToAppend);
    // Files.write(path, strToAppend.getBytes(), StandardOpenOption.APPEND);
    // }
    // } catch (Exception e) {
    // System.out.println("fail as writing " + fileName);
    // }
    //
    // });
    // }

    // private void loadCardGroups(String fileName) throws IOException {
    // dbToYml(fileName, (path) -> {
    // try {
    // StringBuilder sBuilder = new StringBuilder();
    // // sBuilder.append("!!" + classPath + "\n");
    // sBuilder.append("#卡牌组合\n\n");
    // sBuilder.append("key: 唯一\n");
    // sBuilder.append("cardGroups: \n");
    // System.out.println("添加文本：" + sBuilder.toString());
    // Files.write(path, sBuilder.toString().getBytes(), StandardOpenOption.APPEND);
    //
    // List<CfgCardGroupEntity> entities = Cfg.I.get(CfgCardGroupEntity.class);
    // for (CfgCardGroupEntity tmp : entities) {
    // sBuilder = new StringBuilder();
    // sBuilder.append("- {");
    // sBuilder.append("id: " + tmp.getId());
    // sBuilder.append(",name: " + tmp.getName());
    // sBuilder.append(",shortName: " + tmp.getShortName());
    // sBuilder.append(",memo: \"" + tmp.getMemo() + "\"");
    // sBuilder.append("}\n");
    //
    // String strToAppend = sBuilder.toString();
    // System.out.println("添加文本：" + strToAppend);
    // Files.write(path, strToAppend.getBytes(), StandardOpenOption.APPEND);
    // }
    // } catch (Exception e) {
    // System.out.println("fail as writing " + fileName);
    // }
    //
    // });
    // }
    // private void loadTasks(String fileName) throws IOException {
    // dbToYml(fileName, (path) -> {
    // try {
    // StringBuilder sBuilder = new StringBuilder();
    // // sBuilder.append("!!" + classPath + "\n");
    // sBuilder.append("#任务\n\n");
    // sBuilder.append("key: 唯一\n");
    // sBuilder.append("tasks: \n");
    // System.out.println("添加文本：" + sBuilder.toString());
    // Files.write(path, sBuilder.toString().getBytes(), StandardOpenOption.APPEND);
    //
    // List<CfgTaskEntity> entities = Cfg.I.get(CfgTaskEntity.class);
    // for (CfgTaskEntity tmp : entities) {
    // sBuilder = new StringBuilder();
    // sBuilder.append("- {");
    // sBuilder.append("id: " + tmp.getId());
    // sBuilder.append(",type: " + tmp.getType());
    // sBuilder.append(",name: " + tmp.getName());
    // sBuilder.append(",value: " + tmp.getValue());
    // if (tmp.getAward() != null) {
    // List<Award> awards = awardService.parseAwardJson(tmp.getAward());
    // String awardStr = "[";
    // for (Award award : awards) {
    // if (awardStr.length() > 5) {
    // awardStr += ",";
    // }
    // awardStr += "{item: " + award.getItem();
    // if (award.getAwardId() != 0) {
    // awardStr += ",awardId: " + award.getAwardId();
    // }
    // awardStr += ",num: " + award.getNum() + "}";
    // }
    // awardStr += "]";
    // sBuilder.append(",award: " + awardStr);
    // }
    //
    // sBuilder.append(",isValid: " + tmp.getIsValid());
    // sBuilder.append("}\n");
    //
    // String strToAppend = sBuilder.toString();
    // System.out.println("添加文本：" + strToAppend);
    // Files.write(path, strToAppend.getBytes(), StandardOpenOption.APPEND);
    // }
    // } catch (Exception e) {
    // System.out.println("fail as writing " + fileName);
    // }
    //
    // });
    // }

    private void loadCards(String fileName) throws IOException {
        dbToYml(fileName, (path) -> {
            try {
                StringBuilder sBuilder = new StringBuilder();
                // sBuilder.append("!!" + classPath + "\n");
                sBuilder.append("#卡牌\n\n");
                sBuilder.append("key: 唯一\n");
                sBuilder.append("cards: \n");
                System.out.println("添加文本：" + sBuilder.toString());
                Files.write(path, sBuilder.toString().getBytes(), StandardOpenOption.APPEND);

                List<CfgCardEntity> entities = Cfg.I.get(CfgCardEntity.class);
                for (CfgCardEntity tmp : entities) {
                    sBuilder = new StringBuilder();
                    sBuilder.append("- {");
                    sBuilder.append("id: " + tmp.getId());
                    sBuilder.append(",name: " + tmp.getName());
                    sBuilder.append(",type: " + tmp.getType());
                    sBuilder.append(",star: " + tmp.getStar());
                    sBuilder.append(",attack: " + tmp.getAttack());
                    sBuilder.append(",hp: " + tmp.getHp());
                    sBuilder.append(",zeroSkill: " + (tmp.getZeroSkill() == null ? 0 : tmp.getZeroSkill()));
                    sBuilder.append(",fiveSkill: " + (tmp.getFiveSkill() == null ? 0 : tmp.getFiveSkill()));
                    sBuilder.append(",tenSkill: " + (tmp.getTenSkill() == null ? 0 : tmp.getTenSkill()));
                    sBuilder.append(",group: " + (tmp.getGroup() == null ? 0 : tmp.getGroup()));
                    sBuilder.append(",way: " + tmp.getWay());
                    sBuilder.append(",comment: \"" + tmp.getComment() + "\"");
                    sBuilder.append("}\n");

                    String strToAppend = sBuilder.toString();
                    System.out.println("添加文本：" + strToAppend);
                    Files.write(path, strToAppend.getBytes(), StandardOpenOption.APPEND);
                }
            } catch (Exception e) {
                System.out.println("fail as writing " + fileName);
            }

        });
    }

    // private void loadYeG(String fileName) throws IOException {
    // dbToYml(fileName, (path) -> {
    // try {
    // StringBuilder sBuilder = new StringBuilder();
    // // sBuilder.append("!!" + classPath + "\n");
    // sBuilder.append("#野怪\n\n");
    // sBuilder.append("key: 唯一\n");
    // sBuilder.append("ygCards: \n");
    // System.out.println("添加文本：" + sBuilder.toString());
    // Files.write(path, sBuilder.toString().getBytes(), StandardOpenOption.APPEND);
    //
    // List<CfgYgCardsEntity> entities = Cfg.I.get(CfgYgCardsEntity.class);
    // for (CfgYgCardsEntity tmp : entities) {
    // sBuilder = new StringBuilder();
    // sBuilder.append("- {");
    // sBuilder.append("id: " + tmp.getId());
    // sBuilder.append(",cards: \"" + tmp.getCards() + "\"");
    // sBuilder.append("}\n");
    //
    // String strToAppend = sBuilder.toString();
    // System.out.println("添加文本：" + strToAppend);
    // Files.write(path, strToAppend.getBytes(), StandardOpenOption.APPEND);
    // }
    // } catch (Exception e) {
    // System.out.println("fail as writing " + fileName);
    // }
    //
    // });
    // }

    // private void loadYeD(String fileName) throws IOException {
    // dbToYml(fileName, (path) -> {
    // try {
    // StringBuilder sBuilder = new StringBuilder();
    // // sBuilder.append("!!" + classPath + "\n");
    // sBuilder.append("#野地事件\n\n");
    // sBuilder.append("key: 唯一\n");
    // sBuilder.append("events: \n");
    // System.out.println("添加文本：" + sBuilder.toString());
    // Files.write(path, sBuilder.toString().getBytes(), StandardOpenOption.APPEND);
    //
    // List<CfgYdEventEntity> entities = Cfg.I.get(CfgYdEventEntity.class);
    // for (CfgYdEventEntity tmp : entities) {
    // sBuilder = new StringBuilder();
    // sBuilder.append("- {");
    // sBuilder.append("id: " + tmp.getId());
    // sBuilder.append(",probability: " + tmp.getProbability());
    // sBuilder.append(",type: " + tmp.getType());
    // sBuilder.append(",name: \"" + tmp.getName() + "\"");
    // if (tmp.getMemo() != null) {
    // sBuilder.append(",memo: \"" + tmp.getMemo() + "\"");
    // }
    //
    // sBuilder.append("}\n");
    //
    // String strToAppend = sBuilder.toString();
    // System.out.println("添加文本：" + strToAppend);
    // Files.write(path, strToAppend.getBytes(), StandardOpenOption.APPEND);
    // }
    // } catch (Exception e) {
    // System.out.println("fail as writing " + fileName);
    // }
    //
    // });
    // }

    private void dbToYml(String fileName, Consumer<Path> consumer) throws IOException {
        fileName = GAME_CONFIG_PATH + fileName;

        Path path = Paths.get(fileName);
        if (Files.exists(path)) {
            System.out.println("删除文件");
            Files.delete(path);
        }
        System.out.println("创建文件");
        Files.createFile(path);
        consumer.accept(path);
        System.out.println("to " + fileName + " succssfully");
    }

}