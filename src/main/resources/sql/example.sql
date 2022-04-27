# test
-- test
drop table if exists account;
create table account(id int, balance float ,transaction_id int);
begin;
insert into account(id, balance, transaction_id) values(1,1,1),(2,2,2),(3,3,3),(4,4,4);
insert into account(id, balance, transaction_id) values(5,5,5);
commit;
-- test


