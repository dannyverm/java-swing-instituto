/*
SQLyog - Free MySQL GUI v5.02
Host - 5.0.13-rc-nt : Database - cmi
*********************************************************************
Server version : 5.0.13-rc-nt
*/


create database if not exists `cmi`;

USE `cmi`;

SET FOREIGN_KEY_CHECKS=0;

/*Table structure for table `departamento` */

DROP TABLE IF EXISTS `departamento`;

CREATE TABLE `departamento` (
  `dep_cod` int(10) unsigned NOT NULL,
  `dep_nom` varchar(100) default NULL,
  PRIMARY KEY  (`dep_cod`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `departamento` */

LOCK TABLES `departamento` WRITE;

insert into `departamento` values 
(1,'FARMACIA'),
(2,'LABORATORIO'),
(3,'SUMINISTRO');

UNLOCK TABLES;

/*Table structure for table `deposito` */

DROP TABLE IF EXISTS `deposito`;

CREATE TABLE `deposito` (
  `dep_cod` int(6) NOT NULL,
  `dep_desc` varchar(30) default NULL,
  PRIMARY KEY  (`dep_cod`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `deposito` */

LOCK TABLES `deposito` WRITE;

UNLOCK TABLES;

/*Table structure for table `destino` */

DROP TABLE IF EXISTS `destino`;

CREATE TABLE `destino` (
  `des_cod` int(10) unsigned NOT NULL auto_increment,
  `des_desc` varchar(60) default NULL,
  PRIMARY KEY  (`des_cod`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `destino` */

LOCK TABLES `destino` WRITE;

UNLOCK TABLES;

/*Table structure for table `det_entrada` */

DROP TABLE IF EXISTS `det_entrada`;

CREATE TABLE `det_entrada` (
  `ent_cod` int(10) NOT NULL,
  `pro_cod` int(10) NOT NULL,
  `ent_can` double default NULL,
  PRIMARY KEY  (`ent_cod`,`pro_cod`),
  KEY `FK_det_entrad2` (`pro_cod`),
  CONSTRAINT `det_entrada_ibfk_1` FOREIGN KEY (`ent_cod`) REFERENCES `entrada` (`ent_cod`),
  CONSTRAINT `det_entrada_ibfk_2` FOREIGN KEY (`pro_cod`) REFERENCES `productos` (`pro_cod`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `det_entrada` */

LOCK TABLES `det_entrada` WRITE;

UNLOCK TABLES;

/*Table structure for table `det_oc` */

DROP TABLE IF EXISTS `det_oc`;

CREATE TABLE `det_oc` (
  `oc_cod` int(10) unsigned NOT NULL,
  `pro_cod` int(20) NOT NULL,
  `med_cod` int(10) unsigned NOT NULL,
  `detoc_can` decimal(10,0) default NULL,
  `detoc_pre` decimal(10,0) default NULL,
  `detoc_exenta` decimal(10,0) default NULL,
  `detoc_iva5` decimal(10,0) default NULL,
  `detoc_iva10` decimal(10,0) default NULL,
  PRIMARY KEY  (`oc_cod`,`pro_cod`),
  KEY `oc_has_productos_FKIndex1` (`oc_cod`),
  KEY `oc_has_productos_FKIndex2` (`pro_cod`),
  KEY `det_oc_FKIndex3` (`med_cod`),
  CONSTRAINT `det_oc_ibfk_1` FOREIGN KEY (`oc_cod`) REFERENCES `oc` (`oc_cod`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `det_oc_ibfk_2` FOREIGN KEY (`pro_cod`) REFERENCES `productos` (`pro_cod`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `det_oc_ibfk_3` FOREIGN KEY (`med_cod`) REFERENCES `medida` (`med_cod`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `det_oc` */

LOCK TABLES `det_oc` WRITE;

insert into `det_oc` values 
(1,1,1,'800','100',NULL,NULL,'80000'),
(1,2,1,'12','80000',NULL,'960000',NULL),
(2,1,1,'300','50000',NULL,NULL,'15000000'),
(2,2,1,'123','60000',NULL,'7380000',NULL),
(3,1,1,'300','30000',NULL,NULL,'9000000'),
(3,2,1,'200','20000',NULL,'4000000',NULL),
(4,1,1,'300','40000',NULL,NULL,'12000000'),
(4,2,1,'200','20000',NULL,'4000000',NULL),
(5,1,1,'200','40000',NULL,NULL,'8000000'),
(5,2,1,'200','20000',NULL,'4000000',NULL),
(6,1,1,'100','5000',NULL,NULL,'500000'),
(6,2,1,'200','3000',NULL,'600000',NULL),
(7,1,1,'300','500000',NULL,NULL,'150000000'),
(7,2,1,'300','300',NULL,'90000',NULL),
(8,1,1,'340','20000',NULL,NULL,'6800000'),
(8,2,1,'300','20000',NULL,'6000000',NULL),
(9,1,1,'1200','200',NULL,NULL,'240000'),
(9,2,1,'200','20000',NULL,'4000000',NULL),
(10,2,1,'120','30000',NULL,'3600000',NULL),
(11,1,1,'200','40000',NULL,NULL,'8000000'),
(11,2,1,'1200','30000',NULL,'36000000',NULL),
(12,2,1,'2000','200',NULL,'400000',NULL),
(13,2,1,'200','20000',NULL,'4000000',NULL),
(14,1,1,'200','40000',NULL,NULL,'8000000'),
(14,2,1,'300','30000',NULL,'9000000',NULL),
(16,2,1,'200','30000',NULL,'6000000',NULL);

UNLOCK TABLES;

/*Table structure for table `det_salida` */

DROP TABLE IF EXISTS `det_salida`;

CREATE TABLE `det_salida` (
  `salida_cod` int(11) NOT NULL,
  `pro_cod` int(20) NOT NULL,
  `dep_cod` int(6) NOT NULL,
  `cantidad` double default NULL,
  `fecha` date default NULL,
  PRIMARY KEY  (`salida_cod`,`pro_cod`,`dep_cod`),
  KEY `salida_has_stock_FKIndex1` (`salida_cod`),
  KEY `salida_has_stock_FKIndex2` (`pro_cod`,`dep_cod`),
  CONSTRAINT `det_salida_ibfk_1` FOREIGN KEY (`salida_cod`) REFERENCES `salida` (`salida_cod`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `det_salida_ibfk_2` FOREIGN KEY (`pro_cod`, `dep_cod`) REFERENCES `stock` (`pro_cod`, `dep_cod`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `det_salida` */

LOCK TABLES `det_salida` WRITE;

UNLOCK TABLES;

/*Table structure for table `detalle_pedido` */

DROP TABLE IF EXISTS `detalle_pedido`;

CREATE TABLE `detalle_pedido` (
  `pro_cod` int(20) NOT NULL,
  `ped_cod` int(11) NOT NULL,
  `cantidad` double default NULL,
  PRIMARY KEY  (`pro_cod`,`ped_cod`),
  KEY `productos_has_movimiento_FKIndex1` (`pro_cod`),
  KEY `detalle_pedido_FKIndex2` (`ped_cod`),
  CONSTRAINT `detalle_pedido_ibfk_1` FOREIGN KEY (`pro_cod`) REFERENCES `productos` (`pro_cod`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `detalle_pedido_ibfk_2` FOREIGN KEY (`ped_cod`) REFERENCES `pedido` (`ped_cod`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `detalle_pedido` */

LOCK TABLES `detalle_pedido` WRITE;

UNLOCK TABLES;

/*Table structure for table `entrada` */

DROP TABLE IF EXISTS `entrada`;

CREATE TABLE `entrada` (
  `ent_cod` int(10) NOT NULL,
  `oc_cod` int(10) unsigned default NULL,
  `tient_cod` int(10) unsigned default NULL,
  `ent_rem` varchar(100) default NULL,
  `ent_fec` date default NULL,
  PRIMARY KEY  (`ent_cod`),
  KEY `FK_entrada` (`oc_cod`),
  KEY `FK_tipo` (`tient_cod`),
  CONSTRAINT `entrada_ibfk_1` FOREIGN KEY (`oc_cod`) REFERENCES `oc` (`oc_cod`),
  CONSTRAINT `entrada_ibfk_2` FOREIGN KEY (`tient_cod`) REFERENCES `tipo_ent` (`tient_cod`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `entrada` */

LOCK TABLES `entrada` WRITE;

UNLOCK TABLES;

/*Table structure for table `forma_pago` */

DROP TABLE IF EXISTS `forma_pago`;

CREATE TABLE `forma_pago` (
  `pag_cod` int(10) unsigned NOT NULL,
  `pag_des` varchar(60) default NULL,
  PRIMARY KEY  (`pag_cod`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `forma_pago` */

LOCK TABLES `forma_pago` WRITE;

insert into `forma_pago` values 
(1,'LICITACION POR CONCURSO DE OFERTAS 04/2010');

UNLOCK TABLES;

/*Table structure for table `historial_stock` */

DROP TABLE IF EXISTS `historial_stock`;

CREATE TABLE `historial_stock` (
  `dep_cod` int(6) NOT NULL,
  `pro_cod` int(20) NOT NULL,
  `his_fec` date NOT NULL,
  `his_stock` double default NULL,
  PRIMARY KEY  (`dep_cod`,`pro_cod`,`his_fec`),
  KEY `historial_stock_FKIndex1` (`pro_cod`,`dep_cod`),
  CONSTRAINT `historial_stock_ibfk_1` FOREIGN KEY (`pro_cod`, `dep_cod`) REFERENCES `stock` (`pro_cod`, `dep_cod`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `historial_stock` */

LOCK TABLES `historial_stock` WRITE;

UNLOCK TABLES;

/*Table structure for table `imputacion` */

DROP TABLE IF EXISTS `imputacion`;

CREATE TABLE `imputacion` (
  `imp_cod` int(10) unsigned NOT NULL,
  `imp_des` varchar(60) default NULL,
  PRIMARY KEY  (`imp_cod`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `imputacion` */

LOCK TABLES `imputacion` WRITE;

insert into `imputacion` values 
(1,'240 - S.A.N.R.'),
(2,'350 - P.I.Q.N.');

UNLOCK TABLES;

/*Table structure for table `lote` */

DROP TABLE IF EXISTS `lote`;

CREATE TABLE `lote` (
  `lot_cod` int(10) unsigned NOT NULL,
  `pro_cod` int(20) NOT NULL,
  `lot_ven` date default NULL,
  `lot_nro` int(20) default NULL,
  PRIMARY KEY  (`lot_cod`),
  KEY `lote_FKIndex1` (`pro_cod`),
  CONSTRAINT `lote_ibfk_1` FOREIGN KEY (`pro_cod`) REFERENCES `productos` (`pro_cod`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `lote` */

LOCK TABLES `lote` WRITE;

UNLOCK TABLES;

/*Table structure for table `medida` */

DROP TABLE IF EXISTS `medida`;

CREATE TABLE `medida` (
  `med_cod` int(10) unsigned NOT NULL,
  `med_des` varchar(150) default NULL,
  PRIMARY KEY  (`med_cod`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `medida` */

LOCK TABLES `medida` WRITE;

insert into `medida` values 
(1,'UNID.');

UNLOCK TABLES;

/*Table structure for table `oc` */

DROP TABLE IF EXISTS `oc`;

CREATE TABLE `oc` (
  `oc_cod` int(10) unsigned NOT NULL,
  `pag_cod` int(10) unsigned NOT NULL,
  `prv_cod` int(10) unsigned NOT NULL,
  `usu_cod` int(10) unsigned default NULL,
  `oc_nro` varchar(50) default NULL,
  `oc_fec_emi` date default NULL,
  `oc_fec_pla` date default NULL,
  `oc_sub_tot` decimal(10,0) default NULL,
  `oc_tot_gral` decimal(10,0) default NULL,
  `oc_liq_iva` decimal(10,0) default NULL,
  `oc_tot_iva` decimal(10,0) default NULL,
  `oc_lug` varchar(200) default NULL,
  `oc_tipo_presupuesto` varchar(4) default NULL,
  `oc_programa` varchar(4) default NULL,
  `oc_proyecto` varchar(4) default NULL,
  `oc_fue_financiacion` varchar(4) default NULL,
  `oc_org_financiacion` varchar(4) default NULL,
  `oc_obj_gasto` varchar(4) default NULL,
  `oc_cdp` varchar(4) default NULL,
  `oc_admin` varchar(80) default NULL,
  `oc_decano` varchar(80) default NULL,
  `imp_cod` int(10) unsigned default NULL,
  `oc_estado` varchar(30) default NULL,
  `dep_cod` int(10) unsigned default NULL,
  PRIMARY KEY  (`oc_cod`),
  KEY `oc_FKIndex2` (`usu_cod`),
  KEY `oc_FKIndex3` (`prv_cod`),
  KEY `oc_FKIndex4` (`pag_cod`),
  KEY `dep_cod` (`dep_cod`),
  KEY `FK_oc` (`imp_cod`),
  CONSTRAINT `oc_ibfk_1` FOREIGN KEY (`usu_cod`) REFERENCES `usuario` (`usu_cod`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `oc_ibfk_2` FOREIGN KEY (`prv_cod`) REFERENCES `proveedor` (`prv_cod`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `oc_ibfk_3` FOREIGN KEY (`pag_cod`) REFERENCES `forma_pago` (`pag_cod`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `oc_ibfk_4` FOREIGN KEY (`dep_cod`) REFERENCES `departamento` (`dep_cod`),
  CONSTRAINT `oc_ibfk_5` FOREIGN KEY (`imp_cod`) REFERENCES `imputacion` (`imp_cod`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC COMMENT='InnoDB free: 4096 kB';

/*Data for the table `oc` */

LOCK TABLES `oc` WRITE;

insert into `oc` values 
(1,1,1,NULL,'876867-87','2011-02-24','2011-03-31','1040000','1040000','52987','52987','HC','9','8','6','5','4','4','5','LKJHLJ','KJH',1,'ACTIVO',1),
(2,1,1,NULL,'7657-765','2011-02-24','2011-03-31','22380000','22380000','1715065','1715065','HC SAN LORENZO','1','2','7','6','5','2','1','JGHFGH','JHGJ',1,'ACTIVO',1),
(3,1,1,NULL,'453466-567','2011-02-24','2011-03-31','13000000','13000000','1008658','1008658','HC SAN LORENZO','1','1','2','2','3','3','4','DANIEL','CYNTHIA',2,'ACTIVO',1),
(4,1,1,NULL,'2343454-56','2011-02-24','2011-03-31','16000000','16000000','1281385','1281385','HC SAN LORENZO','1','2','2','2','2','2','2','DANIEL','CYNTHIA',2,'ACTIVO',1),
(5,1,1,NULL,'122334-345','2011-02-24','2011-03-31','12000000','12000000','917749','917749','HC SAN LORENZO','1','2','1','1','2','2','2','CYNTHIA','DANIEL',1,'ACTIVO',1),
(6,1,1,NULL,'','2011-02-25','2011-03-31','1100000','1100000','74026','74026','HC SAN LORENZO','1','1','2','3','2','3','3','JULIO','ELIZABETH',1,'ACTIVO',1),
(7,1,1,NULL,'345456-567','2011-02-25','2011-03-31','150090000','150090000','13640650','13640650','HC SAN LORENZO','1','2','3','3','3','2','2','DANIEL','CYNTHIA',1,'ACTIVO',1),
(8,1,1,NULL,'2343-455','2011-02-25','2011-03-31','12800000','12800000','903896','903896','HC SAN LORENZO','1','2','4','3','','2','2','JULIO','VERON',1,'ACTIVO',1),
(9,1,2,NULL,'254435-7','2011-02-25','2011-03-31','4240000','4240000','212294','212294','HC SAN LORENZO','1','1','2','1','1','1','1','JULIO','VERON',2,'ACTIVO',1),
(10,1,1,NULL,'234556-4546','2011-02-25','2011-03-31','3600000','3600000','171429','171429','HC SAN LORENZO','1','1','2','3','4','3','2','JULIO','VERON',2,'ACTIVO',1),
(11,1,1,NULL,'2343445-865','2011-02-25','2011-03-31','44000000','44000000','2441559','2441559','HC SAN LORENZO','1','2','2','1','2','3','2','JULIO','VERON',1,'ACTIVO',1),
(12,1,1,NULL,'345-45756','2011-02-25','2011-03-31','400000','400000','19048','19048','HC SAN LORENZO','1','2','1','1','2','2','2','LIC. NELIDA CORONEL','DR. EDUARDO BOROCOTO',1,'ACTIVO',1),
(13,1,1,NULL,'3454576-5686','2011-02-28','2011-03-31','4000000','4000000','190476','190476','HC SAN LORENZO','1','2','2','1','2','3','1','LIC. NELIDA CORONEL','DR. EDUARDO BOROCOTO',2,'ACTIVO',1),
(14,1,1,NULL,'34546-45646','2011-04-27','2011-06-30','17000000','17000000','1155844','1155844','HC SAN LORENZO','1','2','3','','1','1','','LIC. NELIDA CORONEL','DR. EDUARDO BOROCOTO',1,'ACTIVO',1),
(15,1,2,NULL,'2545-56757','2011-04-27','2011-06-30','9000000','9000000','428571','428571','HC SAN LORENZO','1','2','2','3','3','3','3','LIC. NELIDA CORONEL','DR. EDUARDO BOROCOTO',1,'ACTIVO',1),
(16,1,1,NULL,'2324-354','2011-04-27','2011-06-30','6000000','6000000','285714','285714','HC SAN LORENZO','1','2','2','2','2','2','2','LIC. NELIDA CORONEL','DR. EDUARDO BOROCOTO',2,'ACTIVO',1);

UNLOCK TABLES;

/*Table structure for table `pedido` */

DROP TABLE IF EXISTS `pedido`;

CREATE TABLE `pedido` (
  `ped_cod` int(11) NOT NULL,
  `usu_cod` int(10) unsigned NOT NULL,
  `ped_fec` date default NULL,
  PRIMARY KEY  (`ped_cod`),
  KEY `movimiento_FKIndex2` (`usu_cod`),
  CONSTRAINT `pedido_ibfk_1` FOREIGN KEY (`usu_cod`) REFERENCES `usuario` (`usu_cod`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `pedido` */

LOCK TABLES `pedido` WRITE;

UNLOCK TABLES;

/*Table structure for table `productos` */

DROP TABLE IF EXISTS `productos`;

CREATE TABLE `productos` (
  `pro_cod` int(20) NOT NULL,
  `tip_cod` int(10) unsigned NOT NULL,
  `pro_catalogo` varchar(50) default NULL,
  `pro_desc` varchar(50) default NULL,
  `pro_carac` varchar(300) default NULL,
  `pro_pres` varchar(100) default NULL,
  `med_cod` int(10) unsigned NOT NULL,
  `pro_precio` decimal(10,0) default NULL,
  `pro_apodo` varchar(200) default NULL,
  `pro_stock_min` double default NULL,
  `pro_iva` varchar(10) default NULL,
  `dep_cod` int(10) unsigned default NULL,
  `pro_cod_secundario` varchar(40) default NULL,
  PRIMARY KEY  (`pro_cod`),
  KEY `productos_FKIndex2` (`tip_cod`),
  KEY `FK_productos` (`dep_cod`),
  CONSTRAINT `productos_ibfk_1` FOREIGN KEY (`tip_cod`) REFERENCES `tipo_pro` (`tip_cod`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `productos_ibfk_2` FOREIGN KEY (`dep_cod`) REFERENCES `departamento` (`dep_cod`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC COMMENT='InnoDB free: 4096 kB';

/*Data for the table `productos` */

LOCK TABLES `productos` WRITE;

insert into `productos` values 
(1,4,'2344-5','REGULADOR DE GOTEO','PARA TODAD','INYECTABLE',1,'5000','DOSIFLOW',3,'10%',1,NULL),
(2,2,'2534-6756','FENTANILO','FANTANILO','AMPOLLA DE 10ML',1,'40000','FENTANILL',30,'5%',1,NULL);

UNLOCK TABLES;

/*Table structure for table `proveedor` */

DROP TABLE IF EXISTS `proveedor`;

CREATE TABLE `proveedor` (
  `prv_cod` int(10) unsigned NOT NULL auto_increment,
  `prv_nom` varchar(70) default NULL,
  `prv_ruc` varchar(50) default NULL,
  `prv_tel` varchar(20) default NULL,
  PRIMARY KEY  (`prv_cod`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `proveedor` */

LOCK TABLES `proveedor` WRITE;

insert into `proveedor` values 
(1,'Chaco Internacional','23534-29','0981444555'),
(2,'Scavone Hnos.','2358-5959','021444555');

UNLOCK TABLES;

/*Table structure for table `receta` */

DROP TABLE IF EXISTS `receta`;

CREATE TABLE `receta` (
  `res_cod` int(11) NOT NULL,
  `des_cod` int(10) unsigned NOT NULL,
  `usu_cod` int(10) unsigned NOT NULL,
  `res_nompas` varchar(50) default NULL,
  `res_dr` varchar(30) default NULL,
  `res_fec` date default NULL,
  PRIMARY KEY  (`res_cod`),
  KEY `receta_FKIndex2` (`usu_cod`),
  KEY `receta_FKIndex3` (`des_cod`),
  CONSTRAINT `receta_ibfk_1` FOREIGN KEY (`usu_cod`) REFERENCES `usuario` (`usu_cod`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `receta_ibfk_2` FOREIGN KEY (`des_cod`) REFERENCES `destino` (`des_cod`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `receta` */

LOCK TABLES `receta` WRITE;

UNLOCK TABLES;

/*Table structure for table `salida` */

DROP TABLE IF EXISTS `salida`;

CREATE TABLE `salida` (
  `salida_cod` int(11) NOT NULL,
  `usu_cod` int(10) unsigned NOT NULL,
  `des_cod` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`salida_cod`),
  KEY `salida_FKIndex1` (`des_cod`),
  KEY `salida_FKIndex2` (`usu_cod`),
  CONSTRAINT `salida_ibfk_1` FOREIGN KEY (`des_cod`) REFERENCES `destino` (`des_cod`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `salida_ibfk_2` FOREIGN KEY (`usu_cod`) REFERENCES `usuario` (`usu_cod`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `salida` */

LOCK TABLES `salida` WRITE;

UNLOCK TABLES;

/*Table structure for table `salida_receta` */

DROP TABLE IF EXISTS `salida_receta`;

CREATE TABLE `salida_receta` (
  `res_cod` int(11) NOT NULL,
  `pro_cod` int(20) NOT NULL,
  `cantidad` double default NULL,
  PRIMARY KEY  (`res_cod`,`pro_cod`),
  KEY `receta_has_productos_FKIndex1` (`res_cod`),
  KEY `receta_has_productos_FKIndex2` (`pro_cod`),
  CONSTRAINT `salida_receta_ibfk_1` FOREIGN KEY (`res_cod`) REFERENCES `receta` (`res_cod`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `salida_receta_ibfk_2` FOREIGN KEY (`pro_cod`) REFERENCES `productos` (`pro_cod`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `salida_receta` */

LOCK TABLES `salida_receta` WRITE;

UNLOCK TABLES;

/*Table structure for table `servicio` */

DROP TABLE IF EXISTS `servicio`;

CREATE TABLE `servicio` (
  `servi_cod` int(10) unsigned NOT NULL,
  `servi_nom` varchar(40) default NULL,
  `servi_desc` varchar(100) default NULL,
  PRIMARY KEY  (`servi_cod`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `servicio` */

LOCK TABLES `servicio` WRITE;

insert into `servicio` values 
(1,'FARMACIA','FARMACIA HOSPITAL DE CLINICAS SAN LORENZO');

UNLOCK TABLES;

/*Table structure for table `stock` */

DROP TABLE IF EXISTS `stock`;

CREATE TABLE `stock` (
  `pro_cod` int(20) NOT NULL,
  `dep_cod` int(6) NOT NULL,
  `far_stock` double default NULL,
  PRIMARY KEY  (`pro_cod`,`dep_cod`),
  KEY `stock_FKIndex1` (`pro_cod`),
  KEY `stock_FKIndex2` (`dep_cod`),
  CONSTRAINT `stock_ibfk_1` FOREIGN KEY (`pro_cod`) REFERENCES `productos` (`pro_cod`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `stock_ibfk_2` FOREIGN KEY (`dep_cod`) REFERENCES `deposito` (`dep_cod`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `stock` */

LOCK TABLES `stock` WRITE;

UNLOCK TABLES;

/*Table structure for table `tipo_ent` */

DROP TABLE IF EXISTS `tipo_ent`;

CREATE TABLE `tipo_ent` (
  `tient_cod` int(10) unsigned NOT NULL,
  `tient_desc` varchar(70) default NULL,
  `tient_estado` varchar(60) default NULL,
  PRIMARY KEY  (`tient_cod`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `tipo_ent` */

LOCK TABLES `tipo_ent` WRITE;

insert into `tipo_ent` values 
(1,'Compra Directa',''),
(2,'Licitación',''),
(3,'Donación',''),
(4,'corto',''),
(5,'largo',''),
(6,'para',''),
(7,'nose','');

UNLOCK TABLES;

/*Table structure for table `tipo_pro` */

DROP TABLE IF EXISTS `tipo_pro`;

CREATE TABLE `tipo_pro` (
  `tip_cod` int(10) unsigned NOT NULL,
  `tip_desc` varchar(70) default NULL,
  `dep_cod` int(10) unsigned default NULL,
  PRIMARY KEY  (`tip_cod`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `tipo_pro` */

LOCK TABLES `tipo_pro` WRITE;

insert into `tipo_pro` values 
(1,'MEDICAMENTO',1),
(2,'CONTROLADO',1),
(3,'ANTIBIOTICO',1),
(4,'DESCARTABLE',1);

UNLOCK TABLES;

/*Table structure for table `usuario` */

DROP TABLE IF EXISTS `usuario`;

CREATE TABLE `usuario` (
  `usu_cod` int(10) unsigned NOT NULL,
  `servi_cod` int(10) unsigned NOT NULL,
  `usu_pasw` varchar(30) NOT NULL,
  `usu_nom` varchar(30) default NULL,
  `usu_nic` varchar(10) default NULL,
  `nivel` varchar(70) default NULL,
  `usu_ape` varchar(70) default NULL,
  PRIMARY KEY  (`usu_cod`),
  KEY `usuario_FKIndex1` (`servi_cod`),
  CONSTRAINT `usuario_ibfk_1` FOREIGN KEY (`servi_cod`) REFERENCES `servicio` (`servi_cod`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `usuario` */

LOCK TABLES `usuario` WRITE;

UNLOCK TABLES;

SET FOREIGN_KEY_CHECKS=1;
