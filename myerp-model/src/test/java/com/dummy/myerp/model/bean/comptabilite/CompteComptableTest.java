package com.dummy.myerp.model.bean.comptabilite;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CompteComptableTest {

    private List<CompteComptable> pList;
    private CompteComptable compteComptableExpected; //
    private CompteComptable compteComptableToTest;//

    @Before
    public void setUp() {
        CompteComptable compteComptable;
        pList = new ArrayList<>();
        List<String> accountsNames;
        accountsNames = Arrays.asList(
                "Comptes de Capitaux", "Compte d'immobilisations", "Comptes de Stocks",
                "Comptes de Tiers", "Comptes de financiers", "Comptes de charges",
                "Comptes de Produits", "Comptes Spéciaux"
        );
        int i = 1;
        for (String account : accountsNames) {
            compteComptable = new CompteComptable();
            compteComptable.setNumero(i);
            compteComptable.setLibelle(account);
            pList.add(compteComptable);
            i++;
        }

        compteComptableToTest = CompteComptable.getByNumero(pList, 1);
    }

    @Test
    public void testToString() {
        StringBuilder vStB = new StringBuilder("CompteComptable");
        String vSEP = ", ";
        vStB.append("{")
                .append("numero=").append(compteComptableToTest.getNumero())
                .append(vSEP).append("libelle='").append(compteComptableToTest.getLibelle()).append('\'')
                .append("}");
        Assert.assertEquals(vStB.toString(), compteComptableToTest.toString());
    }

    @Test
    public void getByNumero() {
        compteComptableExpected = new CompteComptable(1, "Comptes de Capitaux");
        Assert.assertEquals(compteComptableExpected.toString(), compteComptableToTest.toString());
    }

    @Test
    public void getNotEqualByNumero() {
        compteComptableExpected = new CompteComptable(2, "Compte d'immobilisations");
        Assert.assertNotEquals(compteComptableExpected.toString(), compteComptableToTest.toString());
    }

    @Test
    public void getNullByNullNumero() {
        compteComptableToTest = CompteComptable.getByNumero(pList, null);
        Assert.assertEquals(null, compteComptableToTest);
    }

    @Test
    public void getNullByNullList() {
        compteComptableToTest = CompteComptable.getByNumero(null, 1);
        Assert.assertEquals(null, compteComptableToTest);
    }
}