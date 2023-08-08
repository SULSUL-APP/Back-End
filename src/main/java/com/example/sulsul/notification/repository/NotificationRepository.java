package com.example.sulsul.notification.repository;

import com.example.sulsul.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * 전체알림 및 첨삭알림 조회
     *
     * @param userId 알림대상의 id
     * @return 첨삭알림 리스트
     */
    @Query("select n from Notification n where n.notiType = 'COMMON' or (n.notiType = 'ESSAY' and n.user.id = :userId) order by n.id desc")
    List<Notification> findNotificationByUserId(@Param("userId") Long userId);

    /**
     * 알림 삭제
     *
     * @param ids 삭제할 알림들의 id 리스트
     */
    @Query("delete from Notification n where n.id in :ids")
    void deleteNotifications(@Param("ids") List<Long> ids);

}