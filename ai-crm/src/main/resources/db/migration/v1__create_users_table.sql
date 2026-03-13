create table if not exists users (
    id bigserial primary key,
    user_id text unique not null,
    name text not null,
    email text unique not null,
    phone text unique,
    role text not null,
    employment_type text not null,
    password text not null,
    date_of_joining date,
    position text,
    profile text
);
