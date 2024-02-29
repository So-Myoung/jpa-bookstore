package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;

    public void save(Item item){
/*        if(item.getId() == null){
            em.persist(item);
        } else {
            em.merge(item);
            merge 사용 하는 방법 좋지 않음. 변경 감지 사용할 것.
            컨트롤러에서 엔티티를 생성하지 말고, 트랜잭션이 있는 서비스 계층에서 조회/변경할 것.
            트랜잭션 커밋 시점에 변경 감지가 실행되기 때문
        }*/
        em.persist(item);
    }

    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
