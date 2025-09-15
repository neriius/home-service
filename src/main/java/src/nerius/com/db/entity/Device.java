package src.nerius.com.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Entity
@Getter
@Setter
@ToString
public class Device {
    @Id
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Transient
    private String nodeId;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String,String> properties;

    private DeviceType deviceType;

    public enum DeviceType {
        LAMP
    }
}
