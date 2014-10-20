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
}
