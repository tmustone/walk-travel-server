package net.dynu.wpeckers.walktraveler.rest.messaging.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    private String email;
    private String fastLoginSecret;
    private Date fastLoginSecretDate;
    private Date lastLoginDate;
    private Date registerDate;
    private String longitude;
    private String latitude;
    private Integer totalCollectCount;
    private Integer currentCollectCount;
}
