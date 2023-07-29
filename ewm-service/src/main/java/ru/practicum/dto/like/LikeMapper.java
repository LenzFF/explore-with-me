package ru.practicum.dto.like;

import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.user.UserMapper;
import ru.practicum.model.Like;

public class LikeMapper {

    public static LikeDto toLikeDto(Like like, EventShortDto eventShortDto) {

        LikeDto likeDto = new LikeDto();
        likeDto.setId(like.getId());
        likeDto.setEvent(eventShortDto);
        likeDto.setUser(UserMapper.toShortDto(like.getUser()));
        likeDto.setLike(like.isLike());

        return likeDto;
    }
}
