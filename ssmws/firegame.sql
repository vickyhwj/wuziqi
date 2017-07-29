/*
Navicat MySQL Data Transfer

Source Server         : vicky
Source Server Version : 50519
Source Host           : localhost:3306
Source Database       : firegame

Target Server Type    : MYSQL
Target Server Version : 50519
File Encoding         : 65001

Date: 2017-07-29 10:37:25
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `relationship`
-- ----------------------------
DROP TABLE IF EXISTS `relationship`;
CREATE TABLE `relationship` (
  `userA` varchar(11) DEFAULT NULL,
  `userB` varchar(11) DEFAULT NULL,
  KEY `f1` (`userA`),
  KEY `f2` (`userB`),
  CONSTRAINT `f1` FOREIGN KEY (`userA`) REFERENCES `user` (`userid`),
  CONSTRAINT `f2` FOREIGN KEY (`userB`) REFERENCES `user` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of relationship
-- ----------------------------
INSERT INTO relationship VALUES ('1', '2');
INSERT INTO relationship VALUES ('2', '1');
INSERT INTO relationship VALUES ('1', '3');
INSERT INTO relationship VALUES ('3', '1');
INSERT INTO relationship VALUES ('google', 'jenny');
INSERT INTO relationship VALUES ('jenny', 'google');
INSERT INTO relationship VALUES ('vicky', 'jenny');
INSERT INTO relationship VALUES ('jenny', 'vicky');

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `userid` varchar(11) NOT NULL,
  `password` varchar(11) DEFAULT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO user VALUES ('1', '1');
INSERT INTO user VALUES ('2', '2');
INSERT INTO user VALUES ('3', '3');
INSERT INTO user VALUES ('google', 'google');
INSERT INTO user VALUES ('jenny', 'jenny');
INSERT INTO user VALUES ('vicky', '123');
