package src.nerius.com.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "device_type")
@Getter
@Setter
public abstract class AbstractDevice {
    @Id
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;


    public enum DeviceType {

    }
}
