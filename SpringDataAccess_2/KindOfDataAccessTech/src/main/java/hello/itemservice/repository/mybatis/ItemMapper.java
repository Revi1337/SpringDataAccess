package hello.itemservice.repository.mybatis;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 1. MyBatis 매핑 XML 을 호출해주는 매퍼 인터페이스
 * 2. 이 인터페이스에는 @Mapper 를 달아줘야 MyBatis 에서 인식할 수 있다.
 * 3. 이 인터페이스의 메서드를 호출하면  xml 에 적은 SQL 을 실행하고 결과를 돌려준다.
 *
 * --> XML 은 이 인터페이스가 속한 package 와 동일한 경로로 resources 하위에 만들어주어야 한다.
 * --> 또한 XML 의 이름은 해당 인터페이스와 동일한 이름으로 해야한다.
 */
@Mapper
public interface ItemMapper {

    void save(Item item);

    /**
     * 파라미터가 두개가 넘어가면 @Param 를 명시해주어야 한다.
     * @param id
     * @param updateParam
     */
    void update(@Param("id") Long id, @Param("updateParam") ItemUpdateDto
            updateParam);

    List<Item> findAll(ItemSearchCond itemSearch);

    Optional<Item> findById(Long id);

}
