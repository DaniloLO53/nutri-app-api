package org.nutri.app.nutri_app_api.repositories.nutritionistRepository;

public interface NutritionistProfileFlatProjection {
    String getFirstName();
    String getLastName();
    String getEmail();
    Boolean getAcceptsRemote();
    String getCrf();
    String getAddress();
    String getPhone1();
    String getPhone2();
    String getPhone3();
    Integer getIbgeApiStateId();
    String getIbgeApiCity();
    String getIbgeApiState();
}
