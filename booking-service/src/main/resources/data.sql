INSERT INTO users ( username, password, role) VALUES ( 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnutIFrkqXN8PsMwwG2bcBXZ8CXT.9QUWK', 'ADMIN');

-- Реальный BCrypt хэш для пароля "user123"
INSERT INTO users ( username, password, role) VALUES ( 'user', '$2a$10$N9qo8uLOickgx2ZMRZoMy.YLL7X0Cyjp.gOsAcVksq9Mmx.q6dh7S', 'USER');