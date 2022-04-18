--
-- PostgreSQL database dump
--

-- Dumped from database version 14.0
-- Dumped by pg_dump version 14.0

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: event_attendees; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.event_attendees (
    event_id integer NOT NULL,
    user_email character varying NOT NULL
);


ALTER TABLE public.event_attendees OWNER TO postgres;

--
-- Name: events; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.events (
    id integer NOT NULL,
    description character varying,
    date character varying
);


ALTER TABLE public.events OWNER TO postgres;

--
-- Name: friend_requests; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.friend_requests (
    from_user character varying NOT NULL,
    to_user character varying NOT NULL,
    status character varying,
    sent_on character varying
);


ALTER TABLE public.friend_requests OWNER TO postgres;

--
-- Name: friendships; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.friendships (
    leftv character varying NOT NULL,
    rightv character varying NOT NULL,
    datef character varying
);


ALTER TABLE public.friendships OWNER TO postgres;

--
-- Name: messages; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.messages (
    id integer NOT NULL,
    fromtbl character varying,
    totbl character varying,
    messagetbl character varying,
    replytbl integer,
    datetbl character varying
);


ALTER TABLE public.messages OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    firstname character varying,
    lastname character varying,
    email character varying NOT NULL,
    password character varying
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Data for Name: event_attendees; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.event_attendees (event_id, user_email) FROM stdin;
3	dianahotca42@yahoo.com
1	dianahotca42@yahoo.com
2	dianahotca42@yahoo.com
5	dianahotca42@yahoo.com
\.


--
-- Data for Name: events; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.events (id, description, date) FROM stdin;
3	Coffee friday	2022-01-21
4	Meet and greet	2022-01-17
1	5 o'clock tea	2022-01-15
2	Girl talk	2022-01-16
5	Project presentation	2022-01-16
\.


--
-- Data for Name: friend_requests; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.friend_requests (from_user, to_user, status, sent_on) FROM stdin;
dianahotca42@yahoo.com	banalex@yahoo.com	approved	2022-01-03 12:41
dianahotca42@yahoo.com	darialacatos@yahoo.com	approved	2022-01-05 17:04
dianahotca42@yahoo.com	marianhotca@yahoo.com	pending	2022-01-15 01:37
dianahotca42@yahoo.com	andaionescu@yahoo.com	pending	2022-01-15 01:37
\.


--
-- Data for Name: friendships; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.friendships (leftv, rightv, datef) FROM stdin;
dianahotca42@yahoo.com	banalex@yahoo.com	2022-01-03 12:42
dianahotca42@yahoo.com	darialacatos@yahoo.com	2022-01-05 17:05
\.


--
-- Data for Name: messages; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.messages (id, fromtbl, totbl, messagetbl, replytbl, datetbl) FROM stdin;
1	dianahotca42@yahoo.com	ionescuanda@yahoo.com gsarca@yahoo.com	Salut!	0	2020-01-03 19:40
2	ionescuanda@yahoo.com	dianahotca42@yahoo.com	Hei!	1	2020-01-04 19:50
3	ionescuanda@yahoo.com	gsarca@yahoo.com dianahotca42@yahoo.com	Hei2!	1	2022-01-03 19:44
4	gsarca@yahoo.com	dianahotca42@yahoo.com ionescuanda@yahoo.com	Saluatre!	0	2022-01-03 19:51
5	dianahotca42@yahoo.com	ionescuanda@yahoo.com gsarca@yahoo.com	Salut!	4	2022-01-03 19:52
6	dianahotca42@yahoo.com	gsarca@yahoo.com	Ce faci?	0	2022-01-10 13:37
7	dianahotca42@yahoo.com	darialacatos@yahoo.com banalex@yahoo.com	Hello guys	0	2022-01-13 15:04
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (firstname, lastname, email, password) FROM stdin;
Diana	Hotca	dianahotca42@yahoo.com	RGlhbmEyMDAx
Anda	Ionescu	andaionescu@yahoo.com	SW9uZXNjdTI=
George	Sarca	gsarca@yahoo.com	U2FyY2FhYTI=
Ana	Hotca	anahotca@yahoo.com	QW5haDE5NjQ=
Marian	Hotca	marianhotca@yahoo.com	TWFyaWFuMTk5Mg==
Teodora	Moldovan	mteodora@yahoo.com	RG9yYWFhMjAwMQ==
Diana	Anton	dianaanton10@yahoo.com	RGFudG9uMjAwMg==
Flavia	Anton	fanton@yahoo.com	RmxhdmlhMjAwMg==
Daria	Lacatos	darialacatos@yahoo.com	RGFyaWEyMDAw
Alex	Ban	banalex@yahoo.com	QWxleGJhbjIw
\.


--
-- Name: event_attendees event_attendees_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_attendees
    ADD CONSTRAINT event_attendees_pk PRIMARY KEY (user_email, event_id);


--
-- Name: events events_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.events
    ADD CONSTRAINT events_pk PRIMARY KEY (id);


--
-- Name: friend_requests friendrequests_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friend_requests
    ADD CONSTRAINT friendrequests_pk PRIMARY KEY (from_user, to_user);


--
-- Name: friendships friendships_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friendships
    ADD CONSTRAINT friendships_pk PRIMARY KEY (leftv, rightv);


--
-- Name: messages messages_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_pk PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (email);


--
-- Name: event_attendees event_attendees___fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_attendees
    ADD CONSTRAINT event_attendees___fk FOREIGN KEY (event_id) REFERENCES public.events(id);


--
-- Name: event_attendees event_attendees_users_email_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event_attendees
    ADD CONSTRAINT event_attendees_users_email_fk FOREIGN KEY (user_email) REFERENCES public.users(email);


--
-- PostgreSQL database dump complete
--

