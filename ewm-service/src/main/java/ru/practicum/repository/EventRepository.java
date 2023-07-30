package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;

import java.util.List;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    List<Event> findAllByInitiatorId(long userId, PageRequest page);

    Event findByIdAndInitiatorId(long eventId, long userId);

    List<Event> findByCategoryId(long catId);

    List<Event> findByIdIn(Set<Long> eventIds);

    @Query("select ev " +
            "from Event as ev " +
            "join ParticipationRequest as req on ev.id = req.requester.id " +
            "where ev.id in ?1 and req.status = ?2 and ev.participantLimit != 0 and count(req.id) < ev.participantLimit " +
            "group by ev.id ")
    List<Event> findAvailableEvents(Set<Long> eventIds, String status);


    @Query("select avg(ev.rating) " +
            "from Event as ev " +
            "where ev.initiator.id = ?1 " +
            "group by ev.id")
    long getUserRating(long userId);
}
