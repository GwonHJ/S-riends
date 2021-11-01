package com.project.autonomous.user.entity;

import com.project.autonomous.common.entity.BaseEntity;
import com.project.autonomous.common.entity.City;
import com.project.autonomous.user.dto.request.UserRegisterPostReq;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    String email;
    String password;
    String name;
    private LocalDate birth;
    String gender;
    String phone;

    @Enumerated(EnumType.STRING)
    private City city;

    String pictureId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserAuthority userAuthority;

    @NotNull
    private boolean deleted;
    private boolean auth_status;

    @OneToMany(mappedBy = "user")
    private List<UserTeam> userTeamList = new ArrayList<>();

    // 유저 삭제
    public void delete() {
        this.deleted = true;
    }

    // 유저 복구
    public void restore() {
        this.deleted = false;
    }

    @Override
    public String toString() {
        return "User{" +
            "email='" + email + '\'' +
            ", name='" + name + '\'' +
            ", birth=" + birth +
            ", gender='" + gender + '\'' +
            ", phone='" + phone + '\'' +
            ", city='" + city + '\'' +
            ", picture_id='" + pictureId + '\'' +
            ", password='" + password + '\'' +
            '}';
    }

    public static User from(UserRegisterPostReq dto, PasswordEncoder passwordEncoder){
        return User.builder()
            .email(dto.getEmail())
            .name(dto.getName())
            .birth(dto.getBirth())
            .gender(dto.getGender())
            .phone(dto.getPhone())
            .city(City.from(dto.getCity()))
            .password(passwordEncoder.encode(dto.getPassword()))
            .userAuthority(UserAuthority.ROLE_USER)
            .deleted(false)
                .auth_status(false)
            .build();
    }
}
