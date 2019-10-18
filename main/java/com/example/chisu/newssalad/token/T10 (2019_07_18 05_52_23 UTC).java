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
public class T10 extends Contract {
    private static final String BINARY = "{\n"
            + "\t\"object\": \"60806040526000600360006101000a81548160ff021916908360ff1602179055503480156200002d57600080fd5b5060405162001c4938038062001c49833981018060405260608110156200005357600080fd5b810190808051906020019092919080516401000000008111156200007657600080fd5b828101905060208101848111156200008d57600080fd5b8151856001820283011164010000000082111715620000ab57600080fd5b50509291906020018051640100000000811115620000c857600080fd5b82810190506020810184811115620000df57600080fd5b8151856001820283011164010000000082111715620000fd57600080fd5b5050929190505050828282336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550620f4240600481905550600454600560003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508160019080519060200190620001b0929190620001d6565b508060029080519060200190620001c9929190620001d6565b5050505050505062000285565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106200021957805160ff19168380011785556200024a565b828001600101855582156200024a579182015b82811115620002495782518255916020019190600101906200022c565b5b5090506200025991906200025d565b5090565b6200028291905b808211156200027e57600081600090555060010162000264565b5090565b90565b6119b480620002956000396000f3fe608060405260043610610122576000357c01000000000000000000000000000000000000000000000000000000009004806305fefda71461012757806306fdde031461016c578063095ea7b3146101fc57806318160ddd1461026f57806323b872dd1461029a578063313ce5671461032d57806342966c681461035e5780634b750334146103b157806370a08231146103dc57806379c650681461044157806379cc67901461049c5780638620410b1461050f5780638da5cb5b1461053a57806395d89b4114610591578063a9059cbb14610621578063b414d4b614610694578063cae9ca51146106fd578063d96a094a14610807578063dd62ed3e14610842578063e4849b32146108c7578063e724529c14610902578063f2fde38b1461095f575b600080fd5b34801561013357600080fd5b5061016a6004803603604081101561014a57600080fd5b8101908080359060200190929190803590602001909291905050506109b0565b005b34801561017857600080fd5b50610181610a1d565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156101c15780820151818401526020810190506101a6565b50505050905090810190601f1680156101ee5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561020857600080fd5b506102556004803603604081101561021f57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610abb565b604051808215151515815260200191505060405180910390f35b34801561027b57600080fd5b50610284610bad565b6040518082815260200191505060405180910390f35b3480156102a657600080fd5b50610313600480360360608110156102bd57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610bb3565b604051808215151515815260200191505060405180910390f35b34801561033957600080fd5b50610342610ce0565b604051808260ff1660ff16815260200191505060405180910390f35b34801561036a57600080fd5b506103976004803603602081101561038157600080fd5b8101908080359060200190929190505050610cf3565b604051808215151515815260200191505060405180910390f35b3480156103bd57600080fd5b506103c6610df7565b6040518082815260200191505060405180910390f35b3480156103e857600080fd5b5061042b600480360360208110156103ff57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610dfd565b6040518082815260200191505060405180910390f35b34801561044d57600080fd5b5061049a6004803603604081101561046457600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610e15565b005b3480156104a857600080fd5b506104f5600480360360408110156104bf57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610f9c565b604051808215151515815260200191505060405180910390f35b34801561051b57600080fd5b506105246111b6565b6040518082815260200191505060405180910390f35b34801561054657600080fd5b5061054f6111bc565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b34801561059d57600080fd5b506105a66111e1565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156105e65780820151818401526020810190506105cb565b50505050905090810190601f1680156106135780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561062d57600080fd5b5061067a6004803603604081101561064457600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291908035906020019092919050505061127f565b604051808215151515815260200191505060405180910390f35b3480156106a057600080fd5b506106e3600480360360208110156106b757600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611296565b604051808215151515815260200191505060405180910390f35b34801561070957600080fd5b506107ed6004803603606081101561072057600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803590602001909291908035906020019064010000000081111561076757600080fd5b82018360208201111561077957600080fd5b8035906020019184600183028401116401000000008311171561079b57600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f8201169050808301925050505050505091929192905050506112b6565b604051808215151515815260200191505060405180910390f35b34801561081357600080fd5b506108406004803603602081101561082a57600080fd5b810190808035906020019092919050505061143a565b005b34801561084e57600080fd5b506108b16004803603604081101561086557600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611448565b6040518082815260200191505060405180910390f35b3480156108d357600080fd5b50610900600480360360208110156108ea57600080fd5b810190808035906020019092919050505061146d565b005b34801561090e57600080fd5b5061095d6004803603604081101561092557600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291908035151590602001909291905050506114f6565b005b34801561096b57600080fd5b506109ae6004803603602081101561098257600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061161b565b005b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610a0b57600080fd5b81600781905550806008819055505050565b60018054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610ab35780601f10610a8857610100808354040283529160200191610ab3565b820191906000526020600020905b815481529060010190602001808311610a9657829003601f168201915b505050505081565b600081600660003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925846040518082815260200191505060405180910390a36001905092915050565b60045481565b6000600660008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020548211151515610c4057600080fd5b81600660008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008282540392505081905550610cd58484846116b9565b600190509392505050565b600360009054906101000a900460ff1681565b600081600560003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205410151515610d4357600080fd5b81600560003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008282540392505081905550816004600082825403925050819055503373ffffffffffffffffffffffffffffffffffffffff167fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca5836040518082815260200191505060405180910390a260019050919050565b60075481565b60056020528060005260406000206000915090505481565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610e7057600080fd5b80600560008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008282540192505081905550806004600082825401925050819055503073ffffffffffffffffffffffffffffffffffffffff16600073ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef836040518082815260200191505060405180910390a38173ffffffffffffffffffffffffffffffffffffffff163073ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef836040518082815260200191505060405180910390a35050565b600081600560008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205410151515610fec57600080fd5b600660008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054821115151561107757600080fd5b81600560008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254039250508190555081600660008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008282540392505081905550816004600082825403925050819055508273ffffffffffffffffffffffffffffffffffffffff167fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca5836040518082815260200191505060405180910390a26001905092915050565b60085481565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60028054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156112775780601f1061124c57610100808354040283529160200191611277565b820191906000526020600020905b81548152906001019060200180831161125a57829003601f168201915b505050505081565b600061128c3384846116b9565b6001905092915050565b60096020528060005260406000206000915054906101000a900460ff1681565b6000808490506112c68585610abb565b15611431578073ffffffffffffffffffffffffffffffffffffffff16638f4ffcb1338630876040518563ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018481526020018373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200180602001828103825283818151815260200191508051906020019080838360005b838110156113c05780820151818401526020810190506113a5565b50505050905090810190601f1680156113ed5780820380516001836020036101000a031916815260200191505b5095505050505050600060405180830381600087803b15801561140f57600080fd5b505af1158015611423573d6000803e3d6000fd5b505050506001915050611433565b505b9392505050565b6114453033836116b9565b50565b6006602052816000526040600020602052806000526040600020600091509150505481565b600030905060075482028173ffffffffffffffffffffffffffffffffffffffff16311015151561149c57600080fd5b6114a73330846116b9565b3373ffffffffffffffffffffffffffffffffffffffff166108fc60075484029081150290604051600060405180830381858888f193505050501580156114f1573d6000803e3d6000fd5b505050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561155157600080fd5b80600960008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055507f48335238b4855f35377ed80f164e8c6f3c366e54ac00b96a6402d4a9814a03a58282604051808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001821515151581526020019250505060405180910390a15050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561167657600080fd5b806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff16141515156116f557600080fd5b80600560008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020541015151561174357600080fd5b600560008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205481600560008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205401101515156117d257600080fd5b600960008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff1615151561182b57600080fd5b600960008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff1615151561188457600080fd5b80600560008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254039250508190555080600560008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825401925050819055508173ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef836040518082815260200191505060405180910390a350505056fea165627a7a72305820447556cb657544d4b4a5b9b3c84f1a4ebd186af142bf36efe56f48cb2d282ca60029\",\n"
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
//    ;
//
//    public static final Event TRANSFER_EVENT = new Event("Transfer",
//            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
//    ;
//
//    public static final Event APPROVAL_EVENT = new Event("Approval",
//            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
//    ;
//
//    public static final Event BURN_EVENT = new Event("Burn",
//            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected T10(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

//    protected T10(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
//        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
//    }

    @Deprecated
    protected T10(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

//    protected T10(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
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

    public RemoteCall<TransactionReceipt> buy(BigInteger amount) {
        final Function function = new Function(
                FUNC_BUY,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amount)),
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
//
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
//
//    public Flowable<FrozenFundsEventResponse> frozenFundsEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
//        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
//        filter.addSingleTopic(EventEncoder.encode(FROZENFUNDS_EVENT));
//        return frozenFundsEventFlowable(filter);
//    }
//
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
//
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
//
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
    public static T10 load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new T10(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static T10 load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new T10(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

//    public static T10 load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
//        return new T10(contractAddress, web3j, credentials, contractGasProvider);
//    }
//
//    public static T10 load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
//        return new T10(contractAddress, web3j, transactionManager, contractGasProvider);
//    }
//
//    public static RemoteCall<T10> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, BigInteger initialSupply, String tokenName, String tokenSymbol) {
//        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(initialSupply),
//                new org.web3j.abi.datatypes.Utf8String(tokenName),
//                new org.web3j.abi.datatypes.Utf8String(tokenSymbol)));
//        return deployRemoteCall(T10.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
//    }
//
//    public static RemoteCall<T10> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, BigInteger initialSupply, String tokenName, String tokenSymbol) {
//        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(initialSupply),
//                new org.web3j.abi.datatypes.Utf8String(tokenName),
//                new org.web3j.abi.datatypes.Utf8String(tokenSymbol)));
//        return deployRemoteCall(T10.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
//    }

    @Deprecated
    public static RemoteCall<T10> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialSupply, String tokenName, String tokenSymbol) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(initialSupply),
                new org.web3j.abi.datatypes.Utf8String(tokenName),
                new org.web3j.abi.datatypes.Utf8String(tokenSymbol)));
        return deployRemoteCall(T10.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<T10> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialSupply, String tokenName, String tokenSymbol) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(initialSupply),
                new org.web3j.abi.datatypes.Utf8String(tokenName),
                new org.web3j.abi.datatypes.Utf8String(tokenSymbol)));
        return deployRemoteCall(T10.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
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
