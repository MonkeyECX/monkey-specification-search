package br.com.monkey.ecx.configuration;

import br.com.monkey.ecx.annotation.SearchParameter;
import br.com.monkey.ecx.specification.SpecificationsBuilder;
import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class SearchParameterArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		return methodParameter.getParameterType().equals(Specification.class)
				&& methodParameter.hasParameterAnnotation(SearchParameter.class);
	}

	@Override
	public Object resolveArgument(MethodParameter methodParameter,
			ModelAndViewContainer modelAndViewContainer,
			NativeWebRequest nativeWebRequest,
			WebDataBinderFactory webDataBinderFactory) {
		SearchParameter parameterAnnotation = methodParameter
				.getParameterAnnotation(SearchParameter.class);
		return buildSpecification(
				nativeWebRequest.getParameter(parameterAnnotation.value()));
	}

	private <T> Specification<T> buildSpecification(String search) {
		if (search == null || search.isEmpty()) {
			return null;
		}
		return new SpecificationsBuilder<T>().withSearch(search).build();
	}

}
