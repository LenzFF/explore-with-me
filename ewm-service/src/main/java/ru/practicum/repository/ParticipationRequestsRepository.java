package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.RequestStatus;

import java.util.List;

@Repository
public interface ParticipationRequestsRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByEventId(long eventId);

    List<ParticipationRequest> findAllByRequesterId(long userId);

    ParticipationRequest findAllByEventIdAndRequesterId(long eventId, long userId);

    long countByEventIdAndStatus(long eventId, RequestStatus state);

    List<ParticipationRequest> findByIdIn(List<Long> requestsId);
}
