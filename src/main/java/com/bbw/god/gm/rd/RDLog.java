package com.bbw.god.gm.rd;

import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class RDLog extends RDSuccess implements Serializable {
    private List<FileInfo> dirs=null;
    private List<FileInfo> files=null;

    public void addDir(FileInfo dir){
        if (dirs==null){
            dirs=new ArrayList<>();
        }
        dirs.add(dir);
    }
    public void addFile(FileInfo file){
        if (files==null){
            files=new ArrayList<>();
        }
        files.add(file);
    }

    @Data
    public static class FileInfo implements Serializable{
        private String name;
        private String path;
        public static FileInfo instance(String name,String path){
            FileInfo info=new FileInfo();
            info.setName(name);
            info.setPath(path);
            return info;
        }
    }
}
