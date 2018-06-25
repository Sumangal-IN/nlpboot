-- MySQL dump 10.13  Distrib 5.7.21, for Win32 (AMD64)
--
-- Host: 127.0.0.1    Database: nlp
-- ------------------------------------------------------
-- Server version	5.0.67-community-nt

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Not dumping tablespaces as no INFORMATION_SCHEMA.FILES table on this server
--

--
-- Table structure for table `article`
--

DROP TABLE IF EXISTS `article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `article` (
  `article_number` int(11) NOT NULL,
  `article_name` varchar(45) default NULL,
  `article_ean` int(20) default NULL,
  PRIMARY KEY  (`article_number`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article`
--

LOCK TABLES `article` WRITE;
/*!40000 ALTER TABLE `article` DISABLE KEYS */;
INSERT INTO `article` VALUES (101,'pencil',1010),(102,'paper',1020),(103,'book',1030),(104,'notebook',1040),(105,'cup',1050),(110,'paint brush',1100);
/*!40000 ALTER TABLE `article` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `city`
--

DROP TABLE IF EXISTS `city`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `city` (
  `city_id` int(11) NOT NULL auto_increment,
  `city_name` varchar(100) default NULL,
  `state_id` int(11) default NULL,
  PRIMARY KEY  (`city_id`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city`
--

LOCK TABLES `city` WRITE;
/*!40000 ALTER TABLE `city` DISABLE KEYS */;
INSERT INTO `city` VALUES (1,'kolkata',1),(2,'nagpur',3),(3,'mumbai',3),(4,'jodhpur',4),(5,'darjeeling',1),(6,'chennai',5),(7,'jaipur',4);
/*!40000 ALTER TABLE `city` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `country`
--

DROP TABLE IF EXISTS `country`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `country` (
  `country_id` int(11) NOT NULL auto_increment,
  `country_name` varchar(100) collate utf8_unicode_ci default NULL,
  PRIMARY KEY  (`country_id`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `country`
--

LOCK TABLES `country` WRITE;
/*!40000 ALTER TABLE `country` DISABLE KEYS */;
INSERT INTO `country` VALUES (1,'india'),(2,'pakistan'),(3,'australia'),(4,'bangladesh');
/*!40000 ALTER TABLE `country` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `matches`
--

DROP TABLE IF EXISTS `matches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `matches` (
  `match_id` int(11) NOT NULL auto_increment,
  `team_1` int(11) default NULL,
  `team_2` int(11) default NULL,
  `winner` int(11) default NULL,
  `stadium` varchar(50) default NULL,
  `year` varchar(100) default NULL,
  PRIMARY KEY  (`match_id`)
) ENGINE=MyISAM AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `matches`
--

LOCK TABLES `matches` WRITE;
/*!40000 ALTER TABLE `matches` DISABLE KEYS */;
INSERT INTO `matches` VALUES (1,1,2,2,'PGC','2011'),(2,1,3,1,'PVF','2012'),(3,1,4,4,'PGC','2013'),(5,2,3,3,'LYP','2011'),(6,2,4,2,'XTK','2016'),(8,2,3,2,'LYP','2018'),(9,3,4,3,'XTK','2017'),(10,3,2,2,'XTK','2018'),(12,3,1,1,'PGC','2015');
/*!40000 ALTER TABLE `matches` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `metadata_column`
--

DROP TABLE IF EXISTS `metadata_column`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `metadata_column` (
  `id` int(11) NOT NULL auto_increment,
  `table_id` int(11) default NULL,
  `name` varchar(45) default NULL,
  `primary` int(1) default NULL,
  `auto_resolve` int(1) default NULL,
  `project` int(1) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=22 DEFAULT CHARSET=utf8 COMMENT='	';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `metadata_column`
--

LOCK TABLES `metadata_column` WRITE;
/*!40000 ALTER TABLE `metadata_column` DISABLE KEYS */;
INSERT INTO `metadata_column` VALUES (1,1,'store_number',1,0,1),(2,1,'store_name',0,1,1),(5,2,'article_number',1,0,1),(6,2,'article_name',0,1,1),(7,2,'article_ean',0,0,1),(8,3,'qty',0,0,1),(9,3,'amount',0,0,1),(10,3,'date',0,0,1),(11,6,'state_name',1,1,1),(12,4,'country_name',1,1,1),(13,7,'match_id',1,0,0),(14,7,'team_1',0,0,0),(15,7,'team_2',0,0,0),(16,7,'winner',0,0,0),(17,7,'stadium',0,0,1),(18,7,'year',0,0,1),(19,8,'city_id',0,0,0),(20,8,'city_name',1,1,1),(21,8,'state_id',0,0,0);
/*!40000 ALTER TABLE `metadata_column` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `metadata_expression`
--

DROP TABLE IF EXISTS `metadata_expression`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `metadata_expression` (
  `id` int(11) NOT NULL auto_increment,
  `table_id` int(11) default NULL,
  `expression` varchar(1000) default NULL,
  `projection` varchar(1000) default NULL,
  `condition` varchar(1000) default NULL,
  `order` varchar(1000) default NULL,
  `limit` varchar(1000) default NULL,
  `having` varchar(1000) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=40 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `metadata_expression`
--

LOCK TABLES `metadata_expression` WRITE;
/*!40000 ALTER TABLE `metadata_expression` DISABLE KEYS */;
INSERT INTO `metadata_expression` VALUES (11,3,'(top|highest) sell','sum(sell.amount) sell_amount',NULL,'sum(sell.amount) desc','limit 1',NULL),(12,3,'((top|highest) (qty|sell) (qty|sell)|sell highest qty)','sum(sell.qty) sell_quantity',NULL,'sum(sell.qty) desc','limit 1',NULL),(19,3,'((least|lowest) (qty|sell) (qty|sell)|sell lowest qty)','sum(sell.amount) sell_amount',NULL,'sum(sell.qty) asc','limit 1',NULL),(20,3,'(least|lowest) sell','sum(sell.amount) sell_amount',NULL,'sum(sell.amount) asc','limit 1',NULL),(22,3,'least \\d+ (store|sell) (store|sell)','sum(sell.amount) sell_amount',NULL,'sum(sell.amount) asc','limit [a]',NULL),(23,3,'top \\d+ (article|sell) (article|sell)','sum(sell.qty) sell_quantity',NULL,'sum(sell.qty) desc','limit [a]',NULL),(24,3,'least \\d+ (article|sell) (article|sell)','sum(sell.qty) sell_quantity',NULL,'sum(sell.qty) asc','limit [a]',NULL),(25,3,'top \\d+ (store|sell) (store|sell)','sum(sell.amount) sell_amount',NULL,'sum(sell.amount) desc','limit [a]',NULL),(26,3,'last \\d+ day',NULL,'sell.date>current_date-([a]-1)',NULL,NULL,NULL),(27,3,'today',NULL,'sell.date=current_date',NULL,NULL,NULL),(28,3,'yesterday',NULL,'sell.date=current_date-1',NULL,NULL,NULL),(29,3,'more than \\d+ qty','sum(sell.qty) sell_quantity',NULL,'sum(sell.qty) asc',NULL,'sum(sell.qty)>[a]'),(30,3,'less than \\d+ qty','sum(sell.qty) sell_quantity',NULL,'sum(sell.qty) desc',NULL,'sum(sell.qty)<[a]'),(31,2,'(article |article number |article code )\\d+',NULL,'article.article_number=\'[a]\'',NULL,NULL,NULL),(32,2,'(article ean |article barcode |ean |barcode )\\d+',NULL,'article.article_ean=\'[a]\'',NULL,NULL,NULL),(33,1,'(store |store number |store code )\\d+',NULL,'store.store_number=\'[a]\'',NULL,NULL,NULL),(34,7,'(sort|arrange|order) year',NULL,NULL,'matches.year asc',NULL,NULL),(35,7,'(what|show|find|display|which|list) (\\w| )+','(select country_name from country where country_id=matches.team_1) team_1, (select country_name from country where country_id=matches.team_2) team_2, (select country_name from country where country_id=matches.winner) winner',NULL,NULL,NULL,NULL),(37,7,'won \\w+',NULL,'matches.winner=(select country_id from country where country_name=\'[a]\')',NULL,NULL,NULL),(38,7,'play \\w+',NULL,'(matches.team_1=(select country_id from country where country_name=\'[a]\') or matches.team_2=(select country_id from country where country_name=\'[a]\'))',NULL,NULL,NULL),(39,7,'last \\d+ (years|year)',NULL,'matches.year>(YEAR(CURDATE())-[a])',NULL,NULL,NULL);
/*!40000 ALTER TABLE `metadata_expression` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `metadata_expression_variable`
--

DROP TABLE IF EXISTS `metadata_expression_variable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `metadata_expression_variable` (
  `id` int(11) NOT NULL auto_increment,
  `expression_id` int(11) default NULL,
  `variable` varchar(45) default NULL,
  `exp_remove` varchar(100) default NULL,
  `exp_extract` varchar(100) default NULL,
  `occurance` int(2) default '1',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `metadata_expression_variable`
--

LOCK TABLES `metadata_expression_variable` WRITE;
/*!40000 ALTER TABLE `metadata_expression_variable` DISABLE KEYS */;
INSERT INTO `metadata_expression_variable` VALUES (1,33,'[a]','(store|code|number)+','\\d+',1),(2,2,'[a]','(store|name| )+','\\w[\\w\\d]+',1),(3,3,'[a]','(region|name| )+','\\w+',1),(4,4,'[a]','(opco|name| )+','\\w+',1),(5,31,'[a]','(article|code|number| )+','\\d+',1),(6,6,'[a]','(article|name| )+','\\w+',1),(7,32,'[a]','(article|barcode|ean| )+','\\d+',1),(8,22,'[a]','(least|store|sell| )+','\\d+',1),(9,23,'[a]','(top|store|sell| )+','\\d+',1),(10,24,'[a]','(least|store|sell| )+','\\d+',1),(11,25,'[a]','(top|store|sell| )+','\\d+',1),(12,26,'[a]','(last|day| )+','\\d+',1),(15,29,'[a]','(more|than|qty| )+','\\d+',1),(16,30,'[a]','(less|than|qty| )+','\\d+',1),(17,37,'[a]','(won)+','\\w+',1),(18,38,'[a]','(play)+','\\w+',1),(19,39,'[a]','(last|year)+','\\d+',1);
/*!40000 ALTER TABLE `metadata_expression_variable` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `metadata_starter`
--

DROP TABLE IF EXISTS `metadata_starter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `metadata_starter` (
  `id` int(11) NOT NULL auto_increment,
  `starts` varchar(100) default NULL,
  `projection` varchar(100) default NULL,
  `aggregration` varchar(100) default NULL,
  `groupby` varchar(100) default NULL,
  `header` int(1) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `metadata_starter`
--

LOCK TABLES `metadata_starter` WRITE;
/*!40000 ALTER TABLE `metadata_starter` DISABLE KEYS */;
INSERT INTO `metadata_starter` VALUES (1,'what','nverb.all',NULL,'[nverb.all]',1),(2,'show','nverb.all',NULL,'[nverb.all]',1),(3,'display','nverb.all',NULL,'[nverb.all]',1),(4,'which','nverb.all',NULL,'[nverb.all]',1),(5,'find','nverb.all',NULL,'[nverb.all]',1),(6,'how many','nverb.primary','ifnull(count(distinct [nverb.primary]),0)',NULL,0),(7,'count','nverb.primary','ifnull(count(distinct [nverb.primary]),0)',NULL,0),(8,'number','nverb.primary','ifnull(count(distinct [nverb.primary]),0)',NULL,0),(9,'how much','column','sum([column])',NULL,0),(10,'list','nverb.all',NULL,'[nverb.all]',1);
/*!40000 ALTER TABLE `metadata_starter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `metadata_table_join`
--

DROP TABLE IF EXISTS `metadata_table_join`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `metadata_table_join` (
  `id` int(11) NOT NULL auto_increment,
  `table1` varchar(45) default NULL,
  `table2` varchar(45) default NULL,
  `condition` varchar(45) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `metadata_table_join`
--

LOCK TABLES `metadata_table_join` WRITE;
/*!40000 ALTER TABLE `metadata_table_join` DISABLE KEYS */;
INSERT INTO `metadata_table_join` VALUES (1,'store','sell','store.store_number=sell.store_number'),(2,'sell','article','sell.article_number=article.article_number'),(3,'state','store','state.state_id=store.state_id'),(4,'country','state','country.country_id=state.country_id'),(6,'country','matches','1=1');
/*!40000 ALTER TABLE `metadata_table_join` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `metadata_tables`
--

DROP TABLE IF EXISTS `metadata_tables`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `metadata_tables` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(45) default NULL,
  `verb` int(1) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `metadata_tables`
--

LOCK TABLES `metadata_tables` WRITE;
/*!40000 ALTER TABLE `metadata_tables` DISABLE KEYS */;
INSERT INTO `metadata_tables` VALUES (1,'store',0),(2,'article',0),(3,'sell',1),(4,'country',0),(6,'state',0),(7,'matches',0),(8,'city',0);
/*!40000 ALTER TABLE `metadata_tables` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sell`
--

DROP TABLE IF EXISTS `sell`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sell` (
  `id` int(11) NOT NULL auto_increment,
  `store_number` varchar(45) default NULL,
  `article_number` varchar(45) default NULL,
  `qty` int(10) default NULL,
  `amount` int(10) default NULL,
  `date` timestamp NULL default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sell`
--

LOCK TABLES `sell` WRITE;
/*!40000 ALTER TABLE `sell` DISABLE KEYS */;
INSERT INTO `sell` VALUES (1,'1','101',5,50,'2018-05-29 22:00:00'),(2,'1','101',4,40,'2018-05-28 23:00:00'),(3,'4','102',4,40,'2018-05-29 23:00:00'),(4,'3','101',6,60,'2018-05-27 23:00:00'),(5,'2','105',10,100,'2018-05-30 03:12:20'),(6,'5','110',2,20,'2018-05-29 03:12:53'),(7,'3','102',5,50,'2018-05-28 03:13:34');
/*!40000 ALTER TABLE `sell` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `state`
--

DROP TABLE IF EXISTS `state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `state` (
  `state_id` int(11) NOT NULL auto_increment,
  `state_name` varchar(100) collate utf8_unicode_ci default NULL,
  `country_id` int(11) default NULL,
  PRIMARY KEY  (`state_id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `state`
--

LOCK TABLES `state` WRITE;
/*!40000 ALTER TABLE `state` DISABLE KEYS */;
INSERT INTO `state` VALUES (1,'west bengal',1),(2,'delhi',1),(3,'maharastra',1),(4,'rajasthan',1),(5,'tamilnadu',1);
/*!40000 ALTER TABLE `state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store`
--

DROP TABLE IF EXISTS `store`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `store` (
  `store_number` int(11) NOT NULL,
  `store_name` varchar(45) default NULL,
  `state_id` int(11) default NULL,
  PRIMARY KEY  (`store_number`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store`
--

LOCK TABLES `store` WRITE;
/*!40000 ALTER TABLE `store` DISABLE KEYS */;
INSERT INTO `store` VALUES (1,'kolkata_bb',1),(2,'delhi_bb',2),(3,'mumbai_sp',3),(4,'darjeeling_sp',1),(5,'nagpur_bb',3);
/*!40000 ALTER TABLE `store` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `test`
--

DROP TABLE IF EXISTS `test`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `test` (
  `id` int(11) NOT NULL auto_increment,
  `input` varchar(1000) default NULL,
  `output` varchar(1000) default NULL,
  `enable` int(1) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test`
--

LOCK TABLES `test` WRITE;
/*!40000 ALTER TABLE `test` DISABLE KEYS */;
INSERT INTO `test` VALUES (1,'which stores has sold article 101 in west bengal in last 7 days',NULL,0),(2,'what are the top 5 selling stores that sells pen and paper in last 7 days',NULL,0),(3,'what are the top 5 selling stores for article 101 between 21st January 2018 and 31st December 2019',NULL,0),(4,'what are the top 5 selling stores for article code 1152 in last 7 days',NULL,0),(5,'how many article with barcode 1050 sold yesterday',NULL,0),(7,'what are the top 5 selling articles in store 4',NULL,0),(8,'how many stores sold the article name pencil yesterday',NULL,0),(9,'how much quantity of paper sold yesterday',NULL,0),(10,'find the store that sold more than 5000 units of pencil in last 7 days',NULL,0),(11,'which store have sold highest qty yesterday',NULL,0),(12,'what are the top 5 articles sold yesterday',NULL,0),(13,'which stores have sold article pen yesterday',NULL,0),(15,'HOW MANY STORES HAS SOLD PAPER',NULL,0),(16,'what is the top selling product in west bengal',NULL,0),(17,'HOW MANY STORIES HAVE SOLD ARTICLE PAIN',NULL,0),(18,'HOW MANY STORE HAVE TOLD PENCIL',NULL,0),(19,'how much qty sold',NULL,0),(20,'how many matches were played',NULL,0),(21,'which store',NULL,0),(22,'LIST ALL THE MATCHES won BY india in last 10 years ORDER BY YEAR',NULL,1),(23,'LIST ALL THE MATCHES played by india in last 6 years',NULL,1);
/*!40000 ALTER TABLE `test` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `word_replace`
--

DROP TABLE IF EXISTS `word_replace`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `word_replace` (
  `id` int(11) NOT NULL auto_increment,
  `word` varchar(100) default NULL,
  `new_word` varchar(100) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=90 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `word_replace`
--

LOCK TABLES `word_replace` WRITE;
/*!40000 ALTER TABLE `word_replace` DISABLE KEYS */;
INSERT INTO `word_replace` VALUES (1,'the',NULL),(3,'a',NULL),(4,'an',NULL),(5,'customer','castorama'),(6,'products','article'),(7,'suppliers','supplier'),(8,'articles','article'),(9,'units','qty'),(10,'my',NULL),(11,'sells','sell'),(12,'sold','sell'),(13,'days','day'),(14,'hours','hour'),(15,'by',NULL),(16,'in',NULL),(17,'on',NULL),(18,'at',NULL),(19,'product','article'),(20,'see','ce'),(21,'item','article'),(22,'items','article'),(24,'what','what'),(25,'show me','what'),(26,'display','what'),(27,'extract','what'),(28,'stores','store'),(29,'shop','store'),(30,'shops','store'),(31,'and',NULL),(32,'or',NULL),(34,'been',NULL),(35,'has',NULL),(36,'have',NULL),(37,'had',NULL),(38,'having',NULL),(39,'considering',NULL),(40,'consider',NULL),(41,'considers',NULL),(42,'hours','hour'),(43,'years','year'),(44,'months','month'),(45,'weeks','week'),(46,'show','what'),(47,'aapko','opco'),(48,'apko','opco'),(49,'barcode','ean'),(50,'selling','sell'),(51,'code','number'),(52,'which','what'),(56,'all',NULL),(57,'with',NULL),(59,'are',NULL),(60,'is',NULL),(61,'in',NULL),(63,'this',NULL),(64,'quantity','qty'),(65,'quantities','qty'),(66,'unit','qty'),(67,'price','amount'),(68,'can you tell me',NULL),(69,'that',NULL),(70,'of',NULL),(71,'for',NULL),(72,'of',NULL),(73,'played','play'),(74,'match','matches'),(79,'play matches','matches'),(88,'tell me',NULL),(77,'were',NULL),(80,NULL,NULL),(81,'was',NULL),(82,'we',NULL),(83,'states','state'),(84,'countries','country'),(85,'arranged','arrange'),(86,'teams','team'),(87,'team','country'),(89,'from',NULL);
/*!40000 ALTER TABLE `word_replace` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-05-30  9:22:58
