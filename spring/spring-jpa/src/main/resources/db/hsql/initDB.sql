DROP TABLE projects IF EXISTS;
DROP TABLE packages IF EXISTS;

/* In HSQLDB you can use IDENTITY keyword to define an auto-increment column */

/* GenerationStrategy.SEQUENCE */
CREATE SEQUENCE packages_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE projects_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE classes_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE projects (
  id   INTEGER PRIMARY KEY,
  name VARCHAR(255)
);

CREATE TABLE packages (
  id         INTEGER PRIMARY KEY,
  name       VARCHAR(255),
  project_id INTEGER NOT NULL,
  parent_id  INTEGER
);

CREATE TABLE classes (
  id         INTEGER PRIMARY KEY,
  name       VARCHAR(255),
  package_id  INTEGER
);

ALTER TABLE packages ADD CONSTRAINT fk_packages_projects FOREIGN KEY (project_id) REFERENCES projects (id);
ALTER TABLE packages ADD CONSTRAINT fk_packages_packages FOREIGN KEY (parent_id) REFERENCES packages (id);
ALTER TABLE classes ADD CONSTRAINT fk_classes_packages FOREIGN KEY (package_id) REFERENCES packages (id);