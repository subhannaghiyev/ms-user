package az.spring.service.specification;

import az.spring.model.UserCriteria;
import az.spring.entity.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class UserSpecification implements Specification<User> {

    private final UserCriteria userCriteria;

    private static final String FIRSTNAME = "firstName";
    private static final String LASTNAME = "lastName";
    private static final String AGE = "age";
    private static final String USERNAME = "username";

    @Override
    public Predicate toPredicate(Root<User> root,
                                 CriteriaQuery<?> query,
                                 CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicates = new ArrayList<>();

        if (userCriteria != null) {
            if (userCriteria.getFirstName() != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get(FIRSTNAME), userCriteria.getFirstName()));
            }

            if (userCriteria.getLastName() != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get(LASTNAME), userCriteria.getLastName()));
            }
            if (userCriteria.getAge() != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get(AGE), userCriteria.getAge()));
            }
            if (userCriteria.getUsername() != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get(USERNAME), userCriteria.getUsername()));
            }

            if (StringUtils.hasText(userCriteria.getFirstName())) {
                predicates.add(
                        criteriaBuilder.like(root.get(FIRSTNAME), "%" + userCriteria.getFirstName() + "%"));
            }
            if (StringUtils.hasText(userCriteria.getLastName())) {
                predicates.add(
                        criteriaBuilder.like(root.get(LASTNAME), "%" + userCriteria.getLastName() + "%"));
            }
            if (StringUtils.hasText(userCriteria.getUsername())) {
                predicates.add(
                        criteriaBuilder.like(root.get(USERNAME), "%" + userCriteria.getUsername() + "%"));
            }
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

}
