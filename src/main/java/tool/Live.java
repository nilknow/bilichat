package tool;

import backend.util.Curl;
import backend.util.Data;
import backend.util.RoomInfo;
import backend.util.RoomListResp;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class Live {

    public static void main(String[] args){
        searchForInterest();
    }

    public static void searchForInterest() {
        writeToFile(list());
        Set<String> keywords = new HashSet<>();
        keywords.add("程序");
        keywords.add("代码");
        keywords.add("码农");
        keywords.add("ava");
        keywords.add("开发");
        keywords.add("编程");
        keywords.add("bug");

//        keywords.add("数学");
        filterRoomByTitle(keywords);
    }


    /**
     * page start from 1
     */
    public static List<RoomInfo> list() {
        List<RoomInfo> result = new ArrayList<>();

        List<RoomInfo> roomInfos = collectRoomInfo("372");

        return result;
    }

    /**
     * collect room info by area id
     */
    public List<RoomInfo> collectRoomInfo (String areaName) {
        List<RoomInfo> result = new ArrayList<>();

        int page = 1;
        while (true) {
            String jsonStr = Curl.get("https://api.live.bilibili.com/xlive/web-interface/v1/second/getList?platform=web&parent_area_id=11&area_id="+areaId+"&sort_type=online&page=" + page);
            if (jsonStr.length() == 0) {
                break;
            }
            RoomListResp respJson = new Gson().fromJson(jsonStr, RoomListResp.class);
            result.addAll(respJson.getData().getList());
            if (respJson.getData().getHasMore() != 1) {
                break;
            }
        }
        log.debug("there are " + page + " pages in area "+areaName);
        log.debug("there are " + result.size() + " rooms in area "+ areaName);
        return result;
    }

    /**
     * get area id by area name
     */


    /**
     * write room info to file
     */
    public static void writeToFile(List<RoomInfo> roomInfoList) {
        try (FileWriter fw = new FileWriter("roomtitle.txt")) {
            StringBuilder sb = new StringBuilder();
            for (RoomInfo info : roomInfoList) {
                sb
                        .append(info.getTitle()).append("\t")
                        .append(info.getRoomid()).append("\t")
                        .append(info.getUid()).append("\t")
                        .append("\n");
            }
            fw.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get all programming related url
     */
    public static void filterRoomByTitle(Set<String> keyWords){
        File file = new File("roomtitle.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                boolean containsKeyword = false;
                for (String keyword : keyWords) {
                    if (line.contains(keyword)) {
                        containsKeyword=true;
                        break;
                    }
                }
                if (containsKeyword) {
                    String[] info = line.split("\t");
                    log.info(info[0] + "\t" + "https://live.bilibili.com/" + info[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Data
    private class RoomListResp {
        private Integer code;
        private RoomInfo data;
        private String message;
        private Integer ttl;
    }

    @Data
    public class RoomInfo {
        private String title;
        private Long roomid;
        private Long uid;
    }


}
