--SQL 관리 파일

insert into member(name) values('spring')

create table member
{
    id bigint generated by default as identity,
    name varchar(255),
    primary key(id)
}