package com.d2d.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "mnemonic_master")
@SQLDelete(sql = "UPDATE mnemonic_master SET deleted_flag =  true WHERE id=?")
public class MnemonicMaster {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "mnemonic_type_id_fk")
  private MnemonicType mnemonicType;

  @Column(name = "master_code")
  private String masterCode;

  @Column(name = "master_name")
  private String masterName;

  @Column(name = "parent_master_code")
  private String parentMasterCode;

  @Column(name = "sort_order")
  private Integer sortOrder;

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
