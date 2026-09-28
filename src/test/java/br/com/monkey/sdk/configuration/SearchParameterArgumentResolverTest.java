package br.com.monkey.sdk.configuration;

import br.com.monkey.sdk.annotation.SearchParameter;
import br.com.monkey.sdk.core.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SearchParameterArgumentResolverTest {

	private final SearchParameterArgumentResolver resolver = new SearchParameterArgumentResolver();

	@Test
	void should_return_null_when_there_is_no_search_and_no_required_keys() throws Exception {
		assertThat(resolve("optional", null)).isNull();
	}

	@Test
	void should_reject_when_there_is_no_search_and_a_key_is_required() {
		assertThatThrownBy(() -> resolve("required", null)).isExactlyInstanceOf(BadRequestException.class)
			.hasMessageContaining("externalId");
	}

	@Test
	void should_reject_when_the_search_is_empty_and_a_key_is_required() {
		assertThatThrownBy(() -> resolve("required", "")).isExactlyInstanceOf(BadRequestException.class);
	}

	@Test
	void should_reject_when_the_search_does_not_filter_by_the_required_key() {
		assertThatThrownBy(() -> resolve("required", "seller:2")).isExactlyInstanceOf(BadRequestException.class);
	}

	@Test
	void should_build_the_specification_when_the_search_filters_by_the_required_key() throws Exception {
		assertThat(resolve("required", "externalId:1 OR seller:2")).isInstanceOf(Specification.class);
	}

	private Object resolve(String methodName, String search) throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		if (search != null) {
			request.setParameter("search", search);
		}
		MethodParameter parameter = new MethodParameter(
				Endpoints.class.getDeclaredMethod(methodName, Specification.class), 0);
		assertThat(resolver.supportsParameter(parameter)).isTrue();
		return resolver.resolveArgument(parameter, null, new ServletWebRequest(request), null);
	}

	@SuppressWarnings("unused")
	static class Endpoints {

		void optional(@SearchParameter Specification<Object> search) {
			// not to do
		}

		void required(@SearchParameter(required = "externalId") Specification<Object> search) {
			// not to do
		}

	}

}
