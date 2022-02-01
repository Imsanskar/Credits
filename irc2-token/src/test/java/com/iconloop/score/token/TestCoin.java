package com.iconloop.score.token;

import com.iconloop.score.test.Account;
import com.iconloop.score.test.Score;
import com.iconloop.score.test.ServiceManager;
import com.iconloop.score.test.TestBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCoin extends TestBase {
    private static final String name = "MySampleToken";
    private static final String symbol = "MST";
    private static final int decimals = 18;
    private static final BigInteger initialSupply = BigInteger.valueOf(1000);

    private static final BigInteger totalSupply = initialSupply.multiply(BigInteger.TEN.pow(decimals));
    private static final ServiceManager sm = getServiceManager();
    private static final Account owner = sm.createAccount();

    private static Score tokenScore;

    @BeforeAll
    public static void setup() throws Exception {
        tokenScore = sm.deploy(owner, Coin.class,
                initialSupply, BigInteger.valueOf(decimals));
        owner.addBalance(symbol, totalSupply);
    }


    @Test
    void totalSupply() {
        assertEquals(totalSupply, tokenScore.call("totalSupply"));
    }

    @Test
    void balanceOf() {
        assertEquals(owner.getBalance(symbol),
                tokenScore.call("balanceOf", tokenScore.getOwner().getAddress()));
    }

    @Test
    void transfer() {
        Account alice = sm.createAccount();
        BigInteger value = BigInteger.TEN.pow(decimals);
        tokenScore.invoke(owner, "transfer", alice.getAddress(), value, "to alice".getBytes());
        owner.subtractBalance(symbol, value);
        assertEquals(owner.getBalance(symbol),
                tokenScore.call("balanceOf", tokenScore.getOwner().getAddress()));
        assertEquals(value,
                tokenScore.call("balanceOf", alice.getAddress()));

        // transfer self
        tokenScore.invoke(alice, "transfer", alice.getAddress(), value, "self transfer".getBytes());
        assertEquals(value, tokenScore.call("balanceOf", alice.getAddress()));
    }
}
