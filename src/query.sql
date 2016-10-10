--1
(SELECT 'Article' AS type, count(*) AS cnt FROM article)
UNION
(SELECT 'Incollection' AS type, count(*) AS cnt FROM incollection)
UNION
(SELECT 'Inproceedings' AS type, count(*) AS cnt FROM inproceedings)
UNION
(SELECT 'Book' AS type, count(*) AS cnt FROM book)
UNION
(SELECT 'Proceedings' AS type, count(*) AS cnt FROM proceedings);

--2
SELECT author_name
FROM pubauthor
GROUP BY author_name
ORDER BY COUNT(*);
LIMIT 10;

--3
CREATE OR REPLACE VIEW pubview AS(
SELECT *
FROM pubauthor NATURAL JOIN author NATURAL JOIN  publication;
);
--3.1
SELECT *
FROM pubview
WHERE author_name = 'X' and year = 2012;
--3.2
SELECT *
FROM pubview
WHERE author_name = 'X' AND pubkey LIKE 'conf/ticttl%' AND year = 2011;
--3.3
SELECT AUTHOR_NAME
FROM pubview
WHERE pubkey LIKE 'conf/compimage%' AND year = 2006
GROUP BY AUTHOR_NAME
HAVING COUNT(*) >= 2;

--4.1
(SELECT AUTHOR_NAME
FROM pubauthor
WHERE PUBKEY LIKE '%pvldb%'
GROUP BY AUTHOR_NAME
HAVING COUNT(*) >= 10)
INTERSECT
(SELECT AUTHOR_NAME
FROM pubauthor
WHERE PUBKEY LIKE '%sigmod%'
GROUP BY AUTHOR_NAME
HAVING COUNT(*) >= 10);

--4.2
(SELECT AUTHOR_NAME
FROM pubauthor
WHERE PUBKEY LIKE '%pvldb%'
GROUP BY AUTHOR_NAME
HAVING COUNT(*) >= 10)
EXCEPT
(SELECT AUTHOR_NAME
FROM pubauthor
WHERE PUBKEY LIKE '%kdd%');




--5
CREATE OR REPLACE FUNCTION totalPublicationsIn5Years(IN yearIn Integer)
RETURNS Integer AS $total$
DECLARE total Integer; counter Integer;
BEGIN
total := 0;
counter := 0;
LOOP
 total := total + (SELECT COUNT(*) FROM publication WHERE year = yearIn + counter);
 counter := counter + 1;
 EXIT WHEN counter = 5;
END LOOP;
RETURN total;
END;
$total$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION totalPublication(IN yearStart Integer, IN yearEnd Integer)
RETURNS TABLE(startYr Integer, endYr Integer, total Integer) AS $result$
DECLARE total_tmp Integer; counter Integer;
BEGIN
CREATE TEMP TABLE result (
 startYr Integer,
 endYr Integer,
 total Integer
);
counter := yearStart;
LOOP
 total_tmp := (SELECT totalPublicationsIn5Years(counter));
 INSERT INTO result (startYr, endYr, total) VALUES (counter, counter + 4, total_tmp);
 counter := counter + 5;
 EXIT WHEN counter > yearEnd;
END LOOP;
RETURN QUERY (SELECT * FROM result);
END;
$result$ LANGUAGE plpgsql;


SELECT totalPublication(1970, 2015);




--6
CREATE OR REPLACE FUNCTION findMostCollaborativeAuthor()
RETURNS TABLE(author_name Text, coauthor_no Numeric) AS $result$
BEGIN
CREATE TEMP TABLE temp1 AS
 SELECT title,count(*)-1 as num_count
 FROM publication
 GROUP BY title;
CREATE TEMP TABLE temp2 AS
 SELECT *
 FROM PUBLICATION NATURAL JOIN temp1 NATURAL JOIN pubauthor;
RETURN QUERY (SELECT temp2.author_name,sum(num_count) AS coauthor_no
 FROM temp2
 GROUP BY temp2.author_name
 ORDER BY sum(num_count) DESC LIMIT 1);
END;
$result$ LANGUAGE plpgsql;


SELECT findMostCollaborativeAuthor();




--7

SELECT articleview.AUTHOR_NAME
FROM (pubview NATURAL JOIN ARTICLE) AS articleview
WHERE articleview.year > 2011 AND ( articleview.pubkey LIKE 'journal%' OR articleview.pubkey LIKE 'conf%')
AND journal IS NOT NULL AND articleview.title LIKE '%Data%'
GROUP BY articleview.AUTHOR_NAME
ORDER BY COUNT(*) DESC
LIMIT 10;

--8
SELECT title
FROM publication
WHERE YEAR > 2010;

--9
SELECT *
FROM book JOIN publication ON book.pubkey = publication.pubkey;


--9.1
SELECT *
FROM (book NATURAL JOIN pubauthor NATURAL JOIN author)
WHERE ISBN is null;