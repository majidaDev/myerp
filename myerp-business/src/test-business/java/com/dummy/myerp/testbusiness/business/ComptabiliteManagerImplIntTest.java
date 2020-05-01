package com.dummy.myerp.testbusiness.business;

import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class ComptabiliteManagerImplIntTest extends BusinessTestCase {

    private static ComptabiliteManager managerTestCase;
    private static EcritureComptable vEcritureComptable;
    private static List<SequenceEcritureComptable> listSeqExpected = new ArrayList<>();
    private static SequenceEcritureComptable sequenceEcritureComptable;
    private static SequenceEcritureComptable sequenceEcritureComptable2;
    private static Date dateNow;
    private static Calendar calendar;

    @Mock
    public ComptabiliteManager managerMock = mock(ComptabiliteManager.class);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @BeforeClass
    public static void setUpBC() {
        managerTestCase = getBusinessProxy().getComptabiliteManager(); // on appel BusinessProxy qui va faire appel à BusinessProxy dans application context qui lui va faire appel à BusinessProxyIMPL pour nous retourner comptabiliteManager
        dateNow = new Date();
        calendar = Calendar.getInstance();
        calendar.setTime(dateNow);
    }

    @Before
    public void setUp() {
        //GIVEN
        vEcritureComptable = new EcritureComptable();
        sequenceEcritureComptable = new SequenceEcritureComptable("AC", 2020, 1);
        listSeqExpected.add(sequenceEcritureComptable);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat",listSeqExpected));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));

        sequenceEcritureComptable2 = new SequenceEcritureComptable();
    }


    /*-- UNIT TEST : addReference --*/
    @Test
    @Transactional
    public void checkAddReferenceIfAlreadyInDB() throws Exception {
        //GIVEN
        sequenceEcritureComptable = new SequenceEcritureComptable("AC", 2016, 40);
        listSeqExpected.add(sequenceEcritureComptable);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat",listSeqExpected));
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 01, 01, 10, 59, 59);
        Date newDate = calendar.getTime();
        vEcritureComptable.setDate(newDate);
        vEcritureComptable.setReference("AC-2016/00040");
        //WHEN
        managerTestCase.addReference(vEcritureComptable);
        //THEN
        List<SequenceEcritureComptable> sECList = managerTestCase.getListSequenceEcritureComptable();
        SequenceEcritureComptable sECToTest = sECList.get(sECList.size()-1);
        int lastNumber = sECToTest.getDerniereValeur();
        int annee = sECToTest.getAnnee();
        Assert.assertEquals(sECToTest.getJournalCode(), "AC");
        Assert.assertEquals(annee, 2016);
        Assert.assertEquals(lastNumber, 41);
    }

    @Test
    @Transactional
    public void checkAddReferenceIsNotInDB() throws Exception {
        //GIVEN
        EcritureComptable vEcritureComptable2 = new EcritureComptable();
        vEcritureComptable2.setJournal(new JournalComptable("OD", "Opérations Diverses"));
        vEcritureComptable2.setDate(dateNow);
        //WHEN
        managerTestCase.addReference(vEcritureComptable2);
        //THEN
        List<SequenceEcritureComptable> sECList = managerTestCase.getListSequenceEcritureComptable();
        SequenceEcritureComptable sECToTest = sECList.get(sECList.size()-1);
        int lastNumber = sECToTest.getDerniereValeur();
        int annee = sECToTest.getAnnee();
        Assert.assertEquals(sECToTest.getJournalCode(), "OD");
        Assert.assertEquals(annee, 2020);
        Assert.assertEquals(lastNumber, 1);
    }

    /*-- UNIT TEST : checkEcritureComptable --*/
    @Test
    @Transactional
    public void checkEcritureComptable() throws Exception {
        //GIVEN
        vEcritureComptable.setReference("AC-2020/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));
        managerTestCase.checkEcritureComptable(vEcritureComptable);
    }

    @Test
    @Transactional
    public void checkEcritureComptableIfEcritureCExist() throws Exception {
        //GIVEN
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 01, 01, 10, 59, 59);
        Date happyNewYearDate = calendar.getTime();
        vEcritureComptable.setDate(happyNewYearDate);
        vEcritureComptable.setReference("AC-2016/00001");
        //THEN
        expectedException.expect(FunctionalException.class);
        expectedException.expectMessage(
                "Une autre écriture comptable existe déjà avec la même référence.");
        //WHEN
        managerTestCase.checkEcritureComptable(vEcritureComptable);
    }

    /*-- UNIT TEST : insertEcritureComptable, update and delete--*/
    @Test
    @Transactional
    public void insertEcritureComptable() throws FunctionalException {
        //GIVEN
        EcritureComptable vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2020/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                null, null,
                new BigDecimal(123)));
        //WHEN
        managerTestCase.insertEcritureComptable(vEcritureComptable);
        //THEN
        List<EcritureComptable> listEcritureComptableExpected = managerTestCase.getListEcritureComptable();
        EcritureComptable ecritureComptableToTest = listEcritureComptableExpected.get(listEcritureComptableExpected.size()-1);
        Assert.assertEquals(ecritureComptableToTest.getLibelle(), "Libelle");
        Assert.assertEquals(ecritureComptableToTest.getReference(), "AC-2020/00001");
        managerTestCase.deleteEcritureComptable(ecritureComptableToTest.getId());
    }

    @Test
    @Transactional
    public void updateEcritureComptable() throws FunctionalException {
        //GIVEN
        EcritureComptable vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2020/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                null, null,
                new BigDecimal(123)));
        managerTestCase.insertEcritureComptable(vEcritureComptable);
        //WHEN
        vEcritureComptable.setLibelle("Libelle2");
        vEcritureComptable.setReference("AC-2020/00002");
        managerTestCase.updateEcritureComptable(vEcritureComptable);
        //THEN
        List<EcritureComptable> listEcritureComptableExpected = managerTestCase.getListEcritureComptable();
        EcritureComptable ecritureComptableToTest = listEcritureComptableExpected.get(listEcritureComptableExpected.size()-1);
        Assert.assertEquals(ecritureComptableToTest.getLibelle(), "Libelle2");
        Assert.assertEquals(ecritureComptableToTest.getReference(), "AC-2020/00002");
        managerTestCase.deleteEcritureComptable(vEcritureComptable.getId());
    }

    @Test
    @Transactional
    public void deleteEcritureComptable() throws FunctionalException {
        //GIVEN
        EcritureComptable vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2020/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                null, null,
                new BigDecimal(123)));
        managerTestCase.insertEcritureComptable(vEcritureComptable);
        //WHEN
        managerTestCase.deleteEcritureComptable(vEcritureComptable.getId());
        //THEN
        List<EcritureComptable> listEcritureComptableExpected = managerTestCase.getListEcritureComptable();
        EcritureComptable ecritureComptableToTest = listEcritureComptableExpected.get(listEcritureComptableExpected.size()-1);
        Assert.assertNotEquals(ecritureComptableToTest.getLibelle(), "Libelle");
        Assert.assertNotEquals(ecritureComptableToTest.getReference(), "AC-2020/00001");
    }

    /*-- UNIT TEST : insertSequenceEcritureComptable update and delete--*/
    @Test
    @Transactional
    public void insertSequenceEcritureComptable() throws FunctionalException {
        //GIVEN
        sequenceEcritureComptable2.setJournalCode("AC");
        sequenceEcritureComptable2.setDerniereValeur(1);
        sequenceEcritureComptable2.setAnnee(2022);
        //WHEN
        managerTestCase.insertSequenceEcritureComptable(sequenceEcritureComptable2);
        //THEN
        List<SequenceEcritureComptable> listSECExpected = managerTestCase.getListSequenceEcritureComptable();
        SequenceEcritureComptable sECExpected = listSECExpected.get(listSECExpected.size()-1);
        int lastNumber = sECExpected.getDerniereValeur();
        int annee = sECExpected.getAnnee();
        Assert.assertEquals(sECExpected.getJournalCode(), "AC");
        Assert.assertEquals(lastNumber, 1);
        Assert.assertEquals(annee, 2022);
        managerTestCase.deleteSequenceEcritureComptable(sECExpected);
    }

    /*-- UNIT TEST : updateSequenceEcritureComptable --*/
    @Test
    @Transactional
    public void updateSequenceEcritureComptableTest() throws FunctionalException {
        //GIVEN
        sequenceEcritureComptable2.setJournalCode("VE");
        sequenceEcritureComptable2.setDerniereValeur(1);
        sequenceEcritureComptable2.setAnnee(2020);
        managerTestCase.insertSequenceEcritureComptable(sequenceEcritureComptable2);
        //WHEN
        sequenceEcritureComptable2.setDerniereValeur(2);
        managerTestCase.updateSequenceEcritureComptable(sequenceEcritureComptable2);
        //THEN
        List<SequenceEcritureComptable> listSECExpected = managerTestCase.getListSequenceEcritureComptable();
        SequenceEcritureComptable sECExpected = listSECExpected.get(listSECExpected.size()-1);
        int lastNumber = sECExpected.getDerniereValeur();
        int annee = sECExpected.getAnnee();
        Assert.assertEquals(sECExpected.getJournalCode(), "VE");
        Assert.assertEquals(lastNumber, 2);
        Assert.assertEquals(annee, 2020);
        managerTestCase.deleteSequenceEcritureComptable(sECExpected);
    }

    /*-- UNIT TEST : deleteSequenceEcritureComptable --*/
    // Mock pour verifier si j bien appéle la méthode deleteSequence
    @Test
    @Transactional
    public void deleteSequenceEcritureComptable() throws FunctionalException {
        //GIVEN
        sequenceEcritureComptable2.setJournalCode("AC");
        sequenceEcritureComptable2.setDerniereValeur(2);
        sequenceEcritureComptable2.setAnnee(2021);
        managerMock.insertSequenceEcritureComptable(sequenceEcritureComptable2);
        //WHEN
        managerMock.deleteSequenceEcritureComptable(sequenceEcritureComptable2);
        //THEN
        List<SequenceEcritureComptable> listSECExpected = managerTestCase.getListSequenceEcritureComptable();
        SequenceEcritureComptable sECExpected = listSECExpected.get(listSECExpected.size()-1);
        int lastNumber = sECExpected.getDerniereValeur();
        int annee = sECExpected.getAnnee();
        Assert.assertNotEquals(sECExpected.getJournalCode(), "AC");
        Assert.assertNotEquals(annee, 2021);
        Assert.assertNotEquals(lastNumber, 2);
        Mockito.verify(managerMock, times(1)).deleteSequenceEcritureComptable(sequenceEcritureComptable2);
    }

}