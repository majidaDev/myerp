package com.dummy.myerp.testbusiness.business;

import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ComptabiliteManagerImplTest extends BusinessTestCase {

    private static ComptabiliteManager managerTestCase;
    private static EcritureComptable vEcritureComptable;
    private static List<SequenceEcritureComptable> listSeqExpected = new ArrayList<>();
    private static SequenceEcritureComptable sequenceEcritureComptable;
    private static SequenceEcritureComptable sequenceEcritureComptable2;
    private static Date dateNow;
    private static Calendar calendar;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @BeforeClass
    public static void setUpBC() {
        managerTestCase = getBusinessProxy().getComptabiliteManager();
        dateNow = new Date();
        calendar = Calendar.getInstance();
        calendar.setTime(dateNow);
    }

    @Before
    public void setUp() {
        vEcritureComptable = new EcritureComptable();
        sequenceEcritureComptable = new SequenceEcritureComptable("AC", 2020, 1);
        listSeqExpected.add(sequenceEcritureComptable);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat",listSeqExpected));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2020/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));

        sequenceEcritureComptable2 = new SequenceEcritureComptable();
    }

    @After
    public void tearDown() throws Exception {
        sequenceEcritureComptable2 = null;
        vEcritureComptable = null;
    }

    /*-- UNIT TEST : addReference --*/
    @Test
    public void checkAddReferenceIfAlreadyInDB() throws Exception {
        sequenceEcritureComptable = new SequenceEcritureComptable("AC", 2016, 40);
        listSeqExpected.add(sequenceEcritureComptable);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat",listSeqExpected));
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 01, 01, 10, 59, 59);
        Date happyNewYearDate = calendar.getTime();
        vEcritureComptable.setDate(happyNewYearDate);
        vEcritureComptable.setReference("AC-2016/00041");
        managerTestCase.addReference(vEcritureComptable);
    }

    /*-- UNIT TEST : addReference --*/
    @Test
    public void checkAddReferenceIsNotInDB() throws Exception {
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(dateNow);
        vEcritureComptable.setReference("AC-2020/00001");
        managerTestCase.addReference(vEcritureComptable);
        managerTestCase.deleteSequenceEcritureComptable(sequenceEcritureComptable2);
    }

    /*-- UNIT TEST : checkEcritureComptable --*/
    @Test
    public void checkEcritureComptable() throws Exception {
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));
        managerTestCase.checkEcritureComptable(vEcritureComptable);
    }

    @Test
    public void checkEcritureComptableIfEcritureCExist() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 01, 01, 10, 59, 59);
        Date happyNewYearDate = calendar.getTime();
        vEcritureComptable.setDate(happyNewYearDate);
        vEcritureComptable.setReference("AC-2016/00001");
        expectedException.expect(FunctionalException.class);
        expectedException.expectMessage(
                "Une autre écriture comptable existe déjà avec la même référence.");
        managerTestCase.checkEcritureComptable(vEcritureComptable);
    }

    /*-- UNIT TEST : insertEcritureComptable, update and delete--*/
    @Test
    public void insert_update_delete_EcritureComptable() throws FunctionalException {
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

        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                null, new BigDecimal(124),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                null, null,
                new BigDecimal(124)));
        managerTestCase.updateEcritureComptable(vEcritureComptable);

        managerTestCase.deleteEcritureComptable(vEcritureComptable.getId());
    }

    /*-- UNIT TEST : insertSequenceEcritureComptable --*/
    @Test
    public void insertSequenceEcritureComptable() throws FunctionalException {
        sequenceEcritureComptable2.setJournalCode("AC");
        sequenceEcritureComptable2.setDerniereValeur(1);
        sequenceEcritureComptable2.setAnnee(2020);

        managerTestCase.insertSequenceEcritureComptable(sequenceEcritureComptable2);
        managerTestCase.deleteSequenceEcritureComptable(sequenceEcritureComptable2);
    }

    /*-- UNIT TEST : updateSequenceEcritureComptable --*/
    @Test
    public void updateSequenceEcritureComptableTest() throws FunctionalException {
        sequenceEcritureComptable2.setJournalCode("AC");
        sequenceEcritureComptable2.setDerniereValeur(2);
        sequenceEcritureComptable2.setAnnee(2020);

        managerTestCase.updateSequenceEcritureComptable(sequenceEcritureComptable2);
    }

    /*-- UNIT TEST : deleteSequenceEcritureComptable --*/
    @Test
    public void deleteSequenceEcritureComptable() throws FunctionalException {
        sequenceEcritureComptable2.setJournalCode("AC");
        sequenceEcritureComptable2.setDerniereValeur(2);
        sequenceEcritureComptable2.setAnnee(2020);

        managerTestCase.deleteSequenceEcritureComptable(sequenceEcritureComptable2);
    }

}