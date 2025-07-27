-- ******************** DEFINIÇÕES DOS CAMPOS CUSTOMIZADOS ********************
-- Na área de configurações, o nutricionista cria um novo campo contendo essas informações
-- Na área de avaliação, na seção de campos personalizados, ele seleciona o nome do novo campo criado (field_label)
-- O campo é puxado de custom_field_definitions com o seu nutritionist_id
-- O nutricionista digita um valor para o campo (será salvo como um BJSON em custom_data no clinical_information)
-- Ao salvar as informações clínicas, todas as informações serão exportadas em PDF e salvas no histórico (em PDF)
-- Qualquer nutricionista pode puxar o histórico do paciente, ainda que não possua os campos customizados de outro nutri, já que o histórico está em PDF
CREATE TABLE custom_field_definitions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- ID do nutricionista que criou este campo
    nutritionist_id UUID NOT NULL REFERENCES nutritionists(id) ON DELETE CASCADE,

    -- Nome do campo que será exibido na interface (ex: "Circunferência do Braço")
    field_label TEXT NOT NULL,

    -- Tipo de dado para ajudar a renderizar o input correto no frontend
    field_type VARCHAR(50) NOT NULL CHECK (field_type IN ('TEXTO', 'NUMERO', 'DATA', 'BOOLEANO')),

    -- Unidade de medida, se aplicável (ex: "cm", "kg", "%")
    unit VARCHAR(20),

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
-- Garante que o mesmo nutricionista não crie o mesmo campo duas vezes
CREATE UNIQUE INDEX idx_field_for_nutritionist_lower_unique ON custom_field_definitions (nutritionist_id, LOWER(field_label));

-- ******************** TABELA PRINCIPAL: INFORMAÇÕES CLÍNICAS ********************
CREATE TABLE clinical_information (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL,
    assessment_date DATE NOT NULL DEFAULT CURRENT_DATE,

    -- Categoria: Anamnese Clínica e Objetivos
    main_goal TEXT,
    previous_diet_history TEXT,

    -- Categoria: Sinais, Sintomas e Saúde Geral
    intestinal_function TEXT,
    sleep_quality TEXT,
    energy_level INTEGER, -- Pode ser uma nota de 0 a 10
    menstrual_cycle_details TEXT, -- Pode ser NULL para pacientes do sexo masculino

    -- Categoria: Avaliação Antropométrica
    weight_kg DECIMAL(5, 2), -- Ex: 150.75 kg
    height_cm DECIMAL(5, 2), -- Ex: 175.50 cm
    waist_circumference_cm DECIMAL(5, 2),
    upper_arm_circumference_cm DECIMAL(5, 2),
    abdomen_circumference_cm DECIMAL(5, 2),
    hip_circumference_cm DECIMAL(5, 2),

    -- Categoria: Composição Corporal Detalhada
    body_fat_percentage DECIMAL(4, 2), -- 1. Percentual de gordura corporal
    muscle_mass_kg DECIMAL(5, 2),      -- 2. Massa muscular em kg

    -- Categoria: Marcadores de Saúde Adicionais
    blood_pressure VARCHAR(20),        -- 3. Pressão arterial (ex: "120/80")
    skin_hair_nails_health TEXT,       -- 4. Saúde da pele, cabelo e unhas (indicador de micronutrientes)
    libido_level INTEGER,              -- 5. Nível de libido (escala 0-10, indicador de saúde hormonal)

    -- Categoria: Saúde Comportamental e Emocional
    emotional_eating_details TEXT,     -- 6. Relação com alimentação emocional (comer por estresse, ansiedade)
    main_food_difficulties TEXT,       -- 7. Maiores dificuldades (ex: "vontade de doces à noite")

    -- Categoria: Detalhes de Hábitos
    chewing_details TEXT,              -- 8. Mastigação e digestão (ex: "come rápido", "sente inchaço")
    weekly_eating_out_frequency INTEGER, -- 9. Frequência de refeições fora de casa por semana
    water_intake_perception VARCHAR(100),

    -- Categoria: Hábitos Alimentares (Avaliação Dietética)
    food_recall_24h TEXT,
    daily_hydration_details TEXT,
    alcohol_consumption TEXT,
    meal_times_and_locations TEXT,
    sugar_and_sweetener_use TEXT,

    -- Categoria: Rotina e Estilo de Vida
    profession_and_work_routine TEXT,
    physical_activity_details TEXT,
    smoking_habits TEXT,
    weekend_routine_changes TEXT,
    who_prepares_meals TEXT,

    -- Categoria: Dados Complementares
    recent_lab_results TEXT, -- Para observações sobre os exames

    -- Contém um JSON (Ex: {"a1b2c3-ef56...": "32.5"} para "Circunferência do Braço")
    -- O nome do campo é o id do campo "Circunferência do Braço" na tabela custom_field_definitions
    custom_data JSONB, -- Valores dos campos customizados

    -- Timestamps de Auditoria
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Chave Estrangeira
    CONSTRAINT fk_patients FOREIGN KEY(patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

-- Índices para otimizar buscas
CREATE INDEX idx_information_patient_id ON clinical_information(patient_id);
CREATE INDEX idx_information_assessment_date ON clinical_information(assessment_date);

-- ******************** SINTOMAS ********************
CREATE TABLE symptoms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    is_approved BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX idx_symptoms_name_lower_unique ON symptoms (LOWER(name));

CREATE TABLE information_symptoms (
    PRIMARY KEY (information_id, symptom_id),

    information_id UUID NOT NULL,
    symptom_id UUID NOT NULL,

    intensity INTEGER, -- Ex: Uma escala de 0 a 10
    frequency VARCHAR(100), -- Ex: "Diariamente", "2x por semana"
    duration VARCHAR(100), -- Ex: "há 2 meses", "desde 10-05-2023"
    notes TEXT, -- Observações adicionais do nutricionista

    CONSTRAINT fk_clinical_information FOREIGN KEY(information_id) REFERENCES clinical_information(id) ON DELETE CASCADE,
    CONSTRAINT fk_symptoms FOREIGN KEY(symptom_id) REFERENCES symptoms(id) ON DELETE CASCADE
);
-- Índices para performance em buscas por sintoma ou por avaliação
CREATE INDEX idx_info_symptoms_information_id ON information_symptoms(information_id);
CREATE INDEX idx_info_symptoms_symptom_id ON information_symptoms(symptom_id);

-- ******************** DOENÇAS ********************
-- Tabela para Doenças (usada para doenças diagnosticadas e histórico familiar)
CREATE TABLE diseases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    is_approved BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX idx_diseases_name_lower_unique ON diseases (LOWER(name));

-- Ligação para Doenças Diagnosticadas
CREATE TABLE information_diagnosed_diseases (
    information_id UUID NOT NULL,
    disease_id UUID NOT NULL,
    notes TEXT, -- Observações específicas sobre a doença neste paciente
    PRIMARY KEY (information_id, disease_id),

    CONSTRAINT fk_clinical_information FOREIGN KEY(information_id) REFERENCES clinical_information(id) ON DELETE CASCADE,
    CONSTRAINT fk_diseases FOREIGN KEY(disease_id) REFERENCES diseases(id) ON DELETE CASCADE
);
CREATE INDEX idx_info_diag_diseases_information_id ON information_diagnosed_diseases(information_id);
CREATE INDEX idx_info_diag_diseases_disease_id ON information_diagnosed_diseases(disease_id);

-- Ligação para Histórico Familiar de Doenças
CREATE TABLE information_family_diseases (
    information_id UUID NOT NULL,
    disease_id UUID NOT NULL,
    family_member VARCHAR(100), -- Ex: "Pai", "Mãe", "Avó Paterna"
    PRIMARY KEY (information_id, disease_id, family_member), -- Chave composta para permitir a mesma doença para parentes diferentes

    CONSTRAINT fk_clinical_information FOREIGN KEY(information_id) REFERENCES clinical_information(id) ON DELETE CASCADE,
    CONSTRAINT fk_diseases FOREIGN KEY(disease_id) REFERENCES diseases(id) ON DELETE CASCADE
);
CREATE INDEX idx_info_fam_diseases_information_id ON information_family_diseases(information_id);
CREATE INDEX idx_info_fam_diseases_disease_id ON information_family_diseases(disease_id);

-- ******************** MEDICAMENTOS E SUPLEMENTOS ********************
-- Tabela para Medicamentos e Suplementos
CREATE TABLE medications_supplements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,

    type VARCHAR(50) NOT NULL CHECK (type IN ('MEDICAMENTO', 'SUPLEMENTO')),
    is_approved BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX idx_medications_name_lower_unique ON medications_supplements (LOWER(name));

-- Ligação para Medicamentos e Suplementos
CREATE TABLE information_medications (
    information_id UUID NOT NULL,
    medication_id UUID NOT NULL,
    dosage TEXT, -- Ex: "50mg/dia", "1 scoop pós-treino"
    notes TEXT,
    PRIMARY KEY (information_id, medication_id),

    CONSTRAINT fk_clinical_information FOREIGN KEY(information_id) REFERENCES clinical_information(id) ON DELETE CASCADE,
    CONSTRAINT fk_medications_supplements FOREIGN KEY(medication_id) REFERENCES medications_supplements(id) ON DELETE CASCADE
);
CREATE INDEX idx_info_meds_information_id ON information_medications(information_id);
CREATE INDEX idx_info_meds_medication_id ON information_medications(medication_id);

-- ******************** ALERGIAS E INTOLERÂNCIAS ********************
-- Tabela para Alergias e Intolerâncias
CREATE TABLE allergens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN ('ALERGIA', 'INTOLERANCIA')),
    is_approved BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX idx_allergens_name_lower_unique ON allergens (LOWER(name));

-- Ligação para Alergias e Intolerâncias
CREATE TABLE information_allergens (
    information_id UUID NOT NULL,
    allergen_id UUID NOT NULL,
    reaction_details TEXT, -- Ex: "Inchaço e gases", "Urticária"
    PRIMARY KEY (information_id, allergen_id),

    CONSTRAINT fk_clinical_information FOREIGN KEY(information_id) REFERENCES clinical_information(id) ON DELETE CASCADE,
    CONSTRAINT fk_allergens FOREIGN KEY(allergen_id) REFERENCES allergens(id) ON DELETE CASCADE
);
CREATE INDEX idx_info_allergens_information_id ON information_allergens(information_id);
CREATE INDEX idx_info_allergens_allergen_id ON information_allergens(allergen_id);

-- ******************** PREFERÊNCIAS E AVERSÕES ********************
-- Tabela para Alimentos (usada para preferências e aversões)
CREATE TABLE foods (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    is_approved BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX idx_foods_name_lower_unique ON foods (LOWER(name));

-- Ligação para Preferências e Aversões Alimentares
CREATE TABLE information_foods (
    information_id UUID NOT NULL,
    food_id UUID NOT NULL,
    -- Este campo diferencia se o paciente gosta ou não do alimento
    type VARCHAR(50) NOT NULL CHECK (type IN ('PREFERENCIA', 'AVERSAO')),
    PRIMARY KEY (information_id, food_id),

    CONSTRAINT fk_clinical_information FOREIGN KEY(information_id) REFERENCES clinical_information(id) ON DELETE CASCADE,
    CONSTRAINT fk_foods FOREIGN KEY(food_id) REFERENCES foods(id) ON DELETE CASCADE
);
CREATE INDEX idx_info_foods_information_id ON information_foods(information_id);
CREATE INDEX idx_info_foods_food_id ON information_foods(food_id);

-- ******************** TRIGGERS ********************

-- Triggers para a tabela 'clinical_information'
CREATE TRIGGER set_timestamp_clinical_information BEFORE UPDATE ON clinical_information FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER trg_clinical_information_prevent_created_at_update BEFORE UPDATE ON clinical_information FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();

-- Triggers para a tabela 'symptoms'
CREATE TRIGGER set_timestamp_symptoms BEFORE UPDATE ON symptoms FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER trg_symptoms_prevent_created_at_update BEFORE UPDATE ON symptoms FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();

-- Triggers para a tabela 'diseases'
CREATE TRIGGER set_timestamp_diseases BEFORE UPDATE ON diseases FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER trg_diseases_prevent_created_at_update BEFORE UPDATE ON diseases FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();

-- Triggers para a tabela 'medications_supplements'
CREATE TRIGGER set_timestamp_medications_supplements BEFORE UPDATE ON medications_supplements FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER trg_medications_supplements_prevent_created_at_update BEFORE UPDATE ON medications_supplements FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();

-- Triggers para a tabela 'allergens'
CREATE TRIGGER set_timestamp_allergens BEFORE UPDATE ON allergens FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER trg_allergens_prevent_created_at_update BEFORE UPDATE ON allergens FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();

-- Triggers para a tabela 'foods'
CREATE TRIGGER set_timestamp_foods BEFORE UPDATE ON foods FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER trg_foods_prevent_created_at_update BEFORE UPDATE ON foods FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();

-- Triggers para a tabela 'custom_field_definitions'
CREATE TRIGGER set_timestamp_custom_field_definitions BEFORE UPDATE ON custom_field_definitions FOR EACH ROW EXECUTE FUNCTION trigger_set_timestamp();
CREATE TRIGGER trg_custom_field_definitions_prevent_created_at_update BEFORE UPDATE ON custom_field_definitions FOR EACH ROW EXECUTE FUNCTION prevent_created_at_update();