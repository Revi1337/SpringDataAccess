package access.springdataaccess_1.repository;

import access.springdataaccess_1.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

/**
 * JDBCTemplate 사용 (Spring 에서 제공)
 */
@Slf4j
public class MemberRepositoryV5 implements MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    public MemberRepositoryV5(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Member save(Member member) {
        String sql = "insert into member(member_id, money) values (?, ?)";
        int influencedCount = jdbcTemplate.update(sql, member.getMemberId(), member.getMoney());
        return member;
    }

    @Override
    public Member findById(String memberId) {
        String sql = "select * from member where member_id = ?";
        return jdbcTemplate.queryForObject(sql, memberRowMapper(), memberId); // 단건 조회에는 queryForObject() 사용
    }

    private RowMapper<Member> memberRowMapper() {
        return (resultSet, rowNum) -> {
            Member member = new Member();
            member.setMemberId(resultSet.getString("member_id"));
            member.setMoney(resultSet.getInt("money"));
            return member;
        };
    }

    @Override
    public void update(String memberId, int money) {
        String sql = "update member set money = ? where member_id = ?";
        int influencedCount = jdbcTemplate.update(sql, money, memberId);
    }

    @Override
    public void delete(String memberId) {
        String sql = "delete member where member_id = ?";
        int update = jdbcTemplate.update(sql, memberId);
    }

}
