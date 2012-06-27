package playtech.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;


import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class PlayerAccess {
    private static final PlayerMapper MAPPER = new PlayerMapper();

    private final JdbcTemplate database;

    @Autowired
    public PlayerAccess(final DataSource dataSource) {
        database = new JdbcTemplate(dataSource);
    }

    public Player load(final String username) {
        return database.queryForObject("SELECT * FROM PLAYER WHERE USERNAME = ?", MAPPER, username);
    }

    public void update(final Player player) {
        database.update("UPDATE PLAYER SET (BALANCE_VERSION, BALANCE) = (?, ?) WHERE USERNAME = ?",
                player.balanceVersion, player.balance, player.username);
    }

    private static class PlayerMapper implements RowMapper<Player> {
        @Override
        public Player mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            final Player player = new Player();
            player.username = rs.getString("USERNAME");
            player.balanceVersion = rs.getInt("BALANCE_VERSION");
            player.balance = rs.getBigDecimal("BALANCE");
            return player;
        }
    }
}
