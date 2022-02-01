package com.iconloop.score.token;

import score.Address;
import score.Context;
import score.DictDB;
import score.VarDB;
import score.annotation.EventLog;
import score.annotation.External;
import score.annotation.Optional;

import java.math.BigInteger;

public class Coin {
    public VarDB<BigInteger> totalSupply = Context.newVarDB("_totalSupply", BigInteger.class);
    public VarDB<BigInteger> decimals = Context.newVarDB("_decimals", BigInteger.class);
    public DictDB<Address, BigInteger> balances = Context.newDictDB("balances", BigInteger.class);

    private static final String TAG = "Credits";

    public static BigInteger pow(BigInteger base, int exponent){
        BigInteger res = BigInteger.ONE;

        for(int i = 1; i <= exponent; i++){
            res = res.multiply(base);
        }

        return res;
    }

    public Coin(BigInteger _supply, BigInteger _decimals){
        Context.require(_decimals.intValue() > 0, TAG + ": Decimal number cannot be less than zero");
        Context.require(_supply.intValue() > 0, TAG + ": Total supply cannot be less");

        // set total supply
        // supply * 10 ** decimals
        BigInteger _totalSupply = _supply.multiply(pow(BigInteger.TEN, _decimals.intValue()));

        // set the total supply and initial balance of the owner
        this.totalSupply.set(_totalSupply);
        this.balances.set(Context.getCaller(), _totalSupply);
    }

    @External(readonly = true)
    public String name(){
        return  "Credits"; // ofc star wars reference
    }

    @External(readonly = true)
    public String symbol(){
        return TAG;
    }

    @External(readonly=true)
    public BigInteger decimals() {
        return decimals.getOrDefault(BigInteger.valueOf(18));
    }

    @External(readonly=true)
    public BigInteger totalSupply() {
        return totalSupply.getOrDefault(BigInteger.valueOf(0));
    }

    @External(readonly=true)
    public BigInteger balanceOf(Address _owner) {
        return balances.getOrDefault(_owner, BigInteger.valueOf(0));
    }

    @External
    public void transfer(Address _to, BigInteger _value, @Optional byte[] _data){
        // get the address of the caller
        // Note from documentation: The caller and the origin may be the same but differ in cross-calls:
        // the origin is the sender of the "first" invocation in the chain while the caller is whoever directly
        // called the current SCORE.
        Address from = Context.getCaller();

        Context.require(_value.compareTo(BigInteger.valueOf(0)) > 0, TAG + ": Transfer value cannot be zero or less than zero");
        Context.require(
                balanceOf(from).compareTo(_value) >= 0,
                TAG + ": Source address must have token greater than transfer amount"
        );


        this.balances.set(from, balanceOf(from).subtract(_value));
        this.balances.set(_to, balanceOf(_to).add(_value));

        if(_to.isContract()) {
            Context.call(_to, "tokenFallBack", from, _value, _data);
        }

        _data = _data == null? new byte[0]:_data;

        // transfer event long
        Transfer(from, _to, _value, _data);
    }

    @External
    public void mint(Address owner, BigInteger value){
        Checks.onlyOwner();
        Context.require(value.compareTo(BigInteger.valueOf(0)) > 0, TAG + ": Total mint value must be greater than zero");

        final Address caller = Context.getCaller();
        balances.set(caller, balances.getOrDefault(caller, BigInteger.valueOf(0)).add(value));

        Mint(owner, value);
    }

    @External
    public void burn(Address owner, BigInteger value){
        Checks.onlyOwner();
        Context.require(value.compareTo(BigInteger.valueOf(0)) > 0, TAG + ": Total burn value must be greater than zero");

        final Address caller = Context.getCaller();
        final BigInteger ownerBalance = balances.getOrDefault(owner, BigInteger.valueOf(0));

        // check if the account balance is greater than burn amount
        Context.require(ownerBalance.compareTo(value) >= 0, TAG + ": Account balance must be greater burn amount");

        balances.set(caller, balances.getOrDefault(caller, BigInteger.valueOf(0)).add(value));

        Burn(owner, value);

    }


    @EventLog(indexed = 3)
    private void Transfer(Address _from, Address _to, BigInteger _value, byte[] _data){}

    @EventLog(indexed = 2)
    private void Mint(Address owner, BigInteger value){}

    @EventLog(indexed = 2)
    private void Burn(Address owner, BigInteger value){}
}