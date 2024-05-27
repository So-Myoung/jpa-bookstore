package jpabook.jpashop.repository.order.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    /*
    * 컬렉션은 별도로 조회
    * Query : 루트 1번, 컬렉션 N번 조회
    * 1 + N 문제 발생
    * 단건 조회에서 많이 사용하는 방식
    * */
    public static List<OrderQueryDto> findOrderQueryDtos() {
    /*
    * 루트 조회 - ToOne 연관 관계 코드 한번에 조회
    * 1번 조회
    * */
    List<OrderQueryDto> result = findOrders();

    /*
    * result list의 요소를 순회하면서 컬렉션 추가(추가 쿼리 실행)
    * n번 조회
    * */

    return result;
    }

    /**
     * ToMany 관계(컬렉션)를 제외한 나머지를 한번에 조회
     * ToOne 코드를 모두 조회 - join 해도 Query 1번만 조회
     */
    private static List<OrderQueryDto> findOrders() {
        return null;
    }
}
