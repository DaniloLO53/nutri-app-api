package org.nutri.app.nutri_app_api.security.models.users;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.nutri.app.nutri_app_api.models.schedules.Schedule;
import org.nutri.app.nutri_app_api.models.locations.Location;
import org.nutri.app.nutri_app_api.repositories.nutritionistRepository.ProfileByParamsProjection;

import java.util.Set;
import java.util.UUID;

@SqlResultSetMapping(
    name = "ProfileByParamsProjectionMapping", // Nome que usaremos para chamar o mapping
    classes = @ConstructorResult(
        targetClass = ProfileByParamsProjection.class,
        columns = {
            @ColumnResult(name = "nutritionistName", type = String.class),
            @ColumnResult(name = "id", type = UUID.class),
            @ColumnResult(name = "address", type = String.class),
            @ColumnResult(name = "acceptsRemote", type = Boolean.class),
            @ColumnResult(name = "ibgeApiCity", type = String.class),
            @ColumnResult(name = "ibgeApiState", type = String.class)
        }
    )
)
@Entity
@Table(name = "nutritionists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Nutritionist {
    public Nutritionist(String crf) {
        this.crf = crf;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID) // Hint for Hibernate to use native UUID type from database, if available
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @NotNull
    @NotBlank
    @Size(
            min = 3,
            max = 20,
            message = "Field crf must have between 3 and 20 characters"
    )
    @Column(name = "crf", nullable = false, unique = true)
    @ToString.Include
    private String crf;

    @NotNull
    @Column(name = "accepts_remote", nullable = false)
    @ToString.Include
    private Boolean acceptsRemote = false;

    /*
    With Lazy, JPA search for Role at DB if and only if we explicitly call user.getUser()
    Otherwise (with Eager), everytime JPA search for User, it brings User within. It can
    lead to issues like N+1 problem.
    */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "nutritionist", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
    private Set<Schedule> schedules;

    @OneToMany(mappedBy = "nutritionist", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private Set<Location> locations;
}
