create table users(
                      id uuid primary key,
                      login varchar(255) not null UNIQUE,
                      password varchar(255) not null,
                      name varchar(255) not null,
                      registered_at timestamp
);