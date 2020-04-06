package alararestaurant.util;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public class ValidationUtilImpl implements ValidationUtil {

    private Validator validator;

    public ValidationUtilImpl() {
        this.validator = Validation
                .buildDefaultValidatorFactory()
                .getValidator();
    }

    @Override
    public <E> boolean isValid(E entity) {
        return this.validator.validate(entity)
                .size()==0;
    }

    @Override
    public <E> String violations(E entity) {
        StringBuilder sb = new StringBuilder();
        Set<ConstraintViolation<E>> violations = this.validator.validate(entity);
        for (ConstraintViolation<E> violation :violations) {
            sb.append(violation.getMessage()).append(System.lineSeparator());
        }
        return sb.toString().trim();
    }
}
