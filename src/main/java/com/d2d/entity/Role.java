package com.d2d.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "role")
@SQLDelete(sql = "UPDATE role SET deleted_flag =  true WHERE id=?")
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(name = "role")
  private String role;

  @Column(name = "menu_data")
  private String menuData;

  @Column(name = "parent_role_id")
  private Integer parentRoleId;

  @Column(name = "is_active")
  private Boolean isActive;

  @Column(name = "deleted_flag")
  private Boolean deletedFlag;

  @Column(name = "created_at")
  @CreationTimestamp
  private Timestamp createdAt;

  @Column(name = "created_by")
  private Integer createdBy;

  @Column(name = "modified_at")
  @UpdateTimestamp
  private Timestamp modifiedAt;

  @Column(name = "modified_by")
  private Integer modifiedBy;

}
