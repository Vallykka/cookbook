create table if not exists product (
    id uuid primary key default uuid_generate_v4 (),
    name text,
    type product_type
);