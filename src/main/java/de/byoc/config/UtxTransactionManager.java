package de.byoc.config;

import org.axonframework.common.transaction.Transaction;
import org.axonframework.common.transaction.TransactionManager;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

public class UtxTransactionManager implements TransactionManager {
    private UserTransaction utx;

    public UtxTransactionManager(UserTransaction utx) {
        this.utx = utx;
    }

    @Override
    public Transaction startTransaction() {
        try {
            if (utx.getStatus() == Status.STATUS_ACTIVE) {
                return new Transaction() {
                    @Override
                    public void commit() {
                        // nopes
                    }

                    @Override
                    public void rollback() {
                        try {
                            utx.setRollbackOnly();
                        } catch (SystemException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
            }
            utx.begin();
            return new UtxTransaction(utx);
        } catch (NotSupportedException | SystemException e) {
            throw new RuntimeException(e);
        }
    }

    private class UtxTransaction implements Transaction {
        private final UserTransaction utx;

        public UtxTransaction(UserTransaction utx) {
            this.utx = utx;
        }

        @Override
        public void commit() {
            try {
                utx.commit();
            } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SystemException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void rollback() {
            try {
                utx.rollback();
            } catch (SystemException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
