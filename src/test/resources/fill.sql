truncate table products restart identity;

insert into products (name, description, price, created)
values ('Плюмбус', 'это универсальное устройство', 215, '1999-01-08 04:05:06+00'),
       ('Портальная пушка', 'устройство создающее порталы', 4200, '2013-02-09 05:07:07+00');

update products
set uuid = '76dbb74c-2f08-4bc0-8029-aed02147e737'
where name = 'Плюмбус';

