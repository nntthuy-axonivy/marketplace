package com.axonivy.market.repository;

import com.axonivy.market.entity.Feedback;
import com.axonivy.market.enums.FeedbackStatus;
import com.axonivy.market.model.FeedbackProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, String> {
  Optional<Feedback> findByIdAndVersion(String id, Integer version);

  List<Feedback> findByProductId(String productId);

  List<Feedback> findByProductIdAndUserIdAndIsLatestTrueAndFeedbackStatusNotIn(String productId, String userId,
      List<FeedbackStatus> excludedStatuses);

  List<Feedback> findByProductIdAndUserIdAndFeedbackStatusNotIn(String productId, String userId,
      List<FeedbackStatus> excludedStatuses);

  List<Feedback> findByProductIdAndIsLatestTrueAndFeedbackStatusNotIn(String productId,
      List<FeedbackStatus> excludedStatuses, Pageable pageable);

  @Query(value = """
        SELECT f.id AS id,
               f.user_id AS userId,
               f.product_id AS productId,
               f.content AS content,
               f.rating AS rating,
               f.feedback_status AS feedbackStatus,
               f.moderator_name AS moderatorName,
               f.review_date AS reviewDate,
               f.created_at AS createdAt,
               f.updated_at AS updatedAt,
               f.version AS version,
             CAST(json_object_agg(pn.language, pn.name) AS TEXT) AS productNamesJson
        FROM FEEDBACK f
        JOIN PRODUCT p ON f.product_id = p.id
        JOIN PRODUCT_NAME pn ON p.id = pn.product_id
        GROUP BY f.id
      """, nativeQuery = true)
  Page<FeedbackProjection> findFeedbackWithProductNames(Pageable pageable);
}
