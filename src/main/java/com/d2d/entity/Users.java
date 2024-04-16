package com.d2d.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "users")
public class Users {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "username")
  private String username;

  @Column(name = "password")
  private String password;

/*
  @Column(name = "pass")
  private String pass;
*/

  @Column(name = "email")
  private String email;

  @Column(name = "mobile_number")
  private String mobileNumber;

  @Column(name = "pan_number")
  private String panNumber;

  @Column(name = "provider")
  private String provider;

  @Column(name = "date_of_birth")
  private String dateOfBirth;

  @Column(name = "address")
  private String address;

  @ManyToOne
  @JoinColumn(name = "role_id")
  private Role role;


  @Column(name = "is_active")
  private Boolean isActive;

  @Column(name = "delete_flag")
  private Boolean deleteFlag;

  @CreationTimestamp
  @Column(name = "created_at",updatable = false)
  private Timestamp createdAt;

  @Column(name = "created_by")
  private String createdBy;

  @UpdateTimestamp
  @Column(name = "modified_at")
  private Timestamp modifiedAt;

  @Column(name = "modified_by")
  private String modifiedBy;

}
