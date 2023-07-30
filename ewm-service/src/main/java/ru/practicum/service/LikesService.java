package ru.practicum.service;

import ru.practicum.dto.like.LikeDto;

public interface LikesService {

    LikeDto putLike(long eventId, long userId, LikeDto likeDto);

    void removeLike(long eventId, long userId);
}
