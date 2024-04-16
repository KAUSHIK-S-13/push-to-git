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

@Entity
@Getter
@Setter
@Table(name = "data_type")
public class DataType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "data_type_name")
    private String dataTypeName;

    @ManyToOne()
    @JoinColumn(name = "data_base_engine_id_fk")
    private DataBaseEngine dataBaseEngine;

    @Column(name = "is_active")
    private Integer isActive;

    @Column(name = "deleted_flag")
    private Integer deletedFlag;

    @Column(name = "length_active")
    private Boolean isLength;

    @Column(name = "Created_at")
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "Updated_at")
    @UpdateTimestamp
    private Timestamp updatedAt;
}
