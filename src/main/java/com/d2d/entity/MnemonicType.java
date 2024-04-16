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
@Table(name = "mnemonic_type")
@SQLDelete(sql = "UPDATE mnemonic_type SET deleted_flag =  true WHERE id=?")
public class MnemonicType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(name = "type_code")
  private String typeCode;

  @Column(name = "type_name")
  private String typeName;

  @ManyToOne
  @JoinColumn(name = "role_id")
  private Role role;

  @Column(name = "parent_type_code")
  private String parentTypeCode;

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
