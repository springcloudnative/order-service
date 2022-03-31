CREATE USER 'order_user'@'%' IDENTIFIED BY 'b3JkZXJfdXNlcgo=';

GRANT ALL PRIVILEGES ON polardb_catalog.orders TO 'order_user'@'%';

COMMIT;