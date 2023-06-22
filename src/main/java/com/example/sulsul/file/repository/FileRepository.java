package com.example.sulsul.file.repository;

import com.example.sulsul.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    /**
     * 학생이 올린 첨삭파일 조회
     * @param essayId 첨삭파일 id
     * @param studentId 학생 userId
     * @return 학생이 올린 첨삭파일 반환
     */
    @Query("select f from File f where f.essay.id = :essayId and f.user.id = :studentId")
    Optional<File> getStudentEssayFile(@Param("essayId") Long essayId, @Param("studentId")Long studentId);

    /**
     * 강사가 올린 첨삭파일 조회
     * @param essayId 첨삭파일 id
     * @param teacherId 강사 userId
     * @return 강사가 올린 첨삭파일 반환
     */
    @Query("select f from File f where f.essay.id = :essayId and f.user.id = :teacherId")
    Optional<File> getTeacherEssayFile(@Param("essayId") Long essayId, @Param("teacherId")Long teacherId);
}
