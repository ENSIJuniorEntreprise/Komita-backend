--
-- PostgreSQL database dump
--

-- Dumped from database version 15.3
-- Dumped by pg_dump version 15.3

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
-- Name: adress; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.adress (
    id bigint NOT NULL,
    city character varying(255),
    country character varying(255),
    postal_code character varying(255),
    province_name character varying(255),
    street_name character varying(255),
    street_number bigint,
    street_type character varying(255)
);


--
-- Name: adress_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.adress_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: adress_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.adress_id_seq OWNED BY public.adress.id;


--
-- Name: category; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.category (
    id bigint NOT NULL,
    created_date timestamp(6) with time zone,
    last_modified_by character varying(50),
    last_modified_date timestamp(6) with time zone,
    name character varying(255)
);


--
-- Name: category_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.category_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: consultation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.consultation (
    id bigint NOT NULL,
    checked boolean NOT NULL,
    consulting_date timestamp(6) without time zone NOT NULL,
    service_id bigint NOT NULL,
    user_id bigint NOT NULL
);


--
-- Name: consultation_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.consultation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: consultation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.consultation_id_seq OWNED BY public.consultation.id;


--
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: -
--

-- CREATE TABLE public.flyway_schema_history (
--     installed_rank integer NOT NULL,
--     version character varying(50),
--     description character varying(200) NOT NULL,
--     type character varying(20) NOT NULL,
--     script character varying(1000) NOT NULL,
--     checksum integer,
--     installed_by character varying(100) NOT NULL,
--     installed_on timestamp without time zone DEFAULT now() NOT NULL,
--     execution_time integer NOT NULL,
--     success boolean NOT NULL
-- );


--
-- Name: image; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.image (
    id bigint NOT NULL,
    imageurl character varying(255),
    service_id bigint
);


--
-- Name: image_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.image_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: image_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.image_id_seq OWNED BY public.image.id;


--
-- Name: keyword_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.keyword_table (
    id bigint NOT NULL,
    keyword_name character varying(255),
    service_id bigint NOT NULL
);


--
-- Name: keyword_table_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.keyword_table_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: keyword_table_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.keyword_table_id_seq OWNED BY public.keyword_table.id;


--
-- Name: links; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.links (
    id bigint NOT NULL,
    facebookurl character varying(255),
    instagramurl character varying(255),
    websiteurl character varying(255)
);


--
-- Name: links_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.links_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: links_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.links_id_seq OWNED BY public.links.id;


--
-- Name: search_history; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.search_history (
    id bigint NOT NULL,
    search_query character varying(255),
    "timestamp" timestamp(6) without time zone,
    user_id bigint
);


--
-- Name: search_history_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.search_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: search_history_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.search_history_id_seq OWNED BY public.search_history.id;


--
-- Name: search_result; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.search_result (
    id bigint NOT NULL,
    search_history_id bigint
);


--
-- Name: search_result_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.search_result_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: search_result_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.search_result_id_seq OWNED BY public.search_result.id;


--
-- Name: search_result_service; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.search_result_service (
    search_result_id bigint NOT NULL,
    service_id bigint NOT NULL
);


--
-- Name: service_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.service_table (
    id bigint NOT NULL,
    checked boolean,
    created_at timestamp(6) without time zone NOT NULL,
    description character varying(255),
    name character varying(255),
    state boolean,
    updated_at timestamp(6) without time zone NOT NULL,
    adress_id bigint,
    category_id bigint,
    links_id bigint,
    professional_id bigint,
    subcategory_id bigint
);


--
-- Name: service_table_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.service_table_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: subcategory; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.subcategory (
    id bigint NOT NULL,
    created_date timestamp(6) with time zone,
    last_modified_by character varying(50),
    last_modified_date timestamp(6) with time zone,
    name character varying(255),
    category_id bigint NOT NULL
);


--
-- Name: subcategory_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.subcategory_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: token; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.token (
    id integer NOT NULL,
    expired boolean NOT NULL,
    revoked boolean NOT NULL,
    token character varying(255),
    token_type character varying(255),
    user_id bigint
);


--
-- Name: token_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.token_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: user_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_table (
    id bigint NOT NULL,
    custom_identifier character varying(255),
    email character varying(255),
    firstname character varying(255),
    lastname character varying(255),
    password character varying(255),
    profile_image oid,
    role character varying(255),
    status boolean NOT NULL,
    user_address_id bigint
);


--
-- Name: user_table_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.user_table_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: user_table_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.user_table_id_seq OWNED BY public.user_table.id;


--
-- Name: verification_token; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.verification_token (
    id bigint NOT NULL,
    expiration_time timestamp(6) without time zone,
    token character varying(255),
    user_id bigint
);


--
-- Name: verification_token_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.verification_token_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: verification_token_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.verification_token_id_seq OWNED BY public.verification_token.id;


--
-- Name: adress id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.adress ALTER COLUMN id SET DEFAULT nextval('public.adress_id_seq'::regclass);


--
-- Name: consultation id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.consultation ALTER COLUMN id SET DEFAULT nextval('public.consultation_id_seq'::regclass);


--
-- Name: image id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.image ALTER COLUMN id SET DEFAULT nextval('public.image_id_seq'::regclass);


--
-- Name: keyword_table id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.keyword_table ALTER COLUMN id SET DEFAULT nextval('public.keyword_table_id_seq'::regclass);


--
-- Name: links id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.links ALTER COLUMN id SET DEFAULT nextval('public.links_id_seq'::regclass);


--
-- Name: search_history id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.search_history ALTER COLUMN id SET DEFAULT nextval('public.search_history_id_seq'::regclass);


--
-- Name: search_result id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.search_result ALTER COLUMN id SET DEFAULT nextval('public.search_result_id_seq'::regclass);


--
-- Name: user_table id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_table ALTER COLUMN id SET DEFAULT nextval('public.user_table_id_seq'::regclass);


--
-- Name: verification_token id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.verification_token ALTER COLUMN id SET DEFAULT nextval('public.verification_token_id_seq'::regclass);


--
-- Name: adress adress_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.adress
    ADD CONSTRAINT adress_pkey PRIMARY KEY (id);


--
-- Name: category category_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (id);


--
-- Name: consultation consultation_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.consultation
    ADD CONSTRAINT consultation_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

-- ALTER TABLE ONLY public.flyway_schema_history
--     ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: image image_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.image
    ADD CONSTRAINT image_pkey PRIMARY KEY (id);


--
-- Name: keyword_table keyword_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.keyword_table
    ADD CONSTRAINT keyword_table_pkey PRIMARY KEY (id);


--
-- Name: links links_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.links
    ADD CONSTRAINT links_pkey PRIMARY KEY (id);


--
-- Name: search_history search_history_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.search_history
    ADD CONSTRAINT search_history_pkey PRIMARY KEY (id);


--
-- Name: search_result search_result_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.search_result
    ADD CONSTRAINT search_result_pkey PRIMARY KEY (id);


--
-- Name: service_table service_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.service_table
    ADD CONSTRAINT service_table_pkey PRIMARY KEY (id);


--
-- Name: subcategory subcategory_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subcategory
    ADD CONSTRAINT subcategory_pkey PRIMARY KEY (id);


--
-- Name: token token_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.token
    ADD CONSTRAINT token_pkey PRIMARY KEY (id);


--
-- Name: user_table uk_eamk4l51hm6yqb8xw37i23kb5; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_table
    ADD CONSTRAINT uk_eamk4l51hm6yqb8xw37i23kb5 UNIQUE (email);


--
-- Name: user_table uk_lkkau8tfh9nmse2tttwm446td; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_table
    ADD CONSTRAINT uk_lkkau8tfh9nmse2tttwm446td UNIQUE (custom_identifier);


--
-- Name: user_table user_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_table
    ADD CONSTRAINT user_table_pkey PRIMARY KEY (id);


--
-- Name: verification_token verification_token_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.verification_token
    ADD CONSTRAINT verification_token_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: -
--

-- CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- Name: verification_token fk140t81x9fnhachhvw7b2eaeo6; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.verification_token
    ADD CONSTRAINT fk140t81x9fnhachhvw7b2eaeo6 FOREIGN KEY (user_id) REFERENCES public.user_table(id);


--
-- Name: service_table fk9xhrag2vrf6lo33u9psiabp1j; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.service_table
    ADD CONSTRAINT fk9xhrag2vrf6lo33u9psiabp1j FOREIGN KEY (professional_id) REFERENCES public.user_table(id);


--
-- Name: search_result fkav2djlc2au70c2g6hw9o5y0gy; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.search_result
    ADD CONSTRAINT fkav2djlc2au70c2g6hw9o5y0gy FOREIGN KEY (search_history_id) REFERENCES public.search_history(id);


--
-- Name: service_table fkd9jk3iorfyuf7blcveapwpgqj; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.service_table
    ADD CONSTRAINT fkd9jk3iorfyuf7blcveapwpgqj FOREIGN KEY (links_id) REFERENCES public.links(id);


--
-- Name: subcategory fke4hdbsmrx9bs9gpj1fh4mg0ku; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subcategory
    ADD CONSTRAINT fke4hdbsmrx9bs9gpj1fh4mg0ku FOREIGN KEY (category_id) REFERENCES public.category(id) ON DELETE CASCADE;


--
-- Name: keyword_table fkedtnoo8jmtwkdp0b48ldiadp5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.keyword_table
    ADD CONSTRAINT fkedtnoo8jmtwkdp0b48ldiadp5 FOREIGN KEY (service_id) REFERENCES public.service_table(id) ON DELETE CASCADE;


--
-- Name: service_table fkegwq4ldpgjbwi88upg2mor4li; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.service_table
    ADD CONSTRAINT fkegwq4ldpgjbwi88upg2mor4li FOREIGN KEY (adress_id) REFERENCES public.adress(id);


--
-- Name: user_table fkf94y9rkygupb5r1j91u7jecw5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_table
    ADD CONSTRAINT fkf94y9rkygupb5r1j91u7jecw5 FOREIGN KEY (user_address_id) REFERENCES public.adress(id);


--
-- Name: token fkklog6ewt21jkbsmpp7b3htumq; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.token
    ADD CONSTRAINT fkklog6ewt21jkbsmpp7b3htumq FOREIGN KEY (user_id) REFERENCES public.user_table(id);


--
-- Name: search_result_service fkkxov2egjh1yu986h9y3clplxt; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.search_result_service
    ADD CONSTRAINT fkkxov2egjh1yu986h9y3clplxt FOREIGN KEY (search_result_id) REFERENCES public.search_result(id);


--
-- Name: consultation fklq3u67svn43cgbcfcfe52mjjp; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.consultation
    ADD CONSTRAINT fklq3u67svn43cgbcfcfe52mjjp FOREIGN KEY (user_id) REFERENCES public.user_table(id);


--
-- Name: service_table fkm9xiidk4yj8woa04b1orl98nc; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.service_table
    ADD CONSTRAINT fkm9xiidk4yj8woa04b1orl98nc FOREIGN KEY (subcategory_id) REFERENCES public.subcategory(id);


--
-- Name: search_history fkmommtuky12qrnm3dtquai90e3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.search_history
    ADD CONSTRAINT fkmommtuky12qrnm3dtquai90e3 FOREIGN KEY (user_id) REFERENCES public.user_table(id);


--
-- Name: search_result_service fkqdwayr41rywwlramxjkf7no7m; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.search_result_service
    ADD CONSTRAINT fkqdwayr41rywwlramxjkf7no7m FOREIGN KEY (service_id) REFERENCES public.service_table(id);


--
-- Name: service_table fkqggwubruc9jh45cf2dhn0gjyx; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.service_table
    ADD CONSTRAINT fkqggwubruc9jh45cf2dhn0gjyx FOREIGN KEY (category_id) REFERENCES public.category(id);


--
-- Name: consultation fksixob1ruxxjhuiaw3w65nsjek; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.consultation
    ADD CONSTRAINT fksixob1ruxxjhuiaw3w65nsjek FOREIGN KEY (service_id) REFERENCES public.service_table(id);


--
-- Name: image fkso4pjr2whcsaqxjbsv92m5mhe; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.image
    ADD CONSTRAINT fkso4pjr2whcsaqxjbsv92m5mhe FOREIGN KEY (service_id) REFERENCES public.service_table(id);


--
-- PostgreSQL database dump complete
--

