CREATE KEYSPACE IF NOT EXISTS dm_national
WITH replication = {
'class': 'SimpleStrategy',
'replication_factor': '1'
};

USE dm_national;

--CONSISTENCY ALL;

TRUNCATE users;
DROP TABLE users;

CREATE TABLE users (
user_id uuid,
user_name text,
country_code int,
state text,
county text,
city text,
zipcode text,
first_name text,
middle_name text,
last_name text,
phone text,
email text,
skills map<text,text>, --skill name and level
interests set<text>,
PRIMARY KEY(user_id)
);

TRUNCATE friend_list;

DROP TABLE friend_list;
--user_id1 watched user_id2
CREATE TABLE friend_list (
user_id1 uuid,
user_id2 uuid,
update_time text,
PRIMARY KEY(user_id1, user_id2)
);

--the search table
TRUNCATE skills;

DROP TABLE skills;
--one skill will become several records here to do blur search
--eg: java --> j, java: ja, java: jav, java: java, java
CREATE TABLE skills (
search_key text,
skill_name text,
user_id uuid,
PRIMARY KEY(search_key, skill_name)
);

TRUNCATE person_skill_1;
DROP TABLE person_skill_1;

CREATE TABLE person_skill_1 (
skill_name text,
state text,
county text,
city text,
zipcode text,
user_id uuid,
update_time text,
PRIMARY KEY(skill_name, state, county, city, zipcode, user_id)
);

TRUNCATE country_list;
DROP TABLE country_list;

CREATE TABLE country_list (
country_name text,
country_code int,
PRIMARY KEY(country_name, country_code)
);

TRUNCATE geolocations;
DROP TABLE geolocations;

CREATE TABLE geolocations (
country_code bigint,
country_name text,
state text,
county text,
city text,
zipcode text,
PRIMARY KEY(country_code, state, county, city, zipcode)
);

TRUNCATE messages;
DROP TABLE messages;

CREATE TABLE messages (
message_from uuid,
message text,
message_to uuid,
read boolean,
insert_time text, --CEDate
PRIMARY KEY(message_to, message_from, message)
);