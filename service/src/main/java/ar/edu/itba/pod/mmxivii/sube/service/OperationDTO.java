package ar.edu.itba.pod.mmxivii.sube.service;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.rmi.server.UID;

public class OperationDTO implements Serializable{

    @Nonnull UID id;

    @Nonnull String description;

    double amount;

    public OperationDTO(@Nonnull UID id, @Nonnull String description, double amount) {
        this.id = id;
        this.description = description;
        this.amount = amount;
    }

    private OperationDTO(String status){
        this.description = status;
    }

    public static OperationDTO buildClearLocalRegistryMessage(){
        return new OperationDTO(CLEAR_LOCAL_CARDREGISTRY);
    }

    public static final String CLEAR_LOCAL_CARDREGISTRY = "CLEAR_LOCAL_CARDREGISTRY";

    public boolean isAMessage() {
        return description!= null && !description.isEmpty() && id == null;
    }

    public boolean isClearLocalRegistry() {
        return description!= null && description.equals(CLEAR_LOCAL_CARDREGISTRY);
    }
}
