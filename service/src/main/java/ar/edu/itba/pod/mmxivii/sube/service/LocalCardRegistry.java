package ar.edu.itba.pod.mmxivii.sube.service;

import ar.edu.itba.pod.mmxivii.sube.common.Card;
import ar.edu.itba.pod.mmxivii.sube.common.CardRegistry;

import javax.annotation.Nonnull;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.util.concurrent.ConcurrentHashMap;

import static ar.edu.itba.pod.mmxivii.sube.common.Utils.*;
import static ar.edu.itba.pod.mmxivii.sube.common.CardRegistry.*;

/**
 * Created by scamisay on 10/23/14.
 */
public class LocalCardRegistry {

    private final ConcurrentHashMap<UID, Double> balances = new ConcurrentHashMap<UID, Double>();
    private CardRegistry cardRegistry;

    public LocalCardRegistry(CardRegistry cardRegistry) {
        this.cardRegistry = cardRegistry;
    }

    public double addCardOperation(@Nonnull UID id, @Nonnull String description, double amount)
    {
        assertAmount(amount);
        assertText(description);

        Double result = balances.get(checkNotNull(id));
        if(result == null){
            try {
                result = cardRegistry.getCardBalance(id);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (result == null) return CARD_NOT_FOUND;

        result = result + amount;
        if (result < 0 || result > MAX_BALANCE) return OPERATION_NOT_PERMITTED_BY_BALANCE;
        balances.put(id, result);
        return result;
    }

    public ConcurrentHashMap<UID, Double> synchronizeToSCardRegistry(CardRegistry cardRegistry) throws RemoteException {
        for(UID aUID : balances.keySet()){
            cardRegistry.addCardOperation(aUID, "charge", balances.get(aUID));
        }
        return balances;
    }

    public void clearBalance(){
        balances.clear();
    }
}
