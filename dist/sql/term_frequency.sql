CREATE TABLE `term_frequency` (
	`term_id`     int(11) NOT NULL,
	`document_id` int(11) NOT NULL,
	`count`       int(11) NOT NULL,
	PRIMARY KEY (`term_id`, `document_id`, `count`)
)
	ENGINE = InnoDB
	DEFAULT CHARSET = `utf8mb4`
