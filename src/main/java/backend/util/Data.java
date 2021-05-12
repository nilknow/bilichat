package backend.util;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {
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