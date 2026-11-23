INSERT INTO offices (name, location, office_number)
SELECT 'Reception', 'Ground floor', 1
WHERE NOT EXISTS (SELECT 1 FROM offices WHERE office_number = 1);

INSERT INTO offices (name, location, office_number)
SELECT 'Consultation Room A', 'First floor', 2
WHERE NOT EXISTS (SELECT 1 FROM offices WHERE office_number = 2);

INSERT INTO offices (name, location, office_number)
SELECT 'Consultation Room B', 'First floor', 3
WHERE NOT EXISTS (SELECT 1 FROM offices WHERE office_number = 3);
