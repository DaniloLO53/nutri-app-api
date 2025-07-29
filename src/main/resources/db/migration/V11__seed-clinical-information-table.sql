-- ===== SINTOMAS (Symptoms) =====
INSERT INTO symptoms (name, is_approved) VALUES
('Inchaço Abdominal', TRUE),
('Azia', TRUE),
('Fadiga Crônica', TRUE),
('Constipação', TRUE),
('Diarreia', TRUE),
('Insônia', TRUE),
('Queda de Cabelo', TRUE),
('Dificuldade de Concentração', TRUE),
('Ansiedade', TRUE),
('Compulsão por Doces', TRUE),
('Cãibras Musculares', TRUE),
('Dores Articulares', TRUE),
('Pele Seca', TRUE),
('Retenção de Líquidos', TRUE),
('Dor de Cabeça Frequente', TRUE);

-- ===== DOENÇAS (Diseases) =====
INSERT INTO diseases (name, is_approved) VALUES
('Diabetes Mellitus Tipo 2', TRUE),
('Hipertensão Arterial', TRUE),
('Hipotireoidismo', TRUE),
('Hipercolesterolemia', TRUE),
('Síndrome do Intestino Irritável (SII)', TRUE),
('Doença Celíaca', TRUE),
('Esteatose Hepática', TRUE),
('Síndrome dos Ovários Policísticos (SOP)', TRUE),
('Gastrite Crônica', TRUE),
('Doença do Refluxo Gastroesofágico (DRGE)', TRUE),
('Osteoporose', TRUE),
('Anemia Ferropriva', TRUE),
('Obesidade', TRUE),
('Enxaqueca Crônica', TRUE),
('Artrite Reumatoide', TRUE);

-- ===== MEDICAMENTOS E SUPLEMENTOS (Medications & Supplements) =====
INSERT INTO medications_supplements (name, type, is_approved) VALUES
('Metformina', 'MEDICAMENTO', TRUE),
('Losartana', 'MEDICAMENTO', TRUE),
('Levotiroxina Sódica', 'MEDICAMENTO', TRUE),
('Omeprazol', 'MEDICAMENTO', TRUE),
('Sinvastatina', 'MEDICAMENTO', TRUE),
('Sertralina', 'MEDICAMENTO', TRUE),
('Whey Protein Isolado', 'SUPLEMENTO', TRUE),
('Creatina Monohidratada', 'SUPLEMENTO', TRUE),
('Vitamina D3 (5.000 UI)', 'SUPLEMENTO', TRUE),
('Ômega 3 (EPA/DHA)', 'SUPLEMENTO', TRUE),
('Citrato de Magnésio', 'SUPLEMENTO', TRUE),
('Coenzima Q10', 'SUPLEMENTO', TRUE),
('Multivitamínico A-Z', 'SUPLEMENTO', TRUE),
('Cafeína Anidra (200mg)', 'SUPLEMENTO', TRUE),
('Melatonina', 'SUPLEMENTO', TRUE);

-- ===== ALÉRGENOS (Allergens) =====
INSERT INTO allergens (name, type, is_approved) VALUES
('Lactose', 'INTOLERANCIA', TRUE),
('Glúten (Sensibilidade Não-Celíaca)', 'INTOLERANCIA', TRUE),
('Glúten (Doença Celíaca)', 'ALERGIA', TRUE),
('Proteína do Leite de Vaca (APLV)', 'ALERGIA', TRUE),
('Amendoim', 'ALERGIA', TRUE),
('Frutos do Mar', 'ALERGIA', TRUE),
('Soja', 'ALERGIA', TRUE),
('Ovos', 'ALERGIA', TRUE),
('Castanhas', 'ALERGIA', TRUE),
('Trigo', 'ALERGIA', TRUE),
('Frutose', 'INTOLERANCIA', TRUE),
('Histamina', 'INTOLERANCIA', TRUE),
('Cafeína', 'INTOLERANCIA', TRUE),
('Sacarose', 'INTOLERANCIA', TRUE),
('Corantes Artificiais', 'INTOLERANCIA', TRUE);

-- ===== ALIMENTOS (Foods) =====
INSERT INTO foods (name, is_approved) VALUES
('Brócolis', TRUE),
('Frango Grelhado', TRUE),
('Arroz Integral', TRUE),
('Salmão', TRUE),
('Batata Doce', TRUE),
('Abacate', TRUE),
('Banana', TRUE),
('Aveia em Flocos', TRUE),
('Espinafre', TRUE),
('Lentilha', TRUE),
('Tomate', TRUE),
('Ovo Cozido', TRUE),
('Jiló', TRUE),
('Quiabo', TRUE),
('Coentro', TRUE);
