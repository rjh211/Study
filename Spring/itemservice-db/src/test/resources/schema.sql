--sql Script를 실행하여 애플리케이션 로딩 시점에 데이터베이스 초기화를 하는 기능(resource 하위의 schema.sql에 작성된 sql)
drop table if exists item CASCADE;
create table item
(
    id          bigint generated by default as identity,
    item_name   varchar(10),
    price       integer,
    quantity    integer,
    primary key (id)

);