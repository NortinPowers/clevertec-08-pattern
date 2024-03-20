drop table if exists products cascade;

create table products
(
    uuid        uuid default gen_random_uuid() not null
        constraint products_pk
            primary key,
    name        varchar(22)                    not null
        constraint check_name
            check (char_length((name)::text) >= 5),
    description varchar(52)                    not null
        constraint check_description
            check (char_length((description)::text) >= 10),
    price       numeric                        not null
        constraint check_price
            check (price > (0)::numeric),
    created     timestamp with time zone       not null
);
