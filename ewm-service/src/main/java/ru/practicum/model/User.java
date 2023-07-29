package ru.practicum.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "email", nullable = false, length = 250)
    private String email;

    @Column(name = "name", nullable = false, length = 255, unique = true)
    private String name;

    @Column(name = "rating", nullable = false)
    long rating;
}
