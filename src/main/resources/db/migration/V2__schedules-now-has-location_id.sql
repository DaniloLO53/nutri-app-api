ALTER TABLE schedules ADD COLUMN location_id UUID;

UPDATE schedules s
SET location_id = (
    SELECT id FROM locations l
    WHERE l.nutritionist_id = s.nutritionist_id
    LIMIT 1
);

ALTER TABLE schedules ALTER COLUMN location_id SET NOT NULL;
ALTER TABLE schedules DROP CONSTRAINT IF EXISTS fk_schedules_nutritionists;
ALTER TABLE schedules DROP COLUMN IF EXISTS nutritionist_id;
ALTER TABLE schedules ADD CONSTRAINT fk_schedules_locations FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE CASCADE;
CREATE INDEX IF NOT EXISTS idx_schedules_location_id ON schedules(location_id);