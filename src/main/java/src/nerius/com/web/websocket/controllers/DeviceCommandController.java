package src.nerius.com.web.websocket.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import src.nerius.com.db.entity.Device;
import src.nerius.com.web.websocket.dto.ServerCommandDTO;
import src.nerius.com.web.websocket.dto.ServerToDeviceCommandDto;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/api/ws")
public class DeviceCommandController {

    private final SimpMessagingTemplate messagingTemplate;

    public DeviceCommandController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    //Name with device
    private final Map<String, Device> connectedDevices = new ConcurrentHashMap<>();

    @MessageMapping("/sendCommandToServer")
    public String handleServerCommand(@RequestBody ServerCommandDTO commandDTO) {

        if (commandDTO.getPayload() == null || commandDTO.getPayload().isEmpty()) {
            return "Нет команд для обработки";
        }

        for (Map.Entry<ServerCommandDTO.DevicesCommandTypes, Object> entry : commandDTO.getPayload().entrySet()) {
            ServerCommandDTO.DevicesCommandTypes commandType = entry.getKey();
            Object data = entry.getValue();

            switch (commandType) {
                case RELOAD_ONLINE_DEVICES:
                    // логика перезагрузки онлайн устройств
                    System.out.println("Перезагружаем онлайн-устройства: " + data);
                    break;
                case DEVICE_CONNECTED:
                    Device device = commandDTO.getDevice(); //from which device received message
                    if (device.getNodeId() != null) {
                        connectedDevices.put(device.getName(), device);
                    } else {
                        connectedDevices.remove(device.getName());
                    }
                    System.out.println("Devices connected: " + commandDTO);
                    break;
                case DEVICE_DISCONNECTED:
                    System.out.println("Устройство отключено: " + data);
                    break;
                case LOG:
                    System.out.println("Лог: " + data);
                    break;
                case UPDATE_STATUS:
                    System.out.println("Обновляем статус: " + data);
                    break;
                default:
                    System.out.println("Неизвестная команда: " + commandType);
            }
        }

        return "Команды успешно обработаны";
    }


    // Пример: POST /api/ws/sendCommand
    @PostMapping("/sendCommandToDevice")
    public String sendCommand(@RequestBody ServerToDeviceCommandDto deviceCommandDto) {
        messagingTemplate.convertAndSend("/deviceCommands", deviceCommandDto);
        return "Command sent to device " + deviceCommandDto.getDeviceForCommand().getName();
    }


    @PostMapping("/sendTestCommand")
    public String sendTestCommand() {
        // Создаём простое "виртуальное" устройство
        Device device = new Device() {
            {
                setId(999L);
                setName("TestDevice");
            }
        };

        Map<ServerToDeviceCommandDto.CommandType, Object> commands = Map.of(ServerToDeviceCommandDto.CommandType.SET_NAME, "bibus");

        ServerToDeviceCommandDto testCommand = new ServerToDeviceCommandDto();
        testCommand.setDeviceForCommand(device);
        testCommand.setCommands(commands);

        // Отправляем через WebSocket
        messagingTemplate.convertAndSend("/deviceCommands", testCommand);

        System.out.println("Sent test command: " + testCommand.getCommands());
        return "Test command sent!";
    }

    @Scheduled(fixedRate = 5000)
    @Async
    public void sendAllCommands() {
        log.info("Connected devices: {}", connectedDevices);
    }
}
