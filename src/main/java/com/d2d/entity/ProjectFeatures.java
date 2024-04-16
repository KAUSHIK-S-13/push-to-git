package com.d2d.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "project_features")
@SQLDelete(sql = "UPDATE role SET deleted_flag =  true WHERE id=?")
public class ProjectFeatures {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "feature_type")
    private String featureType;

    @Column(name = "feature_category")
    private String featureCategory;

    @Column(name = "feature_code")
    private String featureCode;

    @Column(name = "feature_description")
    private String featureDescription;

    @Column(name = "is_default")
    private Boolean isDefault;

    @Column(name = "component_code")
    private String componentCode;

    @Column(name = "feature_level")
    private String featureLevel;

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
