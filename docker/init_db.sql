CREATE TABLE traffic_logs (
    id INTEGER PRIMARY KEY,
    sourceIP TEXT,
    destinationIP TEXT,
    port INTEGER,
    date DATE
);

INSERT INTO
    traffic_logs (id, sourceIP, destinationIP, port, date)
VALUES
    (1, '192.168.1.37', '10.0.0.224', 80, '2024-02-27'),
    (2, '10.0.0.224', '172.16.0.189', 22, '2024-06-15'),
    (3, '172.16.0.189', '192.168.1.37', 3389, '2024-11-03'),
    (4, '192.168.1.37', '10.0.0.224', 443, '2024-03-09'),
    (5, '10.0.0.224', '172.16.0.189', 80, '2024-02-27'),
    (6, '172.16.0.189', '192.168.1.37', 22, '2024-06-15'),
    (7, '192.168.1.37', '10.0.0.224', 3389, '2024-11-03'),
    (8, '10.0.0.224', '172.16.0.189', 443, '2024-03-09'),
    (9, '172.16.0.189', '192.168.1.37', 80, '2024-02-27'),
    (10, '172.16.0.189', '10.0.0.224', 22, '2024-06-15'),
    (11, '10.0.0.224', '192.168.1.37', 80, '2024-06-15'),
    (12, '192.168.1.37', '10.0.0.224', 443, '2024-06-15'),
    (13, '10.0.0.224', '192.168.1.37', 80, '2024-02-27'),
    (14, '172.16.0.189', '192.168.1.37', 443, '2024-03-09'),
    (15, '192.168.1.37', '10.0.0.224', 22, '2024-11-03'),
    (16, '192.168.1.37', '172.16.0.189', 443, '2024-03-09'),
    (17, '10.0.0.224', '172.16.0.189', 3389, '2024-02-27'),
    (18, '10.0.0.224', '192.168.1.37', 3389, '2024-06-15'),
    (19, '192.168.1.37', '172.16.0.189', 443, '2024-06-15'),
    (20, '172.16.0.189', '10.0.0.224', 80, '2024-03-09');
