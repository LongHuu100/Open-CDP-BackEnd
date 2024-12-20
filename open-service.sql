# ************************************************************
# Sequel Ace SQL dump
# Version 20062
#
# https://sequel-ace.com/
# https://github.com/Sequel-Ace/Sequel-Ace
#
# Host: 127.0.0.1 (MySQL 8.0.30)
# Database: open_cdp
# Generation Time: 2024-12-01 10:55:12 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE='NO_AUTO_VALUE_ON_ZERO', SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table attributed
# ------------------------------------------------------------

DROP TABLE IF EXISTS `attributed`;

CREATE TABLE `attributed` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `status` int DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10011 DEFAULT CHARSET=utf8mb3;

LOCK TABLES `attributed` WRITE;
/*!40000 ALTER TABLE `attributed` DISABLE KEYS */;

INSERT INTO `attributed` (`id`, `name`, `status`)
VALUES
	(10008,'PHong cách',1),
	(10009,'Kích thước',1),
	(10010,'Màu sắc',1);

/*!40000 ALTER TABLE `attributed` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table attributed_value
# ------------------------------------------------------------

DROP TABLE IF EXISTS `attributed_value`;

CREATE TABLE `attributed_value` (
  `id` int NOT NULL AUTO_INCREMENT,
  `attributed_id` int NOT NULL,
  `value` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `status` int DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10014 DEFAULT CHARSET=utf8mb3;

LOCK TABLES `attributed_value` WRITE;
/*!40000 ALTER TABLE `attributed_value` DISABLE KEYS */;

INSERT INTO `attributed_value` (`id`, `attributed_id`, `value`, `status`)
VALUES
	(10009,10008,'Tối giản B+',1),
	(10010,10009,'30x55x30',1),
	(10011,10009,'10x40x55',1),
	(10012,10010,'Đen',1),
	(10013,10010,'Đỏ',1);

/*!40000 ALTER TABLE `attributed_value` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table category
# ------------------------------------------------------------

DROP TABLE IF EXISTS `category`;

CREATE TABLE `category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `parent_id` int DEFAULT '0',
  `name` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `slug` varchar(255) DEFAULT NULL,
  `status` int NOT NULL DEFAULT '0',
  `icon` varchar(255) DEFAULT '',
  `image` varchar(100) DEFAULT NULL,
  `order_no` int DEFAULT '0',
  `seo_title` varchar(255) DEFAULT '',
  `seo_description` varchar(255) DEFAULT '',
  `seo_content` text,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10009 DEFAULT CHARSET=utf8mb3;

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;

INSERT INTO `category` (`id`, `parent_id`, `name`, `slug`, `status`, `icon`, `image`, `order_no`, `seo_title`, `seo_description`, `seo_content`, `created_at`, `updated_at`)
VALUES
	(10008,0,'React.Js',NULL,0,'',NULL,0,'','',NULL,'2024-11-13 18:05:31','2024-11-13 18:05:31');

/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table customer_address
# ------------------------------------------------------------

DROP TABLE IF EXISTS `customer_address`;

CREATE TABLE `customer_address` (
  `id` int NOT NULL AUTO_INCREMENT,
  `customer_id` int unsigned DEFAULT '0',
  `name_address` varchar(255) DEFAULT NULL,
  `receiver_name` varchar(100) DEFAULT '',
  `address` varchar(255) DEFAULT NULL,
  `ward_id` int unsigned DEFAULT '0',
  `district_id` int unsigned DEFAULT '0',
  `province_id` int unsigned DEFAULT '0',
  `mobile_phone` varchar(20) DEFAULT NULL,
  `is_default` tinyint unsigned DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8204 DEFAULT CHARSET=utf8mb3;



# Dump of table customer_enterprise
# ------------------------------------------------------------

DROP TABLE IF EXISTS `customer_enterprise`;

CREATE TABLE `customer_enterprise` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `company_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `total_fee` bigint DEFAULT '0',
  `contact_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `tax_code` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT '',
  `director` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT '',
  `ward_id` int unsigned DEFAULT '0',
  `district_id` int unsigned DEFAULT '0',
  `province_id` int unsigned DEFAULT '0',
  `email` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT '',
  `mobile_phone` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT '',
  `in_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `contract_file` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uc_tax_code_mobile_phone` (`tax_code`,`mobile_phone`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;



# Dump of table customer_order
# ------------------------------------------------------------

DROP TABLE IF EXISTS `customer_order`;

CREATE TABLE `customer_order` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `data_id` bigint DEFAULT '0',
  `source` int DEFAULT NULL,
  `enterprise_id` bigint DEFAULT '0',
  `enterprise_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `order_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `code` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `total_not_vat` bigint DEFAULT '0',
  `customer_id` int unsigned DEFAULT '0',
  `customer_receiver_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT '',
  `customer_address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT '',
  `customer_ward_id` int unsigned DEFAULT NULL,
  `customer_district_id` int unsigned DEFAULT NULL,
  `customer_province_id` int unsigned DEFAULT NULL,
  `customer_mobile_phone` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `customer_email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `customer_note` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci,
  `subtotal` bigint unsigned DEFAULT '0',
  `price_off` bigint DEFAULT '0',
  `discount_info` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT '',
  `voucher` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci,
  `shipping_cost` int unsigned DEFAULT '0',
  `shipping_real` int DEFAULT '0',
  `cod_cost` int unsigned DEFAULT '0',
  `transport_type_id` int DEFAULT NULL,
  `total` bigint unsigned DEFAULT '0',
  `vat` tinyint DEFAULT '0',
  `fee_import` int DEFAULT '0',
  `paid` bigint unsigned DEFAULT '0',
  `flag_free_ship` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT 'false',
  `shipping_status` enum('na','danggiao','dagiao','giaoloi') CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT 'na',
  `payment_status` int DEFAULT '0',
  `cancel_at` timestamp NULL DEFAULT NULL,
  `paid_time` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `done_at` timestamp NULL DEFAULT NULL,
  `user_create_id` int unsigned DEFAULT '0',
  `user_create_username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT '',
  `faulty` bit(1) DEFAULT b'0',
  `status` int unsigned DEFAULT '0',
  `type` enum('order','cohoi') CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT 'cohoi',
  `opportunity_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `enterprise_id` (`enterprise_id`),
  KEY `order_code_index` (`code`),
  KEY `user_id_index` (`user_create_id`),
  KEY `ship_idx` (`shipping_status`),
  KEY `phone_idx` (`customer_mobile_phone`),
  KEY `data_id_idx` (`data_id`),
  KEY `customer_id_idx` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=33825 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

LOCK TABLES `customer_order` WRITE;
/*!40000 ALTER TABLE `customer_order` DISABLE KEYS */;

INSERT INTO `customer_order` (`id`, `data_id`, `source`, `enterprise_id`, `enterprise_name`, `order_name`, `code`, `total_not_vat`, `customer_id`, `customer_receiver_name`, `customer_address`, `customer_ward_id`, `customer_district_id`, `customer_province_id`, `customer_mobile_phone`, `customer_email`, `customer_note`, `subtotal`, `price_off`, `discount_info`, `voucher`, `shipping_cost`, `shipping_real`, `cod_cost`, `transport_type_id`, `total`, `vat`, `fee_import`, `paid`, `flag_free_ship`, `shipping_status`, `payment_status`, `cancel_at`, `paid_time`, `created_at`, `updated_at`, `done_at`, `user_create_id`, `user_create_username`, `faulty`, `status`, `type`, `opportunity_at`)
VALUES
	(33824,8,0,NULL,NULL,NULL,'ORBI1324GML',NULL,7,'Hà NAm',NULL,NULL,NULL,NULL,'0936295123','long.huu.100@gmail.com',NULL,4900000,0,NULL,NULL,NULL,NULL,NULL,NULL,5390000,10,NULL,0,NULL,NULL,NULL,NULL,NULL,'2024-12-01 08:48:01','2024-12-01 17:19:07',NULL,67,'longhuu',NULL,0,'cohoi','2024-12-01 16:39:20');

/*!40000 ALTER TABLE `customer_order` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table customer_order_detail
# ------------------------------------------------------------

DROP TABLE IF EXISTS `customer_order_detail`;

CREATE TABLE `customer_order_detail` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `customer_order_id` bigint DEFAULT '0',
  `name` varchar(255) COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `product_id` int NOT NULL,
  `product_name` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci,
  `sku_id` int DEFAULT '0',
  `sku_info` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `price` int DEFAULT '0',
  `quantity` int NOT NULL DEFAULT '0',
  `price_off` int DEFAULT '0',
  `total` int DEFAULT '0',
  `discount` varchar(255) COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `ship_status` int DEFAULT '0',
  `ship_done_at` timestamp NULL DEFAULT NULL,
  `status` int DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `customer_order_id` (`customer_order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=33825 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

LOCK TABLES `customer_order_detail` WRITE;
/*!40000 ALTER TABLE `customer_order_detail` DISABLE KEYS */;

INSERT INTO `customer_order_detail` (`id`, `code`, `customer_order_id`, `name`, `product_id`, `product_name`, `sku_id`, `sku_info`, `price`, `quantity`, `price_off`, `total`, `discount`, `ship_status`, `ship_done_at`, `status`, `created_at`, `updated_at`)
VALUES
	(33823,'ORBI1324GML-1',33824,'QTS - Flast Solution',749,'Hữu Long',10016,'[{\"id\":10010,\"productId\":749,\"skuId\":10016,\"name\":\"PHong cách\",\"value\":\"Tối giản B+\",\"attributedId\":10008,\"attributedValueId\":10009,\"del\":0},{\"id\":10011,\"productId\":749,\"skuId\":10016,\"name\":\"Kích thước\",\"value\":\"30x55x30\",\"attributedId\":10009,\"attributedValueId\":10010,\"del\":1}]',5000,100,300000,200000,'{\"discountUnit\":\"money\",\"discountValue\":300000}',NULL,NULL,101,'2024-12-01 17:52:46','2024-12-01 17:52:46'),
	(33824,'ORBI1324GML-2',33824,'Hộp sẵn 2 tầng - Flast Solution',749,'Hữu Long',10016,'[{\"id\":10010,\"productId\":749,\"skuId\":10016,\"name\":\"PHong cách\",\"value\":\"Tối giản B+\",\"attributedId\":10008,\"attributedValueId\":10009,\"del\":0},{\"id\":10011,\"productId\":749,\"skuId\":10016,\"name\":\"Kích thước\",\"value\":\"30x55x30\",\"attributedId\":10009,\"attributedValueId\":10010,\"del\":1}]',5000,1000,300000,4700000,'{\"discountUnit\":\"money\",\"discountValue\":300000}',NULL,NULL,100,'2024-12-01 17:52:46','2024-12-01 17:52:46');

/*!40000 ALTER TABLE `customer_order_detail` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table customer_order_note
# ------------------------------------------------------------

DROP TABLE IF EXISTS `customer_order_note`;

CREATE TABLE `customer_order_note` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(50) DEFAULT '',
  `type` varchar(30) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT '',
  `table_name` varchar(255) DEFAULT '',
  `table_id` int DEFAULT '0',
  `note` text,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=583 DEFAULT CHARSET=utf8mb3;



# Dump of table customer_order_payment
# ------------------------------------------------------------

DROP TABLE IF EXISTS `customer_order_payment`;

CREATE TABLE `customer_order_payment` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `method` int DEFAULT NULL,
  `amount` bigint unsigned DEFAULT '0',
  `sso_id` varchar(100) NOT NULL,
  `is_confirm` tinyint unsigned DEFAULT '0',
  `content` text,
  `in_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `confirm_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `pay_order_code_index` (`code`),
  KEY `pay_sale_index` (`sso_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb3;



# Dump of table customer_order_status
# ------------------------------------------------------------

DROP TABLE IF EXISTS `customer_order_status`;

CREATE TABLE `customer_order_status` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `color` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT 'black',
  `order` int DEFAULT '0',
  `del_flag` tinyint DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8mb3;

LOCK TABLES `customer_order_status` WRITE;
/*!40000 ALTER TABLE `customer_order_status` DISABLE KEYS */;

INSERT INTO `customer_order_status` (`id`, `name`, `color`, `order`, `del_flag`)
VALUES
	(100,'Đơn mới','red',0,0),
	(101,'Sản xuất','black',1,0),
	(102,'Hoàn thành','black',2,0);

/*!40000 ALTER TABLE `customer_order_status` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table customer_personal
# ------------------------------------------------------------

DROP TABLE IF EXISTS `customer_personal`;

CREATE TABLE `customer_personal` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(100) DEFAULT NULL,
  `id_card` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `sale_id` varchar(100) DEFAULT '0',
  `gender` varchar(20) DEFAULT 'other',
  `source_id` int DEFAULT NULL,
  `level` tinyint(1) DEFAULT '0',
  `facebook_id` varchar(255) DEFAULT NULL,
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `province_id` int unsigned DEFAULT NULL,
  `district_id` int unsigned DEFAULT NULL,
  `ward_id` int unsigned DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `company_name` varchar(255) DEFAULT NULL,
  `company_id` int DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `is_trust_email` int DEFAULT NULL,
  `mobile` varchar(20) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `token_confirm` varchar(200) DEFAULT NULL,
  `status` int unsigned DEFAULT '0',
  `date_of_birth` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `diem_danh_gia` decimal(8,2) DEFAULT '0.00',
  PRIMARY KEY (`id`),
  KEY `mobile_index` (`mobile`),
  KEY `saleId_index` (`sale_id`),
  KEY `token_idx` (`token_confirm`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb3;

LOCK TABLES `customer_personal` WRITE;
/*!40000 ALTER TABLE `customer_personal` DISABLE KEYS */;

INSERT INTO `customer_personal` (`id`, `type`, `id_card`, `sale_id`, `gender`, `source_id`, `level`, `facebook_id`, `name`, `province_id`, `district_id`, `ward_id`, `address`, `company_name`, `company_id`, `avatar`, `email`, `is_trust_email`, `mobile`, `password`, `token_confirm`, `status`, `date_of_birth`, `created_at`, `updated_at`, `diem_danh_gia`)
VALUES
	(7,'customer','03902930390392',NULL,'other',3,NULL,NULL,'Hà NAm',NULL,NULL,NULL,'Hà Nội',NULL,NULL,NULL,'long.huu.100@gmail.com',NULL,'0936295123',NULL,NULL,NULL,'1989-11-07 13:32:22','2022-11-19 13:32:22','2024-10-21 16:16:40',NULL);

/*!40000 ALTER TABLE `customer_personal` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table data
# ------------------------------------------------------------

DROP TABLE IF EXISTS `data`;

CREATE TABLE `data` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `service_id` int DEFAULT '0',
  `level` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `staff` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `province_name` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `source` tinyint(1) DEFAULT '0',
  `customer_name` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `customer_mobile` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `customer_email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT '',
  `customer_facebook` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT '',
  `tags` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci,
  `category_id` int DEFAULT '0',
  `sale_id` int DEFAULT '0',
  `note` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci,
  `assign_to` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT '',
  `in_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` tinyint(1) DEFAULT '0',
  `from_department` tinyint(1) DEFAULT NULL,
  `is_order` tinyint DEFAULT '0',
  PRIMARY KEY (`id`,`in_time`),
  KEY `data_sale_id_index` (`sale_id`),
  KEY `data_assign_to_index` (`assign_to`),
  KEY `status_index` (`status`),
  KEY `department_index` (`from_department`),
  KEY `customer_mobile_idx` (`customer_mobile`)
) ENGINE=InnoDB AUTO_INCREMENT=49735 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci
/*!50100 PARTITION BY RANGE (unix_timestamp(`in_time`))
(PARTITION p2020 VALUES LESS THAN (1609347599) ENGINE = InnoDB,
 PARTITION quimot2021 VALUES LESS THAN (1617209999) ENGINE = InnoDB,
 PARTITION quihai2021 VALUES LESS THAN (1625072399) ENGINE = InnoDB,
 PARTITION quiba2021 VALUES LESS THAN (1633021199) ENGINE = InnoDB,
 PARTITION quitu2021 VALUES LESS THAN (1640969999) ENGINE = InnoDB,
 PARTITION quimot2022 VALUES LESS THAN (1648745999) ENGINE = InnoDB,
 PARTITION quihai2022 VALUES LESS THAN (1656608399) ENGINE = InnoDB,
 PARTITION pfuture VALUES LESS THAN MAXVALUE ENGINE = InnoDB) */;



# Dump of table data_media
# ------------------------------------------------------------

DROP TABLE IF EXISTS `data_media`;

CREATE TABLE `data_media` (
  `id` int NOT NULL AUTO_INCREMENT,
  `data_id` int DEFAULT '0',
  `session_id` int DEFAULT '0',
  `file` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci,
  PRIMARY KEY (`id`),
  KEY `dataIdIdx` (`data_id`),
  KEY `sessionIdIdx` (`session_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20568 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci COMMENT='store customer files';



# Dump of table data_owner
# ------------------------------------------------------------

DROP TABLE IF EXISTS `data_owner`;

CREATE TABLE `data_owner` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `customer_mobile` varchar(20) NOT NULL,
  `sale_id` int DEFAULT '0',
  `department_id` int DEFAULT '0',
  `sale_name` varchar(255) DEFAULT '',
  `in_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `data_owner_mobile` (`customer_mobile`)
) ENGINE=InnoDB AUTO_INCREMENT=40608 DEFAULT CHARSET=utf8mb3;



# Dump of table media
# ------------------------------------------------------------

DROP TABLE IF EXISTS `media`;

CREATE TABLE `media` (
  `id` int NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `object` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `object_id` int DEFAULT '0',
  `section_id` int DEFAULT '0',
  `i_resize` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `obj_idx` (`object`),
  KEY `o_id_idx` (`object_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2517 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



# Dump of table product
# ------------------------------------------------------------

DROP TABLE IF EXISTS `product`;

CREATE TABLE `product` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `service_id` int DEFAULT '0',
  `quality_in_stock` int DEFAULT '0',
  `total_import_stock` int DEFAULT '0',
  `name` varchar(255) DEFAULT NULL,
  `slug` varchar(255) DEFAULT NULL,
  `provider_id` int DEFAULT NULL,
  `unit` varchar(100) DEFAULT NULL,
  `price` int DEFAULT '0',
  `price_ref` int DEFAULT '0',
  `seo_title` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `seo_description` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `seo_content` text,
  `image` varchar(255) DEFAULT NULL,
  `social` varchar(255) DEFAULT '{"view":139,"like":0,"shear":49}',
  `status` int DEFAULT '0',
  `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=750 DEFAULT CHARSET=utf8mb3;

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;

INSERT INTO `product` (`id`, `code`, `service_id`, `quality_in_stock`, `total_import_stock`, `name`, `slug`, `provider_id`, `unit`, `price`, `price_ref`, `seo_title`, `seo_description`, `seo_content`, `image`, `social`, `status`, `created_time`, `updated_time`)
VALUES
	(749,'FLJAJAAC',10008,NULL,NULL,'Hữu Long',NULL,10008,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2024-11-14 16:33:12','2024-11-14 16:33:12');

/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table product_attributed
# ------------------------------------------------------------

DROP TABLE IF EXISTS `product_attributed`;

CREATE TABLE `product_attributed` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `attributed_id` int DEFAULT NULL,
  `attributed_value_id` int DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `value` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10038 DEFAULT CHARSET=utf8mb3;

LOCK TABLES `product_attributed` WRITE;
/*!40000 ALTER TABLE `product_attributed` DISABLE KEYS */;

INSERT INTO `product_attributed` (`id`, `product_id`, `attributed_id`, `attributed_value_id`, `name`, `value`)
VALUES
	(10035,749,10008,10009,'PHong cách','Tối giản B+'),
	(10036,749,10009,10010,'Kích thước','30x55x30'),
	(10037,749,10009,10011,'Kích thước','10x40x55');

/*!40000 ALTER TABLE `product_attributed` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table product_category
# ------------------------------------------------------------

DROP TABLE IF EXISTS `product_category`;

CREATE TABLE `product_category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `category_id` int NOT NULL,
  `product_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx-cmp-category_id` (`category_id`),
  KEY `idx-cmp-product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=308 DEFAULT CHARSET=utf8mb3;



# Dump of table product_image
# ------------------------------------------------------------

DROP TABLE IF EXISTS `product_image`;

CREATE TABLE `product_image` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `product_id` int unsigned DEFAULT '0',
  `file_name` varchar(255) DEFAULT '',
  `is_slideshow` int unsigned DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `product_id_index` (`product_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21589 DEFAULT CHARSET=utf8mb3;



# Dump of table product_property
# ------------------------------------------------------------

DROP TABLE IF EXISTS `product_property`;

CREATE TABLE `product_property` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `value` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10018 DEFAULT CHARSET=utf8mb3;

LOCK TABLES `product_property` WRITE;
/*!40000 ALTER TABLE `product_property` DISABLE KEYS */;

INSERT INTO `product_property` (`id`, `product_id`, `name`, `value`)
VALUES
	(10017,749,'Môi trường','Ánh sáng dưới 30C');

/*!40000 ALTER TABLE `product_property` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table product_skus
# ------------------------------------------------------------

DROP TABLE IF EXISTS `product_skus`;

CREATE TABLE `product_skus` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `del` int DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10017 DEFAULT CHARSET=utf8mb3;

LOCK TABLES `product_skus` WRITE;
/*!40000 ALTER TABLE `product_skus` DISABLE KEYS */;

INSERT INTO `product_skus` (`id`, `product_id`, `name`, `del`)
VALUES
	(10016,749,'Thuộc tính gia công và đóng gói',0);

/*!40000 ALTER TABLE `product_skus` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table product_skus_details
# ------------------------------------------------------------

DROP TABLE IF EXISTS `product_skus_details`;

CREATE TABLE `product_skus_details` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `sku_id` int NOT NULL DEFAULT '0',
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `value` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `attributed_id` int NOT NULL,
  `attributed_value_id` int NOT NULL,
  `del` int DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10012 DEFAULT CHARSET=utf8mb3;

LOCK TABLES `product_skus_details` WRITE;
/*!40000 ALTER TABLE `product_skus_details` DISABLE KEYS */;

INSERT INTO `product_skus_details` (`id`, `product_id`, `sku_id`, `name`, `value`, `attributed_id`, `attributed_value_id`, `del`)
VALUES
	(10010,749,10016,'PHong cách','Tối giản B+',10008,10009,0),
	(10011,749,10016,'Kích thước','30x55x30',10009,10010,1);

/*!40000 ALTER TABLE `product_skus_details` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table product_skus_price
# ------------------------------------------------------------

DROP TABLE IF EXISTS `product_skus_price`;

CREATE TABLE `product_skus_price` (
  `id` int NOT NULL AUTO_INCREMENT,
  `sku_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity_from` int NOT NULL,
  `quantity_to` int NOT NULL,
  `price_ref` int NOT NULL DEFAULT '0',
  `price` int NOT NULL DEFAULT '0',
  `price_import` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10014 DEFAULT CHARSET=utf8mb3;

LOCK TABLES `product_skus_price` WRITE;
/*!40000 ALTER TABLE `product_skus_price` DISABLE KEYS */;

INSERT INTO `product_skus_price` (`id`, `sku_id`, `product_id`, `quantity_from`, `quantity_to`, `price_ref`, `price`, `price_import`)
VALUES
	(10013,10016,749,1,10,0,1000000,0);

/*!40000 ALTER TABLE `product_skus_price` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table product_type
# ------------------------------------------------------------

DROP TABLE IF EXISTS `product_type`;

CREATE TABLE `product_type` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10011 DEFAULT CHARSET=utf8mb3;

LOCK TABLES `product_type` WRITE;
/*!40000 ALTER TABLE `product_type` DISABLE KEYS */;

INSERT INTO `product_type` (`id`, `name`)
VALUES
	(10008,'Software'),
	(10009,'Hardware'),
	(10010,'Co.Center');

/*!40000 ALTER TABLE `product_type` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table provider
# ------------------------------------------------------------

DROP TABLE IF EXISTS `provider`;

CREATE TABLE `provider` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `presentation` varchar(255) DEFAULT NULL,
  `mobile` varchar(20) DEFAULT NULL,
  `status` int DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10009 DEFAULT CHARSET=utf8mb3;

LOCK TABLES `provider` WRITE;
/*!40000 ALTER TABLE `provider` DISABLE KEYS */;

INSERT INTO `provider` (`id`, `name`, `address`, `presentation`, `mobile`, `status`)
VALUES
	(10008,'Flast Solution','35  Lê Văn Lương','H.T.M.D','098793891',1);

/*!40000 ALTER TABLE `provider` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table shipping
# ------------------------------------------------------------

DROP TABLE IF EXISTS `shipping`;

CREATE TABLE `shipping` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `stock_id` int DEFAULT NULL,
  `customer_phone` varchar(30) DEFAULT NULL,
  `customer_name` varchar(255) DEFAULT NULL,
  `warehouse_id` int NOT NULL,
  `product_id` int DEFAULT NULL,
  `user_name` varchar(100) NOT NULL,
  `transporter_id` int NOT NULL,
  `transporter_name` varchar(255) NOT NULL,
  `transporter_code` varchar(100) DEFAULT NULL,
  `fee` int DEFAULT '0',
  `cod` bigint DEFAULT '0',
  `quality` int DEFAULT '0',
  `province_id` int DEFAULT NULL,
  `district_id` int DEFAULT NULL,
  `ward_id` int DEFAULT NULL,
  `address` text,
  `in_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `retain_idx` (`warehouse_id`),
  KEY `trans_code_idx` (`transporter_code`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb3;



# Dump of table shipping_history
# ------------------------------------------------------------

DROP TABLE IF EXISTS `shipping_history`;

CREATE TABLE `shipping_history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_code` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `detail_code` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `order_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `customer_mobile_phone` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `warehouse_id` int DEFAULT NULL,
  `transport_code` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `transport_id` int DEFAULT NULL,
  `shipping_cost` int DEFAULT '0',
  `quantity` int DEFAULT '0',
  `in_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'thoi gian giao',
  `province_id` int DEFAULT NULL,
  `district_id` int DEFAULT NULL,
  `ward_id` int DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `content` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci COMMENT '[{orderDetailId, quantity}, {}...]',
  `note` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci,
  `status` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `transport_id_UNIQUE` (`transport_code`),
  KEY `order_code_index` (`order_code`)
) ENGINE=InnoDB AUTO_INCREMENT=11459 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;



# Dump of table stock
# ------------------------------------------------------------

DROP TABLE IF EXISTS `stock`;

CREATE TABLE `stock` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT '',
  `mobile` varchar(20) DEFAULT NULL,
  `area` varchar(255) DEFAULT NULL,
  `province_id` int DEFAULT NULL,
  `district_id` int DEFAULT NULL,
  `ward_id` int DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `status` int unsigned DEFAULT '0',
  `in_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb3;



# Dump of table transporter
# ------------------------------------------------------------

DROP TABLE IF EXISTS `transporter`;

CREATE TABLE `transporter` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10008 DEFAULT CHARSET=utf8mb3;



# Dump of table user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sso_id` varchar(30) NOT NULL,
  `password` varchar(100) NOT NULL,
  `firebase_token` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci,
  `layout` varchar(255) DEFAULT '',
  `full_name` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `phone` varchar(20) DEFAULT '',
  `email` varchar(30) NOT NULL,
  `status` int DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sso_id` (`sso_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1637 DEFAULT CHARSET=utf8mb3;

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;

INSERT INTO `user` (`id`, `sso_id`, `password`, `firebase_token`, `layout`, `full_name`, `phone`, `email`, `status`)
VALUES
	(2,'admin','$2a$10$GhyjCt8X1xA/staPlqAMFOOqqbMB3qKVAkSI56GJf8PT/txXARC8.','fqSSpT_mou2h1B_ygwekc1:APA91bE3xxIiIMzFivcG6liPBlW-6CspSPwAo4yQ6bXY8h4Y_Y9XdoITmRF-URsXm8KUhS71f6km37Kx8JnDeIJ8e2E21-4Wt9X-e7p2aL6YnIPTAmgRnv4qF16aR6vBrxQNtuZ6WIRc','UserLayout','Administrator','','flast.vn@gmail.com',1),
	(67,'longhuu','$2a$10$GhyjCt8X1xA/staPlqAMFOOqqbMB3qKVAkSI56GJf8PT/txXARC8.','d-y-L_k6plm_7iOjoPY--v:APA91bHNtUmk-Yitl0xGIc3lRCgzQRH7ySXFhV5IhTunuk6vbeYFtuDpiGMdqTW8rtWJxZgFDjyJpGHPhKhG5g33KNOH7IafbPp8S9MFIGruIU0ZJaW5VpNJ6jToBIKyj4JNM8BROxqv','SaleLayout','Hữu Long','','long.huu.100@gmail.com',1);

/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table user_kpi
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user_kpi`;

CREATE TABLE `user_kpi` (
  `id` int NOT NULL AUTO_INCREMENT,
  `department` int DEFAULT '0',
  `type` int DEFAULT '0',
  `user_id` int NOT NULL DEFAULT '0',
  `kpi_total` int NOT NULL DEFAULT '0',
  `kpi_revenue` int NOT NULL DEFAULT '0',
  `month` int NOT NULL DEFAULT '0',
  `year` int DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1021 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;



# Dump of table user_link_profile
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user_link_profile`;

CREATE TABLE `user_link_profile` (
  `user_id` bigint NOT NULL,
  `user_profile_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`user_profile_id`),
  KEY `FK_USER_PROFILE` (`user_profile_id`),
  CONSTRAINT `FK_APP_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_USER_PROFILE` FOREIGN KEY (`user_profile_id`) REFERENCES `user_profile` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

LOCK TABLES `user_link_profile` WRITE;
/*!40000 ALTER TABLE `user_link_profile` DISABLE KEYS */;

INSERT INTO `user_link_profile` (`user_id`, `user_profile_id`)
VALUES
	(2,2),
	(67,3),
	(67,5);

/*!40000 ALTER TABLE `user_link_profile` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table user_permision
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user_permision`;

CREATE TABLE `user_permision` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action` varchar(100) NOT NULL,
  `roles` varchar(255) NOT NULL DEFAULT 'hasRole(''USER'')',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb3;

LOCK TABLES `user_permision` WRITE;
/*!40000 ALTER TABLE `user_permision` DISABLE KEYS */;

INSERT INTO `user_permision` (`id`, `action`, `roles`)
VALUES
	(16,'/data/**','any'),
	(20,'/user-group/**','any'),
	(28,'/sale/list-file','hasRole(\'ADMIN\') or hasRole(\'SALE\')'),
	(38,'/sale/lists-data','hasRole(\'ADMIN\') or hasRole(\'SALE\')'),
	(39,'/sale/lists-order','hasRole(\'ADMIN\') or hasRole(\'SALE\')'),
	(40,'/sale/list-order-types','hasRole(\'ADMIN\') or hasRole(\'SALE\')'),
	(42,'/sale/create-order-detail','hasRole(\'ADMIN\') or hasRole(\'SALE\')'),
	(43,'/sale/update-order-detail','hasRole(\'ADMIN\') or hasRole(\'SALE\')'),
	(44,'/sale/update-order','hasRole(\'ADMIN\') or hasRole(\'SALE\')'),
	(46,'/sale/delete-order','hasRole(\'ADMIN\')'),
	(47,'/sale/delete-order-detail','hasRole(\'ADMIN\') or hasRole(\'SALE\')'),
	(51,'/sale/report','hasRole(\'ADMIN\') or hasRole(\'SALE\')'),
	(52,'/sale/uploads','hasRole(\'ADMIN\') or hasRole(\'SALE\')'),
	(56,'/sale/month-sale-report','hasRole(\'ADMIN\') or hasRole(\'SALE\')'),
	(57,'/sale/weekly-sale-report','hasRole(\'ADMIN\') or hasRole(\'SALE\')'),
	(63,'/customer/**','any'),
	(64,'/admin/**','hasRole(\'ADMIN\') '),
	(67,'/sale/lists-cohoi','hasRole(\'ADMIN\') or hasRole(\'SALE\')');

/*!40000 ALTER TABLE `user_permision` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table user_profile
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user_profile`;

CREATE TABLE `user_profile` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(30) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb3;

LOCK TABLES `user_profile` WRITE;
/*!40000 ALTER TABLE `user_profile` DISABLE KEYS */;

INSERT INTO `user_profile` (`id`, `type`)
VALUES
	(2,'ROLE_ADMIN'),
	(3,'ROLE_DBA'),
	(5,'ROLE_SALE'),
	(13,'ROLE_SALE_MANAGER'),
	(1,'ROLE_USER');

/*!40000 ALTER TABLE `user_profile` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table warehouse
# ------------------------------------------------------------

DROP TABLE IF EXISTS `warehouse`;

CREATE TABLE `warehouse` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `stock_id` int DEFAULT NULL,
  `stock_name` varchar(255) DEFAULT NULL,
  `user_name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `product_id` int NOT NULL,
  `sku_id` int NOT NULL,
  `sku_info` varchar(255) DEFAULT NULL,
  `fee` int DEFAULT '0',
  `quality` int DEFAULT '0',
  `total` int DEFAULT '0',
  `in_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `product_idx` (`product_id`),
  KEY `sku_idx` (`sku_id`),
  KEY `stock_idx` (`stock_id`)
) ENGINE=InnoDB AUTO_INCREMENT=269 DEFAULT CHARSET=utf8mb3;



# Dump of table warehouse_history
# ------------------------------------------------------------

DROP TABLE IF EXISTS `warehouse_history`;

CREATE TABLE `warehouse_history` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `warehouse_retain_id` int NOT NULL,
  `stock_id` int DEFAULT NULL,
  `user_name` varchar(100) DEFAULT NULL,
  `product_id` int NOT NULL,
  `sku_id` int NOT NULL,
  `sku_info` text,
  `fee` int DEFAULT '0',
  `quality` int DEFAULT '0',
  `in_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `product_retain_idx` (`product_id`,`warehouse_retain_id`),
  KEY `sku_idx` (`sku_id`),
  KEY `stock_idx` (`stock_id`)
) ENGINE=InnoDB AUTO_INCREMENT=329 DEFAULT CHARSET=utf8mb3;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
