-- MySQL dump 10.13  Distrib 9.3.0, for Linux (x86_64)
--
-- Host: localhost    Database: soundstream
-- ------------------------------------------------------
-- Server version	9.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `genres`
--

DROP TABLE IF EXISTS `genres`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `genres` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `genres`
--

LOCK TABLES `genres` WRITE;
/*!40000 ALTER TABLE `genres` DISABLE KEYS */;
INSERT INTO `genres` VALUES (1,'Rock'),(2,'Pop'),(3,'Jazz'),(4,'Electronic'),(5,'Hip Hop');
/*!40000 ALTER TABLE `genres` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `instruments`
--

DROP TABLE IF EXISTS `instruments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `instruments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category` varchar(255) DEFAULT NULL,
  `description` text,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKkdoje2ju81w7bdi8oy9wrkffo` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `instruments`
--

LOCK TABLES `instruments` WRITE;
/*!40000 ALTER TABLE `instruments` DISABLE KEYS */;
INSERT INTO `instruments` VALUES (1,NULL,NULL,'Drums'),(2,NULL,NULL,'Bass'),(3,NULL,NULL,'Guitar'),(4,NULL,NULL,'Strings'),(5,NULL,NULL,'Synth');
/*!40000 ALTER TABLE `instruments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playlists`
--

DROP TABLE IF EXISTS `playlists`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `playlists` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKo1cjkwid9wmumca7fc4jf1hgo` (`user_id`),
  CONSTRAINT `FKtgjwvfg23v990xk7k0idmqbrj` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlists`
--

LOCK TABLES `playlists` WRITE;
/*!40000 ALTER TABLE `playlists` DISABLE KEYS */;
/*!40000 ALTER TABLE `playlists` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `song_instruments`
--

DROP TABLE IF EXISTS `song_instruments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `song_instruments` (
  `song_id` bigint NOT NULL,
  `instrument_id` bigint NOT NULL,
  KEY `FKbn7fhjvbytjy73nmao8u3i50e` (`instrument_id`),
  KEY `FKacok4xbn1gs5qf9cu7w0tehoh` (`song_id`),
  CONSTRAINT `FKacok4xbn1gs5qf9cu7w0tehoh` FOREIGN KEY (`song_id`) REFERENCES `songs` (`id`),
  CONSTRAINT `FKbn7fhjvbytjy73nmao8u3i50e` FOREIGN KEY (`instrument_id`) REFERENCES `instruments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `song_instruments`
--

LOCK TABLES `song_instruments` WRITE;
/*!40000 ALTER TABLE `song_instruments` DISABLE KEYS */;
INSERT INTO `song_instruments` VALUES (26,4),(39,1),(39,2),(41,3),(41,5);
/*!40000 ALTER TABLE `song_instruments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `songs`
--

DROP TABLE IF EXISTS `songs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `songs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `duration` double DEFAULT NULL,
  `generated_url` text,
  `prompt_text` varchar(255) DEFAULT NULL,
  `tempo` int DEFAULT NULL,
  `genre_id` bigint DEFAULT NULL,
  `playlist_id` bigint DEFAULT NULL,
  `subgenre_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKd5mor9lg3wkqhn2tp0r75nkm` (`genre_id`),
  KEY `FKknkjyaoal230425r53msjg2n2` (`playlist_id`),
  KEY `FKmvc30ty21efs1rp2vit05j3nb` (`subgenre_id`),
  KEY `FKp2jirc70fqg9rx9jqr7eqypyd` (`user_id`),
  CONSTRAINT `FKd5mor9lg3wkqhn2tp0r75nkm` FOREIGN KEY (`genre_id`) REFERENCES `genres` (`id`),
  CONSTRAINT `FKknkjyaoal230425r53msjg2n2` FOREIGN KEY (`playlist_id`) REFERENCES `playlists` (`id`),
  CONSTRAINT `FKmvc30ty21efs1rp2vit05j3nb` FOREIGN KEY (`subgenre_id`) REFERENCES `subgenres` (`id`),
  CONSTRAINT `FKp2jirc70fqg9rx9jqr7eqypyd` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `songs`
--

LOCK TABLES `songs` WRITE;
/*!40000 ALTER TABLE `songs` DISABLE KEYS */;
INSERT INTO `songs` VALUES (21,'2025-05-04 13:35:25.240764',30,'https://storage.googleapis.com/udio-artifacts-c33fe3ba-3ffe-471f-92c8-5dfef90b3ea3/samples/5e890364283b4cb685a53ce4777d8c79/2/The%2520Untitled.mp3','',NULL,3,NULL,5,NULL,'Echoes of Yesterday'),(22,'2025-05-04 13:56:35.264034',40,'file:/C:/Users/pxadr/AppData/Local/Temp/trimmed-5726268859156384681.mp3','',NULL,1,NULL,NULL,NULL,'Ignite the Night'),(23,'2025-05-04 14:46:14.378229',40,'file:/C:/Users/pxadr/AppData/Local/Temp/trimmed-957470324730874288.mp3','',NULL,1,NULL,NULL,NULL,'Electric Nights'),(24,'2025-05-04 16:19:12.751982',NULL,'https://storage.googleapis.com/udio-artifacts-c33fe3ba-3ffe-471f-92c8-5dfef90b3ea3/samples/08f0f73f5df54e6dad31962f766bd84c/1/The%2520Untitled.mp3','',NULL,2,NULL,3,NULL,'Caught in the Groove'),(25,'2025-05-04 16:25:07.258461',NULL,'https://storage.googleapis.com/udio-artifacts-c33fe3ba-3ffe-471f-92c8-5dfef90b3ea3/samples/c4231139fad4450a9d04478a2e306860/2/The%2520Untitled.mp3','',NULL,2,NULL,4,NULL,'Ongoing Thrill'),(26,'2025-05-04 16:33:27.752527',NULL,'https://storage.googleapis.com/udio-artifacts-c33fe3ba-3ffe-471f-92c8-5dfef90b3ea3/samples/28604611c41c4ef392fe90a9104e7eb0/2/The%2520Untitled.mp3','',NULL,1,NULL,9,NULL,'Break Free from the Norm'),(27,'2025-05-04 16:51:51.835192',40,'http://localhost:8080/songs/trimmed-18267460429172319702.mp3','',NULL,4,NULL,NULL,NULL,'Ethereal Pulse'),(28,'2025-05-04 17:02:12.665189',40,'http://localhost:8080/songs/trimmed-10305176737564417703.mp3','',NULL,4,NULL,NULL,NULL,'Echoes in the Twilight'),(29,'2025-05-04 17:12:30.630984',40,'http://localhost:8080/songs/trimmed-16589565402163195814.mp3','',NULL,4,NULL,NULL,NULL,'Ethereal Journeys'),(30,'2025-05-04 17:21:20.928010',40,'http://localhost:8080/songs/trimmed-15813752524240470110.mp3','',NULL,4,NULL,NULL,NULL,'Digital Dreams'),(31,'2025-05-04 18:01:57.701764',40,'http://localhost:8080/songs/trimmed-16991173390028461819.mp3','',NULL,4,NULL,NULL,NULL,'Rhythms of the Future'),(32,'2025-05-04 18:40:08.567646',40,'http://localhost:8080/songs/trimmed-6436358675014404360.mp3','',NULL,4,NULL,NULL,NULL,'Infinite Horizons'),(33,'2025-05-04 18:47:22.937221',NULL,'https://storage.googleapis.com/udio-artifacts-c33fe3ba-3ffe-471f-92c8-5dfef90b3ea3/samples/561e5598bedd4932b948cb65169215c2/1/The%2520Untitled.mp3','',NULL,4,NULL,NULL,NULL,'Electric Dreams'),(34,'2025-05-04 19:01:16.746843',30,'http://localhost:8080/songs/trimmed-3396547810142327117.mp3','',NULL,4,NULL,NULL,NULL,'Drifting Through the Dark'),(35,'2025-05-04 20:01:08.293292',NULL,'https://storage.googleapis.com/udio-artifacts-c33fe3ba-3ffe-471f-92c8-5dfef90b3ea3/samples/41d28b8f01e14e018dee8de626f09e38/1/The%2520Untitled.mp3','Generate a Rock Song with a lot of female voices',NULL,NULL,NULL,NULL,NULL,'Voices Unleashed'),(36,'2025-05-04 20:13:05.801566',NULL,'https://storage.googleapis.com/udio-artifacts-c33fe3ba-3ffe-471f-92c8-5dfef90b3ea3/samples/4e02ce28daf744bcb00bbeb44c5a5883/2/The%2520Untitled.mp3','Seeeh',NULL,NULL,NULL,NULL,NULL,'Rise from the Ashes'),(37,'2025-05-04 21:11:10.106127',NULL,'https://storage.googleapis.com/udio-artifacts-c33fe3ba-3ffe-471f-92c8-5dfef90b3ea3/samples/7d9aec5545b44d268548398983c907dd/1/The%2520Untitled.mp3','Seeeh',NULL,NULL,NULL,NULL,NULL,'Chasing Sparks'),(38,'2025-05-07 17:15:37.470433',40,'http://localhost:8080/songs/trimmed-18290416623265720232.mp3','',NULL,1,NULL,NULL,NULL,'Chasing Our Skies'),(39,'2025-05-07 17:18:57.166055',40,'http://localhost:8080/songs/trimmed-15371876788081650371.mp3','',80,2,NULL,NULL,NULL,'Ignite the Night'),(40,'2025-05-07 17:21:25.173937',10,'http://localhost:8080/songs/trimmed-9662927897008472986.mp3','',80,4,NULL,10,NULL,'Soaring Together'),(41,'2025-05-07 17:29:26.614595',50,'http://localhost:8080/songs/trimmed-9558599079435537966.mp3','',80,4,NULL,10,NULL,'Chasing the Night'),(42,'2025-05-07 21:58:40.635905',NULL,'https://storage.googleapis.com/udio-artifacts-c33fe3ba-3ffe-471f-92c8-5dfef90b3ea3/samples/4f587fd019cb4e628b6a12cda4de64cb/1/The%2520Untitled.mp3','',NULL,1,NULL,NULL,NULL,'Chasing the Edge');
/*!40000 ALTER TABLE `songs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subgenres`
--

DROP TABLE IF EXISTS `subgenres`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subgenres` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `genre_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpeeoxxrca1w6pdl5qdu28iau7` (`genre_id`),
  CONSTRAINT `FKpeeoxxrca1w6pdl5qdu28iau7` FOREIGN KEY (`genre_id`) REFERENCES `genres` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subgenres`
--

LOCK TABLES `subgenres` WRITE;
/*!40000 ALTER TABLE `subgenres` DISABLE KEYS */;
INSERT INTO `subgenres` VALUES (1,'Indie Rock',1),(2,'Hard Rock',1),(3,'Dance Pop',2),(4,'Teen Pop',2),(5,'Smooth Jazz',3),(6,'Bebop',3),(7,'House',4),(8,'Trap',5),(9,'Punk',1),(10,'Trance',4);
/*!40000 ALTER TABLE `subgenres` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscriptions`
--

DROP TABLE IF EXISTS `subscriptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subscriptions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `commercial_use` bit(1) DEFAULT NULL,
  `end_date` datetime(6) DEFAULT NULL,
  `high_quality_audio` bit(1) DEFAULT NULL,
  `max_downloads_per_day` int DEFAULT NULL,
  `max_downloads_per_month` int DEFAULT NULL,
  `max_track_length` double DEFAULT NULL,
  `max_tracks_per_day` int DEFAULT NULL,
  `max_tracks_per_month` int DEFAULT NULL,
  `start_date` datetime(6) DEFAULT NULL,
  `type` enum('FREE','PERSONAL','PRO') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscriptions`
--

LOCK TABLES `subscriptions` WRITE;
/*!40000 ALTER TABLE `subscriptions` DISABLE KEYS */;
INSERT INTO `subscriptions` VALUES (3,_binary '\0',NULL,_binary '\0',1,NULL,0.5,15,NULL,'2025-05-05 08:55:39.458434','FREE'),(4,_binary '\0',NULL,_binary '\0',1,NULL,0.5,15,NULL,'2025-05-05 15:54:18.557451','FREE'),(5,_binary '\0',NULL,_binary '\0',1,NULL,0.5,15,NULL,'2025-05-06 22:23:31.717402','FREE'),(6,_binary '\0',NULL,_binary '\0',1,NULL,0.5,15,NULL,'2025-05-06 22:40:03.012487','FREE'),(7,_binary '\0',NULL,_binary '\0',1,NULL,0.5,15,NULL,'2025-05-07 06:04:20.871717','FREE'),(8,_binary '\0',NULL,_binary '\0',1,NULL,0.5,15,NULL,'2025-05-07 17:03:06.684354','FREE'),(9,_binary '\0',NULL,_binary '\0',1,NULL,0.5,15,NULL,'2025-05-07 21:37:03.657385','FREE');
/*!40000 ALTER TABLE `subscriptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `password` varchar(100) NOT NULL,
  `subscription_id` bigint NOT NULL,
  `role` enum('ADMIN','USER') NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKns8vi4ouq0uoo25pse5gos0bn` (`subscription_id`),
  CONSTRAINT `FKfwx079xww5uyfbpi9u8gwam34` FOREIGN KEY (`subscription_id`) REFERENCES `subscriptions` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'a@gmail.com','sisisi',3,'USER',NULL),(2,'pxadrian8@gmail.com','5dab5720-d4e6-48aa-8b3c-ac757cd6e45f',4,'USER',NULL),(3,'aaa@gmail.com','rororo',5,'USER','aaa'),(4,'aa@d.com','12345555',6,'USER','aa'),(5,'aa@gm.com','12341234',7,'USER','aaa'),(6,'admin@admin.com','adminadmin',8,'USER','admin'),(7,'eee@g.com','123455',9,'USER','hola');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-07 22:18:24
