create table if not exists category (
    id bigserial primary key,
    title text not null,
    description text,
    created_at timestamp default now(),
    updated_at timestamp default now()
);
