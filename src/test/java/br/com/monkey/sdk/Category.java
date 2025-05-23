package br.com.monkey.sdk;

import lombok.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.Instant;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
class Category {

	@Id
	private Integer id;

	private String name;

	private Instant updatedAt;

	private CategoryType type;

}
