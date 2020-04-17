package br.com.monkey.ecx;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.Instant;

import static javax.persistence.CascadeType.ALL;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
class Product {

	@Id
	private Integer id;

	private String name;

	private Float price;

	private Integer stock;

	private Instant createdAt;

	@ManyToOne(cascade = ALL)
	private Category category;

}
