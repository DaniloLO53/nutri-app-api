package org.nutri.app.nutri_app_api.models.clinicalInformations;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.nutri.app.nutri_app_api.security.models.users.Nutritionist;

import java.util.UUID;

@Entity
@Table(name = "custom_field_definitions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class CustomFieldDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutritionist_id", nullable = false)
    private Nutritionist nutritionist;

    @Column(name = "field_label", nullable = false, columnDefinition = "TEXT")
    private String fieldLabel;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_type", nullable = false)
    private CustomFieldType fieldType;

    @Column(name = "unit", length = 20)
    private String unit;
}
