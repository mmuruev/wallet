package playtech.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import playtech.model.Player;
import playtech.model.PlayerAccess;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;

@Component
@Scope("request")
@Path("/")
public class WalletService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WalletService.class);
    private static final int NOT_FOUND = 1;

    private final PlayerAccess playerAccess;
    private final Statistics statistics;

    @Autowired
    public WalletService(final PlayerAccess playerAccess, final Statistics statistics) {
        this.playerAccess = playerAccess;
        this.statistics = statistics;
    }

    @POST
    @Path("balance/{username}/{transactionId}/{balanceChange}")
    @Produces(MediaType.APPLICATION_JSON)
    public WalletStatus changeBalance(@PathParam("username") final String username,
                                      @PathParam("transactionId") final int transactionId,
                                      @PathParam("balanceChange") final BigDecimal balanceChange) {
        final long time = System.currentTimeMillis();
        LOGGER.info("IN: transaction {} for user {}: requested balance change: {}",
                new Object[]{transactionId, username, balanceChange});
        final WalletStatus status = new WalletStatus();
        status.transactionId = transactionId;

        final Player player;
        try {
            player = playerAccess.load(username);
        } catch (EmptyResultDataAccessException exception) {
            status.errorCode = NOT_FOUND;
            LOGGER.info("OUT: transaction {} for user {}: error code: {}",
                    new Object[]{status.transactionId, username, status.errorCode});
            statistics.reportRequest(System.currentTimeMillis() - time);
            return status;
        }

        player.balanceVersion++;
        player.balance = player.balance.add(balanceChange);
        playerAccess.update(player);

        status.balanceVersion = player.balanceVersion;
        status.balanceChange = balanceChange;
        status.balanceAfterChange = player.balance;
        LOGGER.info("OUT: transaction {} for user {}: balance {} of version {} after change {}",
                new Object[]{status.transactionId, username,
                        status.balanceAfterChange, status.balanceVersion, status.balanceChange});
        statistics.reportRequest(System.currentTimeMillis() - time);
        return status;
    }
}
