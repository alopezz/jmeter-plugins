/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kg.apc.jmeter.gui;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author undera
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({kg.apc.jmeter.gui.DeleteRowActionTest.class,kg.apc.jmeter.gui.ButtonPanelAddCopyRemoveTest.class,kg.apc.jmeter.gui.AddRowActionTest.class,kg.apc.jmeter.gui.CopyRowActionTest.class})
public class GuiSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

}