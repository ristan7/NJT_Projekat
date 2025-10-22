-- V1__seed_lookup.sql
-- Samo inicijalni podaci (Hibernate je već kreirao tabele).

INSERT INTO role (role_name) VALUES
  ('STUDENT'), ('TEACHER'), ('ADMIN');

INSERT INTO course_level (course_level_name) VALUES
  ('BEGINNER'), ('INTERMEDIATE'), ('ADVANCED');

INSERT INTO course_status (course_status_name) VALUES
  ('DRAFT'), ('PUBLISHED'), ('ARCHIVED');

INSERT INTO lesson_type (lesson_type_name) VALUES
  ('VIDEO'), ('ARTICLE'), ('QUIZ'), ('ASSIGNMENT');

INSERT INTO material_type (material_type_name) VALUES
  ('PDF'), ('IMAGE'), ('LINK'), ('PRESENTATION'), ('VIDEO');

INSERT INTO enrollment_status (enrollment_status_name) VALUES
  ('REQUESTED'), ('ACTIVE'), ('COMPLETED'), ('CANCELLED'), ('SUSPENDED');

INSERT INTO payment_status (payment_status_name) VALUES
  ('INITIATED'), ('SUCCESS'), ('FAILED'), ('REFUNDED');

INSERT INTO notification_type (notification_type_name) VALUES
  ('SYSTEM'), ('ENROLLMENT'), ('PAYMENT'), ('COURSE'), ('CERTIFICATE'), ('REVIEW');
