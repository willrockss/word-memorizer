CREATE TABLE IF NOT EXISTS captcha 
(
  question character varying NOT NULL UNIQUE,
  answer character varying NOT NULL
);

WITH data(question, answer) AS (
VALUES
	('Висит груша, нельзя скушать', 'лампочка'),
    ('Зимой и летом одним цветом', 'елка ёлка ель ёлочка елочка'),
	('Сидит девица в темнице, а коса на улице', 'морковь морковка свекла свёкла')
)
INSERT INTO captcha(question, answer)
SELECT data.question, data.answer
FROM data
WHERE NOT EXISTS (SELECT 1 FROM captcha c2 WHERE c2.question = data.question);
