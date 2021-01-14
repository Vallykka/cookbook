create table if not exists dish (
    id uuid primary key default uuid_generate_v4 (),
    name text,
    algo text,
    ingredients jsonb
);