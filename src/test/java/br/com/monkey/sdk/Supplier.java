package br.com.monkey.sdk;

import lombok.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.Instant;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
class Supplier {

	@Id
	private Integer id;

	private String name;

	private String governmentId;

	private Instant updatedAt;

	private BigDecimal practicedPrice;

}
