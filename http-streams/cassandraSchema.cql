CREATE KEYSPACE IF NOT EXISTS akka_streams WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1 };

CREATE TABLE IF NOT EXISTS akka_streams.users ( user_id text, user_name text, age int, PRIMARY KEY (user_id )) ;

CREATE TABLE IF NOT EXISTS akka_streams.user_tracking(user_id text, time timeuuid, event text, PRIMARY KEY (user_id, time));

INSERT INTO akka_streams.users (user_id, age, user_name) VALUES ( 'chbatey', 32, 'Christopher Batey') ;

