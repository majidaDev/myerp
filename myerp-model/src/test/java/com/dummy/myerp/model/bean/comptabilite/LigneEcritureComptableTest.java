package com.dummy.myerp.model.bean.comptabilite;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.validation.constraints.AssertTrue;
import java.math.BigDecimal;

import static org.junit.Assert.*;

public class LigneEcritureComptableTest {

    private LigneEcritureComptable ligneEcritureComptable;

    private LigneEcritureComptable createLigne(Integer pCompteComptableNumero, String pDebit, String pCredit) {
        BigDecimal vDebit = pDebit == null ? null : new BigDecimal(pDebit);
        BigDecimal vCredit = pCredit == null ? null : new BigDecimal(pCredit);
        String vLibelle = ObjectUtils.defaultIfNull(vDebit, BigDecimal.ZERO)
                .subtract(ObjectUtils.defaultIfNull(vCredit, BigDecimal.ZERO)).toPlainString();
        LigneEcritureComptable vRetour = new LigneEcritureComptable();
        vRetour.setCompteComptable(new CompteComptable(pCompteComptableNumero));
        vRetour.setLibelle(vLibelle);
        vRetour.setDebit(vDebit);
        vRetour.setCredit(vCredit);
        return vRetour;
    }

    @Before
    public void setUp() {
        ligneEcritureComptable = this.createLigne(1, "200", null);
    }

    @Test
    public void testToString() {
        StringBuilder vStB = new StringBuilder("LigneEcritureComptable");
        String vSEP = ", ";
        vStB.append("{")
                .append("compteComptable=").append(ligneEcritureComptable.getCompteComptable())
                .append(vSEP).append("libelle='").append(ligneEcritureComptable.getLibelle()).append('\'')
                .append(vSEP).append("debit=").append(ligneEcritureComptable.getDebit())
                .append(vSEP).append("credit=").append(ligneEcritureComptable.getCredit())
                .append("}");
        Assert.assertEquals(vStB.toString(), ligneEcritureComptable.toString());
    }
}
