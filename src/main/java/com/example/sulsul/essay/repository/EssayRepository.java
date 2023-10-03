package com.example.sulsul.essay.repository;

import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.common.type.EssayState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EssayRepository extends JpaRepository<Essay, Long> {
    /**
     * 강사가 자신에게 요청된 첨삭목록을 조회한다.
     * @param teacherId 강사의 userId
     * @param essayState 조회할 첨삭상태
     * @return 지정한 첨삭상태의 첨삭목록을 반환한다.
     */
    List<Essay> findAllByTeacherIdAndEssayState(Long teacherId, EssayState essayState);

    /**
     * 학생이 자신이 요청한 첨삭목록을 조회한다.
     * @param studentId 학생의 userId
     * @param essayState 조회할 첨삭상태
     * @return 지정한 첨삭상태의 첨삭목록을 반환한다.
     */
    List<Essay> findAllByStudentIdAndEssayState(Long studentId, EssayState essayState);

}