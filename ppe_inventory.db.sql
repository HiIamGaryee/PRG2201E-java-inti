BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS hospitals (
    hospitalID INTEGER PRIMARY KEY AUTOINCREMENT,
    hospitalName TEXT NOT NULL,
    contact TEXT,
    address TEXT
);
CREATE TABLE IF NOT EXISTS "ppe_items" (
    item_code TEXT PRIMARY KEY,
    item_name TEXT NOT NULL,
    supplier_code TEXT NOT NULL,
    quantity_in_boxes INTEGER NOT NULL
);
CREATE TABLE IF NOT EXISTS ppe_transactions (transaction_id INTEGER PRIMARY KEY AUTOINCREMENT, item_code TEXT NOT NULL, quantity INTEGER NOT NULL, transaction_type TEXT NOT NULL, source_destination TEXT NOT NULL, transaction_date TEXT NOT NULL, FOREIGN KEY (item_code) REFERENCES ppe_items(item_code));
CREATE TABLE IF NOT EXISTS sqlite_stat4(tbl,idx,neq,nlt,ndlt,sample);
CREATE TABLE IF NOT EXISTS suppliers (
    supplierID INTEGER PRIMARY KEY AUTOINCREMENT,
    supplierName TEXT NOT NULL,
    contact TEXT,
    address TEXT
);
CREATE TABLE IF NOT EXISTS transactions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    item_code TEXT NOT NULL,
    type TEXT NOT NULL,
    source_or_dest_code TEXT NOT NULL,
    quantity INTEGER NOT NULL,
    date_time TEXT NOT NULL
);
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL
, userType TEXT);
INSERT INTO "hospitals" ("hospitalID","hospitalName","contact","address") VALUES (1,'12312','5555','123123');
INSERT INTO "ppe_items" ("item_code","item_name","supplier_code","quantity_in_boxes") VALUES ('HC','Head Cover','SUP1',50),
 ('FS','Face Shield','SUP2',20),
 ('MS','Mask','SUP3',10),
 ('GL','Gloves','SUP2',80),
 ('GW','Gown','SUP1',10);
INSERT INTO "sqlite_stat4" ("tbl","idx","neq","nlt","ndlt","sample") VALUES ('users','sqlite_autoindex_users_1','1 1','0 0','0 0',X'03170961646d696e'),
 ('users','sqlite_autoindex_users_1','1 1','1 1','1 1',X'031d0161646d696e31323304'),
 ('users','sqlite_autoindex_users_1','1 1','2 2','2 2',X'031d0166656c697831323305'),
 ('users','sqlite_autoindex_users_1','1 1','3 3','3 3',X'03190167617279656503'),
 ('users','sqlite_autoindex_users_1','1 1','4 4','4 4',X'031701677261636506');
INSERT INTO "suppliers" ("supplierID","supplierName","contact","address") VALUES (9,'123214214','21312','12312'),
 (10,'dsff','sdfa','231213241');
INSERT INTO "users" ("id","username","password","userType") VALUES (3,'garyee','123','Admin'),
 (5,'felix123','123','Admin'),
 (8,'grace','123','Admin');
COMMIT;
