drop table promo_logs;
create table promo_logs (
  uuid long,date_awarded datetime, datetime_collected datetime, date_collected date, msisdn varchar(20),
  txn_type varchar(20), status_code varchar(3), narrative varchar(100), key (date_collected));