package net.dynu.wpeckers.walktraveler.database.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseObject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;
    private String email;
    private String fastLoginSecret;
    private Date registerDate;
    private Date lastLoginDate;
    private Date fastLoginSecretDate;
    private String longitude;
    private String latitude;
}
