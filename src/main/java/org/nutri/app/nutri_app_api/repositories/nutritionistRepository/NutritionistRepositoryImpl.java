package org.nutri.app.nutri_app_api.repositories.nutritionistRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.*;

// O nome DEVE ser o nome da interface do repositório + "Impl"
public class NutritionistRepositoryImpl implements NutritionistRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Set<ProfileByParamsProjection> findNutritionistProfilesByParams(
            String nutritionistName, String ibgeApiCity, String ibgeApiState, Boolean acceptsRemote) {

        // 1. Base da query nativa, com os joins necessários.
        // O "WHERE 1=1" é um truque para facilitar a adição de cláusulas "AND".
        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        "CONCAT(u.first_name, ' ', u.last_name) AS nutritionistName, " +
                        "n.id, " +
                        "l.id AS locationId, " +
                        "l.address as locationName, " +
                        "n.accepts_remote AS acceptsRemote, " +
                        "l.ibge_api_city AS ibgeApiCity, " +
                        "l.ibge_api_state AS ibgeApiState " +
                        "FROM " +
                        "nutritionists n " +
                        "INNER JOIN " +
                        "users u ON u.id = n.user_id " +
                        "INNER JOIN " +
                        "locations l ON l.nutritionist_id = n.id " +
                        "WHERE 1=1"
        );

        // 2. Mapa para guardar os parâmetros de forma segura (evita SQL Injection)
        Map<String, Object> params = new HashMap<>();

        if (nutritionistName != null && !nutritionistName.isBlank()) {
            // Adiciona filtro por nome do nutricionista (case-insensitive e sem acentos)
            sql.append(" AND unaccent(CONCAT(u.first_name, ' ', u.last_name)) ILIKE :nameFilter");
            params.put("nameFilter", "%" + nutritionistName.trim() + "%");
        }

        if (ibgeApiState != null && !ibgeApiState.isBlank()) {
            // CORREÇÃO: Adiciona o filtro de estado corretamente
            sql.append(" AND l.ibge_api_state = :stateFilter");
            params.put("stateFilter", ibgeApiState.trim().toUpperCase());
        }

        if (ibgeApiCity != null && !ibgeApiCity.isBlank()) {
            sql.append(" AND unaccent(l.ibge_api_city) ILIKE :cityFilter");
            params.put("cityFilter", "%" + ibgeApiCity.trim() + "%");
        }

        System.out.println("***************** ACCEPTS REMOTE: " + acceptsRemote);

        if (acceptsRemote) {
            sql.append(" AND n.accepts_remote = :acceptsRemote");
            params.put("acceptsRemote", acceptsRemote);
        }

        sql.append(" ORDER BY nutritionistName");

        // 4. Cria a query, associando-a ao nosso mapeamento de resultado
        Query query = entityManager.createNativeQuery(sql.toString(), "ProfileByParamsProjectionMapping");

        // 5. Define os parâmetros na query
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        // 6. Executa a query e retorna o resultado como um Set
        @SuppressWarnings("unchecked")
        List<ProfileByParamsProjection> resultList = query.getResultList();

        return new HashSet<>(resultList);
    }
}