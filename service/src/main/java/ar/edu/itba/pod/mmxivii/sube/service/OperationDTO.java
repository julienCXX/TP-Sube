package ar.edu.itba.pod.mmxivii.sube.service;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.rmi.server.UID;

public class OperationDTO implements Serializable{

    @Nonnull UID id;

    @Nonnull String description;

    double amount;

    String operationId;

    boolean isReady;

    public OperationDTO(@Nonnull UID id, @Nonnull String description, double amount, String operationId) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.operationId = operationId;
        this.isReady = false;
    }

    public static OperationDTO buildFinishedOperationMessage(OperationDTO operationDTO){
        operationDTO.isReady = true;
        return operationDTO;
    }

    public boolean isOperationFinished(){
        return isReady;
    }

}
