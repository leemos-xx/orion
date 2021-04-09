package leemos.orion.core.bus;

/**
 * 事件总线类型
 * 
 * @author lihao
 * @date 2020-12-05
 * @version 1.0
 */
public enum EventBusType {

    DEFAULT("default"), QUEUED("queued"), PRIORITY("priority");

    private String type;

    EventBusType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static EventBusType parse(String source) {
        for (EventBusType type : values()) {
            if (type.getType().equals(source)) {
                return type;
            }
        }
        throw new IllegalArgumentException();
    }
}
