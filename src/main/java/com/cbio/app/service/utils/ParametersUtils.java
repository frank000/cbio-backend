package com.cbio.app.service.utils;

import com.cbio.core.v1.dto.ModelDTO;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ParametersUtils {

    public void mountAndPopulateDynamicParameters(List<ModelDTO.Parameter> parameters, Object variablesMap) {

        ExpressionParser parser = new SpelExpressionParser();

        parameters.stream()
                .filter(templateParameterDTO -> ModelUtil.Validators.isValorParamentroVariavel(templateParameterDTO.getValue()))
                .filter(templateParameterDTO -> {
                    return ModelDTO.Parameter.ParameterType.TEXT.equals(templateParameterDTO.getType());
                })
                .map(this::exchangeVariavelParametro)
                .forEach(templateParameterDTO -> {

                    String valorDoCampo;

                    Expression expression = parser.parseExpression(templateParameterDTO.getValue());
                    StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
                    evaluationContext.addPropertyAccessor(new MapAccessor());

                    valorDoCampo = (String) expression.getValue(evaluationContext, variablesMap);

                    templateParameterDTO.setValue(valorDoCampo);
                });

    }

    public ModelDTO.Parameter exchangeVariavelParametro(ModelDTO.Parameter
                                                                dto) {
        String replacement = ModelUtil.Validators.normalizeValorParamentroVariavel(dto.getValue());
        dto.setValue(replacement);
        return dto;
    }

}
