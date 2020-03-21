package com.dummy.myerp.model.bean.comptabilite;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class JournalComptableTest {

    private JournalComptable journalComptable1;

    private JournalComptable journalComptable2;

    private JournalComptable journalComptable3;

    private JournalComptable journalComptableExpected;

    private List<JournalComptable> listJournalComptable;

    @Before
    public void setUp() {
        listJournalComptable = new ArrayList<>();

        journalComptable1 = new JournalComptable();
        journalComptable1.setCode("HA");
        journalComptable1.setLibelle("Journal des Achats et frais");
        listJournalComptable.add(journalComptable1);

        journalComptable2 = new JournalComptable();
        journalComptable2.setCode("CA");
        journalComptable2.setLibelle("Journal de Caisse");
        listJournalComptable.add(journalComptable2);

        journalComptable3 = new JournalComptable();
        journalComptable3.setCode("BQ");
        journalComptable3.setLibelle("Journal de Banque");
        listJournalComptable.add(journalComptable3);

        journalComptableExpected = new JournalComptable("HA", "Journal des Achats et frais");
    }

    @Test
    public void testToString() {
        StringBuilder vStB = new StringBuilder("JournalComptable");
        String vSEP = ", ";
        vStB.append("{")
                .append("code='").append(journalComptable1.getCode()).append('\'')
                .append(vSEP).append("libelle='").append(journalComptable1.getLibelle()).append('\'')
                .append("}");
        Assert.assertEquals(vStB.toString(), journalComptable1.toString());
    }

    @Test
    public void getByCode() {
        Assert.assertEquals(journalComptableExpected.toString(), JournalComptable.getByCode(listJournalComptable,"HA").toString());
    }

    @Test
    public void getNotEqualByCode() {
        Assert.assertNotEquals(journalComptableExpected.toString(), JournalComptable.getByCode(listJournalComptable,"BQ").toString());
    }

    @Test
    public void getNullByCode() {
        Assert.assertEquals(null, JournalComptable.getByCode(listJournalComptable,null));
    }

    @Test
    public void getNullByCodeIfListNull() {
        Assert.assertEquals(null, JournalComptable.getByCode(null,"BQ"));
    }
}
