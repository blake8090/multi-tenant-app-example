create table Book (
    id int8 not null,
    title varchar(255),
    primary key (id)
);

INSERT INTO Book (id, title) VALUES (1, 'The C++ Programming Language');
