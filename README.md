# E-Learning Platform (Spring Boot + React)

This repository contains a university project developed for academic purposes as part of the **NJT / software engineering coursework**.

The application is a **full-stack e-learning platform** built with:

- **Spring Boot** backend
- **React** frontend
- **MySQL** database
- **JWT authentication**
- **role-based access control**

It supports core e-learning functionality such as user authentication, course browsing, course and lesson management, enrollments, and notifications.

---

## Project Overview

The system is designed around three main user roles:

- **STUDENT**
- **TEACHER**
- **ADMIN**

### Main features

#### Authentication & Users
- User registration and login
- JWT-based authentication
- Current user profile endpoint
- Role-based authorization
- Admin ability to change user roles

#### Courses
- Browse all available courses
- Search courses by title
- View course details
- Create, update, and delete courses (teacher only)
- Update course title, description, level, and status

#### Lessons & Materials
- Lessons belong to courses
- Teachers can manage lessons inside their own courses
- Lessons support different lesson types
- Materials support different material types

#### Enrollments
- Students can request/enroll in courses
- Teachers/Admin can manage enrollment status
- Enrollment lifecycle includes multiple statuses

#### Notifications
- View notifications
- Count unread notifications
- Mark individual notifications as read
- Mark all notifications as read
- Admin can create notifications

---

## Project Structure

This repository is organized as a **monorepo** with two main applications:

### 1. `e_learning_platform_njt_backend/`
Spring Boot backend application.

Contains:
- REST API
- business logic
- JWT security
- database integration
- Flyway migrations
- JPA entities, repositories, services, and controllers

### 2. `e_learning_platform_njt_frontend/`
React frontend application.

Contains:
- UI pages and components
- route protection
- course pages
- notifications pages
- admin and teacher views
- API communication via Axios

---

## Technologies Used

### Backend
- **Java 17**
- **Spring Boot 3.3.4**
- **Spring Web**
- **Spring Data JPA**
- **Hibernate**
- **Spring Security**
- **JWT**
- **Flyway**
- **MySQL**
- **Maven**
- **Swagger / OpenAPI**

### Frontend
- **React**
- **React Router DOM**
- **Axios**
- **Material UI (MUI)**
- **Create React App**

---

## Domain Model

The backend is structured around the following main entities:

- **User**
- **Course**
- **Lesson**
- **Material**
- **Enrollment**
- **Notification**

It also uses lookup/reference tables for:

- **Role**
- **CourseLevel**
- **CourseStatus**
- **LessonType**
- **MaterialType**
- **EnrollmentStatus**
- **NotificationType**
- **PaymentStatus** *(present in backend model)*

---

## Security Model

The application uses **JWT-based stateless authentication**.

### Roles
- **STUDENT**
- **TEACHER**
- **ADMIN**

### Access rules in practice
- **Students** can browse courses, view lessons they have access to, manage their notifications, and create enrollment requests.
- **Teachers** can create and manage their own courses, lessons, and course-related content.
- **Admins** can change user roles and manage administrative workflows such as notifications and enrollment oversight.

### Important note
When a new user registers, the backend automatically assigns the **STUDENT** role by default.

---

## Backend Setup

## Prerequisites
Make sure you have installed:

- **Java 17**
- **Maven**
- **MySQL Server**
- an IDE such as **IntelliJ IDEA**, **NetBeans**, or **Eclipse**

---

## Database Setup

The backend is configured to use a MySQL database named:

```sql
e_learning_platform
