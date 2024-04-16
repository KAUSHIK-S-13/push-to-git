package com.d2d.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "project_fileDetails")
public class ProjectFileDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "project_id_fk")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "file_id_fk")
    private FileDetails fileDetails;

    @Column(name = "deleted_flag")
    private Boolean deletedFlag;
}
