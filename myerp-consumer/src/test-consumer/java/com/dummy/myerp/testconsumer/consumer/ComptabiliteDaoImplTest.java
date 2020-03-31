package com.dummy.myerp.testconsumer.consumer;

import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ComptabiliteDaoImplTest extends ConsumerTestCase {

    private static ComptabiliteDao managerTestCase;
    private static EcritureComptable ecritureComptable;
    private static LigneEcritureComptable ligneEcritureComptable;
    private static SequenceEcritureComptable sequenceEcritureComptable;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        //GIVEN
        managerTestCase = getDaoProxy().getComptabiliteDao();
        ecritureComptable = new EcritureComptable();
        ligneEcritureComptable = new LigneEcritureComptable();
        sequenceEcritureComptable = new SequenceEcritureComptable();

        JournalComptable j = new JournalComptable("AC", "Achat");
        ecritureComptable.setJournal(j);
        ecritureComptable.setDate(new Date());
        ecritureComptable.setLibelle("Libelle");

        CompteComptable c = new CompteComptable(401, "Fournisseurs");
        ligneEcritureComptable.setCompteComptable(c);
        ligneEcritureComptable.setDebit(new BigDecimal(3000));
        ligneEcritureComptable.setCredit(new BigDecimal(0));
        ligneEcritureComptable.setLibelle("Libelle ComptabiliteDaoImplTest");

        sequenceEcritureComptable.setAnnee(2020);
        sequenceEcritureComptable.setDerniereValeur(1);
        sequenceEcritureComptable.setJournalCode("AC");

    }

    @Test
    public void getListCompteComptable() {
        //WHEN
        List<CompteComptable> listCompteComptableExpected = managerTestCase.getListCompteComptable();

        //THEN
        Assert.assertTrue(!listCompteComptableExpected.isEmpty());
    }

    @Test
    public void getListJournalComptable() {
        //WHEN
        List<JournalComptable> listjournalComptableExpected = managerTestCase.getListJournalComptable();

        //THEN
        Assert.assertTrue(!listjournalComptableExpected.isEmpty());
    }

    @Test
    public void getListEcritureComptable() {
        //WHEN
        List<EcritureComptable> listEcritureComptableExpected = managerTestCase.getListEcritureComptable();

        //THEN
        Assert.assertTrue(!listEcritureComptableExpected.isEmpty());
    }

    @Test
    public void getEcritureComptable() throws NotFoundException {
        //WHEN
        EcritureComptable ecriture = managerTestCase.getEcritureComptable(-4);
        List<EcritureComptable> listEcriture = managerTestCase.getListEcritureComptable();
        EcritureComptable ecriture2 = listEcriture.get(3);
        boolean ok = ecriture2.getId().equals(ecriture.getId());

        //THEN
        Assert.assertTrue(ok);
    }

    @Test
    public void getEcritureComptableByRef() throws NotFoundException {
        //WHEN
        EcritureComptable ecriture = managerTestCase.getEcritureComptableByRef("AC-2016/00001");
        List<EcritureComptable> listEcriture = managerTestCase.getListEcritureComptable();
        EcritureComptable ecriture2 = listEcriture.get(0);
        boolean ok = ecriture2.getId().equals(ecriture.getId());

        //THEN
        Assert.assertTrue(ok);
    }

    @Test
    public void getEcritureComptableFail() throws NotFoundException {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(
                "EcritureComptable non trouvée : id=100");
        managerTestCase.getEcritureComptable(100);
    }

    @Test
    public void getEcritureComptableByRefFail() throws NotFoundException {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(
                "EcritureComptable non trouvée : reference=AC-2030/00001");
        managerTestCase.getEcritureComptableByRef("AC-2030/00001");
    }

    @Test
    public void insert_update_delete_EcritureComptable() throws NotFoundException {
        //INSERT
        ecritureComptable.setReference("AC-2020/00005");
        managerTestCase.insertEcritureComptable(ecritureComptable);
        EcritureComptable e2 = managerTestCase.getEcritureComptable(ecritureComptable.getId());
        Boolean ok = e2.getReference().equals(ecritureComptable.getReference());

        Assert.assertTrue(ok);

        //UPDATE
        ecritureComptable.setLibelle("update");
        managerTestCase.updateEcritureComptable(ecritureComptable);
        e2 = managerTestCase.getEcritureComptable(ecritureComptable.getId());
        ok = e2.getLibelle().equals(ecritureComptable.getLibelle());
        String okkkk = "oj";

        Assert.assertTrue(ok);

        //DELETE
        managerTestCase.deleteEcritureComptable(ecritureComptable.getId());
    }

    @Test
    public void insert_update_delete_SequenceEcritureComptable() {
        managerTestCase.insertSequenceEcritureComptable(sequenceEcritureComptable);

        sequenceEcritureComptable.setDerniereValeur(20);
        managerTestCase.updateSequenceEcritureComptable(sequenceEcritureComptable);

        managerTestCase.deleteSequenceEcritureComptable(sequenceEcritureComptable);
    }
}