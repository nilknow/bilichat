package tool;

import backend.LiveApi;
import backend.util.Curl;
import backend.util.FileUtil;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;

@Slf4j
public class Live {

    public static void main(String[] args){
        searchForInterest();
    }

    public static void searchForInterest() {
        List<String> nameList = new ArrayList<>();
        nameList.add("陪伴学习");
        nameList.add("职业技能");
        List<RoomInfo> roomList = list(nameList);
        StringBuilder sb = new StringBuilder();
        for (RoomInfo info : roomList) {
            sb
                    .append(info.getTitle()).append("\t")
                    .append(info.getRoomid()).append("\t")
                    .append(info.getUid()).append("\t")
                    .append("\n");
        }
        FileUtil.writeToFile("roomtitle.txt",sb.toString());

        Set<String> keywords = new HashSet<>();
        keywords.add("程序");
        keywords.add("代码");
        keywords.add("码农");
        keywords.add("ava");
        keywords.add("开发");
        keywords.add("编程");
        keywords.add("bug");
        filterRoomByTitle(keywords);
    }

    /**
     * page start from 1
     */
    public static List<RoomInfo> list(List<String> nameList) {
        List<RoomInfo> result = new ArrayList<>();
        for (String name : nameList) {
            result.addAll(collectRoomInfo(name));
        }
        return result;
    }

    /**
     * collect room info by area id
     */
    public static List<RoomInfo> collectRoomInfo(String areaName) {
        List<RoomInfo> result = new ArrayList<>();

        List<LiveApi.Area> webAreaList = LiveApi.getWebAreaList();
        Optional<LiveApi.Area> area = webAreaList.stream().filter(x -> x.getName().equals(areaName)).findFirst();
        if (area.isEmpty()) {
            log.error("there must be a correspond area, check it");
            return result;
        }

        int page = 1;
        while (true) {
            String jsonStr = Curl.get("https://api.live.bilibili.com/xlive/web-interface/v1/second/getList?platform=web&parent_area_id=11&area_id="
                    +area.get().getId()+"&sort_type=online&page=" + page);
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
        private RoomListRespData data;
        private String message;
        private Integer ttl;
    }

    @Data
    public class RoomInfo {
        private String title;
        private Long roomid;
        private Long uid;
    }

    @Data
    public class RoomListRespData {
        @SerializedName("has_more")
        private short hasMore;
        private List<RoomInfo> list;
    }


}
