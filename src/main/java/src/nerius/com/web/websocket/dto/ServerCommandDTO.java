package src.nerius.com.web.websocket.dto;

import lombok.*;
import src.nerius.com.db.entity.Device;

import java.util.Map;

@Getter
@AllArgsConstructor
@Setter
@ToString
@NoArgsConstructor
public class ServerCommandDTO {

    private Device device;
    private Map<DevicesCommandTypes, Object> payload;

    public enum DevicesCommandTypes {
        RELOAD_ONLINE_DEVICES,
        DEVICE_CONNECTED,
        DEVICE_DISCONNECTED,
        LOG,
        UPDATE_STATUS
    }
}
