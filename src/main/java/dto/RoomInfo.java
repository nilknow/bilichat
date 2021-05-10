package dto;

public class RoomInfo {

    /**
     * 0: closed
     * 1: open
     */
    private Integer liveStatus;
    /**
     * @see dto /area.json
     */
    private String areaId;

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public Integer getLiveStatus() {
        return liveStatus;
    }

    public void setLiveStatus(Integer liveStatus) {
        this.liveStatus = liveStatus;
    }
}
