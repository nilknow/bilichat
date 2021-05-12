package backend;

import lombok.Data;

/**
 * the standard json data structure of bilibili
 */
@Data
public class StandardResp {
    private Integer code;
    private String message;
    private Object data;
    private Integer ttl;
}
