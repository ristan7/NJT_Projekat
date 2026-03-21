/*
SQLyog Community v13.3.0 (64 bit)
MySQL - 8.0.42 : Database - e_learning_platform
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`e_learning_platform` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `e_learning_platform`;

/*Table structure for table `course` */

DROP TABLE IF EXISTS `course`;

CREATE TABLE `course` (
  `course_id` bigint NOT NULL AUTO_INCREMENT,
  `course_description` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `course_title` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `published_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `author_id` bigint NOT NULL,
  `course_level_id` bigint NOT NULL,
  `course_status_id` bigint NOT NULL,
  PRIMARY KEY (`course_id`),
  KEY `ix_course_author` (`author_id`),
  KEY `ix_course_status` (`course_status_id`),
  KEY `ix_course_level` (`course_level_id`),
  CONSTRAINT `fk_course_author` FOREIGN KEY (`author_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `fk_course_level` FOREIGN KEY (`course_level_id`) REFERENCES `course_level` (`course_level_id`),
  CONSTRAINT `fk_course_status` FOREIGN KEY (`course_status_id`) REFERENCES `course_status` (`course_status_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `course` */

insert  into `course`(`course_id`,`course_description`,`course_title`,`created_at`,`published_at`,`updated_at`,`author_id`,`course_level_id`,`course_status_id`) values 
(5,'svsdsdb','vdsdvd','2025-10-24 04:41:49.841250',NULL,'2025-10-24 05:00:59.544292',2,1,2),
(6,'casdcsadsd','xcasca','2025-10-24 07:52:23.783380',NULL,'2025-10-24 07:52:23.783380',2,1,1),
(7,'ascas','\\xc\\cas','2025-10-24 07:55:13.934075',NULL,'2025-10-24 07:55:13.934075',2,1,2),
(8,'a  asxasxasx','a a','2025-10-24 08:01:47.098499',NULL,'2025-10-24 08:01:47.098499',2,1,2),
(9,'Osnove','Matematika 1','2025-10-24 08:17:28.667132',NULL,'2025-10-24 08:17:28.667132',2,1,1),
(10,'Uvodni kurs. Upoznavanje sa Spring-om. Hibernate i JPA osnovni pojmovi. Upoznavanje studenata sa osnovama REST-API-ja.','Napredne Java tehnologije','2025-10-26 15:33:18.900680',NULL,'2025-10-26 17:25:12.994813',2,2,2);

/*Table structure for table `course_level` */

DROP TABLE IF EXISTS `course_level`;

CREATE TABLE `course_level` (
  `course_level_id` bigint NOT NULL AUTO_INCREMENT,
  `course_level_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`course_level_id`),
  UNIQUE KEY `UK8chnipov1muwi2tv75ayvmnf5` (`course_level_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `course_level` */

insert  into `course_level`(`course_level_id`,`course_level_name`) values 
(3,'ADVANCED'),
(1,'BEGINNER'),
(2,'INTERMEDIATE');

/*Table structure for table `course_status` */

DROP TABLE IF EXISTS `course_status`;

CREATE TABLE `course_status` (
  `course_status_id` bigint NOT NULL AUTO_INCREMENT,
  `course_status_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`course_status_id`),
  UNIQUE KEY `UKnd52vgvvwy5tyuy3hbq7l3snj` (`course_status_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `course_status` */

insert  into `course_status`(`course_status_id`,`course_status_name`) values 
(3,'ARCHIVED'),
(1,'DRAFT'),
(2,'PUBLISHED');

/*Table structure for table `enrollment` */

DROP TABLE IF EXISTS `enrollment`;

CREATE TABLE `enrollment` (
  `enrollment_id` bigint NOT NULL AUTO_INCREMENT,
  `enrolled_at` datetime(6) NOT NULL,
  `last_accessed_at` datetime(6) DEFAULT NULL,
  `course_id` bigint NOT NULL,
  `enrollment_status_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  PRIMARY KEY (`enrollment_id`),
  UNIQUE KEY `uk_enrollment_student_course` (`student_id`,`course_id`),
  KEY `ix_enrollment_student` (`student_id`),
  KEY `ix_enrollment_course` (`course_id`),
  KEY `ix_enrollment_status` (`enrollment_status_id`),
  KEY `ix_enrollment_enrolled_at` (`enrolled_at`),
  CONSTRAINT `fk_enrollment_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`),
  CONSTRAINT `fk_enrollment_status` FOREIGN KEY (`enrollment_status_id`) REFERENCES `enrollment_status` (`enrollment_status_id`),
  CONSTRAINT `fk_enrollment_student` FOREIGN KEY (`student_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `enrollment` */

/*Table structure for table `enrollment_status` */

DROP TABLE IF EXISTS `enrollment_status`;

CREATE TABLE `enrollment_status` (
  `enrollment_status_id` bigint NOT NULL AUTO_INCREMENT,
  `enrollment_status_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`enrollment_status_id`),
  UNIQUE KEY `UK1j6565ylgjjg10w7evbht3bqy` (`enrollment_status_name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `enrollment_status` */

insert  into `enrollment_status`(`enrollment_status_id`,`enrollment_status_name`) values 
(2,'ACTIVE'),
(4,'CANCELLED'),
(3,'COMPLETED'),
(1,'REQUESTED'),
(5,'SUSPENDED');

/*Table structure for table `flyway_schema_history` */

DROP TABLE IF EXISTS `flyway_schema_history`;

CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `script` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `flyway_schema_history` */

insert  into `flyway_schema_history`(`installed_rank`,`version`,`description`,`type`,`script`,`checksum`,`installed_by`,`installed_on`,`execution_time`,`success`) values 
(1,'1','seed lookup','SQL','lookups/V1__seed_lookup.sql',533943455,'root','2025-10-24 04:08:45',16,1),
(2,'2','sent at','SQL','V2__sent_at.sql',-1792989068,'root','2025-10-24 04:08:45',82,1);

/*Table structure for table `lesson` */

DROP TABLE IF EXISTS `lesson`;

CREATE TABLE `lesson` (
  `lesson_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `lesson_available` bit(1) NOT NULL,
  `lesson_order_index` int NOT NULL,
  `lesson_summary` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `lesson_title` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `course_id` bigint NOT NULL,
  `lesson_type_id` bigint NOT NULL,
  `free_preview` bit(1) NOT NULL,
  PRIMARY KEY (`lesson_id`),
  KEY `ix_lesson_course` (`course_id`),
  KEY `ix_lesson_type` (`lesson_type_id`),
  KEY `ix_lesson_order` (`course_id`,`lesson_order_index`),
  CONSTRAINT `fk_lesson_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`),
  CONSTRAINT `fk_lesson_type` FOREIGN KEY (`lesson_type_id`) REFERENCES `lesson_type` (`lesson_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `lesson` */

insert  into `lesson`(`lesson_id`,`created_at`,`lesson_available`,`lesson_order_index`,`lesson_summary`,`lesson_title`,`updated_at`,`course_id`,`lesson_type_id`,`free_preview`) values 
(6,'2025-10-24 04:41:56.347464','\0',1,'vsdsvsvv','svsvsv','2025-10-24 04:41:56.347464',5,1,'\0'),
(7,'2025-10-24 07:55:34.665046','',1,'aasas aaca','First Lesson','2025-10-24 07:55:34.665046',7,3,'\0'),
(8,'2025-10-24 08:01:59.519528','',1,'AVCASVDW','WFSWEW','2025-10-24 08:01:59.519528',8,1,'\0'),
(9,'2025-10-24 08:02:22.152048','',2,'CQWCWQCCAACC','CQCCC','2025-10-24 08:02:22.152048',8,3,'\0'),
(10,'2025-10-24 08:18:15.929964','',1,'Nesto','First Lesson','2025-10-24 08:18:15.929964',9,1,'\0'),
(11,'2025-10-24 08:18:23.690451','',2,'dqwdqwd','vswvwsvvv','2025-10-24 08:18:23.690451',9,2,'\0'),
(12,'2025-10-26 17:55:56.363537','',1,'Uvodna lekcija. Kviz predvidjen da vidi vase trenutno znanje o izabranoj oblasti.','Prva lekcija','2025-10-26 17:55:56.363537',10,3,'\0');

/*Table structure for table `lesson_type` */

DROP TABLE IF EXISTS `lesson_type`;

CREATE TABLE `lesson_type` (
  `lesson_type_id` bigint NOT NULL AUTO_INCREMENT,
  `lesson_type_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`lesson_type_id`),
  UNIQUE KEY `UKh6cwe74vxk8otyogio3d1td64` (`lesson_type_name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `lesson_type` */

insert  into `lesson_type`(`lesson_type_id`,`lesson_type_name`) values 
(2,'ARTICLE'),
(4,'ASSIGNMENT'),
(3,'QUIZ'),
(1,'VIDEO');

/*Table structure for table `material` */

DROP TABLE IF EXISTS `material`;

CREATE TABLE `material` (
  `material_id` bigint NOT NULL AUTO_INCREMENT,
  `content` longtext COLLATE utf8mb4_unicode_ci,
  `created_at` datetime(6) NOT NULL,
  `material_order_index` int NOT NULL,
  `material_title` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `resource_url` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `lesson_id` bigint NOT NULL,
  `material_type_id` bigint NOT NULL,
  PRIMARY KEY (`material_id`),
  KEY `ix_material_lesson` (`lesson_id`),
  KEY `ix_material_type` (`material_type_id`),
  KEY `ix_material_order` (`lesson_id`,`material_order_index`),
  CONSTRAINT `fk_material_lesson` FOREIGN KEY (`lesson_id`) REFERENCES `lesson` (`lesson_id`),
  CONSTRAINT `fk_material_type` FOREIGN KEY (`material_type_id`) REFERENCES `material_type` (`material_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `material` */

insert  into `material`(`material_id`,`content`,`created_at`,`material_order_index`,`material_title`,`resource_url`,`updated_at`,`lesson_id`,`material_type_id`) values 
(1,'cavcasvsav','2025-10-24 04:56:15.746446',1,'vsvbsdvsdvsv','https://www.youtube.com/results?search_query=java+for+beginners','2025-10-24 04:56:15.746446',6,5),
(2,'cafavcw','2025-10-24 04:56:15.791842',2,'acavavav','','2025-10-24 04:56:15.791842',6,3),
(3,'asfsgvwevw','2025-10-24 07:56:08.401564',1,'a as','http://localhost:3000/teacher/courses/7/lessons/7','2025-10-24 07:56:08.401564',7,3),
(4,'|Cascascac','2025-10-24 07:56:08.406273',2,'ascacasc','http://localhost:3000/teacher/courses/7/lessons/8','2025-10-24 07:56:08.406273',7,4),
(5,'CASASC','2025-10-24 08:02:41.275747',1,'ACQACCQA','CAXCASC','2025-10-24 08:02:41.275747',8,3),
(6,'xvAVGXGa','2025-10-24 08:19:45.566934',1,'ndasfbsdavbsd','http://localhost:3000/teacher/courses/9/lessons/10','2025-10-24 08:19:45.566934',10,3),
(7,'vzdvszd','2025-10-24 08:19:45.575893',2,'vdxvsdz','dvsdzv','2025-10-24 08:19:45.575893',10,2);

/*Table structure for table `material_type` */

DROP TABLE IF EXISTS `material_type`;

CREATE TABLE `material_type` (
  `material_type_id` bigint NOT NULL AUTO_INCREMENT,
  `material_type_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`material_type_id`),
  UNIQUE KEY `UK5iw2ff7t5jcn5molkseurtskg` (`material_type_name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `material_type` */

insert  into `material_type`(`material_type_id`,`material_type_name`) values 
(2,'IMAGE'),
(3,'LINK'),
(1,'PDF'),
(4,'PRESENTATION'),
(5,'VIDEO');

/*Table structure for table `notification` */

DROP TABLE IF EXISTS `notification`;

CREATE TABLE `notification` (
  `notification_id` bigint NOT NULL AUTO_INCREMENT,
  `message` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `notification_title` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_read` bit(1) NOT NULL,
  `sent_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `notification_type_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`notification_id`),
  KEY `idx_notification_user` (`user_id`),
  KEY `idx_notification_type` (`notification_type_id`),
  KEY `idx_notification_sent_at` (`sent_at`),
  CONSTRAINT `FK3x921lcnkybqyh7pqeg9u2x7j` FOREIGN KEY (`notification_type_id`) REFERENCES `notification_type` (`notification_type_id`),
  CONSTRAINT `FKb0yvoep4h4k92ipon31wmdf7e` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `notification` */

insert  into `notification`(`notification_id`,`message`,`notification_title`,`is_read`,`sent_at`,`notification_type_id`,`user_id`) values 
(5,'a a\\ a a a','ax\\ a a','','2025-10-24 08:00:17',1,3),
(6,' z s s cxascasascascas','cas\\caasd','','2025-10-24 08:00:27',3,3),
(7,'Добродошао, драго нам је да си са нама!','Dobrodošao na platformu','','2025-10-25 14:55:44',2,3),
(8,'цсѕвѕс','Dobrodošao na platformu','','2025-10-26 14:36:08',2,3),
(9,'Radimo na otklanjanju smetnji u radu stranice Courses. Hvala na strpljenju.','Tehnicki problem','\0','2025-10-26 20:41:00',1,3);

/*Table structure for table `notification_type` */

DROP TABLE IF EXISTS `notification_type`;

CREATE TABLE `notification_type` (
  `notification_type_id` bigint NOT NULL AUTO_INCREMENT,
  `notification_type_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`notification_type_id`),
  UNIQUE KEY `UK4dr9744h8pvpqx5g2gtu920l4` (`notification_type_name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `notification_type` */

insert  into `notification_type`(`notification_type_id`,`notification_type_name`) values 
(4,'CERTIFICATE'),
(3,'COURSE'),
(2,'ENROLLMENT'),
(5,'REVIEW'),
(1,'SYSTEM');

/*Table structure for table `payment_status` */

DROP TABLE IF EXISTS `payment_status`;

CREATE TABLE `payment_status` (
  `payment_status_id` bigint NOT NULL AUTO_INCREMENT,
  `payment_status_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`payment_status_id`),
  UNIQUE KEY `UKbl68bbqe6ddn5ulge80mwekp9` (`payment_status_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `payment_status` */

/*Table structure for table `role` */

DROP TABLE IF EXISTS `role`;

CREATE TABLE `role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `UKiubw515ff0ugtm28p8g3myt0h` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `role` */

insert  into `role`(`role_id`,`role_name`) values 
(3,'ADMIN'),
(1,'STUDENT'),
(2,'TEACHER');

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `first_name` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_name` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_user_username` (`username`),
  UNIQUE KEY `uk_user_email` (`email`),
  KEY `fk_user_role` (`role_id`),
  CONSTRAINT `fk_user_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `user` */

insert  into `user`(`user_id`,`email`,`first_name`,`last_name`,`password`,`username`,`role_id`) values 
(1,'miki.ristanovic53@gmail.com','Mihajlo','Ristanovic','$2a$10$k567d8bswNxUEk8qs8WvAOkGCB3riNToJEB/VSwNVxF4O5KcyUoA.','ristan771',3),
(2,'marko@gmail.com','Marko','Markovic','$2a$10$5D6a4Ym8TDY6Iy2Ll3GfGeiys6SVXHd1kt1ic/TcvM0mLLc8FhoPS','marko123',2),
(3,'milos.ristanovic@gmail.com','Milos','Ristanovic','$2a$10$RwJae7vNKZer7nvyP6wh.eMrMXUGGSNNRTnLtBAK84cJgp2sTeVW.','milos221',2),
(4,'veljko@gmail.com','Veljko','Rankovic','$2a$10$F80elsSGg03TT0AMP37A6u/6yIS5KAyiQE01zRFbvG7vVxZDIEGC.','veljko123',1);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
