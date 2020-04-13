package com.dummy.myerp.testconsumer.consumer;

import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class ComptabiliteDaoImplIntTest extends ConsumerTestCase {

    private static ComptabiliteDao managerTestCase;
    private static EcritureComptable ecritureComptable;
    private static LigneEcritureComptable ligneEcritureComptable;
    private static SequenceEcritureComptable sequenceEcritureComptable;

    @Mock
    ComptabiliteDao managerMock = mock(ComptabiliteDao.class);

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
    }

    @Test
    @Transactional
    public void getListCompteComptable() {
        //WHEN
        List<CompteComptable> listCompteComptableExpected = managerTestCase.getListCompteComptable();
        CompteComptable compteComptableToTest = listCompteComptableExpected.get(0);
        //THEN
        Assert.assertFalse(listCompteComptableExpected.isEmpty());
        Assert.assertEquals(compteComptableToTest.getNumero().intValue(), 401);
        Assert.assertEquals(compteComptableToTest.getLibelle(), "Fournisseurs");
    }

    @Test
    @Transactional
    public void getListJournalComptable() {
        //WHEN
        List<JournalComptable> listjournalComptableExpected = managerTestCase.getListJournalComptable();
        JournalComptable journalComptableToTest = listjournalComptableExpected.get(0);
        //THEN
        Assert.assertFalse(listjournalComptableExpected.isEmpty());
        Assert.assertEquals(journalComptableToTest.getCode(), "AC");
        Assert.assertEquals(journalComptableToTest.getLibelle(), "Achat");
    }

    @Test
    @Transactional
    public void getListEcritureComptable() {
        //WHEN
        List<EcritureComptable> listEcritureComptableExpected = managerTestCase.getListEcritureComptable();
        EcritureComptable ecritureComptableToTest = listEcritureComptableExpected.get(0);
        //THEN
        Assert.assertFalse(listEcritureComptableExpected.isEmpty());
        Assert.assertEquals(ecritureComptableToTest.getJournal().getCode(), "AC");
        Assert.assertEquals(ecritureComptableToTest.getReference(), "AC-2016/00001");
        Assert.assertEquals(ecritureComptableToTest.getDate().toString(), "2016-12-31");
        Assert.assertEquals(ecritureComptableToTest.getLibelle(), "Cartouches d’imprimante");
    }

    @Test
    @Transactional
    public void getEcritureComptable() throws NotFoundException {
        //WHEN
        EcritureComptable ecriture = managerTestCase.getEcritureComptable(-4);
        List<EcritureComptable> listEcriture = managerTestCase.getListEcritureComptable();
        EcritureComptable ecriture2 = listEcriture.get(3);
        boolean ok = ecriture2.getId().equals(ecriture.getId());
        //THEN
        Assert.assertTrue(ok);
        Assert.assertEquals(ecriture2.getJournal().getCode(), "VE");
        Assert.assertEquals(ecriture2.getReference(), "VE-2016/00004");
        Assert.assertEquals(ecriture2.getDate().toString(), "2016-12-28");
        Assert.assertEquals(ecriture2.getLibelle(), "TMA Appli Yyy");
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
        Assert.assertEquals(ecriture2.getJournal().getCode(), "AC");
        Assert.assertEquals(ecriture2.getReference(), "AC-2016/00001");
        Assert.assertEquals(ecriture2.getDate().toString(), "2016-12-31");
        Assert.assertEquals(ecriture2.getLibelle(), "Cartouches d’imprimante");
    }

    @Test
    @Transactional
    public void getEcritureComptableFail() throws NotFoundException {
        //THEN
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(
                "EcritureComptable non trouvée : id=100");
        //GIVEN
        managerTestCase.getEcritureComptable(100);
    }

    @Test
    @Transactional
    public void getEcritureComptableByRefFail() throws NotFoundException {
        //THEN
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(
                "EcritureComptable non trouvée : reference=AC-2030/00001");
        //GIVEN
        managerTestCase.getEcritureComptableByRef("AC-2030/00001");
    }

    @Test
    @Transactional
    public void insertEcritureComptable() throws NotFoundException {
        //WHEN
        ecritureComptable.setReference("AC-2020/00005");
        managerTestCase.insertEcritureComptable(ecritureComptable);
        EcritureComptable e2 = managerTestCase.getEcritureComptable(ecritureComptable.getId());
        Boolean ok = e2.getReference().equals(ecritureComptable.getReference());
        //THEN
        Assert.assertTrue(ok);
        Assert.assertEquals(e2.getJournal().getCode(), "AC");
        Assert.assertEquals(e2.getReference(), "AC-2020/00005");
        Assert.assertEquals(e2.getLibelle(), "Libelle");
        managerTestCase.deleteEcritureComptable(e2.getId());
    }

    @Test
    @Transactional
    public void updateEcritureComptable() throws NotFoundException {
        //GIVEN
        ecritureComptable.setReference("AC-2020/00005");
        managerTestCase.insertEcritureComptable(ecritureComptable);
        //WHEN
        ecritureComptable.setLibelle("update");
        managerTestCase.updateEcritureComptable(ecritureComptable);
        EcritureComptable e2 = managerTestCase.getEcritureComptable(ecritureComptable.getId());
        Boolean ok = e2.getLibelle().equals(ecritureComptable.getLibelle());
        //THEN
        Assert.assertTrue(ok);
        Assert.assertEquals(e2.getLibelle(), "update");
        managerTestCase.deleteEcritureComptable(e2.getId());
    }

    @Test
    @Transactional
    public void deleteEcritureComptable() throws NotFoundException {
        //GIVEN
        ecritureComptable.setReference("AC-2020/00005");
        managerTestCase.insertEcritureComptable(ecritureComptable);
        //WHEN
        managerTestCase.deleteEcritureComptable(ecritureComptable.getId());
        List<EcritureComptable> listEcritureComptableExpected = managerTestCase.getListEcritureComptable();
        //THEN
        Assert.assertFalse(listEcritureComptableExpected.contains(ecritureComptable));
    }

    @Test
    @Transactional
    public void insertSequenceEcritureComptable() {
        //GIVEN
        sequenceEcritureComptable.setAnnee(2020);
        sequenceEcritureComptable.setDerniereValeur(1);
        sequenceEcritureComptable.setJournalCode("AC");
        //WHEN
        managerMock.insertSequenceEcritureComptable(sequenceEcritureComptable);
        managerTestCase.insertSequenceEcritureComptable(sequenceEcritureComptable);
        //THEN
        Mockito.verify(managerMock, times(1)).insertSequenceEcritureComptable(sequenceEcritureComptable);
        List<SequenceEcritureComptable> listSECExpected = managerTestCase.getListSequenceEcritureComptable();
        SequenceEcritureComptable sECExpected = listSECExpected.get(listSECExpected.size()-1);
        int lastNumber = sECExpected.getDerniereValeur();
        int annee = sECExpected.getAnnee();
        Assert.assertEquals(sECExpected.getJournalCode(), "AC");
        Assert.assertEquals(annee, 2020);
        Assert.assertEquals(lastNumber, 1);
        managerTestCase.deleteSequenceEcritureComptable(sequenceEcritureComptable);
    }

    @Test
    public void updateSequenceEcritureComptable() {
        //GIVEN
        sequenceEcritureComptable.setAnnee(2021);
        sequenceEcritureComptable.setDerniereValeur(1);
        sequenceEcritureComptable.setJournalCode("AC");
        managerMock.insertSequenceEcritureComptable(sequenceEcritureComptable);
        managerTestCase.insertSequenceEcritureComptable(sequenceEcritureComptable);
        sequenceEcritureComptable.setDerniereValeur(2);
        //WHEN
        managerMock.updateSequenceEcritureComptable(sequenceEcritureComptable);
        managerTestCase.updateSequenceEcritureComptable(sequenceEcritureComptable);
        //THEN
        Mockito.verify(managerMock, times(1)).updateSequenceEcritureComptable(sequenceEcritureComptable);
        List<SequenceEcritureComptable> listSECExpected = managerTestCase.getListSequenceEcritureComptable();
        SequenceEcritureComptable sECExpected = listSECExpected.get(listSECExpected.size()-1);
        int lastNumber = sECExpected.getDerniereValeur();
        int annee = sECExpected.getAnnee();
        Assert.assertEquals(sECExpected.getJournalCode(), "AC");
        Assert.assertEquals(annee, 2021);
        Assert.assertEquals(lastNumber, 2);
        managerTestCase.deleteSequenceEcritureComptable(sequenceEcritureComptable);
    }

    @Test
    @Transactional
    public void deleteSequenceEcritureComptable() throws NotFoundException {
        //GIVEN
        sequenceEcritureComptable.setAnnee(2022);
        sequenceEcritureComptable.setDerniereValeur(1);
        sequenceEcritureComptable.setJournalCode("BQ");
        managerMock.insertSequenceEcritureComptable(sequenceEcritureComptable);
        managerTestCase.insertSequenceEcritureComptable(sequenceEcritureComptable);
        //WHEN
        managerMock.deleteSequenceEcritureComptable(sequenceEcritureComptable);
        managerTestCase.deleteSequenceEcritureComptable(sequenceEcritureComptable);
        //THEN
        Mockito.verify(managerMock, times(1)).insertSequenceEcritureComptable(sequenceEcritureComptable);
        Mockito.verify(managerMock, times(1)).deleteSequenceEcritureComptable(sequenceEcritureComptable);
        List<SequenceEcritureComptable> listSECExpected = managerTestCase.getListSequenceEcritureComptable();
        SequenceEcritureComptable sECExpected = listSECExpected.get(listSECExpected.size()-1);
        int lastNumber = sECExpected.getDerniereValeur();
        int annee = sECExpected.getAnnee();
        Assert.assertNotEquals(sECExpected.getJournalCode(), "BQ");
        Assert.assertNotEquals(annee, 2022);
        Assert.assertNotEquals(lastNumber, 1);
    }
}