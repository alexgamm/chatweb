package chatweb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "verifications")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Verification {
    @Id
    @Column(name = "user_id")
    private int userId;
    @Column(name = "code")
    private String code;
    @Column(name = "verified")
    private boolean verified;
}
