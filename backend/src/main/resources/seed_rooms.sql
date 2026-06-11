-- 30 rooms: 10 per floor, 3 floors
-- Room types: 5×DOUBLE, 3×TRIPLE, 2×QUAD per floor
-- Adjust rent_price as needed

INSERT INTO rooms (room_number, room_type, occupancy_status, occupied_places, total_places, floor_number, rent_price)
VALUES
  -- Floor 1
  ('101', 'DOUBLE', 'VACANT', 0, 2, 1, 500.00),
  ('102', 'DOUBLE', 'VACANT', 0, 2, 1, 500.00),
  ('103', 'DOUBLE', 'VACANT', 0, 2, 1, 500.00),
  ('104', 'DOUBLE', 'VACANT', 0, 2, 1, 500.00),
  ('105', 'DOUBLE', 'VACANT', 0, 2, 1, 500.00),
  ('106', 'TRIPLE', 'VACANT', 0, 3, 1, 420.00),
  ('107', 'TRIPLE', 'VACANT', 0, 3, 1, 420.00),
  ('108', 'TRIPLE', 'VACANT', 0, 3, 1, 420.00),
  ('109', 'QUAD',   'VACANT', 0, 4, 1, 350.00),
  ('110', 'QUAD',   'VACANT', 0, 4, 1, 350.00),

  -- Floor 2
  ('201', 'DOUBLE', 'VACANT', 0, 2, 2, 500.00),
  ('202', 'DOUBLE', 'VACANT', 0, 2, 2, 500.00),
  ('203', 'DOUBLE', 'VACANT', 0, 2, 2, 500.00),
  ('204', 'DOUBLE', 'VACANT', 0, 2, 2, 500.00),
  ('205', 'DOUBLE', 'VACANT', 0, 2, 2, 500.00),
  ('206', 'TRIPLE', 'VACANT', 0, 3, 2, 420.00),
  ('207', 'TRIPLE', 'VACANT', 0, 3, 2, 420.00),
  ('208', 'TRIPLE', 'VACANT', 0, 3, 2, 420.00),
  ('209', 'QUAD',   'VACANT', 0, 4, 2, 350.00),
  ('210', 'QUAD',   'VACANT', 0, 4, 2, 350.00),

  -- Floor 3
  ('301', 'DOUBLE', 'VACANT', 0, 2, 3, 500.00),
  ('302', 'DOUBLE', 'VACANT', 0, 2, 3, 500.00),
  ('303', 'DOUBLE', 'VACANT', 0, 2, 3, 500.00),
  ('304', 'DOUBLE', 'VACANT', 0, 2, 3, 500.00),
  ('305', 'DOUBLE', 'VACANT', 0, 2, 3, 500.00),
  ('306', 'TRIPLE', 'VACANT', 0, 3, 3, 420.00),
  ('307', 'TRIPLE', 'VACANT', 0, 3, 3, 420.00),
  ('308', 'TRIPLE', 'VACANT', 0, 3, 3, 420.00),
  ('309', 'QUAD',   'VACANT', 0, 4, 3, 350.00),
  ('310', 'QUAD',   'VACANT', 0, 4, 3, 350.00);
