package hello.itemservice.repository.jdbctemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * JdbcTemplate
 */
@Slf4j
public class JdbcTemplateItemRepositoryV1 implements ItemRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateItemRepositoryV1(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 데이터를 변경할 때는 update() 를 사용하면 된다.
     * 즉, INSERT, UPDATE, DELETE 에는 update() 를 사용하면 된다.
     *
     * 1. 현재 DB 에서 PK 를 Auto Increment 해주기 때문에, 저장할 때는 PK 를 비워두고 저장한다.
     * 2. PK 는 DB 에 저장되어야 알 수 있기 떄문에, DB 에 저장된 후, PK 가 어떤 값인지 알기 위해서
     *    KeyHolder 와 .prepareStatement(sql, new String[]{"id"}); 로 DB 에서 생성된 ID 값을 조회한다.

     * --> 나중에 SimpleJdbcInsert 라는 편리한 기능이 있으므로 이런게 있다고만 알아두자.
     * @param item
     * @return
     */
    @Override
    public Item save(Item item) {
        String sql = "insert into item(item_name, price, quantity) values (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int influencedCount = jdbcTemplate.update(connection -> {
            // 자동 증가 키 조회를 위함.
            PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setString(1, item.getItemName());
            preparedStatement.setInt(2, item.getPrice());
            preparedStatement.setInt(3, item.getQuantity());
            return preparedStatement;
        }, keyHolder);

        long key = keyHolder.getKey().longValue();
        item.setId(key);

        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        String sql = "update item set item_name = ?, price = ?, quantity = ? where id = ?";

        jdbcTemplate.update(
                sql,
                updateParam.getItemName(),
                updateParam.getPrice(),
                updateParam.getQuantity(),
                itemId
        );
    }

    /**
     * queryForObject() 는 값이 없으면 EmptyResultDataAccessException 가 터진다.
     * 결과가 둘 이상이면 IncorrectResultSizeDataAccessException 이 터진다.
     * @param id
     * @return
     */
    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id, item_name, price, quantity from item where id = ?";
        try {
            Item item = jdbcTemplate.queryForObject(sql, itemRowMapper(), id);
            return Optional.of(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * jdbcTemplate.query() 는 결과가 하나 이상일 떄 사용함.
     * 결과가 없으면 빈 컬렉션을 반환한다.
     * @param cond
     * @return
     */
    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        String sql = "select id, item_name, price, quantity from item";

        // 동적 쿼리 (매우 복잡)
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where";
        }
        boolean andFlag = false;
        List<Object> param = new ArrayList<>();
        if (StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%',?,'%')";
            param.add(itemName);
            andFlag = true;
        }
        if (maxPrice != null) {
            if (andFlag) {
                sql += " and";
            }
            sql += " price <= ?";
            param.add(maxPrice);
        }

        log.info("sql={}", sql);
        return jdbcTemplate.query(sql, itemRowMapper(), param.toArray());
    }

    private RowMapper<Item> itemRowMapper() {
        return (rs, rowNum) -> {
            Item item = new Item();
            item.setId(rs.getLong("id"));
            item.setItemName(rs.getString("item_name"));
            item.setPrice(rs.getInt("price"));
            item.setQuantity(rs.getInt("quantity"));
            return item;
        };
    }
}
