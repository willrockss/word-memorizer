-- first of all create tables for authentication
CREATE TABLE users
(
  username character varying NOT NULL,
  password character varying NOT NULL,
  enabled boolean,
  CONSTRAINT u_username UNIQUE (username)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE authorities
(
  username character varying,
  authority character varying,
  CONSTRAINT fk_username FOREIGN KEY (username)
      REFERENCES users (username) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

-- add default admin user with 'admin' password and ADMIN role
INSERT INTO users(username, password, enabled)
VALUES ('admin', '$2a$10$UuP7OjR6XGhoGf/POC/Zo.bpnTlSWdVWgRQ4ZUSEMdr1pUgAOPKhu', TRUE);


INSERT INTO authorities(username, authority)
VALUES ('admin', 'ADMIN');


-- Add utils tables
CREATE TABLE action_log
(
  username character varying NOT NULL,
  action_name character varying NOT NULL,
  details character varying,
  action_time timestamp with time zone,
  CONSTRAINT action_log_username_fkey FOREIGN KEY (username)
      REFERENCES users (username) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

CREATE TABLE app_property
(
  key character varying NOT NULL,
  value character varying NOT NULL,
  CONSTRAINT app_property_key_key UNIQUE (key)
)
WITH (
  OIDS=FALSE
);


CREATE SEQUENCE word_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

-- Add application's data tables
CREATE TABLE word
(
  id integer NOT NULL DEFAULT nextval('word_id_seq'::regclass),
  value character varying NOT NULL,
  video_page character varying,
  video_url character varying,
  video_id integer,
  loaded boolean,
  created timestamp without time zone NOT NULL,
  CONSTRAINT sign_word_pkey2 PRIMARY KEY (id),
  CONSTRAINT sign_word_video_id_unique UNIQUE (video_id)
)
WITH (
  OIDS=FALSE
);


CREATE SEQUENCE dict_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

CREATE TABLE dict
(
  id integer NOT NULL DEFAULT nextval('dict_id_seq'::regclass),
  name character varying,
  CONSTRAINT dict_pkey PRIMARY KEY (id),
  CONSTRAINT dict_name_key UNIQUE (name)
)
WITH (
  OIDS=FALSE
);

CREATE SEQUENCE word_dict_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

CREATE TABLE word_dict
(
  id integer NOT NULL DEFAULT nextval('word_dict_id_seq'::regclass),
  video_id integer,
  dict_id integer,
  CONSTRAINT word_dict_pkey PRIMARY KEY (id),
  CONSTRAINT word_dict_dict_id_fkey FOREIGN KEY (dict_id)
      REFERENCES dict (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT word_dict_video_id_fkey FOREIGN KEY (video_id)
      REFERENCES word (video_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);


CREATE SEQUENCE word_progress_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

CREATE TABLE word_progress
(
  id integer NOT NULL DEFAULT nextval('word_progress_id_seq'::regclass),
  video_id integer,
  count integer,
  last_time timestamp with time zone NOT NULL,
  first_time timestamp with time zone,
  username character varying,
  CONSTRAINT word_progress_pkey PRIMARY KEY (id),
  CONSTRAINT fk_username FOREIGN KEY (username)
      REFERENCES users (username) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_video_id FOREIGN KEY (video_id)
      REFERENCES word (video_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT word_progress_video_id_fkey FOREIGN KEY (video_id)
      REFERENCES word (video_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);