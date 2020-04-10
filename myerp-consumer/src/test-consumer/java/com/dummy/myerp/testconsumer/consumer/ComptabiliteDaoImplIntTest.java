package com.dummy.myerp.testconsumer.consumer;

import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;

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

        sequenceEcritureComptable.setAnnee(2020);
        sequenceEcritureComptable.setDerniereValeur(1);
        sequenceEcritureComptable.setJournalCode("AC");

    }

    @Test
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
    public void getEcritureComptable() throws NotFoundException {
        //WHEN
        EcritureComptable ecriture = managerTestCase.getEcritureComptable(-4);
        List<EcritureComptable> listEcriture = managerTestCase.getListEcritureComptable();
        EcritureComptable ecriture2 = listEcriture.get(3);
        boolean ok = ecriture2.getId().equals(ecriture.getId());

        //THEN
        Assert.assertTrue(ok);
        Assert.assertEquals(ecriture.getJournal().getCode(), "VE");
        Assert.assertEquals(ecriture.getReference(), "VE-2016/00004");
        Assert.assertEquals(ecriture.getDate().toString(), "2016-12-28");
        Assert.assertEquals(ecriture.getLibelle(), "TMA Appli Yyy");
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
        Assert.assertEquals(ecriture.getJournal().getCode(), "AC");
        Assert.assertEquals(ecriture.getReference(), "AC-2016/00001");
        Assert.assertEquals(ecriture.getDate().toString(), "2016-12-31");
        Assert.assertEquals(ecriture.getLibelle(), "Cartouches d’imprimante");
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
    public void insertEcritureComptable() throws NotFoundException {
        //WHEN
        ecritureComptable.setReference("AC-2020/00005");
        managerTestCase.insertEcritureComptable(ecritureComptable);
        EcritureComptable e2 = managerTestCase.getEcritureComptable(ecritureComptable.getId());
        Boolean ok = e2.getReference().equals(ecritureComptable.getReference());

        //THEN
        Assert.assertTrue(ok);
        Assert.assertEquals(ecritureComptable.getJournal().getCode(), "AC");
        Assert.assertEquals(ecritureComptable.getReference(), "AC-2020/00005");
        Assert.assertEquals(ecritureComptable.getLibelle(), "Libelle");
        managerTestCase.deleteEcritureComptable(ecritureComptable.getId());
    }

    @Test
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
        Assert.assertEquals(ecritureComptable.getLibelle(), "update");
        managerTestCase.deleteEcritureComptable(ecritureComptable.getId());
    }

    @Test
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
    public void insertSequenceEcritureComptable() {
        //WHEN
        managerMock.insertSequenceEcritureComptable(sequenceEcritureComptable);

        //THEN
        Mockito.verify(managerMock, times(1)).insertSequenceEcritureComptable(sequenceEcritureComptable);
        managerMock.deleteSequenceEcritureComptable(sequenceEcritureComptable);
        Mockito.verify(managerMock, times(1)).deleteSequenceEcritureComptable(sequenceEcritureComptable);
    }

    @Test
    public void updateSequenceEcritureComptable() {
        //GIVEN
        sequenceEcritureComptable.setDerniereValeur(20);

        //WHEN
        managerMock.updateSequenceEcritureComptable(sequenceEcritureComptable);

        //THEN
        Assert.assertEquals(sequenceEcritureComptable.getDerniereValeur().intValue(), 20);
        Mockito.verify(managerMock, times(1)).updateSequenceEcritureComptable(sequenceEcritureComptable);
        managerMock.deleteSequenceEcritureComptable(sequenceEcritureComptable);
        Mockito.verify(managerMock, times(1)).deleteSequenceEcritureComptable(sequenceEcritureComptable);
    }

    @Test
    public void deleteSequenceEcritureComptable() throws NotFoundException {
        //GIVEN
        managerMock.insertSequenceEcritureComptable(sequenceEcritureComptable);

        //WHEN
        managerMock.deleteSequenceEcritureComptable(sequenceEcritureComptable);

        //THEN
        Mockito.verify(managerMock, times(1)).insertSequenceEcritureComptable(sequenceEcritureComptable);
        Mockito.verify(managerMock, times(1)).deleteSequenceEcritureComptable(sequenceEcritureComptable);
    }
}