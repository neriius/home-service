package src.nerius.com.web.websocket.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import src.nerius.com.db.entity.Device;
import src.nerius.com.web.websocket.DeviceCommandDto;

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

    @MessageMapping("/connectDevice")
    public void connectDevice(@Payload Device device) {
        if (device.getNodeId() != null) {
            connectedDevices.put(device.getName(), device);
        } else {
            connectedDevices.remove(device.getName());
        }
        System.out.println("Devices connected: " + device);
    }

    @MessageMapping("/sendAllDevices")
    public void sendAllDevices(@Payload List<Device> devices) {
        System.out.println(devices);
        // messagingTemplate.convertAndSend("/deviceCommands", connectedDevices.values());
    }

    // Пример: POST /api/ws/sendCommand
    @PostMapping("/sendCommandToDevice")
    public String sendCommand(@RequestBody DeviceCommandDto deviceCommandDto) {
        messagingTemplate.convertAndSend("/deviceCommands", deviceCommandDto);
        return "Command sent to device " + deviceCommandDto.getDevice().getName();
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

        Map<String, String> commands = Map.of("action", "toggle");

        DeviceCommandDto testCommand = new DeviceCommandDto();
        testCommand.setDevice(device);
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
