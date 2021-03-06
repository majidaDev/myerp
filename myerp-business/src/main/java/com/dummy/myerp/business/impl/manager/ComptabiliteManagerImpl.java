package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.dummy.myerp.model.bean.comptabilite.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.TransactionStatus;
import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;


/**
 * Comptabilite manager implementation.
 */
public class ComptabiliteManagerImpl extends AbstractBusinessManager implements ComptabiliteManager {

    // ==================== Attributs ====================


    // ==================== Constructeurs ====================
    /**
     * Instantiates a new Comptabilite manager.
     */
    public ComptabiliteManagerImpl() {
    }


    // ==================== Getters/Setters ====================
    @Override
    public List<CompteComptable> getListCompteComptable() {
        return getDaoProxy().getComptabiliteDao().getListCompteComptable();
    }


    @Override
    public List<JournalComptable> getListJournalComptable() {
        return getDaoProxy().getComptabiliteDao().getListJournalComptable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EcritureComptable> getListEcritureComptable() {
        return getDaoProxy().getComptabiliteDao().getListEcritureComptable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SequenceEcritureComptable> getListSequenceEcritureComptable() {
        return getDaoProxy().getComptabiliteDao().getListSequenceEcritureComptable();
    }

    /**
     * {@inheritDoc}
     */
    // TODO à tester
    @Override
    public synchronized void addReference(EcritureComptable pEcritureComptable) throws FunctionalException {
        // TODO à implémenter
        // Bien se réferer à la JavaDoc de cette méthode !
        Date datePEcritureComptable = pEcritureComptable.getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(datePEcritureComptable);
        String finalRef;
        int newDerniereValeur = 0;

        /*1.  Remonter depuis la persitance la dernière valeur de la séquence du journal pour l'année de l'écriture
              (table sequence_ecriture_comptable) */
        Stream<SequenceEcritureComptable> streamSeq = pEcritureComptable.getJournal().getSequenceEcritureComptables().stream()
                .filter(sequence -> sequence.getAnnee() == calendar.get(Calendar.YEAR));
        List<SequenceEcritureComptable> listSeq = streamSeq.collect(Collectors.toList());

        if (!listSeq.isEmpty()) {
            for (SequenceEcritureComptable sequenceEcritureComptable : listSeq) {
                /*  2.  * S'il n'y a aucun enregistrement pour le journal pour l'année concernée :
                          Utiliser le numéro 1. */
                int pDerniereValeur = sequenceEcritureComptable.getDerniereValeur();
                newDerniereValeur = derniereValeurSequenceEcritureComptable(pDerniereValeur);

                /* 4.  Enregistrer (insert/update) la valeur de la séquence en persitance (table sequence_ecriture_comptable) */
                sequenceEcritureComptable.setDerniereValeur(newDerniereValeur);
                try {
                    updateSequenceEcritureComptable(sequenceEcritureComptable);
                } catch (FunctionalException e) {
                    e.printStackTrace();
                }
            }
        } else {
            /*  2.  * S'il n'y a aucun enregistrement pour le journal pour l'année concernée :
                      Utiliser le numéro 1. */
            newDerniereValeur = 1;

            /* 4.  Enregistrer (insert/update) la valeur de la séquence en persitance (table sequence_ecriture_comptable) */
            SequenceEcritureComptable newSeq = new SequenceEcritureComptable();
            newSeq.setJournalCode(pEcritureComptable.getJournal().getCode());
            newSeq.setAnnee(calendar.get(Calendar.YEAR));
            newSeq.setDerniereValeur(1);
            try {
                insertSequenceEcritureComptable(newSeq);
            } catch (FunctionalException e) {
                e.printStackTrace();
            }
        }

        // 3.  Mettre à jour la référence de l'écriture avec la référence calculée (RG_Compta_5)
        finalRef = getNewReference(pEcritureComptable.getJournal().getCode(),
                Integer.toString(calendar.get(Calendar.YEAR)), String.format("%05d", newDerniereValeur));
        pEcritureComptable.setReference(finalRef);
    }

    /**
     * {@inheritDoc}
     */
    // TODO à tester
    @Override
    public void checkEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptableUnit(pEcritureComptable);
        this.checkEcritureComptableContext(pEcritureComptable);
    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion unitaires,
     * c'est à dire indépendemment du contexte (unicité de la référence, exercie comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    // TODO tests à compléter
    protected void checkEcritureComptableUnit(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== Vérification des contraintes unitaires sur les attributs de l'écriture
        Set<ConstraintViolation<EcritureComptable>> vViolations = getConstraintValidator().validate(pEcritureComptable);
        if (!vViolations.isEmpty()) {
            throw new FunctionalException("L'écriture comptable ne respecte pas les règles de gestion.",
                    new ConstraintViolationException(
                            "L'écriture comptable ne respecte pas les contraintes de validation",
                            vViolations));
        }

        // ===== RG_Compta_2 : Pour qu'une écriture comptable soit valide, elle doit être équilibrée
        if (!pEcritureComptable.isEquilibree()) {
            throw new FunctionalException("L'écriture comptable n'est pas équilibrée.");
        }

        // ===== RG_Compta_3 : une écriture comptable doit avoir au moins 2 lignes d'écriture (1 au débit, 1 au crédit)
        int vNbrCredit = 0;
        int vNbrDebit = 0;
        for (LigneEcritureComptable vLigneEcritureComptable : pEcritureComptable.getListLigneEcriture()) {
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getCredit(),
                    BigDecimal.ZERO)) != 0) {
                vNbrCredit++;
            }
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getDebit(),
                    BigDecimal.ZERO)) != 0) {
                vNbrDebit++;
            }
        }
        // On test le nombre de lignes car si l'écriture à une seule ligne
        //      avec un montant au débit et un montant au crédit ce n'est pas valable
        if (pEcritureComptable.getListLigneEcriture().size() < 2
                || vNbrCredit < 1
                || vNbrDebit < 1) {
            throw new FunctionalException(
                    "L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");
        }

        // TODO ===== RG_Compta_5 : Format et contenu de la référence
        // vérifier que l'année dans la référence correspond bien à la date de l'écriture, idem pour le code journal...
        Date datePEcritureComptable = pEcritureComptable.getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(datePEcritureComptable);
        String pAnnee  = Integer.toString(calendar.get(Calendar.YEAR));
        String pAnneRef = StringUtils.substring(pEcritureComptable.getReference(), 3, 7);
        String pCode = pEcritureComptable.getJournal().getCode();
        String pCodeRef = StringUtils.substring(pEcritureComptable.getReference(), 0, 2);
        if (!pCode.equals(pCodeRef)) {
            throw new FunctionalException("Le code journal dans la référence ne correspond pas au code journal de l'écriture.");
        } else if (!pAnnee.equals(pAnneRef)) {
            throw new FunctionalException("L'année dans la référence ne correspond pas à l'année de l'écriture.");
        }

    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion liées au contexte
     * (unicité de la référence, année comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    protected void checkEcritureComptableContext(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== RG_Compta_6 : La référence d'une écriture comptable doit être unique
        if (StringUtils.isNoneEmpty(pEcritureComptable.getReference())) {
            try {
                // Recherche d'une écriture ayant la même référence
                EcritureComptable vECRef = getDaoProxy().getComptabiliteDao().getEcritureComptableByRef(
                        pEcritureComptable.getReference());

                /* Si l'écriture à vérifier est une nouvelle écriture (id == null),
                   ou si elle ne correspond pas à l'écriture trouvée (id != idECRef),
                   c'est qu'il y a déjà une autre écriture avec la même référence */
                if (pEcritureComptable.getId() == null
                        || !pEcritureComptable.getId().equals(vECRef.getId())) {
                    throw new FunctionalException("Une autre écriture comptable existe déjà avec la même référence.");
                }
            } catch (NotFoundException vEx) {
                // Dans ce cas, c'est bon, ça veut dire qu'on n'a aucune autre écriture avec la même référence.
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptable(pEcritureComptable);
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().insertEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptable(pEcritureComptable);
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().updateEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEcritureComptable(Integer pId) {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().deleteEcritureComptable(pId);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertSequenceEcritureComptable(SequenceEcritureComptable pSequenceEcritureComptable) throws FunctionalException {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().insertSequenceEcritureComptable(pSequenceEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateSequenceEcritureComptable(SequenceEcritureComptable pSequenceEcritureComptable) throws FunctionalException {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().updateSequenceEcritureComptable(pSequenceEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteSequenceEcritureComptable(SequenceEcritureComptable pSequenceEcritureComptable) {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().deleteSequenceEcritureComptable(pSequenceEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    public int derniereValeurSequenceEcritureComptable(int derniereValeur) {
        int newDerniereValeur;

        if (derniereValeur == 0) {
            newDerniereValeur = 1;
        } else {
            newDerniereValeur = derniereValeur + 1;
        }

        return newDerniereValeur;
    }

    public String getNewReference(String ref, String year, String seqDerniereValeur) {
        String newRef = ref + "-" + year + "/" + seqDerniereValeur;
        return newRef;
    }
}