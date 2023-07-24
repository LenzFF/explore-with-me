package ru.practicum.controller.privateControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.service.ParticipationRequestsService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@Slf4j
@RequiredArgsConstructor
public class ParticipationRequestPrivateController {

    private final ParticipationRequestsService participationService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto postParticipationRequest(@PathVariable(name = "userId") @Positive long userId,
                                                            @RequestParam(name = "eventId", required = true) @Positive long eventId) {

        ParticipationRequestDto request = participationService.create(userId, eventId);
        log.info("created new participation request, userId - {}, eventId - {}, requestId - {}", userId, eventId, request.getId());

        return request;
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getUserParticipationRequests(@PathVariable(name = "userId") @Positive long userId) {

        List<ParticipationRequestDto> requests = participationService.getUserRequests(userId);
        log.info("user participation requests founded, userId - {}", userId);
        return requests;
    }


    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelParticipationRequest(@PathVariable(name = "userId") @Positive long userId,
                                                              @PathVariable(name = "requestId") @PositiveOrZero long requestId) {

        ParticipationRequestDto request = participationService.cancelParticipationRequest(userId, requestId);
        log.info("user participation request canceled, userId - {}, requestId - {}", userId, requestId);
        return request;
    }
}
