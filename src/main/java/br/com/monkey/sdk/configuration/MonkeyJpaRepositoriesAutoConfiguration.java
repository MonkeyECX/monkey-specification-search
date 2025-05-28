package br.com.monkey.sdk.configuration;

import br.com.monkey.sdk.jpa.MonkeySimpleJpaRepositoryImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ConditionalOnClass(JpaRepository.class)
@EnableJpaRepositories(basePackages = { "br.com.monkey.ecx", "br.com.monkey.sdk" },
		repositoryBaseClass = MonkeySimpleJpaRepositoryImpl.class)
@AutoConfiguration(after = HibernateJpaAutoConfiguration.class)
public class MonkeyJpaRepositoriesAutoConfiguration {

}