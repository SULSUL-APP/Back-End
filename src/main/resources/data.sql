insert into users(
    user_id,
    name,
    email,
    u_type,
    e_type,
    login_type
) values (1, '김경근', 'sulsul@gmail.com', 'STUDENT', 'NATURE', 'KAKAO');

insert into users(
    user_id,
    name,
    email,
    u_type,
    e_type,
    login_type,
    catch_phrase
) values (2, '임탁균', 'sulsul@naver.com', 'TEACHER', 'NATURE', 'APPLE', '화이링');

insert into essays(
    essay_id,
    univ,
    e_type,
    exam_year,
    essay_state,
    inquiry,
    review_state,
    student_id,
    teacher_id
) values (1, '홍익대', '수리', '2022', 'REQUEST', '문의내용', 'OFF', 1, 2);