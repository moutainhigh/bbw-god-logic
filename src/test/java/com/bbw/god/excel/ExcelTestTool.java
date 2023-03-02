package com.bbw.god.excel;

import com.bbw.BaseTest;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.card.CardSkillTool;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-05-31
 */

public class ExcelTestTool extends BaseTest {

    @Test
    public void getExcel() throws FileNotFoundException, IOException {
        File excelFile = new File("/Users/liuwenbin/Desktop/t1.xls");
        HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(excelFile));
        HSSFSheet sheet = wb.getSheetAt(0);
        List<List<String>> listAll = new ArrayList<List<String>>();
        for (Row row : sheet) {
            String content="  - {id: %s,skill0: %s,skill5: %s,skill10: %s,useScroll: 1}";
            int id=CardTool.getCardByName(row.getCell(0).toString()).getId();
            int skill0= CardSkillTool.getSkillIdByName(row.getCell(3).toString().replace("【","").replace("】",""));
            int skill5= CardSkillTool.getSkillIdByName(row.getCell(4).toString().replace("【","").replace("】",""));
            int skill10= CardSkillTool.getSkillIdByName(row.getCell(5).toString().replace("【","").replace("】",""));
            String format = String.format(content, id, skill0, skill5, skill10);
            System.err.println(format);
        }
    }

    /**
     *   - tokenLevel: %s
     *     baseAwards: %s
     *     supAwards: %s
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Test
    public void getWarTokenExcel() throws FileNotFoundException, IOException {
        File excelFile = new File("/Users/liuwenbin/Desktop/战令.xls");
        HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(excelFile));
        HSSFSheet sheet = wb.getSheetAt(0);
        for (Row row : sheet) {
            String conetnt="--- {tokenLevel: %s,baseAwards: %s,supAwards: %s}";
            Double tokenLevel=Double.parseDouble(row.getCell(0).toString());
            Award award = getAward(row.getCell(1).toString());
            String baseAwardStr="[]";
            if (award!=null){
                baseAwardStr=String.format("[{item: %s,awardId: %s,num: %s}]",award.getItem(),award.getAwardId(),award.getNum());
            }
            List<Award> supAwards=new ArrayList<>();
            Award award1=getAward(row.getCell(2).toString());
            if (award1!=null){
                supAwards.add(award1);
            }
            Award award2=getAward(row.getCell(3).toString());
            if (award2!=null){
                supAwards.add(award2);
            }
            String supAwardStr="[";
            for (Award supAward : supAwards) {
                supAwardStr+=String.format("{item: %s,awardId: %s,num: %s},",supAward.getItem(),supAward.getAwardId(),supAward.getNum());
            }
            supAwardStr+="]";
            supAwardStr=supAwardStr.replace(",]","]");
            System.err.println(String.format(conetnt,tokenLevel.intValue(),baseAwardStr,supAwardStr));
        }
    }

    private Award getAward(String str){
        if (str.equals("0.0")){
            return null;
        }
        String[] split = str.split("\\*");
        int num= split.length==2?Integer.parseInt(split[1]):Integer.parseInt(split[2]);
        if (split[0].indexOf("元宝")>-1){
            return Award.instance(0, AwardEnum.YB,num);
        }else if (split[0].indexOf("铜钱")>-1){
            return Award.instance(0, AwardEnum.TQ,num);
        }else if (split[0].indexOf("卡牌")>-1){
            String[] cardStr = split[0].split("卡牌-");
            CfgCardEntity card = CardTool.getCardByName(cardStr[1]);
            return Award.instance(card.getId(), AwardEnum.KP,num);
        }else if (split.length>2){
            CfgTreasureEntity treasure = TreasureTool.getTreasureByName(split[0]+"*"+split[1]);
            return Award.instance(treasure.getId(), AwardEnum.FB,num);
        }
        CfgTreasureEntity treasure = TreasureTool.getTreasureByName(split[0]);
        return Award.instance(treasure.getId(), AwardEnum.FB,num);
    }
}
