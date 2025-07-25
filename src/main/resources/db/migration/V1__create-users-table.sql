CREATE EXTENSION IF NOT EXISTS unaccent;

CREATE OR REPLACE FUNCTION prevent_created_at_update()
RETURNS TRIGGER AS $$
BEGIN
    -- 'OLD' representa a linha como ela estava ANTES do update.
    -- 'NEW' representa a linha como ela ficará DEPOIS do update.
    -- Usamos 'IS DISTINCT FROM' para tratar corretamente valores NULL.
    IF NEW.created_at IS DISTINCT FROM OLD.created_at THEN
        -- Se o valor de 'created_at' estiver sendo alterado, lançamos um erro.
        RAISE EXCEPTION 'Column "created_at" is not updatable';
    END IF;

    -- Se a coluna 'created_at' não foi alterada, a operação é permitida.
    -- Retornamos 'NEW' para que o update das outras colunas possa continuar.
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION trigger_set_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  -- Define o campo 'updated_at' da nova versão da linha ('NEW')
  -- para o tempo atual antes de o update ser salvo.
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_PATIENT',
    password VARCHAR(255) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS nutritionists (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    crf VARCHAR(20) NOT NULL,
    accepts_remote BOOLEAN NOT NULL DEFAULT FALSE,
    user_id UUID NOT NULL UNIQUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_nutritionists_users FOREIGN KEY (user_id) REFERENCES users(id),

    CONSTRAINT uk_nutritionists_crf UNIQUE (crf)
);

CREATE TABLE IF NOT EXISTS patients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cpf VARCHAR(20) NOT NULL,
    birthday DATE,
    user_id UUID NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_patients_users FOREIGN KEY (user_id) REFERENCES users(id),

    CONSTRAINT uk_patients_cpf UNIQUE (cpf)
);

CREATE TABLE IF NOT EXISTS schedules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nutritionist_id UUID NOT NULL, -- atualizado para location_id
    -- added accepts_remote

    start_time TIMESTAMPTZ NOT NULL,
    duration_minutes INTEGER NOT NULL DEFAULT 30,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_schedules_nutritionists FOREIGN KEY (nutritionist_id) REFERENCES nutritionists(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS appointments_status (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(15) NOT NULL DEFAULT 'AGENDADO',

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_appointments_status_name UNIQUE (name)
);

INSERT INTO appointments_status(name) VALUES ('AGENDADO'), ('CONFIRMADO'), ('CONCLUÍDO'), ('CANCELADO'), ('NÃO COMPARECEU');

CREATE TABLE IF NOT EXISTS appointments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    patient_id UUID NOT NULL,
    schedule_id UUID NOT NULL,
    appointments_status_id UUID NOT NULL,

    is_remote BOOLEAN NOT NULL DEFAULT FALSE,

    nutritionist_notes TEXT,
    patient_reason TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_appointments_patients FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE RESTRICT, -- Impede que um paciente com consultas seja deletado
    CONSTRAINT fk_appointments_status FOREIGN KEY (appointments_status_id) REFERENCES appointments_status(id) ON DELETE RESTRICT,
    CONSTRAINT fk_schedule FOREIGN KEY (schedule_id) REFERENCES schedules(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS locations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nutritionist_id UUID NOT NULL,
    ibge_api_city VARCHAR(100) NOT NULL,
    ibge_api_state VARCHAR(100) NOT NULL,
    ibge_api_state_id VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    phone1 VARCHAR(15),
    phone2 VARCHAR(15),
    phone3 VARCHAR(15),

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_locations_nutritionists FOREIGN KEY (nutritionist_id) REFERENCES nutritionists(id) ON DELETE CASCADE
);

CREATE TRIGGER trg_users_prevent_created_at_update BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();
CREATE TRIGGER set_timestamp_users BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();

CREATE TRIGGER trg_nutritionists_prevent_created_at_update BEFORE UPDATE ON nutritionists FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();
CREATE TRIGGER set_timestamp_nutritionists BEFORE UPDATE ON nutritionists FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();

CREATE TRIGGER trg_patients_prevent_created_at_update BEFORE UPDATE ON patients FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();
CREATE TRIGGER set_timestamp_patients BEFORE UPDATE ON patients FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();

CREATE TRIGGER trg_appointments_prevent_created_at_update BEFORE UPDATE ON appointments FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();
CREATE TRIGGER set_timestamp_appointments BEFORE UPDATE ON appointments FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();

CREATE TRIGGER trg_schedule_prevent_created_at_update BEFORE UPDATE ON schedules FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();
CREATE TRIGGER set_timestamp_schedule BEFORE UPDATE ON schedules FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();

CREATE TRIGGER trg_appointments_status_prevent_created_at_update BEFORE UPDATE ON appointments_status FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();
CREATE TRIGGER set_timestamp_appointments_status BEFORE UPDATE ON appointments_status FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();

CREATE TRIGGER trg_locations_prevent_created_at_update BEFORE UPDATE ON locations FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();
CREATE TRIGGER set_timestamp_locations BEFORE UPDATE ON locations FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();

CREATE INDEX IF NOT EXISTS idx_appointments_patient_id ON appointments(patient_id);
CREATE INDEX IF NOT EXISTS schedule_id ON appointments(schedule_id);
