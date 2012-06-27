package playtech.service;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement
public class WalletStatus {
    public int transactionId;
    public int errorCode;
    public int balanceVersion;
    public BigDecimal balanceChange = BigDecimal.ZERO;
    public BigDecimal balanceAfterChange = BigDecimal.ZERO;
}
