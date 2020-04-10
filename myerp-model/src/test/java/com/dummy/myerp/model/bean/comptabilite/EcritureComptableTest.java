package com.dummy.myerp.model.bean.comptabilite;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class EcritureComptableTest {

    private EcritureComptable vEcriture;

    private LigneEcritureComptable createLigne(Integer pCompteComptableNumero, String pDebit, String pCredit) {
        BigDecimal vDebit = pDebit == null ? null : new BigDecimal(pDebit);
        BigDecimal vCredit = pCredit == null ? null : new BigDecimal(pCredit);
        String vLibelle = ObjectUtils.defaultIfNull(vDebit, BigDecimal.ZERO)
                                     .subtract(ObjectUtils.defaultIfNull(vCredit, BigDecimal.ZERO)).toPlainString();
        LigneEcritureComptable vRetour = new LigneEcritureComptable(new CompteComptable(pCompteComptableNumero),
                                                                    vLibelle,
                                                                    vDebit, vCredit);
        return vRetour;
    }

    @Before // avant chaque lancement du test
    public void init() {
        vEcriture = new EcritureComptable();
        vEcriture.setId(1);
        vEcriture.setJournal(new JournalComptable());
        vEcriture.setReference("1");
        vEcriture.setDate(new Date());

        vEcriture.setLibelle("Equilibrée");

        vEcriture.getListLigneEcriture().add(this.createLigne(1, "200.50", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "100.50", "33"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "301"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "40", "7"));

    }
    @Test
    public void isEquilibree() {
        Assert.assertTrue(vEcriture.toString(), vEcriture.isEquilibree());
    }
    @Test
    public void isNotEquilibree() {
        vEcriture.getListLigneEcriture().clear();
        vEcriture.setLibelle("Non équilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "10", null));
        Assert.assertFalse(vEcriture.toString(), vEcriture.isEquilibree());
    }


    @Test
    public void getTotalDebit() {
        int IsEqualToExceptedResult = vEcriture.getTotalDebit().compareTo(new BigDecimal(341));
        Assert.assertSame(0, IsEqualToExceptedResult);
    }

    @Test
    public void getTotalCredit() {
        int IsEqualToExceptedResult = vEcriture.getTotalCredit().compareTo(new BigDecimal(341));
        Assert.assertSame(0, IsEqualToExceptedResult);
    }

    @Test
    public void testToString() {

        StringBuilder vStB = new StringBuilder("EcritureComptable");
        String vSEP = ", ";
        vStB.append("{")
                .append("id=").append(vEcriture.getId())
                .append(vSEP).append("journal=").append(vEcriture.getJournal())
                .append(vSEP).append("reference='").append(vEcriture.getReference()).append('\'')
                .append(vSEP).append("date=").append(vEcriture.getDate())
                .append(vSEP).append("libelle='").append(vEcriture.getLibelle()).append('\'')
                .append(vSEP).append("totalDebit=").append(vEcriture.getTotalDebit().toPlainString())
                .append(vSEP).append("totalCredit=").append(vEcriture.getTotalCredit().toPlainString())
                .append(vSEP).append("listLigneEcriture=[\n")
                .append(StringUtils.join(vEcriture.getListLigneEcriture(), "\n")).append("\n]")
                .append("}");
        boolean IsEqualToExceptedResult = vStB.toString().equals(vEcriture.toString());
        Assert.assertTrue(IsEqualToExceptedResult);
    }
}
