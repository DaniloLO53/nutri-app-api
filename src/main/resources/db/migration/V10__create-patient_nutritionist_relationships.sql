CREATE TABLE patient_nutritionist_relationships (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    nutritionist_id UUID NOT NULL,
    patient_id UUID NOT NULL,

    -- Datas de início e fim do acompanhamento
    start_date DATE NOT NULL DEFAULT CURRENT_DATE,
    end_date DATE,

    -- Contexto e detalhes
    relationship_notes TEXT,
    termination_reason TEXT,

    -- Controle de permissões
    can_patient_schedule_freely BOOLEAN NOT NULL DEFAULT TRUE,

    -- Timestamps de auditoria
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Garante que não exista mais de um relacionamento ativo entre o mesmo paciente e nutricionista
    CONSTRAINT unique_nutritionist_patient_relationship UNIQUE (nutritionist_id, patient_id),

    -- ✅ CONSTRAINTS DE REFERÊNCIA SEPARADAS
    CONSTRAINT fk_relationships_nutritionist FOREIGN KEY (nutritionist_id) REFERENCES nutritionists(id) ON DELETE CASCADE,
    CONSTRAINT fk_relationships_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

-- Índices para otimizar buscas por nutricionista ou por paciente
CREATE INDEX idx_relationships_nutritionist_id ON patient_nutritionist_relationships(nutritionist_id);
CREATE INDEX idx_relationships_patient_id ON patient_nutritionist_relationships(patient_id);

-- Triggers para a tabela 'patient_nutritionist_relationships'
CREATE TRIGGER set_timestamp_relationships BEFORE UPDATE ON patient_nutritionist_relationships FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER trg_relationships_prevent_created_at_update BEFORE UPDATE ON patient_nutritionist_relationships FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();