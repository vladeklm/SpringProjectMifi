INSERT INTO hotel (id, name, address) VALUES (1, 'Grand Hotel', 'Moscow, Red Square');
INSERT INTO room (id, hotel_id, number, available, times_booked) VALUES
(1, 1, '101', true, 0),
(2, 1, '102', true, 0);