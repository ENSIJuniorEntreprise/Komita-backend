package com.yt.backend.model;

import com.yt.backend.model.user.User;
import java.util.Date;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "consultation")
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service serviceConsulting;

    @Getter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User userConsulting;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "consulting_date", nullable = false)
    private Date consultingDate;

    private boolean checked;


}

