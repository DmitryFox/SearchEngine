CREATE TABLE `document` (
	`id`          int(11) NOT NULL,
	`name`        varchar(20) DEFAULT NULL,
	`size`        int(11)     DEFAULT NULL,
	`crc32`       int(11)     DEFAULT NULL,
	`token_count` int(11)     DEFAULT NULL,
	PRIMARY KEY (`id`)
)
	ENGINE = InnoDB
	DEFAULT CHARSET = `utf8mb4`