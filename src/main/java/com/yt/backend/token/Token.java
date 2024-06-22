package com.yt.backend.token;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yt.backend.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {
    @Id
    @GeneratedValue
    private  Integer id;
    private String token;
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;
    private boolean expired;
    private boolean revoked;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

}
