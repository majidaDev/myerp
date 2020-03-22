package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import com.dummy.myerp.technical.exception.FunctionalException;
import java.util.List;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;	import org.junit.*;
import org.junit.rules.ExpectedException;

public class ComptabiliteManagerImplTest {

    private static ComptabiliteManagerImpl manager;
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
        manager = new ComptabiliteManagerImpl();
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
        sequenceEcritureComptable2 = new SequenceEcritureComptable();
    }
    @After
    public void tearDown() throws Exception {
        sequenceEcritureComptable2 = null;
        vEcritureComptable = null;
    }
    /*-- UNIT TEST : checkEcritureComptableUnit --*/
     @Test
     public void checkEcritureComptableUnit() throws Exception {
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitViolation() throws Exception {
        vEcritureComptable.setReference("AKKC-"+Integer.toString(calendar.YEAR)+"/00001");
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG2() throws Exception {
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(1234)));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test
    public void checkEcritureComptableUnitRG3() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, null,
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, null,

                expectedException.expect(FunctionalException.class);
        expectedException.expectMessage(
                "L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test
    public void checkEcritureComptableUnitRG5Ref() throws Exception {
        vEcritureComptable.setReference("BK-2020/00001");
        expectedException.expect(FunctionalException.class);
        expectedException.expectMessage(
                "Le code journal dans la référence ne correspond pas au code journal de l'écriture.");                                                                     null));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    @Test
    public void checkEcritureComptableUnitRG5Year() throws Exception {
        vEcritureComptable.setReference("AC-2018/00001");
        expectedException.expect(FunctionalException.class);
        expectedException.expectMessage(
                "L'année dans la référence ne correspond pas à l'année de l'écriture.");
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    /*-- UNIT TEST : DerniereValeurSequenceEcritureComptable --*/
    @Test
    public void givenZero_WhenDerniereValeurSequenceEcritureComptable_ThenReturnOne() {
        Assert.assertEquals(1, manager.derniereValeurSequenceEcritureComptable(0));
    }

    @Test
    public void givenNumber_WhenDerniereValeurSequenceEcritureComptable_ThenIncrementNumber() {
        Assert.assertEquals(2, manager.derniereValeurSequenceEcritureComptable(1));
    }

    /*-- UNIT TEST : GetNewReference --*/
    @Test
    public void givenThreeString_WhenGetNewReference_ThenReturnString() {
        Assert.assertEquals("AC-2020/00001", manager.getNewReference("AC","2020","00001"));
    }
}
