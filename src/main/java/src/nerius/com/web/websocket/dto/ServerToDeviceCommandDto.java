package src.nerius.com.web.websocket.dto;

import lombok.Getter;
import lombok.Setter;
import src.nerius.com.db.entity.Device;

import java.util.Map;

@Getter
@Setter
public class ServerToDeviceCommandDto {
    private Device deviceForCommand;
    private Map<CommandType, Object> commands;


    public enum CommandType{
        SET_NAME,
        SET_PROPERTY,
        GET_DEVICE_INFO
    }

    public ServerToDeviceCommandDto() {}

    public ServerToDeviceCommandDto(Device device, Map<CommandType, Object> commands) {
        this.deviceForCommand = device;
        this.commands = commands;
    }

}

