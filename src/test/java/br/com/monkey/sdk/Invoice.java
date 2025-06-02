package br.com.monkey.sdk;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
class Invoice {

	@Id
	private Integer id;

	private int installment;

	private String invoiceKey;

	private String governmentId;

}