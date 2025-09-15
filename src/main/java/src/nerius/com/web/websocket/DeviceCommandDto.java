package src.nerius.com.web.websocket;

import lombok.Getter;
import lombok.Setter;
import src.nerius.com.db.entity.Device;

import java.util.Map;

@Getter
@Setter
public class DeviceCommandDto {
    private Device device;
    private Map<String,String> commands;
}
