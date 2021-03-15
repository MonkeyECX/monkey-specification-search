package br.com.monkey.ecx;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
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
