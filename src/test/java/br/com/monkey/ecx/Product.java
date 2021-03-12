package br.com.monkey.ecx;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

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

	private boolean visible;

	@ManyToOne(cascade = ALL)
	private Category category;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Supplier> suppliers;

}
