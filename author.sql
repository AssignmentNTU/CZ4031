drop table RAW_AUTHOR;
drop table pubauthor;
drop table article;
drop table incollection;
drop table inproceedings;
drop table book;
drop table proceedings;
drop table publication;
drop table author;


CREATE TABLE RAW_AUTHOR (AUTHOR_ID SERIAL PRIMARY KEY, AUTHOR_NAME TEXT);

CREATE TABLE author (AUTHOR_ID SERIAL PRIMARY KEY ,AUTHOR_NAME TEXT UNIQUE);


CREATE TABLE publication (PUBKEY TEXT PRIMARY KEY, TITLE TEXT,YEAR INT,
PAGES TEXT);


CREATE TABLE pubauthor ( AUTHOR_NAME TEXT REFERENCES author(AUTHOR_NAME)
                          , PUBKEY TEXT REFERENCES publication(PUBKEY));


CREATE TABLE article(PUBKEY TEXT REFERENCES publication(PUBKEY), JOURNAL TEXT, MONTH INT,
VOLUME INT, NUMBER INT);

x
CREATE TABLE incollection(PUBKEY TEXT REFERENCES publication(PUBKEY) , BOOK_TITLE TEXT);

CREATE TABLE inproceedings (PUBKEY TEXT REFERENCES publication(PUBKEY) , BOOK_TITLE TEXT);

CREATE TABLE book(PUBKEY TEXT REFERENCES publication(PUBKEY), ISBN TEXT, SERIES TEXT);

CREATE TABLE proceedings (PUBKEY TEXT REFERENCES publication(PUBKEY), BOOK_TITLE TEXT,
PUBLISHER TEXT, SERIES TEXT,VOLUME INT,ISBN TEXT);

