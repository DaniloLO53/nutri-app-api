package org.nutri.app.nutri_app_api.services.clinicalInformationService;

import java.util.UUID;

public interface MasterDataProjection {
    UUID getId();
    String getName();
    String getType(); // Será nulo para symptoms, diseases e foods
    String getSource(); // Identifica a tabela de origem (ex: "SYMPTOM")
}