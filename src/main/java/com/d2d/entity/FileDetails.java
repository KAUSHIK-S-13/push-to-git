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
@Table(name = "file_details")
@SQLDelete(sql = "UPDATE role SET deleted_flag =  true WHERE id=?")
public class FileDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "generated_file_name")
    private String generatedFileName;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "deleted_flag")
    private Boolean deletedFlag;

    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "modified_at")
    @UpdateTimestamp
    private Timestamp modifiedAt;

    @Column(name = "modified_by")
    private Long modifiedBy;
}
