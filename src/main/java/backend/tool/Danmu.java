package backend.tool;

import java.util.List;

public class Danmu {
    /**
     * DANMU_MSG: normal 弹幕
     */
    private String cmd;
    /**
     * the second element of info is danmu message
     */
    private List<Object> info;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public List<Object> getInfo() {
        return info;
    }

    public void setInfo(List<Object> info) {
        this.info = info;
    }
}
