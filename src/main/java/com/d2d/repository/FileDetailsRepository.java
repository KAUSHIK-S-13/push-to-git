package com.d2d.repository;

import com.d2d.entity.FileDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FileDetailsRepository extends JpaRepository<FileDetails,Integer> {

    @Query("select f from FileDetails as f " +
            "join ProjectFileDetails as pfd on f.id= pfd.fileDetails.id " +
            "join Project as p on pfd.project.id=p.id  " +
            "join ProjectUsers as ps on p.id=ps.project.id " +
            "join MnemonicMaster as pt on pt.id=p.projectType.id " +
            "where p.id= :projectId and p.projectName = :projectName and f.deletedFlag=False ")
    Optional<FileDetails> findAllByFilters(Integer projectId, String projectName);

    Optional<FileDetails> findByIdAndDeletedFlag(Integer id, boolean b);
}
