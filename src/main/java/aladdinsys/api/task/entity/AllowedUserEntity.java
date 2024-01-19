package aladdinsys.api.task.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name="ALLOWEDUSERS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AllowedUserEntity {

    @Id
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String regNo;


    public AllowedUserEntity(String name, String regNo) {
        this.name = name;
        this.regNo = regNo;
    }

}
