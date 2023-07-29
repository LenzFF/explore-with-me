insert into categories (name) values ('cat1');

insert into users (email, name, rating) values ('123@bk.ru', 'admin', 0);
insert into users (email, name, rating) values ('456@bk.ru', 'user1', 0);
insert into users (email, name, rating) values ('789@bk.ru', 'user2', 0);
insert into users (email, name, rating) values ('asd@bk.ru', 'user3', 0);


insert into locations (latitude, longitude) values (0, 0);

insert into events (annotation, category_id, confirmed_requests, created_on, description, event_date, initiator_id, location_id, paid, participant_limit, published_on, request_moderation, state, title, rating) values ('123', 1, 0, '2020-01-01 00:00:01', '456', '2020-05-01 00:00:01', 1, 1,true, 0, '2020-01-01 00:01:01', false, 'PUBLISHED', '---', 0);
insert into events (annotation, category_id, confirmed_requests, created_on, description, event_date, initiator_id, location_id, paid, participant_limit, published_on, request_moderation, state, title, rating) values ('123', 1, 0, '2020-01-01 00:00:01', '456', '2020-05-01 00:00:01', 2, 1,true, 0, '2020-01-01 00:01:01', false, 'PUBLISHED', '---', 0);

insert into participation_requests (created, event_id, requester_id, status) values ('2020-02-01 00:01:01', 1, 2, 'CONFIRMED');
insert into participation_requests (created, event_id, requester_id, status) values ('2020-02-01 00:01:01', 1, 3, 'CONFIRMED');
insert into participation_requests (created, event_id, requester_id, status) values ('2020-02-02 00:01:01', 1, 4, 'CONFIRMED');

insert into participation_requests (created, event_id, requester_id, status) values ('2020-02-01 00:01:01', 2, 1, 'CONFIRMED');
insert into participation_requests (created, event_id, requester_id, status) values ('2020-02-01 00:01:01', 2, 3, 'CONFIRMED');
insert into participation_requests (created, event_id, requester_id, status) values ('2020-02-02 00:01:01', 2, 4, 'CONFIRMED');


insert into likes (event_id, user_id, is_like) values (1, 2, true);
insert into likes (event_id, user_id, is_like) values (1, 3, false);
insert into likes (event_id, user_id, is_like) values (1, 4, true);

insert into likes (event_id, user_id, is_like) values (2, 1, true);
insert into likes (event_id, user_id, is_like) values (2, 3, false);
insert into likes (event_id, user_id, is_like) values (2, 4, true);