package backend.tool;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Room {
    private static final Logger logger = LoggerFactory.getLogger(Room.class);

    private class RoomInfo {
        private String title;
        private Long roomid;
        private Long uid;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Long getRoomid() {
            return roomid;
        }

        public void setRoomid(Long roomid) {
            this.roomid = roomid;
        }

        public Long getUid() {
            return uid;
        }

        public void setUid(Long uid) {
            this.uid = uid;
        }
    }

    private class Data {
        @SerializedName("has_more")
        private short hasMore;
        private List<RoomInfo> list;

        public List<RoomInfo> getList() {
            return list;
        }

        public void setList(List<RoomInfo> list) {
            this.list = list;
        }

        public short getHasMore() {
            return hasMore;
        }

        public void setHasMore(short hasMore) {
            this.hasMore = hasMore;
        }
    }

    private class RoomListResp {
        private Integer code;
        private Data data;
        private String message;
        private Integer ttl;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Integer getTtl() {
            return ttl;
        }

        public void setTtl(Integer ttl) {
            this.ttl = ttl;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }
    }

    /**
     * page start from 1
     */
    public static List<RoomInfo> list() {
        List<RoomInfo> result = new ArrayList<>();
        int i = 1;
        while (true) {
            String jsonStr = Curl.get("https://api.live.bilibili.com/xlive/web-interface/v1/second/getList?platform=web&parent_area_id=11&area_id=372&sort_type=online&page=" + i);
            if (jsonStr.length() == 0) {
                break;
            }
            RoomListResp respJson = new Gson().fromJson(jsonStr, RoomListResp.class);
            result.addAll(respJson.getData().getList());
            if (respJson.getData().getHasMore() != 1) {
                break;
            }
            ++i;
        }
        logger.debug("there are " + i + " pages");
        logger.debug("there are " + result.size() + " rooms");
        return result;
    }

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
                    logger.info(info[0] + "\t" + "https://live.bilibili.com/" + info[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void main(String[] args) {
        searchForInterest();
    }
}
