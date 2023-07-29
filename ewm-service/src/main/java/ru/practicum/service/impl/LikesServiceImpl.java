package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.EventMapper;
import ru.practicum.dto.like.LikeDto;
import ru.practicum.dto.like.LikeMapper;
import ru.practicum.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.dto.user.UserMapper;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.model.Like;
import ru.practicum.model.RequestStatus;
import ru.practicum.repository.LikesRepository;
import ru.practicum.service.*;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikesServiceImpl implements LikesService {

    private final LikesRepository likesRepository;
    private final UserService userService;
    private final EventService eventService;
    private final ParticipationRequestsService requestsService;


    @Override
    @Transactional
    public LikeDto putLike(long eventId, long userId, LikeDto likeDto) {

        Like newLike;
        Like oldLike = likesRepository.findByEventIdAndUserId(eventId, userId);

        if (oldLike == null) {
            // если пользователь еще не ставил лайк событию, то проверяем
            newLike = validateLike(eventId, userId, likeDto.isLike());

        } else {
            // если лайк уже есть, то достаем из базы и обновляем на лайк/дизлайк
            newLike = oldLike;
            newLike.setLike(likeDto.isLike());
        }

        newLike = likesRepository.save(newLike);
        // обновляем рейтинг мероприятия и его автора
        eventService.updateEventAndInitiatorRatings(eventId, getEventRating(eventId));

        return LikeMapper.toLikeDto(newLike, EventMapper.toEventShortDto(eventService.getById(eventId)));
    }


    private Like validateLike(long eventId, long userId, boolean isLike) {

        Like like = new Like();
        like.setEvent(eventService.get(eventId));
        like.setUser(UserMapper.fromUserDto(userService.get(userId)));
        like.setLike(isLike);

        ParticipationRequestDto requestDto = requestsService.getUserRequestOnEvent(userId, eventId);

        //пользователь должен был оставить заявку на участие
        if (requestDto == null) {
            throw new DataNotFoundException("the user did not request to participate in event, eventId - " + eventId);
        }

        //заявка должна была быть подтверждена
        if (!requestDto.getStatus().equals(RequestStatus.CONFIRMED.toString())) {
            throw new DataNotFoundException("the user request did not  confirmed, requestId - " + requestDto.getId());
        }

        //событие должно начаться
        if (like.getEvent().getEventDate().isAfter(LocalDateTime.now())) {
            throw new ValidationException("event did not started yet, eventId - " + eventId);
        }

        return like;
    }


    @Override
    @Transactional
    public void removeLike(long eventId, long userId) {

        Like like = likesRepository.findByEventIdAndUserId(eventId, userId);

        if (like == null) {
            throw new DataNotFoundException("like not exist, userId - " + userId + " , eventId - " + eventId);
        }

        likesRepository.deleteById(like.getId());
        // обновляем рейтинг мероприятия и его автора
        eventService.updateEventAndInitiatorRatings(eventId, getEventRating(eventId));
    }


    @Transactional
    public long getEventRating(long eventId) {

        //формула для расчета рейтинга взята отсюда https://habr.com/ru/companies/darudar/articles/143188/

        long positive = likesRepository.countByLikeTrueAndEventId(eventId);
        long negative = likesRepository.countByLikeFalseAndEventId(eventId);
        long all = positive + negative;

        if (all == 0) return 0;

        // сначала рейтинг будет в диапазоне от 0 до 1
        double rating = ((positive + 1.9208) / all -
                1.96 * Math.sqrt((positive * negative) / all + 0.9604) /
                        all) / (1 + 3.8416 / all);

        // умножаем на 100 и округляем, в итоге рейтинг будет от 0 до 100
        return Math.round(rating * 100.0);
    }
}
