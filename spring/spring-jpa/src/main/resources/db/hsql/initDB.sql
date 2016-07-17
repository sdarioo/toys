DROP TABLE projects IF EXISTS;
DROP TABLE packages IF EXISTS;

/* In HSQLDB you can use IDENTITY keyword to define an auto-increment column */

CREATE TABLE projects (
  id   INTEGER IDENTITY PRIMARY KEY,
  name VARCHAR(255)
);

CREATE TABLE packages (
  id         INTEGER IDENTITY PRIMARY KEY,
  name       VARCHAR(255),
  project_id INTEGER NOT NULL,
  parent_id  INTEGER
);

ALTER TABLE packages ADD CONSTRAINT fk_packages_projects FOREIGN KEY (project_id) REFERENCES projects (id);
ALTER TABLE packages ADD CONSTRAINT fk_packages_packages FOREIGN KEY (parent_id) REFERENCES packages (id);