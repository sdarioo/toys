
DROP TABLE IF EXISTS public.packages;
DROP TABLE IF EXISTS public.projects;

DROP SEQUENCE IF EXISTS public.projects_id_seq;
DROP SEQUENCE IF EXISTS public.packages_id_seq;
DROP SEQUENCE IF EXISTS public.hibernate_sequence;

/* GenerationStrategy.IDENTITY
 * SERIAL 
 * http://www.postgresql.org/docs/current/static/datatype-numeric.html#DATATYPE-SERIAL
 * The naming tablename_columname_seq is the PostgreSQL default sequence naming for SERIAL
 */

/* GenerationStrategy.AUTO */
CREATE SEQUENCE IF NOT EXISTS public.hibernate_sequence;

/* GenerationStrategy.SEQUENCE */
CREATE SEQUENCE IF NOT EXISTS public.projects_id_seq;
CREATE SEQUENCE IF NOT EXISTS public.packages_id_seq;

CREATE TABLE IF NOT EXISTS public.projects (
  id   int4 UNIQUE NOT NULL, 
  name varchar(255) NOT NULL UNIQUE, 
  PRIMARY KEY (id)
) WITHOUT OIDS TABLESPACE pg_default;

CREATE TABLE IF NOT EXISTS public.packages (
    id         int4 NOT NULL, 
    name       varchar(255), 
    project_id int4 NOT NULL, 
    parent_id  int4, 
    PRIMARY KEY (id),
    CONSTRAINT fk_packages_projects FOREIGN KEY (project_id) REFERENCES public.projects (id),
    CONSTRAINT fk_packages_packages FOREIGN KEY (parent_id) REFERENCES public.packages (id)
) WITHOUT OIDS TABLESPACE pg_default;
