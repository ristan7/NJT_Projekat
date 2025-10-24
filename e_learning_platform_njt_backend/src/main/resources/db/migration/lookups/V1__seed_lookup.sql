-- V1__seed_lookup.sql
-- Samo inicijalni lookup podaci (Hibernate već kreira tabele)

-- Uloge korisnika
INSERT INTO role (role_name) VALUES 
('STUDENT'), 
('TEACHER'), 
('ADMIN');

-- Nivoi kursa
INSERT INTO course_level (course_level_name) VALUES 
('BEGINNER'), 
('INTERMEDIATE'), 
('ADVANCED');

-- Statusi kursa
INSERT INTO course_status (course_status_name) VALUES 
('DRAFT'), 
('PUBLISHED'), 
('ARCHIVED');

-- Tipovi lekcija
INSERT INTO lesson_type (lesson_type_name) VALUES 
('VIDEO'), 
('ARTICLE'), 
('QUIZ'), 
('ASSIGNMENT');

-- Tipovi materijala
INSERT INTO material_type (material_type_name) VALUES 
('PDF'), 
('IMAGE'), 
('LINK'), 
('PRESENTATION'), 
('VIDEO');

-- Statusi upisa (enrollment)
INSERT INTO enrollment_status (enrollment_status_name) VALUES 
('REQUESTED'), 
('ACTIVE'), 
('COMPLETED'), 
('CANCELLED'), 
('SUSPENDED');

-- Tipovi notifikacija (bez payment-a)
INSERT INTO notification_type (notification_type_name) VALUES 
('SYSTEM'), 
('ENROLLMENT'), 
('COURSE'), 
('CERTIFICATE'), 
('REVIEW');


