package br.com.monkey.ecx;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
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

}
