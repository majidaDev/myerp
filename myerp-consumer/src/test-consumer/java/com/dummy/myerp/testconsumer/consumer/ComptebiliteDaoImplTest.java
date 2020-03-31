package com.dummy.myerp.testconsumer.consumer;

import org.junit.Test;

public class ComptebiliteDaoImplTest extends ConsumerTestCase {

    @Test
    public void setup() {
        getDaoProxy().getComptabiliteDao().getListJournalComptable();
    }
}