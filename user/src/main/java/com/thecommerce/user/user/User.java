package com.thecommerce.user.user;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sun.istack.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Table(name = "Users")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @NotNull
    @GeneratedValue
    @Column(name = "userIndex", unique = true)
    private Long userIndex;

    @NotNull
    @Column(name = "userId", unique = true)
    private String userId;

    @Column(name = "userName", unique = true)
    private String userName;

    @NotNull
    @Column(name = "email", unique = true)
    private String email;

    @NotNull
    @Column(name = "password")
    private String password;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @CreatedDate
    @Column(name = "createDate")
    private LocalDateTime createDate;

}
