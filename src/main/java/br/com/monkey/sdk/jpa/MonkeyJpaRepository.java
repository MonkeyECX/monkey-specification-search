package br.com.monkey.sdk.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface MonkeyJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

}