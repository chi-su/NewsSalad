package com.example.chisu.newssalad.token;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.0.1.
 */
public class T6 extends Contract {
    private static final String BINARY = "{\n"
            + "\t\"object\": \"60806040526012600360006101000a81548160ff021916908360ff1602179055503480156200002d57600080fd5b5060405162001c4238038062001c42833981018060405260608110156200005357600080fd5b810190808051906020019092919080516401000000008111156200007657600080fd5b828101905060208101848111156200008d57600080fd5b8151856001820283011164010000000082111715620000ab57600080fd5b50509291906020018051640100000000811115620000c857600080fd5b82810190506020810184811115620000df57600080fd5b8151856001820283011164010000000082111715620000fd57600080fd5b5050929190505050828282336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550600360009054906101000a900460ff1660ff16600a0a620f424002600481905550600454600560003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508160019080519060200190620001c7929190620001ed565b508060029080519060200190620001e0929190620001ed565b505050505050506200029c565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106200023057805160ff191683800117855562000261565b8280016001018555821562000261579182015b828111156200026057825182559160200191906001019062000243565b5b50905062000270919062000274565b5090565b6200029991905b80821115620002955760008160009055506001016200027b565b5090565b90565b61199680620002ac6000396000f3fe608060405260043610610122576000357c01000000000000000000000000000000000000000000000000000000009004806305fefda71461012757806306fdde031461016c578063095ea7b3146101fc57806318160ddd1461026f57806323b872dd1461029a578063313ce5671461032d57806342966c681461035e5780634b750334146103b157806370a08231146103dc57806379c650681461044157806379cc67901461049c5780638620410b1461050f5780638da5cb5b1461053a57806395d89b4114610591578063a6f2ae3a14610621578063a9059cbb14610638578063b414d4b6146106ab578063cae9ca5114610714578063dd62ed3e1461081e578063e4849b32146108a3578063e724529c146108de578063f2fde38b1461093b575b600080fd5b34801561013357600080fd5b5061016a6004803603604081101561014a57600080fd5b81019080803590602001909291908035906020019092919050505061098c565b005b34801561017857600080fd5b506101816109f9565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156101c15780820151818401526020810190506101a6565b50505050905090810190601f1680156101ee5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561020857600080fd5b506102556004803603604081101561021f57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610a97565b604051808215151515815260200191505060405180910390f35b34801561027b57600080fd5b50610284610b89565b6040518082815260200191505060405180910390f35b3480156102a657600080fd5b50610313600480360360608110156102bd57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610b8f565b604051808215151515815260200191505060405180910390f35b34801561033957600080fd5b50610342610cbc565b604051808260ff1660ff16815260200191505060405180910390f35b34801561036a57600080fd5b506103976004803603602081101561038157600080fd5b8101908080359060200190929190505050610ccf565b604051808215151515815260200191505060405180910390f35b3480156103bd57600080fd5b506103c6610dd3565b6040518082815260200191505060405180910390f35b3480156103e857600080fd5b5061042b600480360360208110156103ff57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610dd9565b6040518082815260200191505060405180910390f35b34801561044d57600080fd5b5061049a6004803603604081101561046457600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610df1565b005b3480156104a857600080fd5b506104f5600480360360408110156104bf57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610f78565b604051808215151515815260200191505060405180910390f35b34801561051b57600080fd5b50610524611192565b6040518082815260200191505060405180910390f35b34801561054657600080fd5b5061054f611198565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561059d57600080fd5b506105a66111bd565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156105e65780820151818401526020810190506105cb565b50505050905090810190601f1680156106135780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561062d57600080fd5b5061063661125b565b005b34801561064457600080fd5b506106916004803603604081101561065b57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291908035906020019092919050505061126f565b604051808215151515815260200191505060405180910390f35b3480156106b757600080fd5b506106fa600480360360208110156106ce57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611286565b604051808215151515815260200191505060405180910390f35b34801561072057600080fd5b506108046004803603606081101561073757600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001909291908035906020019064010000000081111561077e57600080fd5b82018360208201111561079057600080fd5b803590602001918460018302840111640100000000831117156107b257600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f8201169050808301925050505050505091929192905050506112a6565b604051808215151515815260200191505060405180910390f35b34801561082a57600080fd5b5061088d6004803603604081101561084157600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061142a565b6040518082815260200191505060405180910390f35b3480156108af57600080fd5b506108dc600480360360208110156108c657600080fd5b810190808035906020019092919050505061144f565b005b3480156108ea57600080fd5b506109396004803603604081101561090157600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291908035151590602001909291905050506114d8565b005b34801561094757600080fd5b5061098a6004803603602081101561095e57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506115fd565b005b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156109e757600080fd5b81600781905550806008819055505050565b60018054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610a8f5780601f10610a6457610100808354040283529160200191610a8f565b820191906000526020600020905b815481529060010190602001808311610a7257829003601f168201915b505050505081565b600081600660003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925846040518082815260200191505060405180910390a36001905092915050565b60045481565b6000600660008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020548211151515610c1c57600080fd5b81600660008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008282540392505081905550610cb184848461169b565b600190509392505050565b600360009054906101000a900460ff1681565b600081600560003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205410151515610d1f57600080fd5b81600560003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008282540392505081905550816004600082825403925050819055503373ffffffffffffffffffffffffffffffffffffffff167fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca5836040518082815260200191505060405180910390a260019050919050565b60075481565b60056020528060005260406000206000915090505481565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610e4c57600080fd5b80600560008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008282540192505081905550806004600082825401925050819055503073ffffffffffffffffffffffffffffffffffffffff16600073ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef836040518082815260200191505060405180910390a38173ffffffffffffffffffffffffffffffffffffffff163073ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef836040518082815260200191505060405180910390a35050565b600081600560008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205410151515610fc857600080fd5b600660008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054821115151561105357600080fd5b81600560008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254039250508190555081600660008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008282540392505081905550816004600082825403925050819055508273ffffffffffffffffffffffffffffffffffffffff167fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca5836040518082815260200191505060405180910390a26001905092915050565b60085481565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60028054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156112535780601f1061122857610100808354040283529160200191611253565b820191906000526020600020905b81548152906001019060200180831161123657829003601f168201915b505050505081565b6000600a905061126c30338361169b565b50565b600061127c33848461169b565b6001905092915050565b60096020528060005260406000206000915054906101000a900460ff1681565b6000808490506112b68585610a97565b15611421578073ffffffffffffffffffffffffffffffffffffffff16638f4ffcb1338630876040518563ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018481526020018373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200180602001828103825283818151815260200191508051906020019080838360005b838110156113b0578082015181840152602081019050611395565b50505050905090810190601f1680156113dd5780820380516001836020036101000a031916815260200191505b5095505050505050600060405180830381600087803b1580156113ff57600080fd5b505af1158015611413573d6000803e3d6000fd5b505050506001915050611423565b505b9392505050565b6006602052816000526040600020602052806000526040600020600091509150505481565b600030905060075482028173ffffffffffffffffffffffffffffffffffffffff16311015151561147e57600080fd5b61148933308461169b565b3373ffffffffffffffffffffffffffffffffffffffff166108fc60075484029081150290604051600060405180830381858888f193505050501580156114d3573d6000803e3d6000fd5b505050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561153357600080fd5b80600960008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055507f48335238b4855f35377ed80f164e8c6f3c366e54ac00b96a6402d4a9814a03a58282604051808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001821515151581526020019250505060405180910390a15050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561165857600080fd5b806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff16141515156116d757600080fd5b80600560008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020541015151561172557600080fd5b600560008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205481600560008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205401101515156117b457600080fd5b600960008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff1615151561180d57600080fd5b600960008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff1615151561186657600080fd5b80600560008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254039250508190555080600560008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825401925050819055508173ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef836040518082815260200191505060405180910390a350505056fea165627a7a7230582064c2ad40975c65ab7842e4179862b8fa2a39c453334010130ba3ec0d34e043b80029\",\n"
            + "}\n";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_APPROVEANDCALL = "approveAndCall";

    public static final String FUNC_BURN = "burn";

    public static final String FUNC_BURNFROM = "burnFrom";

    public static final String FUNC_BUY = "buy";

    public static final String FUNC_FREEZEACCOUNT = "freezeAccount";

    public static final String FUNC_MINTTOKEN = "mintToken";

    public static final String FUNC_SELL = "sell";

    public static final String FUNC_SETPRICES = "setPrices";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_BUYPRICE = "buyPrice";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_FROZENACCOUNT = "frozenAccount";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_SELLPRICE = "sellPrice";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

//    public static final Event FROZENFUNDS_EVENT = new Event("FrozenFunds",
//            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Bool>() {}));
    ;

//    public static final Event TRANSFER_EVENT = new Event("Transfer",
//            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
//    ;
//
//    public static final Event APPROVAL_EVENT = new Event("Approval",
//            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

//    public static final Event BURN_EVENT = new Event("Burn",
//            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected T6(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

//    protected T6(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
//        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
//    }

    @Deprecated
    protected T6(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

//    protected T6(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
//        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
//    }

    public RemoteCall<TransactionReceipt> approve(String _spender, BigInteger _value) {
        final Function function = new Function(
                FUNC_APPROVE,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender),
                        new org.web3j.abi.datatypes.generated.Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> approveAndCall(String _spender, BigInteger _value, byte[] _extraData) {
        final Function function = new Function(
                FUNC_APPROVEANDCALL,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender),
                        new org.web3j.abi.datatypes.generated.Uint256(_value),
                        new org.web3j.abi.datatypes.DynamicBytes(_extraData)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> burn(BigInteger _value) {
        final Function function = new Function(
                FUNC_BURN,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> burnFrom(String _from, BigInteger _value) {
        final Function function = new Function(
                FUNC_BURNFROM,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_from),
                        new org.web3j.abi.datatypes.generated.Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> buy() {
        final Function function = new Function(
                FUNC_BUY,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> freezeAccount(String target, Boolean freeze) {
        final Function function = new Function(
                FUNC_FREEZEACCOUNT,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(target),
                        new org.web3j.abi.datatypes.Bool(freeze)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> mintToken(String target, BigInteger mintedAmount) {
        final Function function = new Function(
                FUNC_MINTTOKEN,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(target),
                        new org.web3j.abi.datatypes.generated.Uint256(mintedAmount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> sell(BigInteger amount) {
        final Function function = new Function(
                FUNC_SELL,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setPrices(BigInteger newSellPrice, BigInteger newBuyPrice) {
        final Function function = new Function(
                FUNC_SETPRICES,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(newSellPrice),
                        new org.web3j.abi.datatypes.generated.Uint256(newBuyPrice)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value) {
        final Function function = new Function(
                FUNC_TRANSFER,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to),
                        new org.web3j.abi.datatypes.generated.Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transferFrom(String _from, String _to, BigInteger _value) {
        final Function function = new Function(
                FUNC_TRANSFERFROM,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_from),
                        new org.web3j.abi.datatypes.Address(_to),
                        new org.web3j.abi.datatypes.generated.Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transferOwnership(String newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(newOwner)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

//    public List<FrozenFundsEventResponse> getFrozenFundsEvents(TransactionReceipt transactionReceipt) {
//        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(FROZENFUNDS_EVENT, transactionReceipt);
//        ArrayList<FrozenFundsEventResponse> responses = new ArrayList<FrozenFundsEventResponse>(valueList.size());
//        for (Contract.EventValuesWithLog eventValues : valueList) {
//            FrozenFundsEventResponse typedResponse = new FrozenFundsEventResponse();
//            typedResponse.log = eventValues.getLog();
//            typedResponse.target = (String) eventValues.getNonIndexedValues().get(0).getValue();
//            typedResponse.frozen = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
//            responses.add(typedResponse);
//        }
//        return responses;
//    }

//    public Flowable<FrozenFundsEventResponse> frozenFundsEventFlowable(EthFilter filter) {
//        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, FrozenFundsEventResponse>() {
//            @Override
//            public FrozenFundsEventResponse apply(Log log) {
//                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(FROZENFUNDS_EVENT, log);
//                FrozenFundsEventResponse typedResponse = new FrozenFundsEventResponse();
//                typedResponse.log = log;
//                typedResponse.target = (String) eventValues.getNonIndexedValues().get(0).getValue();
//                typedResponse.frozen = (Boolean) eventValues.getNonIndexedValues().get(1).getValue();
//                return typedResponse;
//            }
//        });
//    }

//    public Flowable<FrozenFundsEventResponse> frozenFundsEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
//        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
//        filter.addSingleTopic(EventEncoder.encode(FROZENFUNDS_EVENT));
//        return frozenFundsEventFlowable(filter);
//    }

//    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
//        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
//        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
//        for (Contract.EventValuesWithLog eventValues : valueList) {
//            TransferEventResponse typedResponse = new TransferEventResponse();
//            typedResponse.log = eventValues.getLog();
//            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
//            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
//            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
//            responses.add(typedResponse);
//        }
//        return responses;
//    }
//
//    public Flowable<TransferEventResponse> transferEventFlowable(EthFilter filter) {
//        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, TransferEventResponse>() {
//            @Override
//            public TransferEventResponse apply(Log log) {
//                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);
//                TransferEventResponse typedResponse = new TransferEventResponse();
//                typedResponse.log = log;
//                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
//                typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
//                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
//                return typedResponse;
//            }
//        });
//    }

//    public Flowable<TransferEventResponse> transferEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
//        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
//        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
//        return transferEventFlowable(filter);
//    }
//
//    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
//        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
//        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
//        for (Contract.EventValuesWithLog eventValues : valueList) {
//            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
//            typedResponse.log = eventValues.getLog();
//            typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
//            typedResponse._spender = (String) eventValues.getIndexedValues().get(1).getValue();
//            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
//            responses.add(typedResponse);
//        }
//        return responses;
//    }

//    public Flowable<ApprovalEventResponse> approvalEventFlowable(EthFilter filter) {
//        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, ApprovalEventResponse>() {
//            @Override
//            public ApprovalEventResponse apply(Log log) {
//                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(APPROVAL_EVENT, log);
//                ApprovalEventResponse typedResponse = new ApprovalEventResponse();
//                typedResponse.log = log;
//                typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
//                typedResponse._spender = (String) eventValues.getIndexedValues().get(1).getValue();
//                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
//                return typedResponse;
//            }
//        });
//    }
//
//    public Flowable<ApprovalEventResponse> approvalEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
//        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
//        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
//        return approvalEventFlowable(filter);
//    }
//
//    public List<BurnEventResponse> getBurnEvents(TransactionReceipt transactionReceipt) {
//        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(BURN_EVENT, transactionReceipt);
//        ArrayList<BurnEventResponse> responses = new ArrayList<BurnEventResponse>(valueList.size());
//        for (Contract.EventValuesWithLog eventValues : valueList) {
//            BurnEventResponse typedResponse = new BurnEventResponse();
//            typedResponse.log = eventValues.getLog();
//            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
//            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
//            responses.add(typedResponse);
//        }
//        return responses;
//    }
//
//    public Flowable<BurnEventResponse> burnEventFlowable(EthFilter filter) {
//        return web3j.ethLogFlowable(filter).map(new io.reactivex.functions.Function<Log, BurnEventResponse>() {
//            @Override
//            public BurnEventResponse apply(Log log) {
//                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(BURN_EVENT, log);
//                BurnEventResponse typedResponse = new BurnEventResponse();
//                typedResponse.log = log;
//                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
//                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
//                return typedResponse;
//            }
//        });
//    }
//
//    public Flowable<BurnEventResponse> burnEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
//        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
//        filter.addSingleTopic(EventEncoder.encode(BURN_EVENT));
//        return burnEventFlowable(filter);
//    }

    public RemoteCall<BigInteger> allowance(String param0, String param1) {
        final Function function = new Function(FUNC_ALLOWANCE,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0),
                        new org.web3j.abi.datatypes.Address(param1)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> balanceOf(String param0) {
        final Function function = new Function(FUNC_BALANCEOF,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> buyPrice() {
        final Function function = new Function(FUNC_BUYPRICE,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function(FUNC_DECIMALS,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Boolean> frozenAccount(String param0) {
        final Function function = new Function(FUNC_FROZENACCOUNT,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<String> name() {
        final Function function = new Function(FUNC_NAME,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> owner() {
        final Function function = new Function(FUNC_OWNER,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> sellPrice() {
        final Function function = new Function(FUNC_SELLPRICE,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> symbol() {
        final Function function = new Function(FUNC_SYMBOL,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> totalSupply() {
        final Function function = new Function(FUNC_TOTALSUPPLY,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @Deprecated
    public static T6 load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new T6(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static T6 load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new T6(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

//    public static T6 load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
//        return new T6(contractAddress, web3j, credentials, contractGasProvider);
//    }
//
//    public static T6 load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
//        return new T6(contractAddress, web3j, transactionManager, contractGasProvider);
//    }
//
//    public static RemoteCall<T6> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, BigInteger initialSupply, String tokenName, String tokenSymbol) {
//        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(initialSupply),
//                new org.web3j.abi.datatypes.Utf8String(tokenName),
//                new org.web3j.abi.datatypes.Utf8String(tokenSymbol)));
//        return deployRemoteCall(T6.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
//    }
//
//    public static RemoteCall<T6> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, BigInteger initialSupply, String tokenName, String tokenSymbol) {
//        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(initialSupply),
//                new org.web3j.abi.datatypes.Utf8String(tokenName),
//                new org.web3j.abi.datatypes.Utf8String(tokenSymbol)));
//        return deployRemoteCall(T6.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
//    }

    @Deprecated
    public static RemoteCall<T6> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialSupply, String tokenName, String tokenSymbol) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(initialSupply),
                new org.web3j.abi.datatypes.Utf8String(tokenName),
                new org.web3j.abi.datatypes.Utf8String(tokenSymbol)));
        return deployRemoteCall(T6.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<T6> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialSupply, String tokenName, String tokenSymbol) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(initialSupply),
                new org.web3j.abi.datatypes.Utf8String(tokenName),
                new org.web3j.abi.datatypes.Utf8String(tokenSymbol)));
        return deployRemoteCall(T6.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static class FrozenFundsEventResponse {
        public Log log;

        public String target;

        public Boolean frozen;
    }

    public static class TransferEventResponse {
        public Log log;

        public String from;

        public String to;

        public BigInteger value;
    }

    public static class ApprovalEventResponse {
        public Log log;

        public String _owner;

        public String _spender;

        public BigInteger _value;
    }

    public static class BurnEventResponse {
        public Log log;

        public String from;

        public BigInteger value;
    }
}
