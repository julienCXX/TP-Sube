package ar.edu.itba.pod.mmxivii.sube.service;

import ar.edu.itba.pod.mmxivii.sube.common.Card;
import ar.edu.itba.pod.mmxivii.sube.common.CardRegistry;
import ar.edu.itba.pod.mmxivii.sube.common.Utils;

import javax.annotation.Nonnull;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import static ar.edu.itba.pod.mmxivii.sube.common.Utils.*;
import static ar.edu.itba.pod.mmxivii.sube.common.CardRegistry.*;

/**
 * Created by scamisay on 10/23/14.
 */
public class LocalCardRegistry {

    private final ConcurrentHashMap<UID, Double> balances = new ConcurrentHashMap<UID, Double>();
    private final Map<String, OperationDTO> pendingOperations = new ConcurrentHashMap<String, OperationDTO>();
    private final Map<String, OperationDTO> finishedOperations = new ConcurrentHashMap<String, OperationDTO>();
    private CardRegistry cardRegistry;

    private String baseId;
    private Integer incrementId;

    public LocalCardRegistry(CardRegistry cardRegistry) {
        this.cardRegistry = cardRegistry;

        startId();
    }

    private void startId() {
        baseId = UUID.randomUUID().toString();
        incrementId = 0;
    }

    public double getCardBalance(@Nonnull UID id)
    {
        final Double result = balances.get(checkNotNull(id));
        return result == null ? CARD_NOT_FOUND : result;
    }

    public OperationDTO buildOperation(@Nonnull UID id, @Nonnull String description, double amount){
        return new OperationDTO(id,description,amount,buildNextId());
    }

    private String buildNextId() {
        String currentId = String.format("%s--%d", baseId, incrementId);
        incrementId++;
        return currentId;
    }

    public double addCardOperation(OperationDTO dto)
    {
        assertAmount(dto.amount);
        assertText(dto.description);

        Double result = balances.get(checkNotNull(dto.id));
        if(result == null){
            try {
                result = cardRegistry.getCardBalance(dto.id);
            } catch (RemoteException e) {
                reLookUpCardRegistry();
                try {
                    result = cardRegistry.getCardBalance(dto.id);
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (result == null) return CARD_NOT_FOUND;

        result = result + dto.amount;
        if (result < 0 || result > MAX_BALANCE) return OPERATION_NOT_PERMITTED_BY_BALANCE;
        balances.put(dto.id, result);
        return result;
    }

    public ConcurrentHashMap<UID, Double> synchronizeToSCardRegistry(CardRegistry cardRegistry) throws RemoteException {
        for(String operationId : pendingOperations.keySet()){
            OperationDTO pendingOperation = pendingOperations.get(operationId);
            try{
                cardRegistry.addCardOperation(pendingOperation.id, "charge", pendingOperation.amount);
System.out.println(String.format("Sinchronization: %s charge",Double.toString(pendingOperation.amount)));
                finishedOperations.put(pendingOperation.operationId, pendingOperation);
                pendingOperations.remove(pendingOperation.operationId);
            }catch (Exception e){

                try {
                    ((CardRegistry)Utils.rmiRegistry.lookup(CARD_REGISTRY_BIND))
                            .addCardOperation(pendingOperation.id, "charge", pendingOperation.amount);
            System.out.println(String.format("Re-Sinchronization: %s charge",Double.toString(pendingOperation.amount)));
                    finishedOperations.put(pendingOperation.operationId, pendingOperation);
                    pendingOperations.remove(pendingOperation.operationId);
                } catch (NotBoundException e1) {
                    System.out.println("Server is down");
                    break;
                }
                //cardRegistry = (CardRegistry)rmiRegistry.lookup("cardRegistry");

                //cardRegistry.addCardOperation(pendingOperation.id, "charge", pendingOperation.amount);

            }
        }

        return balances;
    }

    private void reLookUpCardRegistry() {
        try {
            cardRegistry = (CardRegistry)Utils.lookupObject(CARD_REGISTRY_BIND);
            System.out.println("Me volvi a reconectar al cardRegistry");
        } catch (NotBoundException e1) {
            System.out.println("No se pudo encontrar al cardRegistry");
        }
    }

    public void clearBalance(){
        balances.clear();
    }

    public void addOpertaion(OperationDTO dto) {
        pendingOperations.put(dto.operationId, dto);
    }

    public List<OperationDTO> listFinishedOperations() {
        List<OperationDTO> aList = new ArrayList<>();
        for(String operationId : finishedOperations.keySet()){
            aList.add(finishedOperations.get(operationId));
        }
        return aList;
    }

    public void removeFromPendings(OperationDTO dto) {
        pendingOperations.remove(dto.operationId);
    }

    public void clearFinishedOperation(String operationId) {
        finishedOperations.remove(operationId);
    }
}
