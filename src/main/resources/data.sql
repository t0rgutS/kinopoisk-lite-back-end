--Filling database with predefined age ratings
INSERT INTO age_ratings(age_rating_id, rating_category, min_age)
VALUES ('74bb600b-b337-46dd-931b-2d67b3bde2c4', '6+', 6),
      ('be3be8da-028d-4411-ac71-03eb1e06fd20', '12+', 12),
      ('9cda921f-1bad-439d-8f4b-9aaf0297e014', '16+', 16),
      ('f1997889-dbf8-4ad4-b349-f370f3034602', '18+', 18) ON CONFLICT DO NOTHING;