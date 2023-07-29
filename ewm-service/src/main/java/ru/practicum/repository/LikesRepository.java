package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Like;

@Repository
public interface LikesRepository extends JpaRepository<Like, Long> {

    Like findByEventIdAndUserId(long eventId, long userId);

    long countByLikeTrueAndEventId(long eventId);

    long countByLikeFalseAndEventId(long eventId);
}
