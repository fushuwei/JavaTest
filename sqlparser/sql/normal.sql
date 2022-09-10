
select `id`, `xm` name, `nl` as age from t_user;

select `id`, `xm` name, `nl` as age from (select * from t_user);

select id, xm name, nl as age from t_user1 union select id, xm name, nl as age from t_user2;

select id, xm name, nl as age from t_user1 union all select id, xm name, nl as age from t_user2;
