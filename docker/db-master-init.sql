create table tenant (
    id int8 not null,
    db_driver_class varchar(255),
    db_password varchar(255),
    db_url varchar(255),
    db_username varchar(255),
    tenant_id varchar(255),
    primary key (id)
);

INSERT INTO tenant (id, tenant_id, db_driver_class, db_url, db_username, db_password)
VALUES (1, 'tenant1', 'org.postgresql.Driver', 'jdbc:postgresql://db-tenant1:5432/app', 'root', 'password');

INSERT INTO tenant (id, tenant_id, db_driver_class, db_url, db_username, db_password)
VALUES (2, 'tenant2', 'org.postgresql.Driver', 'jdbc:postgresql://db-tenant2:5432/app', 'root', 'password');
