/*
    Создаем табличку, в которую будут записываться атрибуты словарей
*/

CREATE TABLE IF NOT EXISTS dict_attribute_value
(
  dict_id integer NOT NULL REFERENCES dict (id),
  attribute_code character varying NOT NULL,
  string_value character varying,
  timestamp_value timestamp with time zone,
  number_value numeric
);

-- У всех существующих словарей выставляем в качестве автора 'admin'
INSERT INTO dict_attribute_value(dict_id, attribute_code, string_value)
SELECT d.id, 'AUTHOR', 'admin'
FROM dict d
WHERE NOT EXISTS (SELECT 1 FROM dict_attribute_value WHERE dict_id = d.id AND attribute_code = 'AUTHOR');