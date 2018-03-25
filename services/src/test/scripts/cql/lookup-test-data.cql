-- INSERT INTO friend_list () VALUES ();
-- country list
INSERT INTO country_list (country_name, country_code) VALUES ('United States', 1);

-- users
INSERT INTO users (user_id, user_name, country_code, state, county, city, zipcode, first_name, middle_name, last_name, phone, email, skills, interests) VALUES (3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7, 'first user', 1, 'Maryland', 'Prince George', 'College Park', '20740', 'Daniel', '', 'Lee', '7089894563', 'testUser@gmail.com', {'Java': 'Medium', 'Javascript': 'Export', 'Scala':'Beginner'}, {'stock trading','taxes holdem','plumber'});
INSERT INTO users (user_id, user_name, country_code, state, county, city, zipcode, first_name, middle_name, last_name, phone, email, skills, interests) VALUES (6a07917c-b5d3-4b89-88cd-9108e68cfba9, 'second user', 1, 'Virginia', 'Fairfax', 'Fairfax', '20370', 'Wang', '', 'Ming', '123456789', 'testUser2@gmail.com', {'jsp': 'Medium', 'home decoration': 'Export', 'holdem':'Beginner'}, {'java','javascript','scala'});

-- people watching
INSERT INTO friend_list (user_id1, user_id2, update_time) VALUES (3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7, 6a07917c-b5d3-4b89-88cd-9108e68cfba9, '2015-4-7');
INSERT INTO friend_list (user_id1, user_id2, update_time) VALUES (3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7, 1a76e2ee-7aec-40ed-a4b6-53cf580c2f48, '2014-4-7');

-- skill blur search table
INSERT INTO skills (search_key, skill_name) VALUES ('j', 'java');
INSERT INTO skills (search_key, skill_name) VALUES ('ja', 'java');
INSERT INTO skills (search_key, skill_name) VALUES ('jav', 'java');
INSERT INTO skills (search_key, skill_name) VALUES ('java', 'java');
INSERT INTO skills (search_key, skill_name) VALUES ('j', 'javascript');
INSERT INTO skills (search_key, skill_name) VALUES ('ja', 'javascript');
INSERT INTO skills (search_key, skill_name) VALUES ('jav', 'javascript');
INSERT INTO skills (search_key, skill_name) VALUES ('java', 'javascript');
INSERT INTO skills (search_key, skill_name) VALUES ('javas', 'javascript');
INSERT INTO skills (search_key, skill_name) VALUES ('javasc', 'javascript');
INSERT INTO skills (search_key, skill_name) VALUES ('javascr', 'javascript');
INSERT INTO skills (search_key, skill_name) VALUES ('javascri', 'javascript');
INSERT INTO skills (search_key, skill_name) VALUES ('javascrip', 'javascript');
INSERT INTO skills (search_key, skill_name) VALUES ('javascript', 'javascript');
INSERT INTO skills (search_key, skill_name) VALUES ('j', 'jsp');
INSERT INTO skills (search_key, skill_name) VALUES ('js', 'jsp');
INSERT INTO skills (search_key, skill_name) VALUES ('jsp', 'jsp');
INSERT INTO skills (search_key, skill_name) VALUES ('s', 'scala');
INSERT INTO skills (search_key, skill_name) VALUES ('sc', 'scala');
INSERT INTO skills (search_key, skill_name) VALUES ('sca', 'scala');
INSERT INTO skills (search_key, skill_name) VALUES ('scal', 'scala');
INSERT INTO skills (search_key, skill_name) VALUES ('scala', 'scala');
INSERT INTO skills (search_key, skill_name) VALUES ('h', 'home decoration');
INSERT INTO skills (search_key, skill_name) VALUES ('ho', 'home decoration');
INSERT INTO skills (search_key, skill_name) VALUES ('hom', 'home decoration');
INSERT INTO skills (search_key, skill_name) VALUES ('home', 'home decoration');
INSERT INTO skills (search_key, skill_name) VALUES ('homed', 'home decoration');
INSERT INTO skills (search_key, skill_name) VALUES ('homede', 'home decoration');
INSERT INTO skills (search_key, skill_name) VALUES ('homedec', 'home decoration');
INSERT INTO skills (search_key, skill_name) VALUES ('homedeco', 'home decoration');
INSERT INTO skills (search_key, skill_name) VALUES ('homedecor', 'home decoration');
INSERT INTO skills (search_key, skill_name) VALUES ('homedecora', 'home decoration');
INSERT INTO skills (search_key, skill_name) VALUES ('homedecorat', 'home decoration');
INSERT INTO skills (search_key, skill_name) VALUES ('homedecorati', 'home decoration');
INSERT INTO skills (search_key, skill_name) VALUES ('homedecoratio', 'home decoration');
INSERT INTO skills (search_key, skill_name) VALUES ('homedecoration', 'home decoration');
INSERT INTO skills (search_key, skill_name) VALUES ('h', 'holdem');
INSERT INTO skills (search_key, skill_name) VALUES ('ho', 'holdem');
INSERT INTO skills (search_key, skill_name) VALUES ('hol', 'holdem');
INSERT INTO skills (search_key, skill_name) VALUES ('hold', 'holdem');
INSERT INTO skills (search_key, skill_name) VALUES ('holde', 'holdem');
INSERT INTO skills (search_key, skill_name) VALUES ('holdem', 'holdem');


-- person skill table
INSERT INTO person_skill_1 (skill_name,state, county, city, zipcode, user_id) VALUES ('java', 'Maryland', 'Prince George', 'College Park', '20740', 3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7);
INSERT INTO person_skill_1 (skill_name,state, county, city, zipcode, user_id) VALUES ('javascript', 'Maryland', 'Prince George', 'College Park', '20740', 3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7);
INSERT INTO person_skill_1 (skill_name,state, county, city, zipcode, user_id) VALUES ('scala', 'Maryland', 'Prince George', 'College Park', '20740', 3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7);
INSERT INTO person_skill_1 (skill_name,state, county, city, zipcode, user_id) VALUES ('jsp', 'Virginia', 'Fairfax', 'Fairfax', '20370', 6a07917c-b5d3-4b89-88cd-9108e68cfba9);
INSERT INTO person_skill_1 (skill_name,state, county, city, zipcode, user_id) VALUES ('holdem', 'Virginia', 'Fairfax', 'Fairfax', '20370', 6a07917c-b5d3-4b89-88cd-9108e68cfba9);
INSERT INTO person_skill_1 (skill_name,state, county, city, zipcode, user_id) VALUES ('home decoration', 'Virginia', 'Fairfax', 'Fairfax', '20370', 6a07917c-b5d3-4b89-88cd-9108e68cfba9);


-- message
INSERT INTO messages (message_from, message, message_to, read, insert_time) VALUES (3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7, 'do you have to team up?', 6a07917c-b5d3-4b89-88cd-9108e68cfba9, false, '2015-4-7');
INSERT INTO messages (message_from, message, message_to, read, insert_time) VALUES (3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7, 'I have sth you may interested', 6a07917c-b5d3-4b89-88cd-9108e68cfba9, false, '2013-10-10');