package hello.itemservice.repository.jdbctemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * + SimpleJdbcInsert (PK 가 db 에서 AutoIncrement 될 때 쓰면 된다.)
 */
@Slf4j
public class JdbcTemplateItemRepositoryV3 implements ItemRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    /**
     * SimpleJdbcInsert 에는 DataSource 가 필요.
     * .withTableName() --> 데이터를 저장할 테이블 명을 지정한다.
     * .usingGeneratedKeyColumns() --> key 를 생성하는 PK 컬럼명을 지정한다.
     * .usingColumns() -->  INSERT SQL 에 사용할 컬럼을 지정한다. 특정 값만 지정하고 싶을 때 사용한다. 따라서 생략 가능하다. (디폴트 모든 값)
     */
    public JdbcTemplateItemRepositoryV3(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("item")
                .usingGeneratedKeyColumns("id");
//                .usingColumns("item_name", "price", "quantity"); // 생략 가능
    }

    @Override
    public Item save(Item item) {
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(item);
        Number key = jdbcInsert.executeAndReturnKey(sqlParameterSource);
        item.setId(key.longValue());
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        String sql = "update item set item_name = :itemName, price = :price, quantity = :quantity where id = :id";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("itemName", updateParam.getItemName())
                .addValue("price", updateParam.getPrice())
                .addValue("quantity", updateParam.getQuantity())
                .addValue("id", itemId);

        jdbcTemplate.update(sql, sqlParameterSource);
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id, item_name, price, quantity from item where id = :id";
        try {
            Map<String, Object> param = Map.of("id", id);
            Item item = jdbcTemplate.queryForObject(sql, param, itemRowMapper());
            return Optional.of(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(cond);

        String sql = "select id, item_name, price, quantity from item";

        // 동적 쿼리 (매우 복잡)
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where";
        }
        boolean andFlag = false;
        if (StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%',:itemName,'%')";
            andFlag = true;
        }
        if (maxPrice != null) {
            if (andFlag) {
                sql += " and";
            }
            sql += " price <= :maxPrice";
        }

        log.info("sql={}", sql);
        return jdbcTemplate.query(sql, sqlParameterSource, itemRowMapper());
    }

    private RowMapper<Item> itemRowMapper() {
        return BeanPropertyRowMapper.newInstance(Item.class); // _ 표기법의 camel 변환을 지원한다.
    }
}
